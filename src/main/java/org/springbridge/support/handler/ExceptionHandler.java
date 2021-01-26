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
 */package org.springbridge.support.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionForward;
import org.springbridge.action.ActionMapping;

/**
 * Strategy interface for handling action execution Exceptions
 * 
 * @author Anoop V S
 *
 */
public interface ExceptionHandler {
	/**
	 * Checks the current exception can be handled by this Handler
	 * 
	 * @param clazz
	 * @return
	 */
	boolean supports(final Class<?> clazz);

	/**
	 * Generate a valid ActionForward based on exception.
	 * 
	 * @param exc          Exception generated
	 * @param mapping      The ActionMapping based on incoming request URI
	 * @param form         The optional ActionForm(or ModelAttribute) bean for this
	 *                     request
	 * @param httpRequest  The current HttpServletRequest
	 * @param httpResponse The current HttpServletResponse
	 * @return The forward to which control need to be passed.Default Struts
	 *         behavior is passing {@code null},{@code null} means processing
	 *         complete and Response is committed.
	 */
	public ActionForward handleActionExecutionException(final Exception exc, final ActionMapping mapping,
			final ActionForm actionForm, final HttpServletRequest httpRequest, final HttpServletResponse httpResponse);
}
