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

import org.springframework.web.servlet.ModelAndView;

/**
 * ActionHandler is spring-bridge framework component which provides the
 * RequestProcessor capabilities in the struts world.Actual implementation will
 * give you subset of functionality as in Apache Struts,The functionalities
 * which help us to reuse the existing Struts 1.x code with little/No code
 * change in Spring MVC.
 * 
 * @author Anoop V S
 *
 */
@FunctionalInterface
public interface ActionHandler {

	/**
	 * Invoke execute method and generate ModelAndView
	 * 
	 * @param ctx
	 * @return
	 */
	ModelAndView handleActionExecution(final ActionContext ctx);

}