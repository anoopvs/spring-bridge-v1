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
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Globals contains common constants used in <em>spring-bridge</em> Framework.
 * 
 * @author Anoop V S
 *
 */
public final class Globals implements Serializable {

	private static final long serialVersionUID = 1L;

	private Globals() {
	}

	/**
	 * Match values like /jsp/{1}/Source.jsp or
	 * org.apache.struts.apps.mailreader.actions.{1}Action or {1}Form
	 */
	public static final Pattern MESSAGE_FORMAT_PATTERN = Pattern.compile("(.*\\{\\d{1}\\}.*)*");

	public static final String APPLICATION_CONTEXT_CANNOT_BE_NULL = "ApplicationContext cannot be null.";

	public static final String NOT_AUTHORIZED_MESSAGE = "You are not Authorized to access path ['%s']";

	public static final String SPRINGBRIDGE_ERROR_PAGE_KEY = "springbridge.webapp.default_error_page";

	public static final String UTF_8 = StandardCharsets.UTF_8.name();
	/**
	 * The request attributes key under which current Exception  GUID is stored.
	 */
	public static final String SPRINGBRIDGE_EXCEPTION_GUID = "springbridge_exception_guid";

	public static final String MEDIA_TYPE_TEXT_HTML_UTF8 = "text/html;charset=UTF-8";

	// Validation Messages
	public static final String HTTP_SERVLET_RESPONSE_CANNOT_BE_NULL = "HttpServletResponse cannot be null.";

	public static final String HTTP_SERVLET_REQUEST_CANNOT_BE_NULL = "HttpServletRequest cannot be null.";

	public static final String ACTION_CANNOT_BE_NULL = "Action cannot be null.";

	public static final String BINDING_RESULT_CANNOT_BE_NULL = "BindingResult cannot be null.";

	public static final String FORM_NAME_CANNOT_BE_NULL = "formName cannot be null.";

	public static final String ACTION_MAPPING_CANNOT_BE_NULL = "ActionMapping cannot be null.";

	public static final String EXCEPTION_CANNOT_BE_NULL = "Exception cannot be null.";

	public static final String ACTION_CONTEXT_CANNOT_BE_NULL = "ActionContext cannot be null.";

	public static final String CONSUMER_CANNOT_BE_NULL = "Consumer cannot be null";

	public static final String NO_INPUT_PAGE_TO_FORWARD = "Form Validation Failed.No 'input' attribute defined for ActionMapping Bean ['%s']."
			+ "Please check ActionMapping configuration.";

	public static final String NEW_LINE = System.getProperty("line.separator", "\\n");

	/**
	 * The action execution was successful. Show the success page to the end user.
	 */
	public static final String SUCCESS = "success";

	/**
	 * The action execution was failed. Show failure page
	 */
	public static final String FAILURE = "failure";
	/**
	 * The action could not execute, since the user was not logged in. Force user to
	 * re-login
	 */
	public static final String LOGIN = "login";

	/**
	 * The action could not execute, since the user doen't have required authority
	 */
	public static final String ACCESS_DENIED = "access-denied";
	/**
	 * Globals Package Name
	 */
	public static final String PACKAGE_NAME = Globals.class.getPackage().getName();
	/**
	 * The request attributes key under which a boolean {@code true} value
	 * should be stored If this request was cancelled.
	 */
	public static final String CANCEL_KEY = PACKAGE_NAME + ".CANCEL";
	/**
	 * The request attributes key under which current {@code ActionContext} is
	 * saved
	 */
	public static final String ACTION_CONTEXT_KEY = PACKAGE_NAME + ".ACTION_CONTEXT";

	/**
	 * The Session attributes key under which our transaction token is stored, If it
	 * is used.
	 */
	public static final String TRANSACTION_TOKEN_KEY = PACKAGE_NAME + ".TOKEN";
	/**
	 * The name of the taglib package
	 */
	public static final String TAGLIB_PACKAGE = PACKAGE_NAME;
	/**
	 * The property under which a transaction token is reported.
	 */
	public static final String TOKEN_KEY = TAGLIB_PACKAGE + ".TOKEN";
	/**
	 * The request attributes key under which our <Code>ActionMapplng</Code>
	 * instance is passed.This key is populated by
	 * ActionMappingConfigMethodProcessor
	 */
	public static final String MAPPING_KEY = PACKAGE_NAME + ".mapping.instance";
	/**
	 * The property for Cancel button press.
	 */
	public static final String CANCEL_PROPERTY = TAGLIB_PACKAGE + ".CANCEL";
	/**
	 * The property for Cancel button press, The Cancel button is rendered as an
	 * image.
	 */
	public static final String CANCEL_PROPERTY_X = TAGLIB_PACKAGE + ".CANCEL.x";
	/**
	 * The request attributes key under which your action should store an
	 * {@code ActionErrors} Object, This can be accessed as HttpServletRequest
	 * attribute.
	 */
	public static final String ERROR_KEY = PACKAGE_NAME + ".ERROR";

	/**
	 * The request attributes key under which your handler store an
	 * {@code Exception} Object, This can be accessed as HttpServletRequest
	 * attribute.
	 */
	public static final String EXCEPTION_KEY = PACKAGE_NAME + ".EXCEPTION";
	/**
	 * The request attributes key under which your action should store an
	 * {@code ActlonMessages} Object, This can be accessed as
	 * HttpServletRequest attribute.
	 */
	public static final String MESSAGE_KEY = PACKAGE_NAME + ".ACTION_MESSAGE";
	/**
	 * The request attributes key under which CustomModelAttributeMethodProcessor
	 * stores Model attribute
	 */
	public static final String MODEL_ATTRIBUTE_KEY = PACKAGE_NAME + ".MODEL_ATTRIBUTE";
	/**
	 * The request attributes key under which CustomModelAttributeMethodProcessor
	 * stores Model attribute name
	 */
	public static final String MODEL_ATTRIBUTE_NAME_KEY = PACKAGE_NAME + ".MODEL_ATTRIBUTE_NAME";
	/**
	 * The request attributes key under which PathMatchingActionMappingResolver
	 * stores matchedPatterns from WildcardPathMatcher match method
	 */
	public static final String MATCHED_PATTERNS_MAP = PACKAGE_NAME + ".MATCHED_PATTERNS_MAP";
}
