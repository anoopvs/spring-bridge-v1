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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * 
 * @author Anoop V S
 *
 */
public class ActionMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String key;
	private String[] values = null;
	private boolean resource = true;
	// Default message from Spring F/W validators
	protected String defaultMessage;

	public ActionMessage(final String key) {
		this.key = Objects.requireNonNull(key, "key cannot be null.");
		this.resource = false;
		// User is providing text message
		this.defaultMessage = key;
	}

	public ActionMessage(final String key, final boolean resource) {
		this(key);
		this.resource = resource;
	}

	public ActionMessage(final String key, final String... arguments) {
		this(key);
		this.values = arguments;
	}

	public String getKey() {
		return key;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(final String[] values) {
		this.values = values;
	}

	public boolean isResource() {
		return (this.resource);
	}

	public void setResource(final boolean resource) {
		this.resource = resource;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(final String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(128);
		builder.append("ActionMessage [key=").append(key).append(",values=").append(Arrays.toString(values))
				.append(",default message=").append(defaultMessage).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultMessage, key, resource);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!ActionMessage.class.isInstance(obj)) {
			return false;
		}
		final ActionMessage other = ActionMessage.class.cast(obj);
		return Objects.equals(key, other.getKey()) && Objects.equals(defaultMessage, other.getDefaultMessage())
				&& Objects.equals(resource, other.isResource());
	}

}
