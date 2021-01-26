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

import org.springframework.core.NestedRuntimeException;

/**
 * Exception thrown to denote Invalid Cancel attempt as in Struts. In
 * spring-bridge framework we are treating this as a RuntimeException
 * 
 * @author Anoop V S
 *
 */
public class InvalidCancelException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct the exception with the Specified message.
	 *
	 * @param message the message
	 */
	public InvalidCancelException(final String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public InvalidCancelException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
