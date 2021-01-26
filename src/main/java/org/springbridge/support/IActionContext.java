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
package org.springbridge.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

/**
 * Interface representing common parameters for Action Execution
 * 
 * @author Anoop V S
 *
 **/
public interface IActionContext {

	HttpServletRequest getHttpServletRequest();

	HttpServletResponse getHttpServletResponse();

	<F extends ActionForm> F getForm();

	Object getController();

	ActionMapping getActionMapping();

	BindingResult getBindingResult();

	String getFormName();

	ExecuteFunction getExecuteFunction();

	Model getInputModel();
	
	boolean isFormValidationRequired();

}
