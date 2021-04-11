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
package org.springbridge.support.handler;

import java.util.Objects;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.Globals;
import org.springbridge.support.ExecuteFunction;
import org.springbridge.support.IActionContext;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

/**
 * Object representing a context for Action execution. This can also used as a
 * Context to carry information between various framework components.
 *
 * @author Anoop V S
 *
 **/

public class ActionContext implements IActionContext {

	private final ActionMapping mapping;
	private final ActionForm actionForm;
	private final HttpServletRequest httpServletRequest;
	private final HttpServletResponse httpServletResponse;
	private final String formName;
	private final Object controller;
	private final BindingResult bindingResult;
	private final Model inputModel;
	private ExecuteFunction executeFunction;
	private Exception executionException;
	private boolean formValidationRequired = true;

	protected ActionContext(final Builder ctxBuilder) {
		super();
		this.mapping = ctxBuilder.mapping;
		this.actionForm = ctxBuilder.actionForm;
		this.httpServletRequest = ctxBuilder.httpServletRequest;
		this.httpServletResponse = ctxBuilder.httpServletResponse;
		this.controller = ctxBuilder.controller;
		this.bindingResult = ctxBuilder.bindingResult;
		this.executeFunction = ctxBuilder.executeFunction;
		this.inputModel = ctxBuilder.inputModel;
		this.formName = ctxBuilder.formName;
		this.formValidationRequired = ctxBuilder.formValidationRequired;
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return this.httpServletRequest;
	}

	@Override
	public HttpServletResponse getHttpServletResponse() {
		return this.httpServletResponse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends ActionForm> F getForm() {
		return (F) actionForm;
	}

	@Override
	public Object getController() {
		return this.controller;
	}

	@Override
	public ActionMapping getActionMapping() {
		return this.mapping;
	}

	@Override
	public BindingResult getBindingResult() {
		return this.bindingResult;
	}

	@Override
	public ExecuteFunction getExecuteFunction() {
		return executeFunction;
	}

	/**
	 * If formName attribute is present,Pass it value back to caller. Else pass
	 * ActionMapping attribute.
	 * 
	 * @see ActionMapping#getAttribute()
	 */
	@Override
	public String getFormName() {
		if (StringUtils.hasText(formName)) {
			return formName;
		}
		return this.mapping.getAttribute();
	}

	@Override
	public Model getInputModel() {
		return this.inputModel;
	}

	public Exception getExecutionException() {
		return executionException;
	}

	public void setExecutionException(Exception executionException) {
		this.executionException = executionException;
	}

	@Override
	public boolean isFormValidationRequired() {
		return formValidationRequired;
	}

	/**
	 * Static factory method to create new {@code Builder} instance.
	 * 
	 * @return
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Helper class to create {@link ActionContext} instances with a fluent API.
	 */
	public static class Builder {
		private Model inputModel;
		private HttpServletRequest httpServletRequest;
		private HttpServletResponse httpServletResponse;
		private ActionForm actionForm;
		private Object controller;
		private ActionMapping mapping;
		private BindingResult bindingResult;
		private ExecuteFunction executeFunction;
		private String formName;
		private boolean formValidationRequired = true;

		private Builder() {
		}

		public Builder withHttpServletRequest(final HttpServletRequest httpServletRequest) {
			this.setHttpServletRequest(httpServletRequest);
			return this;
		}

		public void setHttpServletRequest(final HttpServletRequest httpServletRequest) {
			this.httpServletRequest = httpServletRequest;
		}

		public Builder withHttpServletResponse(final HttpServletResponse httpServletResponse) {
			this.setHttpServletResponse(httpServletResponse);
			return this;
		}

		public void setHttpServletResponse(final HttpServletResponse httpServletResponse) {
			this.httpServletResponse = httpServletResponse;
		}

		public Builder withForm(final ActionForm actionForm) {
			this.setForm(actionForm);
			return this;
		}

		public void setForm(final ActionForm actionForm) {
			this.actionForm = actionForm;
		}

		/**
		 * Pass the Controller/Action class reference
		 * 
		 * @param controller - Action class reference.Always points to {@code this}
		 *                   reference.
		 * @return Builder instance with new internal state.
		 */
		public Builder withController(final Object controller) {
			this.setController(controller);
			return this;
		}

		public void setController(final Object controller) {
			this.controller = controller;
		}

		public Builder withMapping(final ActionMapping mapping) {
			this.setMapping(mapping);
			return this;
		}

		public void setMapping(final ActionMapping mapping) {
			this.mapping = mapping;
		}

		public Builder withBindingResult(final BindingResult bindingResult) {
			this.setBindingResult(bindingResult);
			return this;
		}

		public void setBindingResult(final BindingResult bindingResult) {
			this.bindingResult = bindingResult;
		}

		public Builder withExecuteFunction(final ExecuteFunction executeFunction) {
			this.setExecuteFunction(executeFunction);
			return this;
		}

		public void setExecuteFunction(final ExecuteFunction executeFunction) {
			this.executeFunction = executeFunction;
		}

		/**
		 * Pass the required formName to Builder,This has higher precedence than
		 * formName present in ActionMapping.
		 * 
		 * @see ActionContext#getFormName()
		 * 
		 * @param formName
		 * @return Builder instance with new internal state.
		 */
		public Builder withFormName(final String formName) {
			this.setFormName(formName);
			return this;
		}

		public void setFormName(final String formName) {
			this.formName = formName;
		}

		/**
		 * Validation flow kill switch.CompositeValidator honors this kills switch. Skip
		 * the internal Form validation logic and continue Action execution.
		 * 
		 * @return Builder instance with new internal state.
		 */
		public Builder noFormValidate() {
			this.formValidationRequired = false;
			return this;
		}

		public Builder withInputModel(final Model inputModel) {
			this.setInputModel(inputModel);
			return this;
		}

		public void setInputModel(final Model inputModel) {
			this.inputModel = inputModel;
		}

		/*
		 * Getters to expose Builder state to perform specific state change or
		 * validations before building Context.Is there any better approach to expose
		 * Builder state to Consumer?.
		 */
		public Model getInputModel() {
			return inputModel;
		}

		public HttpServletRequest getHttpServletRequest() {
			return httpServletRequest;
		}

		public HttpServletResponse getHttpServletResponse() {
			return httpServletResponse;
		}

		public ActionForm getForm() {
			return actionForm;
		}

		public Object getController() {
			return controller;
		}

		public ActionMapping getMapping() {
			return mapping;
		}

		public BindingResult getBindingResult() {
			return bindingResult;
		}

		public ExecuteFunction getExecuteFunction() {
			return executeFunction;
		}

		public String getFormName() {
			return formName;
		}

		public boolean isFormValidationRequired() {
			return formValidationRequired;
		}

		/**
		 * @param consumer to perform Application specific validations/builder
		 *                 enrichment before building ActionContext
		 * @return ActionContext
		 */
		public ActionContext build(final Consumer<Builder> consumer) {
			Objects.requireNonNull(consumer, Globals.CONSUMER_CANNOT_BE_NULL);
			consumer.accept(this);
			return build();
		}

		public ActionContext build() {
			validateState();
			return new ActionContext(this);
		}

		/**
		 * At this point I am forcing validation on the four parameters.Three mandatory
		 * parameters expected by Struts execute method,Optional actionForm and the
		 * Action class reference
		 * 
		 * @return true indicating validation Success
		 * @throws NullPointerException if any of the required parameter is null
		 */
		public boolean validateState() {
			Objects.requireNonNull(mapping, Globals.ACTION_MAPPING_CANNOT_BE_NULL);
			// All actions doen't have ActionForm
			if (Objects.nonNull(actionForm)) {
				if (Objects.isNull(formName)) {
					Objects.requireNonNull(mapping.getAttribute(), Globals.FORM_NAME_CANNOT_BE_NULL);
				}
				Objects.requireNonNull(bindingResult, Globals.BINDING_RESULT_CANNOT_BE_NULL);
			}
			Objects.requireNonNull(httpServletRequest, Globals.HTTP_SERVLET_REQUEST_CANNOT_BE_NULL);
			Objects.requireNonNull(httpServletResponse, Globals.HTTP_SERVLET_RESPONSE_CANNOT_BE_NULL);
			Objects.requireNonNull(controller, Globals.ACTION_CANNOT_BE_NULL);
			return true;
		}
	}
}
