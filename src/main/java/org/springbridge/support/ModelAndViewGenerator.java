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

import org.springbridge.action.ActionForward;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

/**
 * Strategy interface for generating ModelAndView from ActionForward
 * 
 * @author Anoop V S
 *
 */
@FunctionalInterface
public interface ModelAndViewGenerator {

	ModelAndView generateModelAndViewFromActionForward(ActionForward inForward, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, Model inModel);

}