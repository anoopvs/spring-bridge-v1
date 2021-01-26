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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springbridge.action.Globals;
import org.springbridge.utils.ControllerUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * TokenRequestDataValueProcessor helps to transparently add transaction token
 * as hidden field to all forms rendered using Spring Form Tags.Bean name must
 * be 'requestDataValueProcessor'. TokenRequestDataValueProcessor provides
 * protection against cross-site request forgery (CSRF) by automatically adding
 * transaction token as hidden field in all forms generated using Spring MVC
 * Form Tag library.In order to generate transaction token in UI you must call
 * saveToken() method on Action Class(Before rendering UI).
 *
 * This also decides session ID needs to be encoded in the action URL or not in
 * form tags generated using Spring MVC tag-lib.This is to support URL based
 * session tacking which is used by some of the legacy applications and they
 * want to stick with that.
 *
 * @author Anoop V S
 *
 **/
@Component(TokenRequestDataValueProcessor.BEAN_NAME)
public class TokenRequestDataValueProcessor implements RequestDataValueProcessor {
	public static final String BEAN_NAME = "requestDataValueProcessor";

	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * This method is invoked when HTML form is rendered using Spring MVC form-tag.
	 * This method will append Session-ID with Action URLs if application is using
	 * URL based session tracking.Some legacy applications still use URL based
	 * session tracking.
	 * 
	 * @param httpRequest the current request
	 * @param action      the form action
	 * @param httpMethod  the form HTTP method
	 * @return the action to use, possibly modified
	 */
	@Override
	public String processAction(final HttpServletRequest httpRequest, final String action, final String httpMethod) {
		logger.debug("processAction()");
		return processPath(action, httpRequest);
	}

	@Override
	public String processFormFieldValue(final HttpServletRequest httpRequest, final String name, final String value,
			final String type) {
		return value;
	}

	@Override
	public Map<String, String> getExtraHiddenFields(final HttpServletRequest httpRequest) {
		logger.debug("getExtraHiddenFields::Entry");
		final HttpSession session = httpRequest.getSession(false);
		if (Objects.nonNull(session)) {
			// check for TRANSACTION_TOKEN_KEY in session
			final String token = (String) session.getAttribute(Globals.TRANSACTION_TOKEN_KEY);
			if (Objects.nonNull(token)) {
				logger.debug("TokenRequestDataValueProcessor::getExtraHiddenFields::token['" + token + "'] ");
				return Map.of(Globals.TRANSACTION_TOKEN_KEY, token);
			}
		}
		logger.debug("getExtraHiddenFields::Exit");
		return Collections.emptyMap();
	}

	@Override
	public String processUrl(final HttpServletRequest httpRequest, final String url) {
		logger.debug("processUrl()");
		// Spring framework classes such as UrlTag,RedirectView uses processUrl method.
		return processPath(url, httpRequest);
	}

	/**
	 * Encodes the specified URL by including the session ID to it based on the
	 * configured Session tracking mode,Unchanged URL in case of Cookie based Http
	 * Session Tracking
	 * 
	 * @param path
	 * @param httpRequest
	 * @return
	 */
	protected String processPath(final String path, final HttpServletRequest httpRequest) {
		final HttpServletResponse httpResponse = ControllerUtils.getResponse();
		return ControllerUtils.encodeURL(path, false, httpRequest, httpResponse);
	}
}