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
package org.springbridge.action;

import javax.servlet.http.HttpServletRequest;

/**
 * Form interface defines the common behavior for all User input forms
 * 
 * @author Anoop V S
 *
 */
public interface Form {
	/**
	 * 
	 * @param mapping Current ActionMapping for Form Bean
	 * @param request Current HttpServletRequest
	 * @return
	 */
	public default ActionErrors validate(final ActionMapping mapping, final HttpServletRequest request) {
		/*
		 * Default implementation assumes there is no validation errors.Passing null to
		 * keep behavioral consistency as in Struts 1.x.This also avoids unwanted Object
		 * creation.
		 */
		return null;
	}

	/**
	 * This method is invoked automatically by
	 * CustomModelAttributeMethodProcessor before data population.
	 * 
	 * @param mapping Current ActionMapping for Form Bean
	 * @param request Current HttpServletRequest
	 */
	public default void reset(final ActionMapping mapping, final HttpServletRequest request) {
		//NOOP
	}
}
