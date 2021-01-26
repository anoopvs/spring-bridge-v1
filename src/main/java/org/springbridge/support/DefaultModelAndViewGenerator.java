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

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springbridge.action.ActionForward;
import org.springbridge.utils.ControllerUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Default ModelAndViewGenerator which supports most of the normal use cases.
 * 
 * @author Anoop V S
 */
public class DefaultModelAndViewGenerator implements ModelAndViewGenerator {

	@Override
	public final ModelAndView generateModelAndViewFromActionForward(final ActionForward inForward,
			final HttpServletRequest httpRequest, final HttpServletResponse httpResponse, final Model inModel) {
		String path = getRealPath(httpRequest, inForward.getPath());
		boolean isRedirect = false;
		if (inForward.getRedirect() && !path.startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX)) {
			path = new StringBuilder(UrlBasedViewResolver.REDIRECT_URL_PREFIX).append(path).toString();
			isRedirect = true;
		}
		final String viewName = processPath(path, isRedirect, httpRequest, httpResponse);
		final ModelAndView mav = new ModelAndView(viewName);
		if (Objects.nonNull(inModel)) {
			mav.addAllObjects(inModel.asMap());
		}
		return mav;
	}

	/**
	 * Check the given path is real or not and convert to real path based on current
	 * context.
	 * 
	 * @param httpRequest
	 * @param path
	 * @return realPath based on incoming request URI.
	 */
	protected String getRealPath(final HttpServletRequest httpRequest, final String path) {
		return ControllerUtils.formatValue(httpRequest, path);
	}

	/**
	 * Encodes the specified URL by including the session ID to it based on the
	 * configured Session tracking mode,Unchanged URL in case of Cookie based Http
	 * Session Tracking
	 * 
	 * @param path
	 * @param isRedirect
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 */
	protected String processPath(final String path, final boolean isRedirect, final HttpServletRequest httpRequest,
			final HttpServletResponse httpResponse) {
		return ControllerUtils.encodeURL(path, isRedirect, httpRequest, httpResponse);
	}
}
