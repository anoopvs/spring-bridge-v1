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
package org.springbridge.validation.impl;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springbridge.action.ActionForm;
import org.springbridge.action.ActionMapping;
import org.springbridge.support.utils.MessageResources;
import org.springbridge.validation.AbstractResourceBasedActionFormValidator;
import org.springbridge.validation.CustomBeanValidator;
import org.springframework.validation.Errors;

/**
 * This Adaptor class is to support ActionForm validation based on Spring
 * spring-modules-validation project.So you can use it with any POJOs. This
 * helps us to use XML based Client and server side Validation. <b>Note :- </b>
 * spring-modules-validation(spring-modules-validation-0.8a) is old project
 * doesn't have any active development.<b>valang.tld at line number 13 there is
 * a known issue with body-content element value.
 * &lt;body-content&gt;None&lt;/body-content&gt; which needs to be changed to
 * &lt;body-content&gt;empty&lt;/body-content&gt; if you are running it along
 * with Jboss EAP</b>.You can do this correction and host it under different
 * version in your private Artifactory if you really want to use XML based form
 * validation along with spring-bridge framework.Otherwise application startup
 * will fail.If you are running your application in Tomcat based environment
 * then these issues won't appear.
 * 
 * 
 * @author Anoop V S
 *
 */
public class SpringCommonsActionFormValidator extends AbstractResourceBasedActionFormValidator {

	private final CustomBeanValidator beanValidator;

	public SpringCommonsActionFormValidator(final CustomBeanValidator beanValidator,
			final MessageResources messageResources) {
		super(messageResources);
		this.beanValidator = Objects.requireNonNull(beanValidator,
				"CustomBeanValidator cannot be Null.Please check the Spring Configuration.");
	}

	@Override
	public void validate(final ActionMapping mapping, final ActionForm form, final HttpServletRequest httpRequest,
			final Errors errors) {
		if (logger.isDebugEnabled()) {
			logger.debug("SpringCommonsActionFormValidator::validate()::Entry");
		}
		beanValidator.validate(form, errors, mapping);
		saveValidationErrorsIfPresent(httpRequest, errors);
		if (logger.isDebugEnabled()) {
			logger.debug("SpringCommonsActionFormValidator::validate()::Exit");
		}
	}

	public CustomBeanValidator getBeanValidator() {
		return beanValidator;
	}
}
