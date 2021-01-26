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
package org.springbridge.utils;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springbridge.action.ActionForm;
import org.springbridge.action.Globals;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

/**
 * Utility class with static methods for dealing with ModelAttribute or
 * ActionForm. This class contains utility methods to handle request scoped
 * ModelAttribute as in Apache struts ,basically lookup the ModelAttribute from
 * request scope before creating the new one.This can be invoked from
 * {@code ModelAttribute} annotated methods.
 * 
 * @author Anoop V S
 *
 */
public final class ModelAttributeUtils {

	private static final Log logger = LogFactory.getLog(ModelAttributeUtils.class);

	private ModelAttributeUtils() {
	}

	/**
	 * In case of session scoped ModelAttribute,Spring framework will do the
	 * lookup.ModelAttribute annotated method will be invoked only for first
	 * flow,Subsequent request spring will take the value from session and pass it
	 * to handler method means ModelAttribute annotated method won't be invoked.But
	 * in case of normal request scoped model attribute explicitly do the look up on
	 * HttpServletRequest.This will help to resolve the issues like populate data in
	 * one action class and forward it to next action class using the same
	 * ModelAttribute,So Next action class will get the same
	 * data.{@link Globals#MODEL_ATTRIBUTE_KEY} is populated by
	 * {@link org.springbridge.web.method.support.CustomModelAttributeMethodProcessor}
	 * 
	 * @param modelAttributeClass
	 * @return
	 */
	public static <T> T lookupOrCreateModelAttribute(final Class<T> modelAttributeClass) {
		Objects.requireNonNull(modelAttributeClass, "ModelAttribute Class cannot be null.");
		final HttpServletRequest httpRequest = ControllerUtils.getRequest();
		Object modelAttribute = httpRequest.getAttribute(Globals.MODEL_ATTRIBUTE_KEY);
		if (Objects.isNull(modelAttribute)) {
			// Normal No Arg Constructor invocation as in case of Apache Struts 1.x
			modelAttribute = BeanUtils.instantiateClass(modelAttributeClass);
		} else {
			if (!modelAttributeClass.isInstance(modelAttribute)) {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format(
							"Current Request scoped ModelAttribute class ['%s'] is not an Instance of ['%s'].",
							modelAttribute.getClass(), modelAttributeClass));
				}
				/*
				 * If current request scoped ModelAttribute is different type than expected
				 * ,Then Silently create new expected ModelAttribute.
				 */
				modelAttribute = BeanUtils.instantiateClass(modelAttributeClass);
			}
		}
		return modelAttributeClass.cast(modelAttribute);
	}

	/**
	 * 
	 * @param modelAttributeClass
	 * @return ActionForm object corresponding given modelAttributeClass
	 */
	public static ActionForm lookupOrCreateActionForm(final Class<?> modelAttributeClass) {
		return ActionForm.class.cast(lookupOrCreateModelAttribute(modelAttributeClass));
	}

	/**
	 * This method can be used in scenarios where generated DynaActionForm or its
	 * subclasses exists in different Module which doesn't have any dependency with
	 * module in which Action class resides .{@code @ModelAttribute} annotated
	 * method can use this utility method to create required ActionForm instance.
	 * This helps to avoid compile time dependency with Other module.
	 * 
	 * <b> Correct Fix will be pull the required module dependency using
	 * Maven/Gradle dependency management tool </b>
	 * 
	 * @param modelAttributeClassName Correct fully qualified class name
	 * @return ActionForm object corresponding given class name
	 */
	public static ActionForm lookupOrCreateModelAttribute(final String modelAttributeClassName) {
		final Class<?> modelAttributeClass = ClassUtils.resolveClassName(modelAttributeClassName,
				ClassUtils.getDefaultClassLoader());
		return ActionForm.class.cast(lookupOrCreateModelAttribute(modelAttributeClass));
	}
}
