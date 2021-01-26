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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

/**
 * Simple POJO hold and transfer the action messages between various framework
 * components.
 * 
 * @author Anoop V S
 *
 */
public class ActionMessages implements Serializable {

	protected static final long serialVersionUID = 1L;

	public static final String PACKAGE_NAME = ActionMessages.class.getPackage().getName();

	public static final String GLOBAL_MESSAGE = PACKAGE_NAME + ".GLOBAL_MESSAGE";

	private Map<String, Collection<ActionMessage>> messages;

	public ActionMessages() {
		super();
	}

	public final void add(final String key, final ActionMessage error) {
		if (Objects.isNull(messages)) {
			/*
			 * Even after working large number of projects ,it is hard to come up with a
			 * sensible default value for messages map,Ideally number of user input controls
			 * in UI for which we have configured some validation rules. So going with
			 * default HashMap size(16).Using LinkedHashMap for predictable iteration order
			 */
			messages = new LinkedHashMap<>();
		}
		/*
		 * LinkedHashSet default size 16.LinkedHashSet will always preserve the order
		 * Set also avoids duplicate ActionMessage
		 */
		messages.computeIfAbsent(key, k -> new LinkedHashSet<ActionMessage>(5)).add(error);
	}

	/**
	 *
	 * @param actionMessages
	 */
	public void add(final ActionMessages actionMessages) {
		if (Objects.nonNull(actionMessages) && !actionMessages.isEmpty()) {
			actionMessages.getMessages().forEach((k, v) -> {
				if (Objects.nonNull(v)) {
					v.forEach(value -> this.add(k, value));
				}
			});
		}
	}

	public void clear() {
		if (Objects.isNull(messages)) {
			return;
		}
		messages.clear();
	}

	public boolean empty() {
		return (this.isEmpty());
	}

	public final boolean isEmpty() {
		if (Objects.isNull(messages)) {
			return true;
		}
		return (messages.isEmpty());
	}

	public int size() {
		if (isEmpty()) {
			return 0;
		}
		int size = 0;
		for (Collection<ActionMessage> actionMessages : messages.values()) {
			if (Objects.nonNull(actionMessages)) {
				size += actionMessages.size();
			}
		}
		return size;
	}

	public Map<String, Collection<ActionMessage>> getMessages() {
		if (Objects.isNull(messages)) {
			// Avoid passing Null reference
			return Collections.emptyMap();
		}
		// Pass an unmodifiable Copy
		return Collections.unmodifiableMap(this.messages);
	}

	@Override
	public String toString() {
		return String.format("ActionMessages [messages=%s]", messages);
	}

}
