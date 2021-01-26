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

import java.util.Map;
import java.util.Objects;

/**
 * Pulling public methods from Struts 1.x WildcardHelper to an interface
 * 
 * @author Anoop V S
 * 
 * @see WildcardPathMatcher
 *
 */
public interface PathMatcher {

	/**
	 * Determines if the pattern contains any * characters
	 *
	 * @param pattern The pattern
	 * @return True if no wildcards are found
	 */
	default boolean isLiteral(final String pattern) {
		return (Objects.isNull(pattern) || pattern.indexOf('*') == -1);
	}

	/**
	 * Translate the given {@code String} into a {@code int[]} representing the
	 * pattern matchable by this class.
	 *
	 * @param pattern Pattern string to translate.
	 * @return int array, terminated by the MATCH_END value
	 * @throws NullPointerException If data is null.
	 */
	int[] compilePattern(String pattern);

	/**
	 * Match a pattern against a string
	 *
	 * @param map  The map to store matched values
	 * @param data The string to match
	 * @param expr The compiled wildcard expression
	 * @return {@code true} if a match
	 * @throws NullPointerException If any parameters are null
	 */
	boolean match(Map<String, String> map, String data, int[] expr);

}