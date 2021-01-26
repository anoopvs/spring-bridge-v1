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
package org.springbridge.web.method.support;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.Globals;
import org.springbridge.support.ActionMappingResolver;
import org.springbridge.support.PathMatchingActionMappingResolver;
import org.springbridge.support.annotation.ActionMappingConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @implNote This ArgumentResolver will lookup ActionMapping from Spring
 *           ApplicationContext and pass it to handler method.This also places
 *           ActionMapping in request scope .<b>ActionMapping argument must come
 *           before ModelAttribute argument in handler method.</b>
 *           .WebMvcConfigurer interface defines callback methods to customize
 *           the Java-based configuration for Spring MVC enabled via
 *           {@code @EnableWebMvc}.
 * 
 * @author Anoop V S
 * @see #addArgumentResolvers(List) from WebMvcConfigurer
 * 
 *
 */
public class ActionMappingConfigMethodProcessor
		implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler, WebMvcConfigurer {

	public static final Log logger = LogFactory.getLog(ActionMappingConfigMethodProcessor.class);

	public static final String ILLEGAL_CONFIG_MSG = "Unable to locate ActionMapping with name ['%s'].Have Configured ActionMapping as a bean in Application Context ?";

	private final ApplicationContext applicationContext;
	/**
	 * Strategy interface for Identifying current ActionMapping for Action execution
	 */
	private ActionMappingResolver actionMappingResolver;

	public ActionMappingConfigMethodProcessor(final ApplicationContext applicationContext) {
		this.applicationContext = Objects.requireNonNull(applicationContext,
				Globals.APPLICATION_CONTEXT_CANNOT_BE_NULL);
		this.actionMappingResolver = new PathMatchingActionMappingResolver(applicationContext);
	}

	@Override
	public boolean supportsReturnType(final MethodParameter returnType) {
		return isValidMethodParameter(returnType);
	}

	public final boolean isValidMethodParameter(final MethodParameter parameter) {
		final Class<?> paramType = parameter.getParameterType();
		return (parameter.hasParameterAnnotation(ActionMappingConfig.class)
				&& ActionMapping.class.isAssignableFrom(paramType)) || ActionMapping.class.isAssignableFrom(paramType);
	}

	@Override
	public void handleReturnValue(final Object returnValue, final MethodParameter returnType,
			final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest) throws Exception {
		if (Objects.nonNull(returnValue)) {
			final String name = ModelFactory.getNameForReturnValue(returnValue, returnType);
			mavContainer.addAttribute(name, returnValue);
		}
	}

	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return isValidMethodParameter(parameter);
	}

	protected Map<String, ActionMapping> getActionMappingCache() {
		return CacheHolder.actionMappingCache;
	}

	protected ActionMapping getActionMapping(final MethodParameter parameter, final NativeWebRequest webRequest) {
		final HttpServletRequest httpRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		Objects.requireNonNull(httpRequest, Globals.HTTP_SERVLET_REQUEST_CANNOT_BE_NULL);
		final ActionMappingConfig annActionMapping = parameter.getParameterAnnotation(ActionMappingConfig.class);
		ActionMapping actionMapping = null;
		if (Objects.nonNull(annActionMapping)) {
			final String beanName = processBeanName(httpRequest, annActionMapping.value());
			if (!StringUtils.hasText(beanName)) {
				throw new IllegalStateException(
						"Unable to locate Valid Bean Name from ActionMappingConfig Annotation on Method Parameter");
			}
			actionMapping = CacheHolder.actionMappingCache.get(beanName);
			if (Objects.isNull(actionMapping)) {
				actionMapping = applicationContext.getBean(beanName, ActionMapping.class);
				CacheHolder.actionMappingCache.putIfAbsent(beanName, actionMapping);
				if (logger.isDebugEnabled()) {
					logger.debug("Got Non null ActionMapping with name ['" + beanName + "']");
				}
			}
		} else {
			/*
			 * ActionMappingConfig annotation not present,Do some fallback strategy to
			 * resolve ActionMapping.Most of the cases this will be the default strategy.
			 */
			actionMapping = actionMappingResolver.resolveActionMapping(httpRequest);
		}
		return actionMapping;
	}

	/**
	 * Hook for subclass to change bean name for Module based application
	 * 
	 * @param httpRequest - HttpServletRequest for subclass
	 * @param beanName
	 * @return
	 */
	protected String processBeanName(final HttpServletRequest httpRequest, String beanName) {
		return beanName;
	}

	@Override
	public final Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
			final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
		final ActionMapping actionMapping = getActionMapping(parameter, webRequest);
		if (Objects.nonNull(actionMapping)) {
			final HttpServletRequest httpRequest = Objects.requireNonNull(
					webRequest.getNativeRequest(HttpServletRequest.class), Globals.HTTP_SERVLET_REQUEST_CANNOT_BE_NULL);
			// MAPPING_KEY is commonly used by JSP Pages
			httpRequest.setAttribute(Globals.MAPPING_KEY, actionMapping);
		}
		return actionMapping;
	}

	public ActionMappingResolver getActionMappingResolver() {
		return actionMappingResolver;
	}

	public void setActionMappingResolver(final ActionMappingResolver actionMappingResolver) {
		if (Objects.nonNull(actionMappingResolver)) {
			this.actionMappingResolver = actionMappingResolver;
		}
	}

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(this);
	}

	/**
	 * Static nested class to lazily create the Cache in a thread safe way.
	 * 
	 * @author Anoop V S
	 */
	private static class CacheHolder {
		static final Map<String, ActionMapping> actionMappingCache = new ConcurrentHashMap<>(256);
	}
}
