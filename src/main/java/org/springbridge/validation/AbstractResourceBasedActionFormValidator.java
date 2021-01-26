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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springbridge.action.ActionErrors;
import org.springbridge.action.ActionMessage;
import org.springbridge.support.utils.MessageResources;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * Abstract implementation of
 * {@link ActionFormValidator}.AbstractResourceBasedActionFormValidator <b>(Experimental)</b> is
 * capable of resolving validation error message from resource bundles based on
 * current locale
 * 
 * @author Anoop V S
 * 
 * @see org.springbridge.validation.impl.JSR303BasedActionFormValidator
 * @see org.springbridge.validation.impl.SpringCommonsActionFormValidator
 *
 */
public abstract class AbstractResourceBasedActionFormValidator extends AbstractActionFormValidator {

	public final MessageResources messageResources;

	public AbstractResourceBasedActionFormValidator(final MessageResources messageResources) {
		super();
		this.messageResources = Objects.requireNonNull(messageResources, "MessageResources cannot be null.");
	}

	protected void convertBeanValidatorErrorsToActionErrorsAndSave(final HttpServletRequest httpRequest,
			final Errors errors) {
		if (logger.isDebugEnabled()) {
			logger.debug("convertBeanValidatorErrorsToActionErrorsAndSave()::Entry");
		}
		if (hasErrors(errors)) {
			/*
			 * We need to sort FieldErrors ,So Create a separate Copy,getFieldErrors
			 * provides unmodifiable List,We can't Sort it. Below sorting will eat
			 * additional CPU Cycles and Memory,This will always preserve the Error message
			 * order in UI.
			 */
			final List<FieldError> fieldErrors = new LinkedList<>(errors.getFieldErrors());
			if (!fieldErrors.isEmpty()) {
				// Sort FieldErrors
				Collections.sort(fieldErrors, Comparator.comparing(FieldError::getField)
						.thenComparing(Comparator.comparing(FieldError::getCode)));
				final ActionErrors validationErrors = getErrors(httpRequest);
				for (FieldError fieldError : fieldErrors) {
					final ActionMessage actionMessage = new ActionMessage(fieldError.getCode());
					populateActionMessageFromFieldError(actionMessage, fieldError);
					compareAndSetResourceFlag(actionMessage);
					validationErrors.add(fieldError.getField(), actionMessage);
				}
				saveErrors(httpRequest, validationErrors);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("convertBeanValidatorErrorsToActionErrorsAndSave()::Exit");
		}
	}

	protected void populateActionMessageFromFieldError(final ActionMessage actionMessage, final FieldError fieldError) {
		if (Objects.nonNull(fieldError) && Objects.nonNull(fieldError.getArguments())) {
			final Object[] arguments = fieldError.getArguments();
			final List<String> argList = new ArrayList<>();
			for (Object object : arguments) {
				if (DefaultMessageSourceResolvable.class.isInstance(object)) {
					final DefaultMessageSourceResolvable defaultMessageSource = DefaultMessageSourceResolvable.class
							.cast(object);
					logger.debug("populateActionMessageFromFieldError()::" + defaultMessageSource);
					final String[] codes = defaultMessageSource.getCodes();
					if (Objects.nonNull(codes) && codes.length > 0) {
						Arrays.stream(codes).map(this::getMessage).forEach(argList::add);
					}
				}else {
					/*
					 * Arguments from XML <var-value> tag ,Not sure we really need to lookup value from MessageSource 
					 */
					final String argValue=String.valueOf(object);
					argList.add(argValue);
				}
			}
			actionMessage.setValues(argList.toArray(new String[0]));
			actionMessage.setDefaultMessage(fieldError.getDefaultMessage());
		}
	}

	public MessageResources getMessageResources() {
		return messageResources;
	}

	/**
	 * Check given code is some message key in ResourceBundle,If message key pass
	 * the corresponding value from ResourceBundle Otherwise pass the code
	 * 
	 * @param code
	 * @return
	 */
	public String getMessage(final String code) {
		if (messageResources.isPresent(code)) {
			return messageResources.getMessage(code);
		}
		return code;
	}

	/**
	 * This method checks any Errors are reported by Validation framework.
	 * 
	 * @param errors
	 * @return
	 */
	public final boolean hasErrors(final Errors errors) {
		return Objects.nonNull(errors) && errors.hasErrors();
	}

	/**
	 * Check is there any validation errors present,If present save it as
	 * ActionErrors,So UI can use it.
	 * 
	 * @param httpRequest
	 * @param errors
	 */
	protected void saveValidationErrorsIfPresent(final HttpServletRequest httpRequest, final Errors errors) {
		if (hasErrors(errors)) {
			if (logger.isDebugEnabled()) {
				logger.debug("validate() Failed.");
			}
			convertBeanValidatorErrorsToActionErrorsAndSave(httpRequest, errors);
		}
	}
}
