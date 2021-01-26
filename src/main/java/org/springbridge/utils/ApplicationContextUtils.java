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

import java.util.Locale;
import java.util.Objects;

import org.springbridge.support.utils.MessageResources;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * @author Anoop V S
 *
 */
public final class ApplicationContextUtils implements ApplicationContextAware {

	private static final String ERROR_MESSAGE = "Have you Configured ['ApplicationContextUtils'] class as a bean in Spring ApplicationContext?.";

	/**
	 * ApplicationContext in which ApplicationContextUtils bean is configured.
	 */
	private static ApplicationContext applicationContext;

	/**
	 * Initialization-on-demand holder(IODH) for MessageResources.Since all struts
	 * projects don't have message-resources tag in struts-config.xml.So Lazily
	 * Lookup MessageResources bean from Spring Context.This also avoids declaring
	 * unused beans in Spring Context.
	 * 
	 * @author Anoop V S
	 *
	 */
	private static class MessageResourcesHolder {
		/**
		 * MessageResources for transparently handle NoSuchMessageException.
		 */
		static final MessageResources messageResources = lookupMessageResources(
				ApplicationContextUtils.getApplicationContext());

		/**
		 * Lazily lookup messageResources Proxy .
		 */
		private static MessageResources lookupMessageResources(final ApplicationContext applicationContext) {
			return applicationContext.getBean(MessageResources.class);
		}
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		initApplicationContext(Objects.requireNonNull(applicationContext, ERROR_MESSAGE));
	}

	private static void initApplicationContext(final ApplicationContext applicationContext) {
		ApplicationContextUtils.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		Objects.requireNonNull(ApplicationContextUtils.applicationContext, ERROR_MESSAGE);
		return applicationContext;
	}

	public static String getMessage(final String code, final Object[] args, final String defaultMessage,
			final Locale locale) {
		return getApplicationContext().getMessage(code, args, defaultMessage, locale);
	}

	public static String getMessage(final String code) {
		return getMessageResources().getMessage(code);
	}

	public static String getMessage(final String code, final Object[] args) {
		return getMessageResources().getMessage(code, args);
	}

	public static String getMessage(final String code, final Object[] args, final Locale locale) {
		return getMessageResources().getMessage(code, args, locale);
	}

	public static Object getBean(final String name) {
		return getApplicationContext().getBean(name);
	}

	public static <T> T getBean(final String name, final Class<T> requiredType) {
		return getApplicationContext().getBean(name, requiredType);
	}

	public static <T> T getBean(final Class<T> requiredType) {
		return getApplicationContext().getBean(requiredType);
	}

	public static MessageResources getMessageResources() {
		Objects.requireNonNull(MessageResourcesHolder.messageResources, ERROR_MESSAGE);
		return MessageResourcesHolder.messageResources;
	}
}