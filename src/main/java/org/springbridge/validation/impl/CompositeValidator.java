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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.springbridge.action.ActionErrors;
import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.ActionMessage;
import org.springbridge.action.Globals;
import org.springbridge.action.InvalidCancelException;
import org.springbridge.support.handler.ActionContext;
import org.springbridge.utils.ControllerUtils;
import org.springbridge.validation.AbstractActionFormValidator;
import org.springbridge.validation.ActionFormValidator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.Errors;

/**
 * CompositeValidator for loose coupling and hide Validation implementation
 * details and different validation API details from
 * {@link org.springbridge.support.handler.ActionHandler}
 * 
 * @author Anoop V S
 * 
 */
public class CompositeValidator extends AbstractActionFormValidator implements InitializingBean {
	// Generic Exception pattern will be Exception class name :: Exception message
	public static final String GENERIC_EXCEPTION_MSG = "%s::%s";

	private List<ActionFormValidator> validators;

	public CompositeValidator() {
		super();
	}

	public void setValidators(final List<? extends ActionFormValidator> validators) {
		Objects.requireNonNull(validators, "ActionFormValidators cannot be null.");
		//sanitize user input 
		validators.removeIf(this::isNullActionFormValidator);
		this.validators = new ArrayList<>(validators);
	}
	
	protected boolean isNullActionFormValidator(final ActionFormValidator formValidator) {
		return Objects.isNull(formValidator);
	}

	private boolean isCancelled(final ActionMapping mapping, final HttpServletRequest request) {
		boolean isCancelled = false;
		// No Need to validate the incoming POJO
		if (ControllerUtils.isCancelled(request)) {
			// Check Can we really Cancel a request ?
			if (mapping.getCancellable()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Cancelled transaction,skipping validation.");
				}
				isCancelled = true;
			} else {
				throw new InvalidCancelException(
						"Invalid Cancel Attempt.Cancellation is attempted without the proper ActionMapping configuration.");
			}
		}
		return isCancelled;
	}

	@Override
	public final void validate(final ActionMapping mapping, @Nullable final ActionForm form,
			final HttpServletRequest request, final Errors errors) {
		if (Objects.isNull(form) || !mapping.isValidate()) {
			if (logger.isDebugEnabled()) {
				logger.debug("ActionForm is Null/Validate flag is ['" + mapping.isValidate() + "']");
			}
			// No need to validate.
			return;
		}
		final boolean doValidate = isFormValidationRequired(request) && mapping.isValidate() && Objects.nonNull(form);
		if (doValidate) {
			if (isCancelled(mapping, request)) {
				// No need to validate
				return;
			}
			try {
				for (ActionFormValidator formValidator : this.validators) {
					formValidator.validate(mapping, form, request, errors);
				}
			} catch (final Exception exc) {
				logger.error("Exception during validate Phase.", exc);
				this.resolveValidationException(mapping, form, request, exc);
			}
		}

	}

	/**
	 * Give programmers some additional flexibility to decide whether validation is
	 * required or not based current flow .
	 * 
	 * @param request Can be used by child class to restrict validations based on
	 *                HTTP methods or request/session attributes or HTTP Headers etc
	 * @return boolean value indicating validation is required or not
	 */
	protected boolean isFormValidationRequired(final HttpServletRequest httpRequest) {
		boolean formValidationRequired = true;
		final ActionContext ctx = ControllerUtils.getActionContext(httpRequest);
		if (Objects.nonNull(ctx)) {
			formValidationRequired = ctx.isFormValidationRequired();
		}
		return formValidationRequired;
	}

	/**
	 * 
	 * @param mapping ActionMapping for subclass
	 * @param form    ActionForm for subclass
	 * @param request
	 * @param exc
	 */
	protected void resolveValidationException(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final Exception exc) {
		// Populate Generic Error Message
		final ActionErrors validationErrors = getErrors(request);
		validationErrors.add(Globals.ERROR_KEY,
				new ActionMessage(String.format(GENERIC_VALIDATION_FAILED_MSG, getExceptionInfo(exc))));
		saveErrors(request, validationErrors);
	}

	/**
	 * Give a useful Exception String
	 * 
	 * @param exc
	 * @return
	 */
	protected String getExceptionInfo(final Exception exc) {
		return String.format(GENERIC_EXCEPTION_MSG, exc.getClass().getName(), exc.getMessage());
	}

	@Override
	public void afterPropertiesSet() {
		if (Objects.isNull(validators) || validators.isEmpty()) {
			// DefaultActionFormValidator will invoke validate method in Struts ActionForm
			this.setValidators(Arrays.asList(new DefaultActionFormValidator()));
			if (logger.isDebugEnabled()) {
				logger.debug("afterPropertiesSet()::DefaultActionFormValidator Added.");
			}
		}
	}

}
