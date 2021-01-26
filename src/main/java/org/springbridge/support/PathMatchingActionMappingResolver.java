 /*
 * Copyright 2020 Anoop V S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springbridge.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.Globals;
import org.springbridge.utils.ControllerUtils;
import org.springbridge.utils.PathMatcher;
import org.springbridge.utils.WildcardPathMatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

/**
 * Strategy implementation which identifies ActionMapping based on Controllers
 * RequestMapping.It will return ActionMapping from Spring Context with path
 * equal or matching to incoming request URI
 * 
 * @author Anoop V S
 *
 */
public class PathMatchingActionMappingResolver implements ActionMappingResolver {

	public static final String NO_ACTION_MAPPING_FOUND = "Unable to locate any ActionMapping for input path ['%s'].";

	private static final UrlPathHelper urlPathHelper = new UrlPathHelper();

	protected static final Log logger = LogFactory.getLog(PathMatchingActionMappingResolver.class);

	/**
	 * WildcardPathMatcher Copied from Struts 1.x/2.x Framework code.Spring
	 * Framework also provides AntPathMatcher.Struts based WildcardPathMatcher which
	 * provides additional meta data (Matched Patterns Map) to regenerate Paths for
	 * Wildcard mappings. WildcardPathMatchers match method always need Map as input
	 * parameter.Spring UriTemplate is more elegant than this eventually we need to
	 * move that path.
	 * 
	 * <action path="/Source*" forward="/jsp/{1}/Source.jsp" />
	 */
	private PathMatcher wildcardPathMatcher = new WildcardPathMatcher();

	/**
	 * Predicate identifies how to find the correct ActionMapping from Spring
	 * Context.Default strategy is handlerPath equals ActionMapping path,When
	 * control reaches at ActionMappingResolver handlerPath cannot be {@code null}
	 */
	private BiPredicate<ActionMapping, String> pathMatcher = (m, p) -> p.equals(m.getPath());

	private final ApplicationContext applicationContext;

	private Collection<ActionMapping> actionMappings;

	private int order = Ordered.LOWEST_PRECEDENCE;

	public PathMatchingActionMappingResolver(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected final Collection<ActionMapping> getActionMappingBeans() {
		if (Objects.isNull(actionMappings)) {
			actionMappings = ControllerUtils.findActionMappings(applicationContext);
		}
		return actionMappings;
	}

	protected Map<String, ActionMapping> getActionMappingCache() {
		return CacheHolder.actionMappingCache;
	}

	public final PathMatcher getWildcardPathMatcher() {
		return this.wildcardPathMatcher;
	}

	public void setWildPathMatcher(final PathMatcher wildcardPathMatcher) {
		if (Objects.nonNull(wildcardPathMatcher)) {
			this.wildcardPathMatcher = wildcardPathMatcher;
		}
	}

	@Override
	public final ActionMapping resolveActionMapping(final HttpServletRequest httpRequest) {
		final String pathWithServletMapping = getHandlerPath(httpRequest);
		logger.debug("HandlerPath::['" + pathWithServletMapping + "']");
		final String handlerPath = processHandlerPath(pathWithServletMapping);
		logger.debug("Processed Handler Path::['" + handlerPath + "']");
		ActionMapping actionMapping = CacheHolder.actionMappingCache.get(handlerPath);
		if (Objects.isNull(actionMapping)) {
			actionMapping = lookupAndCacheActionMapping(httpRequest, handlerPath);
			logger.debug("Got ActionMapping for servletPath::['" + handlerPath + "'];" + actionMapping);
		}
		return actionMapping;
	}

	/**
	 * Lookup matching ActionMapping from Spring Context based on handlerPath. This
	 * method will go for Wildcard matching for mappings paths if no direct path
	 * ActionMapping found e.g:- {@code simpleMapping.setPath("/Prepare*.do"); }
	 * 
	 * @param httpRequest
	 * @param handlerPath
	 * @return
	 */
	protected ActionMapping lookupAndCacheActionMapping(final HttpServletRequest httpRequest,
			final String handlerPath) {
		ActionMapping actionMapping;
		/*
		 * Two step lookup process to avoid accidently picking wrong ActionMapping in
		 * case of Wildcard RequestMapping .First go for direct Path matching Strategy
		 */
		final Optional<ActionMapping> directMatchingActionMapping = getActionMappingBeans().stream()
				.filter(m -> getPathMatcher().test(m, handlerPath)).findFirst();
		if (directMatchingActionMapping.isPresent()) {
			actionMapping = directMatchingActionMapping.get();
			/*
			 * Avoid OOM errors in Framework due to DDOS by caching actionMapping details
			 * only for literal Paths.So we always dealt with limited set of data
			 */
			CacheHolder.actionMappingCache.putIfAbsent(handlerPath, actionMapping);
			logger.debug("Cached ActionMapping for Handler Path::['" + handlerPath + "']");
		} else {
			// No luck ,Go for pattern matching using WildcardPathMatcher.
			logger.debug("Unable to locate ActionMapping for servlet path ['" + handlerPath
					+ "'] using direct path matching Strategy.Going to match the given servlet path "
					+ " against the existing ActionMapping path pattern.");
			/*
			 * Map Keys are always same,So values will be overwritten in each iteration. "0"
			 * key always contains complete data ie handlerPath
			 */
			final Map<String, String> matchedPatterns = new HashMap<>(2);
			actionMapping = getActionMappingBeans().stream().filter(m -> isPattern(m.getPath()))
					.filter(m -> matches(matchedPatterns, m, handlerPath)).findFirst()
					.orElseThrow(() -> new IllegalStateException(String.format(NO_ACTION_MAPPING_FOUND, handlerPath)));
			if (matchedPatterns.size() > 1) {// Additional data present
				httpRequest.setAttribute(Globals.MATCHED_PATTERNS_MAP, matchedPatterns);
			}
		}
		return actionMapping;
	}

	/**
	 * Checks path contains wildcards
	 * 
	 * @param path
	 * @return true if path contains wildcards.
	 */
	public final boolean isPattern(final String path) {
		return !getWildcardPathMatcher().isLiteral(path);
	}

	/**
	 * Try to match the given handlerPath with ActionMapping Path
	 * 
	 * @param matchedPatterns
	 * @param actionMapping
	 * @param handlerPath
	 * @return true if matches ,false otherwise
	 */
	protected boolean matches(final Map<String, String> matchedPatterns, final ActionMapping actionMapping,
			final String handlerPath) {
		final int[] compiledPattern = wildcardPathMatcher.compilePattern(actionMapping.getPath());
		return getWildcardPathMatcher().match(matchedPatterns, handlerPath, compiledPattern);
	}

	/**
	 * Hook for subclass to change handlerPath for Module based application
	 * 
	 * @param handlerPath
	 * @return modified/unmodified handlerPath
	 */
	protected String processHandlerPath(String handlerPath) {
		return handlerPath;
	}

	/**
	 * @return BiPredicate used to locate ActionMapping from Spring Context
	 */
	public final BiPredicate<ActionMapping, String> getPathMatcher() {
		return pathMatcher;
	}

	public void setPathMatcher(final BiPredicate<ActionMapping, String> pathMatcher) {
		if (Objects.nonNull(pathMatcher)) {
			this.pathMatcher = pathMatcher;
		}
	}

	protected String getHandlerPath(final HttpServletRequest httpRequest) {
		String handlerPath = (String) httpRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		/*
		 * Ideally below code won't execute Since Spring MVC Handlers will populate
		 * required Request Attributes
		 */
		if (Objects.isNull(handlerPath)) {
			handlerPath = getUrlPathHelper().getLookupPathForRequest(httpRequest);
		}
		return handlerPath;
	}

	public static UrlPathHelper getUrlPathHelper() {
		return urlPathHelper;
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * Static nested class to lazily load the Cache in a thread safe way. Do we really
	 * need IODH here ?? Or can we create and allocate it eagerly??
	 * 
	 * @author Anoop V S
	 */
	private static class CacheHolder {
		// key handlerPath value ActionMapping
		static final Map<String, ActionMapping> actionMappingCache = new ConcurrentHashMap<>(256);
	}
}
