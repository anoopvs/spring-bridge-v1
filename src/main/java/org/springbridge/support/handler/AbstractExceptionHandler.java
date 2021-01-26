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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.springbridge.action.Action;
import org.springbridge.utils.ControllerUtils;

/**
 * 
 * @author Anoop V S
 *
 */
public abstract class AbstractExceptionHandler implements ExceptionHandler {
	/**
	 * Action Classes for which this ExceptionHandler is active
	 */
	private Collection<Class<? extends Action>> actionClasses;
	/**
	 * Supported Exception types
	 */
	private Collection<Class<? extends Exception>> exceptionClasses;

	@Override
	public final boolean supports(Class<?> clazz) {
		final Class<?> currentActionClass = getActionClass();
		for (Class<? extends Action> actionClass : getActionClasses()) {
			if (actionClass.isAssignableFrom(currentActionClass)) {
				return true;
			}
		}
		for (Class<? extends Exception> excClass : getExceptionClasses()) {
			if (excClass.isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}

	public Collection<Class<? extends Action>> getActionClasses() {
		if (Objects.nonNull(actionClasses)) {
			return actionClasses;
		}
		return Collections.emptyList();
	}

	public void setActionClasses(Collection<Class<? extends Action>> actionClasses) {
		if (Objects.nonNull(actionClasses) && !actionClasses.isEmpty()) {
			this.actionClasses = new ArrayList<>();
			this.actionClasses.addAll(actionClasses);
		}
	}

	public Collection<Class<? extends Exception>> getExceptionClasses() {
		if (Objects.nonNull(exceptionClasses)) {
			return exceptionClasses;
		}
		return Collections.emptyList();
	}

	public void setExceptionClasses(Collection<Class<? extends Exception>> exceptionClasses) {
		if (Objects.nonNull(exceptionClasses) && !exceptionClasses.isEmpty()) {
			this.exceptionClasses = new ArrayList<>();
			this.exceptionClasses.addAll(exceptionClasses);
		}
	}

	/**
	 * @return The current Action Class or Controller which handles the current
	 *         request URI.
	 */
	protected Class<?> getActionClass() {
		return ControllerUtils.getActionContext().getController().getClass();
	}

}
