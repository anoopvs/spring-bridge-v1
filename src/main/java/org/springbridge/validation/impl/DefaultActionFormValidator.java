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
package org.springbridge.validation.impl;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springbridge.action.ActionErrors;
import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionMapping;
import org.springbridge.validation.AbstractActionFormValidator;
import org.springframework.validation.Errors;

/**
 * 
 * @author Anoop V S
 * 
 *         DefaultActionFormValidator make use of ActionForm validate method (
 *         {@link ActionForm#validate(ActionMapping, HttpServletRequest)} )
 *         method to perform validations on user input.
 *
 */
public class DefaultActionFormValidator extends AbstractActionFormValidator {

	public DefaultActionFormValidator() {
		super();
	}

	@Override
	public void validate(final ActionMapping mapping, final ActionForm inForm, final HttpServletRequest httpRequest,
			final Errors errors) {
		if (logger.isDebugEnabled()) {
			logger.debug("DefaultActionFormValidator::validate()::Entry");
		}
		final ActionErrors actionErrors = inForm.validate(mapping, httpRequest);
		if (Objects.nonNull(actionErrors) && !actionErrors.isEmpty()) {
			saveErrors(httpRequest, actionErrors);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("DefaultActionFormValidator::validate()::Exit");
		}
	}

}
