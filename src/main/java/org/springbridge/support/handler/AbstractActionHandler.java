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
package org.springbridge.support.handler;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionForward;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.ConfigurationException;
import org.springbridge.action.Globals;
import org.springbridge.support.DefaultModelAndViewGenerator;
import org.springbridge.support.ExecuteFunction;
import org.springbridge.support.ModelAndViewGenerator;
import org.springbridge.support.utils.MessageResources;
import org.springbridge.utils.ControllerUtils;
import org.springbridge.validation.impl.CompositeValidator;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

/**
 * Almost complete implementation of how handle execution flow of
 * ActionClass.<b>handleActionExecution</b> template method delegates execution
 * flow to fine grained internal methods which can be customized for specific
 * application needs.This class provides subset of functionalities provided by
 * old Struts 1.x RequestProcessor.AbstractActionHandler bean can resolve
 * exceptions thrown during handler mapping or execution since this also
 * implements {@code HandlerExceptionResolver}
 * 
 * @author Anoop V S
 *
 */
public abstract class AbstractActionHandler implements HandlerExceptionResolver, EnvironmentAware, EnvironmentCapable,
		InitializingBean, ApplicationContextAware, ApplicationEventPublisherAware, BeanNameAware, ActionHandler {
	/** Cache Control headers */
	public static final String EXPIRES = "Expires";

	public static final String PRAGMA = "Pragma";

	public static final String CACHE_CONTROL = "Cache-Control";

	/** Commons logger */
	public final Log logger = LogFactory.getLog(getClass());
	
	/** Spring bridge MessageSource Proxy */
	private MessageResources messageResources;

	private ApplicationContext applicationContext;

	private ModelAndViewGenerator mavGenerator;

	private CompositeValidator compositeValidator;

	private Environment environment;
	/**
	 * Flag to decide whether to set no cache HTTP response headers
	 */
	private boolean noCache = false;
	/**
	 * Flag to decide whether to set specific characterEncoding to HTTP response
	 */
	private boolean characterEncoding = false;
	/**
	 * Flag to decide whether to populate Error Request Attributes
	 */
	private boolean exposeErrorRequestAttributes = false;

	/** Default Struts 1.x contentType */
	private String contentType = MediaType.TEXT_HTML_VALUE;
	
	/** Charset value */
	private String charset = WebUtils.DEFAULT_CHARACTER_ENCODING;
	
	/** Generic response headers added with each response */
	private Map<String, List<Supplier<String>>> globalResponseHeaderSuppliers;

	private ActionExceptionHandler exceptionHandler;

	/** ApplicationEventPublisher for this handler. */
	private ApplicationEventPublisher applicationEventPublisher;

	/**
	 * Should we publish a RequestHandledEvent at the end of handleActionExecution.
	 * This is turned off by default.Turn on this to identify Performance issues.
	 */
	private boolean publishEvents = false;
	
	/**
	 * Application specific ActionHandler name .Used in publishActionExecutedEvent
	 * method.This is the bean name of ActionHandler configured in Spring
	 * ApplicationContext.
	 */
	private String handlerName = this.getClass().getSimpleName() + "('%s') ";
	
	/**
	 * Application specific User Details extract Function.
	 */
	private Function<HttpServletRequest, String> userDetailsFunction;

	public boolean isNoCache() {
		return noCache;
	}

	public void setNoCache(boolean noCache) {
		this.noCache = noCache;
	}

	public boolean isPublishEvents() {
		return publishEvents;
	}

	public void setPublishEvents(boolean publishEvents) {
		this.publishEvents = publishEvents;
	}

	public boolean isCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(boolean characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public boolean isExposeErrorRequestAttributes() {
		return exposeErrorRequestAttributes;
	}

	public void setExposeErrorRequestAttributes(boolean exposeErrorRequestAttributes) {
		this.exposeErrorRequestAttributes = exposeErrorRequestAttributes;
	}

	public CompositeValidator getCompositeValidator() {
		return compositeValidator;
	}

	@Autowired(required = false)
	public void setCompositeValidator(@Nullable final CompositeValidator compositeValidator) {
		this.compositeValidator = compositeValidator;
	}

	public ModelAndViewGenerator getMavGenerator() {
		return mavGenerator;
	}

	@Autowired(required = false)
	public void setMavGenerator(@Nullable final ModelAndViewGenerator mavGenerator) {
		this.mavGenerator = mavGenerator;
	}

	/**
	 * Method from {@link EnvironmentCapable#getEnvironment()}
	 */
	@Override
	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setApplicationEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public ApplicationEventPublisher getApplicationEventPublisher() {
		return applicationEventPublisher;
	}

	public Map<String, List<Supplier<String>>> getGlobalResponseHeaderSuppliers() {
		return Collections.unmodifiableMap(globalResponseHeaderSuppliers);
	}

	public void setGlobalResponseHeaderSuppliers(@Nullable final Map<String, List<Supplier<String>>> responseHeaders) {
		if (Objects.nonNull(responseHeaders) && !responseHeaders.isEmpty()) {
			if (Objects.isNull(this.globalResponseHeaderSuppliers)) {
				this.globalResponseHeaderSuppliers = new HashMap<>(responseHeaders.size());
			}
			this.globalResponseHeaderSuppliers.putAll(responseHeaders);
		}
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(final String charset) {
		this.charset = charset;
	}

	public MessageResources getMessageResources() {
		return messageResources;
	}

	@Autowired(required = false)
	public void setMessageResources(final MessageResources messageResources) {
		this.messageResources = messageResources;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ActionExceptionHandler getExceptionHandler() {
		if (Objects.isNull(exceptionHandler)) {
			this.exceptionHandler = new DefaultActionExceptionHandler(applicationContext);
		}
		return exceptionHandler;
	}

	public void setExceptionHandler(final ActionExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public void setBeanName(final String beanName) {
		this.handlerName = String.format(handlerName, beanName);
	}

	public Function<HttpServletRequest, String> getUserDetailsFunction() {
		return userDetailsFunction;
	}

	public void setUserDetailsFunction(final Function<HttpServletRequest, String> userDetailsFunction) {
		this.userDetailsFunction = userDetailsFunction;
	}

	/**
	 * Even if Some exception happens during execution flow.This assumes we will
	 * internally handle it gracefully,Just as error-page concept in JEE
	 */
	@Override
	public final ModelAndView handleActionExecution(final ActionContext ctx) {
		debug("handleActionExecution::Entry");
		final long startTime = System.currentTimeMillis();
		Throwable failureCause = null;
		final HttpServletRequest httpRequest = ctx.getHttpServletRequest();
		final HttpServletResponse httpResponse = ctx.getHttpServletResponse();
		final ActionForm form = ctx.getForm();
		final ActionMapping mapping = ctx.getActionMapping();
		final Errors bindingResult = ctx.getBindingResult();
		final Model inModel = ctx.getInputModel();
		final Object controller = ctx.getController();
		ActionForward forward = null;
		try {
			// Put ActionContext in request scope so other Web components can reuse it
			populateHttpServletRequestAttributes(ctx);
			// Populate Response Headers based on ActionMapping configuration.
			populateGlobalResponseHeaders(ctx);
			// Set the content type and no-caching headers
			processContent(ctx);
			processNoCache(ctx);
			// Set the CharacterEncoding at a common place
			processEncoding(ctx);
			// Check user roles based on ActionMapping configuration.
			if (!processRoles(httpRequest, httpResponse, mapping)) {
				// Block the user
				debug("processRoles()::Failed.");
				return null;
			}
			// Take ActionForm and set it to correct scope
			processActionForm(ctx);
			// Do Form Validation based on ActionMapping configuration.
			forward = processFormValidation(form, mapping, httpRequest, bindingResult);
			if (Objects.isNull(forward)) {
				/*
				 * All execution through ActionHandler requires Spring Controllers to initiate
				 * the execution Flow using handleActionExecution method,so no processForward or
				 * processInclude as in RequestProcessor. Directly perform actual Action
				 * Execution.
				 */
				forward = doActionExecution(ctx);
			} else {
				/* Indicate Validation Failure.Move to Input Page */
				debug("processFormValidation()::Failed.['" + forward + "']");
			}
			if (Objects.isNull(forward) && logger.isDebugEnabled()) {
				debug("handleActionExecution()::Null inForward.Action Class ['" + mapping.getType() + "']");
			}
			return generateModelAndViewFromActionForward(forward, httpRequest, httpResponse, inModel);
		} catch (final Exception exc) {
			// DispatcherServlets doDispatch handles Throwable
			failureCause = exc;
			final StringBuilder errorInfoBuilder = new StringBuilder(128);
			errorInfoBuilder.append("URI['").append(httpRequest.getServletPath());
			if (Objects.nonNull(mapping)) {
				errorInfoBuilder.append("'];ActionMapping Path['").append(mapping.getPath());
				errorInfoBuilder.append("'];Action['").append(mapping.getType());
			}
			errorInfoBuilder.append("'];").append(exc.getMessage());
			logger.error("Exception::handleActionExecution()::" + errorInfoBuilder.toString());
			// Resolve Exception will log the actual Exception
			return resolveException(httpRequest, httpResponse, controller, exc);
		} finally {
			debug("handleActionExecution::Exit");
			publishActionExecutedEvent(httpRequest, httpResponse, startTime, failureCause);
		}
	}

	/**
	 * Do normal execute method invocation.If any exception during action execution
	 * ,Pass controls to appropriate ExceptionHandler
	 * 
	 * @param ctx
	 * @return ActionForward
	 * @throws Exception
	 */
	protected ActionForward doActionExecution(final ActionContext ctx) throws Exception {
		debug("doActionExecution::Entry");
		final HttpServletRequest httpRequest = ctx.getHttpServletRequest();
		final HttpServletResponse httpResponse = ctx.getHttpServletResponse();
		final ActionForm form = ctx.getForm();
		final ActionMapping mapping = ctx.getActionMapping();
		final ExecuteFunction executeMethod = ctx.getExecuteFunction();
		ActionForward forward = null;
		try {
			forward = executeMethod.execute(mapping, form, httpRequest, httpResponse);
		} catch (final Exception exc) {
			// First delegate exception handling to ExceptionHandler impls
			forward = handleActionExecutionException(ctx, exc);
			debug("ActionForward after handleActionExecutionException::['" + forward + "']");
			if (Objects.isNull(forward)) {
				// not able to handle Exception .Propagate to higher Layers
				throw exc;
			}
		}
		debug("doActionExecution::Exit");
		return forward;

	}

	/**
	 * Take current ActionForm Object and set it to appropriate scope before
	 * reaching Actions execute method.Old Struts code is written based on this
	 * contract.Springframework also does the same but after Actions or before
	 * reaching JSP,this is not adequate for old Struts applications.
	 * 
	 * @param ctx
	 */
	protected void processActionForm(final ActionContext ctx) {
		debug("processActionForm::Entry");
		final ActionForm form = ctx.getForm();
		if (Objects.isNull(form)) {
			return;
		}
		final HttpServletRequest httpRequest = ctx.getHttpServletRequest();
		final ActionMapping mapping = ctx.getActionMapping();
		if (StringUtils.hasText(ctx.getFormName())) {
			if (WebApplicationContext.SCOPE_REQUEST.equals(mapping.getScope())) {
				httpRequest.setAttribute(ctx.getFormName(), form);
			} else {
				// Struts 1.x default scope is Session
				final HttpSession session = httpRequest.getSession(true);
				session.setAttribute(ctx.getFormName(), form);
			}
		}
		debug("processActionForm::Exit");
	}

	protected void populateHttpServletRequestAttributes(final ActionContext ctx) {
		final HttpServletRequest httpRequest = ctx.getHttpServletRequest();
		httpRequest.setAttribute(Globals.ACTION_CONTEXT_KEY, ctx);
	}

	/**
	 * Set no-caching headers
	 * 
	 * @param ctx
	 */
	protected void processNoCache(final ActionContext ctx) {
		if (this.isNoCache()) {
			final HttpServletResponse httpResponse = ctx.getHttpServletResponse();
			httpResponse.setHeader(PRAGMA, "No-cache");
			httpResponse.setHeader(CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate"); // HTTP 1.1
			httpResponse.setDateHeader(EXPIRES, 1); // 0 means now
		}
	}

	/**
	 * Sets the content type of the response
	 * 
	 * @param ctx
	 */
	protected void processContent(final ActionContext ctx) {
		if (Objects.nonNull(contentType)) {
			final HttpServletResponse httpResponse = ctx.getHttpServletResponse();
			httpResponse.setContentType(contentType);
		}
	}

	protected void processEncoding(final ActionContext ctx) {
		if (this.isCharacterEncoding()) {
			final HttpServletResponse httpResponse = ctx.getHttpServletResponse();
			if (Objects.nonNull(charset)) {
				httpResponse.setCharacterEncoding(charset);
			} else {
				final HttpServletRequest httpRequest = ctx.getHttpServletRequest();
				httpResponse.setCharacterEncoding(httpRequest.getCharacterEncoding());
			}
		}
	}

	/**
	 * Set the Global ResponseHeaders
	 * 
	 * @param ctx
	 * @return
	 */
	protected boolean populateGlobalResponseHeaders(final ActionContext ctx) {
		final HttpServletRequest httpRequest = ctx.getHttpServletRequest();
		// Set Global headers once during server side Execution
		if (ControllerUtils.isExternalRequest(httpRequest) && Objects.nonNull(globalResponseHeaderSuppliers)) {
			debug("populateGlobalResponseHeaders::Entry");
			final HttpServletResponse httpResponse = ctx.getHttpServletResponse();
			globalResponseHeaderSuppliers.forEach((key, valueSuppliers) -> {
				if (Objects.nonNull(valueSuppliers)) {
					for (Supplier<String> valueSupplier : valueSuppliers) {
						final String headerValue = valueSupplier.get();
						if (Objects.nonNull(headerValue)) {
							httpResponse.addHeader(key, headerValue);
						}
					}
				}

			});
			debug("populateGlobalResponseHeaders::Exit");
		}
		return true;
	}

	/**
	 * Resolve the exception that occurred during Action execution, Return
	 * {@link ModelAndView} to application error page.
	 * 
	 * @param httpRequest  Current HTTP request
	 * @param httpResponse Current HTTP response
	 * @param handler      Current Action class reference
	 * @param exc          The exception from handleActionExecution method
	 */
	@Override
	public final ModelAndView resolveException(final HttpServletRequest httpRequest,
			final HttpServletResponse httpResponse, @Nullable final Object handler, final Exception exc) {
		final String guid = ControllerUtils.getOrComputeExceptionGUIDIfAbsent(httpRequest, exc);
		logger.error("['" + guid + "'] resolveException() ['" + exc.getMessage() + "']");
		final ActionContext ctx = ControllerUtils.getActionContext(httpRequest);
		// Exception during Action Execution.
		if (Objects.nonNull(ctx)) {
			final ActionForward forward = handleActionExecutionException(ctx, exc);
			debug("ActionForward after handleActionExecutionException::resolveException::['" + forward + "']");
			if (Objects.nonNull(forward)) {
				final Model inModel = ctx.getInputModel();
				return generateModelAndViewFromActionForward(forward, httpRequest, httpResponse, inModel);
			}
		} else {
			populateErrorRequestAttributes(httpRequest, exc, null);
		}
		return this.resolveExecutionException(httpRequest, httpResponse, handler, exc);
	}

	/**
	 * Set data for error pages that are rendered directly rather than error-page
	 * mechanism in web.xml element.This will provide us exception implicit Object
	 * in error page
	 * 
	 * @param httpRequest
	 * @param exc
	 * @param servletName
	 */
	protected void populateErrorRequestAttributes(final HttpServletRequest httpRequest, final Exception exc,
			@Nullable final String servletName) {
		// keep current Exception as a request attribute for easy access in Views.
		httpRequest.setAttribute(Globals.EXCEPTION_KEY, exc);
		if (isExposeErrorRequestAttributes()) {
			WebUtils.exposeErrorRequestAttributes(httpRequest, exc, servletName);
		}
	}

	/**
	 * Method for gracefully handle Action execution exceptions and continue
	 * processing.This method log exception along with a UUID/GUID and set the same
	 * GUID as a request attribute with name
	 * {@code Globals.SPRINGBRIDGE_EXCEPTION_GUID}
	 * 
	 * @param ctx
	 * @return ActionForward corresponding to some application specific error page.
	 */
	protected ActionForward handleActionExecutionException(final ActionContext ctx, final Exception exc) {
		Objects.requireNonNull(ctx, Globals.ACTION_CONTEXT_CANNOT_BE_NULL);
		final HttpServletRequest httpRequest = ctx.getHttpServletRequest();
		final String guid = ControllerUtils.getOrComputeExceptionGUIDIfAbsent(httpRequest, exc);
		logger.error("['" + guid + "'] handleActionExecutionException()", exc);
		populateErrorRequestAttributes(httpRequest, exc, null);
		ctx.setExecutionException(exc);
		return getExceptionHandler().handleActionExecutionException(ctx);
	}

	/**
	 * Publish the ActionHandlerEvents to Spring context so any other component can
	 * subscribe to events using
	 * ApplicationListener&lt;ServletRequestHandledEvent&gt; to get the exact
	 * execution time in Handler.Event Publishing is Turned off by default
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param startTime
	 * @param exc
	 * 
	 * @see #publishEvents attribute
	 */
	protected void publishActionExecutedEvent(final HttpServletRequest httpRequest,
			final HttpServletResponse httpResponse, final long startTime, @Nullable final Throwable exc) {
		// Event Publishing is Turned off by default
		if (this.isPublishEvents() && Objects.nonNull(this.applicationEventPublisher)) {
			final long processingTime = System.currentTimeMillis() - startTime;
			/*
			 * ServletRequestHandledEvent Contains most of the Required attributes,So we are
			 * not going to define any new RequestHandledEvent Type in Spring Bridge.
			 */
			this.applicationEventPublisher.publishEvent(
					new ServletRequestHandledEvent(this, httpRequest.getRequestURI(), httpRequest.getRemoteAddr(),
							httpRequest.getMethod(), handlerName, WebUtils.getSessionId(httpRequest),
							getUserSpecificDetails(httpRequest), processingTime, exc, httpResponse.getStatus()));
		}
	}

	/**
	 * Determine the User Specific Details for the given request.
	 * <p>
	 * The default implementation is taken from Spring Dispatcher servlet code
	 * (getUsernameForRequest). But applications can supply
	 * {@code userDetailsFunction} to extract User Specific Details details based on
	 * current application setup. Like SSO headers set by CA SiteMinder SSO or
	 * Application Domain Specific attributes like Plan/Participant details(Non PII
	 * data like PIN) for Insurance Domain or User/Product specific attributes for
	 * Retail Domain etc
	 * </p>
	 */
	public final String getUserSpecificDetails(final HttpServletRequest httpRequest) {
		if (Objects.nonNull(userDetailsFunction)) {
			return userDetailsFunction.apply(httpRequest);
		}
		final Principal userPrincipal = httpRequest.getUserPrincipal();
		return (Objects.nonNull(userPrincipal) ? userPrincipal.getName() : null);
	}

	/**
	 * Hook to handle some un-handled Exception based on Application usage Pattern
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param handler
	 * @param ex
	 * @return
	 */
	public abstract ModelAndView resolveExecutionException(final HttpServletRequest httpRequest,
			final HttpServletResponse httpResponse, @Nullable final Object handler, final Exception ex);

	protected boolean processRoles(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
			final ActionMapping mapping) throws IOException {
		// Is this action protected by role requirements?
		final String[] roles = mapping.getRoleNames();
		if (ControllerUtils.isEmpty(roles)) {
			return true;
		}
		// Check the current user against the list of required roles
		for (String role : roles) {
			if (httpRequest.isUserInRole(role)) {
				if (logger.isDebugEnabled()) {
					debug("User '" + httpRequest.getRemoteUser() + "' has role '" + role + "', granting access");
				}
				return true;
			}
		}
		// The current user is not authorized for this action
		debug("User '" + httpRequest.getRemoteUser() + "' does not have any required role, denying access");
		httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
				String.format(Globals.NOT_AUTHORIZED_MESSAGE, mapping.getPath()));
		return false;
	}

	/**
	 * Perform Input Form Validation
	 * 
	 * @param form
	 * @param mapping
	 * @param httpRequest
	 * @param bindingResult
	 * @return
	 */
	protected ActionForward processFormValidation(final ActionForm form, final ActionMapping mapping,
			final HttpServletRequest httpRequest, final Errors bindingResult) {
		debug("processFormValidation()::Entry");
		ActionForward forward = null;
		compositeValidator.validate(mapping, form, httpRequest, bindingResult);
		if (ControllerUtils.hasErrors(httpRequest)) {
			// If errors move back to input page
			forward = mapping.getInputForward();
			debug("Form Validation Failed Forwarding to Input Page.['" + forward + "'].ActionMapping configuration ['"
					+ mapping + "']");
			if (Objects.isNull(forward)) {
				final String noInputPageToForward = String.format(Globals.NO_INPUT_PAGE_TO_FORWARD,
						mapping.getBeanName());
				logger.error(noInputPageToForward);
				// Stop Action Execution
				throw new ConfigurationException(noInputPageToForward);
			}
		}
		debug("processFormValidation()::Exit");
		return forward;
	}

	/**
	 * 
	 * @param inForward    - ActionForward null means already completed Request
	 *                     processing
	 * @param httpRequest
	 * @param httpResponse
	 * @param inModel
	 * @return
	 */
	protected ModelAndView generateModelAndViewFromActionForward(final ActionForward inForward,
			final HttpServletRequest httpRequest, final HttpServletResponse httpResponse, final Model inModel) {
		debug("generateModelAndViewFromActionForward()::Entry");
		ModelAndView mav = null;
		if (Objects.nonNull(inForward)) {
			mav = mavGenerator.generateModelAndViewFromActionForward(inForward, httpRequest, httpResponse, inModel);
		}
		debug("generateModelAndViewFromActionForward()::Exit");
		return mav;
	}

	/**
	 * @return error page based on configured PropertySource The value of property
	 *         springbridge.webapp.default_error_page contains Error page JSP/tiles
	 *         path including prefix and suffix.Note tiles3 is deprecated now.
	 */
	protected String getApplicationErrorPage() {
		return environment.getProperty(Globals.SPRINGBRIDGE_ERROR_PAGE_KEY);
	}

	@Override
	public final void afterPropertiesSet() {
		debug("afterPropertiesSet()::Entry");
		// Environment is required for lookup error-page
		Objects.requireNonNull(environment, "Environment property is Null.Unable to proceed.");
		if (Objects.isNull(compositeValidator)) {
			// Create Default Validator
			compositeValidator = new CompositeValidator();
			compositeValidator.afterPropertiesSet();
		}
		if (Objects.isNull(mavGenerator)) {
			mavGenerator = new DefaultModelAndViewGenerator();
		}
		debug("afterPropertiesSet()::Exit");
		initActionHandler();
	}

	public final void debug(final String message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
	}

	/**
	 * Life-cycle Hook for Internal initialization by child class
	 */
	protected void initActionHandler() {
		// NOOP
	}
}