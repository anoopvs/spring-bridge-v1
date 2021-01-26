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

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.Globals;
import org.springframework.validation.Errors;
import org.springmodules.validation.commons.DefaultBeanValidator;
import org.springmodules.validation.commons.ValidatorFactory;

/**
 * A custom BeanValidator which keep the backwards compatibility with existing
 * Struts 1.x XML based form Validation.
 * 
 * @author Anoop V S
 *
 */
public class CustomBeanValidator extends DefaultBeanValidator {

	/** Commons logger */
	private static final Log logger = LogFactory.getLog(CustomBeanValidator.class);

	protected ValidatorFactory validatorFactory;

	public void validate(final Object obj, final Errors errors, final ActionMapping actionMapping) {
		logger.debug("Validator XML rules based validate()::Entry");
		Objects.requireNonNull(actionMapping, Globals.ACTION_MAPPING_CANNOT_BE_NULL);
		/*
		 * Take the validationRule Based on the Form Bean Name.No need to infer bean
		 * name from Objects class name as in the case of DefaultBeanValidator's
		 * validate method. springbridge supports Composite Validators,So inform
		 * developers about configuration during sandbox testing.So they can correct the
		 * ActionMapping configuration
		 */
		if (Objects.nonNull(actionMapping.getAttribute())) {
			final Validator commonsValidator = this.getValidator(obj, errors, actionMapping.getAttribute());
			if (Objects.nonNull(commonsValidator)) {
				initValidator(commonsValidator);
				try {
					commonsValidator.validate();
				} catch (final ValidatorException validatorExc) {
					logger.error("CustomBeanValidator::Exception while validating object", validatorExc);
				} finally {
					cleanupValidator(commonsValidator);
				}
			} else {
				// Do we really need warn/error here?
				logger.debug(String.format("Unable to locate matching Validator for Form '%s'",
						actionMapping.getAttribute()));
			}
		} else {
			logger.debug("formName is null,Skipping validation.");
		}
		logger.debug("Validator XML rules based validate()::Exit");

	}

	@Override
	public final void validate(final Object obj, final Errors errors) {
		throw new UnsupportedOperationException(
				"validate(Object,Errors) method is not Supported in Spring-Bridge CustomBeanValidator.");
	}

	protected Validator getValidator(final Object obj, final Errors errors, final String formName) {
		return this.validatorFactory.getValidator(formName, obj, errors);
	}

	@Override
	public void setValidatorFactory(final ValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
		super.setValidatorFactory(validatorFactory);
	}

	@Override
	public void setUseFullyQualifiedClassName(boolean useFullyQualifiedClassName) {
		throw new UnsupportedOperationException(
				"'useFullyQualifiedClassName' attribute is not Supported in Spring-Bridge CustomBeanValidator.");
	}
}
