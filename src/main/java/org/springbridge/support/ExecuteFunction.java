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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionForward;
import org.springbridge.action.ActionMapping;

/**
 * One of the good thing about java 8 or higher is method referencess.All Struts
 * 1.x actions request handling logic can be represented using spring-bridge
 * ExecuteFunction interface, which exposes the
 * {@link #execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)}
 * method or methods having same signature.Controller logic resides either in
 * execute method or method having same signature.ExecuteFunction Functional
 * interface help us take the required Controller Logic and pass it as a method
 * reference to ActlonHandler .ActionHandler take care of specific pre or post
 * processing logic which needs to be executed before or after Controller Logic.
 * 
 * @see org.springbridge.support.handler.ActionHandler
 * @author Anoop V S
 *
 */
@FunctionalInterface
public interface ExecuteFunction {

	/**
	 * This has same method Signature as that of Struts 1.x execute method.
	 *
	 * @param mapping      The ActionMapping based on incoming request URI
	 * @param form         The optional ActionForm(or ModelAttribute) bean for this
	 *                     request
	 * @param httpRequest  The current HttpServletRequest
	 * @param httpResponse The current HttpServletResponse
	 * @return The forward to which control need to be passed.Default Struts
	 *         behavior is passing {@code null},{@code null} means processing
	 *         complete and Response is committed.
	 * 
	 * @throws Exception or Its subclass in case of some un-handled scenario
	 */
	public ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) throws Exception;
}
