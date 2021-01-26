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

import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.ActionMessage;
import org.springbridge.support.utils.MessageResources;
import org.springbridge.validation.AbstractResourceBasedActionFormValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/**
 * This class make use of Springs JSR-303 Support. So this also supports Bean
 * Validation Bean Validation 1.1 as well as 2.0.
 * 
 * @author Anoop V S
 *
 */
public class JSR303BasedActionFormValidator extends AbstractResourceBasedActionFormValidator {
	
	protected final SpringValidatorAdapter jsr303Validator;

	public JSR303BasedActionFormValidator(final SpringValidatorAdapter validator,
			final MessageResources messageResources) {
		super(messageResources);
		this.jsr303Validator = Objects.requireNonNull(validator,
				"JSR 303 Validator cannot be null.Please check the Spring Configuration File.");
	}

	@Override
	public void validate(final ActionMapping mapping, final ActionForm form, final HttpServletRequest httpRequest,
			final Errors errors) {
		if (logger.isDebugEnabled()) {
			logger.debug("JSR303BasedActionFormValidator::validate()::Entry");
		}
		this.doJSR303Validation(form, httpRequest, errors);
		if (logger.isDebugEnabled()) {
			logger.debug("JSR303BasedActionFormValidator::validate()::Exit");
		}
	}

	protected <T> void doJSR303Validation(final T target, final HttpServletRequest httpRequest, final Errors errors) {
		if (logger.isDebugEnabled()) {
			logger.debug("JSR303BasedActionFormValidator::doJSR303Validation()::Entry");
		}
		jsr303Validator.validate(target, errors);
		saveValidationErrorsIfPresent(httpRequest, errors);
		if (logger.isDebugEnabled()) {
			logger.debug("JSR303BasedActionFormValidator::doJSR303Validation()::Exit");
		}
	}

	@Override
	protected void populateActionMessageFromFieldError(final ActionMessage actionMessage, final FieldError fieldError) {
		if (Objects.nonNull(fieldError)) {
			actionMessage.setDefaultMessage(fieldError.getDefaultMessage());
		}
	}

	public SpringValidatorAdapter getJsr303Validator() {
		return this.jsr303Validator;
	}

}
