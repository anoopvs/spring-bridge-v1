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
package org.springbridge.validation;

import javax.servlet.http.HttpServletRequest;

import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionMapping;
import org.springframework.validation.Errors;

/**
 * A strategy interface defines the contract between the handler and validator.
 * 
 * @see org.springbridge.support.handler.AbstractActionHandler#processFormValidation
 * 
 * @author Anoop V S
 *
 */
@FunctionalInterface
public interface ActionFormValidator {
	/**
	 * Validate the supplied ActionForm,Using either default or any of the pre
	 * configured validators in Spring application context.
	 * 
	 * @param mapping     The ActionMapping based on incoming request URI
	 * @param form        The optional ActionForm(or ModelAttribute) bean for this
	 *                    request
	 * @param httpRequest The current HttpServletRequest
	 * @param errors Errors or BindingResult from Spring MVC Framework
	 */
	void validate(ActionMapping mapping, ActionForm form, HttpServletRequest httpRequest, Errors errors);
}
