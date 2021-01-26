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
package org.springbridge.config.validation.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springbridge.action.ActionMapping;
import org.springbridge.config.validation.ActionMappingValidator;

/**
 * @author Anoop V S
 * 
 */
public class AbstractActionMappingValidator implements ActionMappingValidator {

	protected final Log logger = LogFactory.getLog(getClass());
	/**
	 * JVM system property decides ActionMapping instances needs to be validated or
	 * not.This can be used to validate ActionMapping configurations during
	 * development/Migration to lower env.Set this only in developer sandbox
	 * ,assuming all validations are done prior to ACCP or PROD.There is nothing
	 * wrong in keeping this property for all environment, but we all developers
	 * always strive to improve application startup time.This will do basic sanity
	 * checks on ActionMapping bean in Spring Application Context.
	 */
	public static final String VALIDATE_MAPPING_SYSTEM_PROPERTY = "sb_validate_actionmapping";

	public static final String ACTION_MAPPING_VALIDATION_FAILED = "ActionMapping validation failed."
			+ "Please re-validate ActionMapping.Refer application logs with GUID ['%s'] for details.";

	public static final PrivilegedAction<Boolean> action = () -> Boolean.getBoolean(VALIDATE_MAPPING_SYSTEM_PROPERTY);

	/**
	 * Validate ActionMapping Property value.
	 */
	public static final boolean VALIDATE_MAPPING = AccessController.doPrivileged(action);
	/**
	 * Flag to Turn ON/OFF common validation Logic
	 */
	private boolean validateCommonAttributes = true;
	/**
	 * Map key ActionMapping bean name Value Collection of ActionMapping validation
	 * Error Messages.The messages are Developer friendly Strings representing
	 * various issues in the ActionMapping beans.
	 */
	private Map<String, Collection<String>> configurationErrors;

	public final Map<String, Collection<String>> getConfigurationErrors() {
		if (Objects.isNull(configurationErrors)) {
			return Collections.emptyMap();
		}
		// Pass an unmodifiable Copy
		return Collections.unmodifiableMap(this.configurationErrors);
	}

	@Override
	public final boolean hasErrors() {
		return !getConfigurationErrors().isEmpty();
	}

	public boolean isValidateCommonAttributes() {
		return validateCommonAttributes;
	}

	public void setValidateCommonAttributes(boolean validateCommonAttributes) {
		this.validateCommonAttributes = validateCommonAttributes;
	}

	/**
	 * Capture Configuration errors associated with given ActionMapping
	 * 
	 * @param beanName ActionMapping bean name
	 * @param message  Developer friendly message shows reason for rejection.
	 */
	public final void rejectActionMapping(final String beanName, final String message) {
		if (Objects.isNull(configurationErrors)) {
			configurationErrors = new HashMap<>();
		}
		configurationErrors.computeIfAbsent(beanName, k -> new ArrayList<String>(3)).add(message);
	}

	@Override
	public final void validateActionMappings(final Collection<ActionMapping> actionMappings) {
		if (logger.isDebugEnabled()) {
			logger.debug("validateActionMappings()::Entry");
		}
		for (ActionMapping actionMapping : actionMappings) {
			validateActionMapping(actionMapping);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("validateActionMappings()::Exit");
		}
	}

	protected final StringBuilder createErrorInfoIfRequired(final StringBuilder errorInfo) {
		if (Objects.isNull(errorInfo)) {
			return new StringBuilder(256);
		}
		return errorInfo;
	}

	/**
	 * This method performs normal Sanity checks on current ActionMapping instance
	 * and pass control to doValidateActionMapping for implementing application
	 * specific validations.Applications which has child ActionMappings with
	 * additional properties.
	 */
	public final void validateActionMapping(final ActionMapping actionMapping) {
		if (isValidateCommonAttributes()) {
			StringBuilder errorInfo = null;
			if (actionMapping.isValidate() && Objects.isNull(actionMapping.getInput())) {
				errorInfo = createErrorInfoIfRequired(errorInfo);
				errorInfo.append(
						"For ActionMapping 'validate' attribute is set to 'true',But no 'input' attribute defined,Is this correct?.");
			}
			if (Objects.isNull(actionMapping.getPath())) {
				errorInfo = createErrorInfoIfRequired(errorInfo);
				errorInfo.append("'path' attribute is missing for ActionMapping.");
			}
			if (Objects.nonNull(errorInfo)) {
				errorInfo.append(actionMapping);
				rejectActionMapping(actionMapping.getBeanName(), errorInfo.toString());
			}
		}
		this.doValidateActionMapping(actionMapping);
	}

	/**
	 * Hook for child class to implement specific validation on current
	 * ActionMapping.Normally during Spring Container Startup.Collect all errors
	 * associated with ActionMapping and reject ActionMapping using
	 * {@code AbstractActionMappingValidator#rejectActionMapping(String, String)}
	 * 
	 * @see #rejectActionMapping(String, String)
	 * @param actionMapping ActionMapping for subclass
	 * @return
	 */
	protected void doValidateActionMapping(final ActionMapping actionMapping) {
		// NOOP
	}

}
