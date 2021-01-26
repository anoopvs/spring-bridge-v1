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
package org.springbridge.action;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springbridge.support.ExecuteFunction;
import org.springbridge.support.utils.MessageResources;
import org.springbridge.utils.ApplicationContextUtils;
import org.springbridge.utils.ControllerUtils;

/**
 * Pulling Struts Action Specific methods from Action to Spring Bridge Action.
 * This will help us to retain the existing struts methods calls or references
 * in action class and reuse the code as such by simply changing the import
 * statement.Most of the delegate method internally make use of Spring
 * framework.
 *
 * @author Anoop V S
 *
 */
public class Action implements ExecuteFunction {

	/*
	 * Pulling Struts Action base class methods from Apache struts Action to
	 * springbridge Action. This will help us to retain the existing struts method
	 * reference in action class and reuse the code as such. Delegate method to
	 * Simulate struts 1.x behavior
	 */
	/**
	 * Generate CSRF tokens and save it in server for validating it at in later
	 * phase.
	 * 
	 * @param httpRequest current HttpServletRequest
	 * 
	 */
	protected void saveToken(final HttpServletRequest httpRequest) {
		ControllerUtils.saveToken(httpRequest);
	}

	/**
	 * Validating the incoming CSRF token against the one present in server.
	 * 
	 * @param httpRequest current HttpServletRequest
	 * @return {@code true} if HttpServletRequest token is valid.
	 */
	protected boolean isTokenValid(final HttpServletRequest httpRequest) {
		return ControllerUtils.isTokenValid(httpRequest, false);
	}

	/**
	 * Validating the incoming CSRF token against the one present in server. This
	 * method also clears CSRF token if rest attribute is {@code true}
	 * 
	 * @param httpRequest current HttpServletRequest
	 * @param reset
	 * @return
	 */
	protected boolean isTokenValid(final HttpServletRequest httpRequest, final boolean reset) {
		return ControllerUtils.isTokenValid(httpRequest, reset);
	}

	/**
	 * Reset the server-side Transaction token
	 * 
	 * @param httpRequest current HttpServletRequest
	 */
	protected void resetToken(final HttpServletRequest httpRequest) {
		ControllerUtils.resetToken(httpRequest);
	}

	/**
	 * Check current request is generated by hitting cancel button.
	 * 
	 * @param httpRequest current HttpServletRequest
	 * @return
	 */
	protected boolean isCancelled(final HttpServletRequest httpRequest) {
		return ControllerUtils.isCancelled(httpRequest);
	}

	/**
	 * Adds the {@code ActionErrors} as the request attribute under key
	 * {@link Globals#ERROR_KEY}
	 * 
	 * @param httpRequest current HttpServletRequest
	 * @param errors      ActionErrors object to save ,may be null
	 */
	protected void saveErrors(final HttpServletRequest httpRequest, final ActionErrors errors) {
		ControllerUtils.saveErrors(httpRequest, errors);
	}

	/**
	 * Adds the {@code ActionErrors} as the request attribute under key
	 * {@link Globals#ERROR_KEY}
	 * 
	 * @param session current HttpSession
	 * @param errors  ActionErrors object to save ,may be null
	 */
	protected void saveErrors(final HttpSession session, final ActionErrors errors) {
		ControllerUtils.saveErrors(session, errors);
	}

	/**
	 * Adds the {@code ActionMessages} as the request attribute under key
	 * {@link Globals#MESSAGE_KEY}
	 * 
	 * @param httpRequest current HttpServletRequest
	 * @param messages
	 */
	protected void saveMessages(final HttpServletRequest httpRequest, final ActionMessages messages) {
		ControllerUtils.saveMessages(httpRequest, messages);
	}

	/**
	 * Adds the {@code ActionMessages} as the session attribute under key
	 * {@link Globals#MESSAGE_KEY}
	 * 
	 * @param session  current HttpSession
	 * @param messages
	 */
	protected void saveMessages(final HttpSession session, final ActionMessages messages) {
		ControllerUtils.saveMessages(session, messages);
	}

	/**
	 * Adds the {@code ActionMessages} as the request attribute under key
	 * {@link Globals#MESSAGE_KEY}
	 * 
	 * @param httpRequest current HttpServletRequest
	 * @param messages    ActionMessages object to save ,may be null
	 */
	protected void addMessages(final HttpServletRequest httpRequest, final ActionMessages messages) {
		ControllerUtils.addMessages(httpRequest, messages);
	}

	/**
	 * Adds the {@code ActionErrors} as the request attribute under key
	 * {@link Globals#ERROR_KEY}
	 * 
	 * @param httpRequest current HttpServletRequest
	 * @param messages    ActionErrors object to save ,may be null
	 */
	protected void addErrors(final HttpServletRequest httpRequest, final ActionErrors errors) {
		ControllerUtils.addErrors(httpRequest, errors);
	}

	/**
	 * Retrieves any existing errors placed in the request by previous actions. This
	 * method could be called instead of creating a new
	 */
	protected ActionMessages getErrors(final HttpServletRequest httpRequest) {
		return ControllerUtils.getErrors(httpRequest);
	}

	/**
	 * Return the user's currently selected Locale
	 * 
	 * @param httpRequest The current HttpServletRequest request We are
	 *                    processing,Unused
	 * @return The user's currently Selected Locale.
	 */
	protected Locale getLocale(final HttpServletRequest httpRequest) {
		return ControllerUtils.getLocale();
	}

	/**
	 * Retrieves any existing messages placed in the request by previous actions
	 * This method could be called instead of creating new
	 * 
	 * @param httpRequest The current HttpServletRequest request we are processing
	 * @return the Messages that already exist in the request, or a new
	 *         ActionMessages object if empty.
	 * 
	 */
	protected ActionMessages getMessages(final HttpServletRequest httpRequest) {
		return ControllerUtils.getMessages(httpRequest);
	}

	/**
	 * Give the current MessageResources from Spring Context
	 * 
	 * @param httpRequest Unused parameter in current implementation
	 * @param key         Unused parameter in current implementation
	 * @return MessageResources
	 */
	protected MessageResources getResources(final HttpServletRequest httpRequest, final String key) {
		return ApplicationContextUtils.getMessageResources();
	}

	/**
	 * Change the user Locale to new Value
	 * 
	 * @param httpRequest The current HttpServletRequest request we are processing
	 * @param locale      new Locale
	 */
	protected void setLocale(final HttpServletRequest httpRequest, final Locale locale) {
		ControllerUtils.setLocale(httpRequest, locale);
	}

	/** Default Actions execute method as in struts base action */
	@Override
	public ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) throws Exception {
		// Default Struts 1.x behavior
		return null;
	}
}
