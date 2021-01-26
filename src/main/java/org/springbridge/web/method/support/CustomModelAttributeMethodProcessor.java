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
package org.springbridge.web.method.support;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntSupplier;

import javax.servlet.http.HttpServletRequest;

import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionMapping;
import org.springbridge.action.Globals;
import org.springbridge.support.ActionMappingInstanceLocator;
import org.springbridge.support.propertyeditors.StringToNumberEditor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

/**
 * <p>
 * CustomModelAttributeMethodProcessor is capable of invoking <em>reset</em>
 * method on ActionForm before data population.This is a critical
 * {@code ActionForm} life cycle phase in Apache Struts Framework
 * </p>
 * 
 * @author Anoop V S
 * 
 *         <pre class="code">
 * 
 *         CustomModelAttributeMethodProcessor can be created and prioritized
 *         using below snippet.
 * 
 *         {@code 
 *         public CustomModelAttributeMethodProcessor
 *         customModelAttributeMethodProcessor(ApplicationContext ctx) {
 *         		CustomModelAttributeMethodProcessor methodProcessor= new CustomModelAttributeMethodProcessor(ctx);
 *         		methodProcessor.setConvertNullToDefaultValue(true); 
 *         		methodProcessor.afterPropertiesSet();
 *         		return methodProcessor;
 *         }
 * 
 * 		&#64;Bean("#sb_config_handler_adapter") 
 * 		public String dummyBean(ApplicationContext ctx,RequestMappingHandlerAdapter adapter){
 *           	CustomModelAttributeMethodProcessor methodProcessor= customModelAttributeMethodProcessor(ctx);
 *         	 	final List&lt;HandlerMethodArgumentResolver&gt; resolvers=new ArrayList<>(adapter.getArgumentResolvers()); 
 *         	 	resolvers.add(0,methodProcessor);
 *           	adapter.setArgumentResolvers(resolvers); 
 *           	return "OK"; 
 *       }
 * 
 *         </pre>
 */
public class CustomModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor
		implements InitializingBean {

	private final ApplicationContext applicationContext;

	private Map<Class<?>, PropertyEditor> globalCustomEditors;

	private Map<Class<?>, PropertyEditor> nullToDefaultCustomEditors;

	private List<ActionMappingInstanceLocator> actionMappingLocators;
	/**
	 * Enforcing default Struts 1.x behavior 
	 */
	private boolean convertNullToDefaultValue = true;

	private IntSupplier defaultValueGenerator;

	/**
	 * 
	 * @param annotationNotRequired If "true" means any non-simple method arguments
	 *                              and return values are considered model
	 *                              attributes with or without the presence of an
	 *                              {@code @ModelAttribute}.For spring-bridge this
	 *                              needs to be {@code false}.
	 * @param applicationContext
	 */
	public CustomModelAttributeMethodProcessor(final boolean annotationNotRequired,
			final ApplicationContext applicationContext) {
		super(annotationNotRequired);
		this.applicationContext = Objects.requireNonNull(applicationContext,
				Globals.APPLICATION_CONTEXT_CANNOT_BE_NULL);
	}

	/**
	 * Default Constructor enforcing the required behavior for annotationNotRequired
	 * attribute (value {@code false}).
	 * 
	 * @param applicationContext
	 */
	public CustomModelAttributeMethodProcessor(final ApplicationContext applicationContext) {
		this(false, applicationContext);
	}

	protected void validateTypeAndPropertyEditor(final Class<?> requiredType, final PropertyEditor propertyEditor) {
		if (Objects.isNull(requiredType) || Objects.isNull(propertyEditor)) {
			throw new IllegalArgumentException("Type or PropertyEditor is Null.");
		}
	}

	protected Map<Class<?>, PropertyEditor> getGlobalCustomEditors() {
		if (Objects.isNull(this.globalCustomEditors)) {
			this.globalCustomEditors = new HashMap<>(12);
		}
		return globalCustomEditors;
	}

	protected Map<Class<?>, PropertyEditor> getNullToDefaultCustomEditors() {
		if (Objects.isNull(nullToDefaultCustomEditors)) {
			this.nullToDefaultCustomEditors = new HashMap<>(12);
		}
		return nullToDefaultCustomEditors;
	}

	public final void registerGlobalCustomEditor(final Class<?> requiredType, final PropertyEditor propertyEditor) {
		validateTypeAndPropertyEditor(requiredType, propertyEditor);
		getGlobalCustomEditors().put(requiredType, propertyEditor);
	}

	public final void registerNullToPrimitiveDefaultCustomEditors(final Class<?> requiredType,
			final PropertyEditor propertyEditor) {
		validateTypeAndPropertyEditor(requiredType, propertyEditor);
		getNullToDefaultCustomEditors().put(requiredType, propertyEditor);
	}

	protected void registerNullToPrimitiveDefaultCustomEditors() {
		/*
		 * Avoid NullPointerException while converting Null/Empty String to primitives
		 * .At this point I haven't considered 'char' and boolean primitive type
		 */
		registerNullToPrimitiveDefaultCustomEditors(byte.class,
				new StringToNumberEditor(Byte.class, getDefaultValueGenerator()));
		registerNullToPrimitiveDefaultCustomEditors(short.class,
				new StringToNumberEditor(Short.class, getDefaultValueGenerator()));
		registerNullToPrimitiveDefaultCustomEditors(int.class,
				new StringToNumberEditor(Integer.class, getDefaultValueGenerator()));
		registerNullToPrimitiveDefaultCustomEditors(long.class,
				new StringToNumberEditor(Long.class, getDefaultValueGenerator()));
		registerNullToPrimitiveDefaultCustomEditors(float.class,
				new StringToNumberEditor(Float.class, getDefaultValueGenerator()));
		registerNullToPrimitiveDefaultCustomEditors(double.class,
				new StringToNumberEditor(Double.class, getDefaultValueGenerator()));
	}

	protected void registerCustomEditor(final WebDataBinder binder) {
		if (Objects.nonNull(globalCustomEditors)) {
			globalCustomEditors.forEach(binder::registerCustomEditor);
		}
		if (Objects.nonNull(nullToDefaultCustomEditors)) {
			nullToDefaultCustomEditors.forEach(binder::registerCustomEditor);
		}
	}

	public boolean isConvertNullToDefaultValue() {
		return convertNullToDefaultValue;
	}

	public void setConvertNullToDefaultValue(final boolean convertNullToDefaultValue) {
		this.convertNullToDefaultValue = convertNullToDefaultValue;
	}

	public IntSupplier getDefaultValueGenerator() {
		return defaultValueGenerator;
	}

	public void setDefaultValueGenerator(final IntSupplier defaultValueGenerator) {
		this.defaultValueGenerator = defaultValueGenerator;
	}

	/**
	 * I am not seeing any reason to apply some fallback Strategy for
	 * getActionMapping, If you really want to apply some fallback Strategy use
	 * ActionMappingResolver interface.
	 * 
	 * @param httpRequest
	 * @return ActionMapping
	 * 
	 * @see org.springbridge.support.ActionMappingResolver.ActionMappingResolver#resolveActionMapping(HttpServletRequest)
	 * @see org.springbridge.support.PathMatchingActionMappingResolver
	 */
	protected ActionMapping getActionMapping(final HttpServletRequest httpRequest) {
		ActionMapping mapping = null;
		if (Objects.isNull(actionMappingLocators)) {
			initActionMappingLocators();
		}
		if (Objects.nonNull(actionMappingLocators)) {
			for (ActionMappingInstanceLocator actionMappingLocator : this.actionMappingLocators) {
				mapping = actionMappingLocator.locateActionMapping(httpRequest);
				if (Objects.nonNull(mapping)) {
					break;
				}
			}
		}
		return mapping;
	}

	protected void initActionMappingLocators() {
		// Find all ActionMappingInstanceLocator in the ApplicationContext
		final Map<String, ActionMappingInstanceLocator> actionMappingResolverBeans = applicationContext
				.getBeansOfType(ActionMappingInstanceLocator.class);
		if (!actionMappingResolverBeans.isEmpty()) {
			this.actionMappingLocators = new ArrayList<>(actionMappingResolverBeans.values());
			// sort by order integer value
			Collections.sort(this.actionMappingLocators,
					Comparator.comparingInt(ActionMappingInstanceLocator::getOrder));
		}
	}

	/**
	 * If DataBinder target is ActionForm invoke the reset method before actual data
	 * population.
	 * 
	 * @param binder  Current DataBinder instance
	 * @param request The current NativeWebRequest
	 */
	@Override
	public final void bindRequestParameters(final WebDataBinder binder, final NativeWebRequest request) {
		final HttpServletRequest httpRequest = request.getNativeRequest(HttpServletRequest.class);
		Objects.requireNonNull(httpRequest, Globals.HTTP_SERVLET_REQUEST_CANNOT_BE_NULL);
		this.registerCustomEditor(binder);
		final Object target = binder.getTarget();
		if (Objects.nonNull(target) && ActionForm.class.isInstance(target)) {
			final ActionForm actionForm = ActionForm.class.cast(target);
			final ActionMapping mapping = getActionMapping(httpRequest);
			// Invoke reset method in ActionForm before data Population.
			actionForm.reset(mapping, httpRequest);
			final String modelAttributeName = binder.getObjectName();
			if (logger.isDebugEnabled()) {
				logger.debug("CustomModelAttributeMethodProcessor invoked reset() method on FormBean ['"
						+ modelAttributeName + "'].ActionMapping used " + mapping);
			}
			// Just Set attribute name and value as request attributes.
			populateModelAttributeDetails(httpRequest, actionForm, modelAttributeName);
		}
		super.bindRequestParameters(binder, request);
	}

	protected void populateModelAttributeDetails(final HttpServletRequest httpRequest, final ActionForm actionForm,
			final String modelAttributeName) {
		httpRequest.setAttribute(Globals.MODEL_ATTRIBUTE_KEY, actionForm);
		httpRequest.setAttribute(Globals.MODEL_ATTRIBUTE_NAME_KEY, modelAttributeName);
	}

	@Override
	public void afterPropertiesSet() {
		if (convertNullToDefaultValue) {
			registerNullToPrimitiveDefaultCustomEditors();
			if (logger.isDebugEnabled()) {
				logger.debug(
						"CustomModelAttributeMethodProcessor::afterPropertiesSet()::Registered Null To Primitive Default CustomEditors.");
			}
		}
	}

}
