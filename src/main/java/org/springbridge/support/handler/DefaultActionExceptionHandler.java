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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionForward;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.Globals;
import org.springframework.context.ApplicationContext;

/**
 * Internal framework class for handling Exception during Action Execution This
 * handler delegates exception handling to application specific ExceptionHandler
 * 
 * @author Anoop V S
 *
 */
class DefaultActionExceptionHandler implements ActionExceptionHandler {
	/** Commons logger */
	private final Log logger = LogFactory.getLog(getClass());

	private final ApplicationContext applicationContext;

	private Collection<ExceptionHandler> exceptionHandlers;

	DefaultActionExceptionHandler(final ApplicationContext applicationContext) {
		this.applicationContext = Objects.requireNonNull(applicationContext,
				Globals.APPLICATION_CONTEXT_CANNOT_BE_NULL);
	}

	@Override
	public final ActionForward handleActionExecutionException(final ActionContext ctx) {
		if (logger.isDebugEnabled()) {
			logger.debug("DefaultActionExceptionHandler::handleActionExecutionException::Entry");
		}
		final HttpServletRequest httpRequest = ctx.getHttpServletRequest();
		final HttpServletResponse httpResponse = ctx.getHttpServletResponse();
		final ActionForm form = ctx.getForm();
		final ActionMapping mapping = ctx.getActionMapping();
		final Exception executionException = ctx.getExecutionException();
		Objects.requireNonNull(executionException, Globals.EXCEPTION_CANNOT_BE_NULL);
		final Class<?> excClass = executionException.getClass();
		ActionForward forward = null;
		for (ExceptionHandler exceptionHandler : this.getExceptionHandlers()) {
			if (exceptionHandler.supports(excClass)) {
				forward = exceptionHandler.handleActionExecutionException(executionException, mapping, form,
						httpRequest, httpResponse);
				if (Objects.nonNull(forward)) {
					break;
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("DefaultActionExceptionHandler::handleActionExecutionException::Exit");
		}
		return forward;
	}

	public final Collection<ExceptionHandler> getExceptionHandlers() {
		if (Objects.isNull(exceptionHandlers)) {
			exceptionHandlers = findExceptionHandlers();
		}
		return exceptionHandlers;
	}

	/**
	 * Find the application specific ExceptionHandler from Spring Context.Empty
	 * Collection if no matching ExceptionHandler found in Spring Context
	 * 
	 * @return
	 */
	protected final Collection<ExceptionHandler> findExceptionHandlers() {
		final Map<String, ExceptionHandler> exceptionHandlerBeans = applicationContext
				.getBeansOfType(ExceptionHandler.class);
		if (!exceptionHandlerBeans.isEmpty()) {
			return exceptionHandlerBeans.values();
		}
		return Collections.emptyList();
	}

}
