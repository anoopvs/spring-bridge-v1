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

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 * You can customize the Action execution behavior by sub-classing and
 * overriding the method(s)
 * 
 * @author Anoop V S
 *
 */
public class DefaultActionHandler extends AbstractActionHandler {
	
	private ModelAndView errorPageModelAndView;
	
	@Override
	public ModelAndView resolveExecutionException(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
			final Object handler, final Exception exc) {
		if (logger.isDebugEnabled()) {
			logger.debug("resolveExecutionException::ErrorPage ModelAndView ['"+errorPageModelAndView+"']");
		}
		return errorPageModelAndView;
	}
	/**
	 * initActionHandler is invoked from afterPropertiesSet()
	 * Current Exception object is available in requestScope with key
	 * org.springbridge.action.Globals.EXCEPTION_KEY.
	 */
	@Override
	public void initActionHandler() {
		if(Objects.isNull(errorPageModelAndView)) {
			errorPageModelAndView = new ModelAndView(getApplicationErrorPage());
		}
	}
	
	public ModelAndView getErrorPageModelAndView() {
		return errorPageModelAndView;
	}
	
	public void setErrorPageModelAndView(final ModelAndView errorPageModelAndView) {
		this.errorPageModelAndView = errorPageModelAndView;
	}
	

}
