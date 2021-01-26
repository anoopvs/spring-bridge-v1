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
package org.springbridge.config.validation;

import java.util.Collection;

import org.springbridge.action.ActionMapping;

/**
 * Defines generic contract for validating ActionMappings
 * configurations present in spring application context
 * 
 * @author Anoop V S
 * 
 */
public interface ActionMappingValidator {
	/**
	 * Validate all ActionMappings in the Spring Application Context
	 * 
	 * @param actionMappings
	 */
	void validateActionMappings(Collection<ActionMapping> actionMappings);

	/**
	 * Is there any validation errors?
	 * 
	 * @return {@code true} if there are any errors in any of the ActionMapping
	 */
	boolean hasErrors();
}
