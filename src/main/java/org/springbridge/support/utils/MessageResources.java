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
package org.springbridge.support.utils;

import java.util.Locale;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springbridge.utils.ControllerUtils;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.StringValueResolver;

/**
 * Proxy class to transparently handle {@code NoSuchMessageException}.
 * {@code MessageResources} also provides overloaded getMessage methods for more
 * flexibility while dealing with Spring configured {@code MessageSource}.Note
 * :- For MessageResources bean to work correctly the bean name for
 * ResourceBundleMessageSource or ReloadableResourceBundleMessageSource in
 * Spring context must always be <b>messageSource</b>.Otherwise we will get
 * messages in {@link #UNKNOWN_MESSAGE_PATTERN} pattern
 * 
 * @see {@link #getMessage}
 * 
 * @author Anoop V S
 *
 */
public class MessageResources implements MessageSource, EmbeddedValueResolverAware {

	public static final String UNKNOWN_MESSAGE_PATTERN = "???%s???";

	private static final Log logger = LogFactory.getLog(MessageResources.class);

	private final MessageSource delegate;

	private StringValueResolver valueResolver;

	/**
	 * @param delegate - Most of the cases delegate will be Spring Application
	 *                 context itself.
	 * 
	 *                 <pre class="code">
	 * {@code public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
	 *	MessageSource, ApplicationEventPublisher, ResourcePatternResolver }
	 * </pre>
	 */
	public MessageResources(final MessageSource delegate) {
		this.delegate = Objects.requireNonNull(delegate,
				"MessageSource cannot be null.Have you Configured any MessageSource bean (for example ReloadableResourceBundleMessageSource) in Spring Application Context ?.");
	}

	@Override
	public void setEmbeddedValueResolver(final StringValueResolver resolver) {
		this.valueResolver = resolver;
	}

	/**
	 * Resolve the String value after replacing placeholders e.g:-${my.key}.
	 * 
	 * @param strVal
	 * @return
	 */
	public String resolveStringValue(final String strVal) {
		Objects.requireNonNull(strVal, "Method parameter cannot be null.");
		Objects.requireNonNull(this.valueResolver, "No StringValueResolver found,Unable to Proceed");
		final String resolvedValue = this.valueResolver.resolveStringValue(strVal);
		return strVal.equals(resolvedValue) ? strVal : resolvedValue;
	}

	@Override
	public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
		return delegate.getMessage(code, args, defaultMessage, locale);
	}

	/**
	 * NoSuchMessageException is handled explicitly to get the Struts 1.x behavior.
	 */
	@Override
	public String getMessage(final String code, final Object[] args, final Locale locale) {
		try {
			return delegate.getMessage(code, args, locale);
		} catch (final NoSuchMessageException noSuchMessageExc) {
			logger.debug("No Message found for code ['" + code + "']");
		}
		// Struts Default Behavior
		return String.format(UNKNOWN_MESSAGE_PATTERN, code);
	}

	@Override
	public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) {
		return delegate.getMessage(resolvable, locale);
	}

	public String getMessage(final String code) {
		return getMessage(code, null, ControllerUtils.getLocale());
	}

	public String getMessage(final String code, final Object[] args) {
		return getMessage(code, args, ControllerUtils.getLocale());
	}

	public String getMessage(final Locale locale, final String code, final String... args) {
		return getMessage(code, args, locale);
	}

	public boolean isPresent(final Locale locale, final String code) {
		final String message = getMessage(code, null, locale);
		boolean messagePresent = true;
		// Second condition Only valid for impl throws NoSuchMessageException
		if (Objects.isNull(message) || (message.startsWith("???") && message.endsWith("???"))) {
			messagePresent = false;
		}
		return messagePresent;
	}

	public boolean isPresent(final String code) {
		return this.isPresent(ControllerUtils.getLocale(), code);
	}

	public void log(final String message, final Throwable tbl) {
		if (logger.isDebugEnabled()) {
			logger.debug(message, tbl);
		}
	}

	public void log(final String message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
	}
}
