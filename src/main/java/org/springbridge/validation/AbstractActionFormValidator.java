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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springbridge.action.ActionErrors;
import org.springbridge.action.ActionMessage;
import org.springbridge.utils.ControllerUtils;

/**
 * Abstract implementation of {@link ActionFormValidator}.Convenience base class
 * with utility methods for validating ActionForm
 * 
 * @author Anoop V S
 *
 */
public abstract class AbstractActionFormValidator implements ActionFormValidator {

	public static final String VALIDATION_FAILED_MSG = "Validation failed for Field ['%s']";

	public static final String GENERIC_VALIDATION_FAILED_MSG = "Validation failed. ['%s']";
	/** Commons logger */
	protected final Log logger = LogFactory.getLog(getClass());

	protected AbstractActionFormValidator() {
	}

	protected void compareAndSetResourceFlag(final ActionMessage actionMessage) {
		if (Objects.nonNull(actionMessage)) {
			final String code = actionMessage.getKey();
			// If code and defaultMessage are equal means not able to resolve message from
			// ResourceBundle
			if (Objects.nonNull(code) && code.equals(actionMessage.getDefaultMessage())) {
				actionMessage.setResource(true);
			}
		}
	}

	protected void saveErrors(final HttpServletRequest httpRequest, final ActionErrors errors) {
		ControllerUtils.saveErrors(httpRequest, errors);
	}

	protected ActionErrors getErrors(final HttpServletRequest httpRequest) {
		return ControllerUtils.getErrors(httpRequest);
	}
}
