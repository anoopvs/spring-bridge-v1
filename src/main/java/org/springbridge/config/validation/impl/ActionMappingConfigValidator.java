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
package org.springbridge.config.validation.impl;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springbridge.action.ActionMapping;
import org.springbridge.action.ConfigurationException;
import org.springbridge.action.Globals;
import org.springbridge.utils.ControllerUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * In case of ActionMappingConfigValidator validation will kick start when
 * {@code ApplicationContext} gets initialized or refreshed.This works based on
 * Spring containers loosely coupled pub-sub model.This class listens framework
 * event {@code ContextRefreshedEvent} published by Spring container.
 * 
 * @author Anoop V S
 * 
 */
public class ActionMappingConfigValidator extends AbstractActionMappingValidator
		implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public final void onApplicationEvent(final ContextRefreshedEvent event) {
		onApplicationContextRefresh(event.getApplicationContext());
	}

	public final void onApplicationContextRefresh(final ApplicationContext applicationContext) {
		if (VALIDATE_MAPPING) {
			// Get all ActionMapping beans from Spring ApplicationContext
			final Collection<ActionMapping> actionMappings = ControllerUtils.findActionMappings(applicationContext);
			if (!actionMappings.isEmpty()) {
				validateActionMappings(actionMappings);
				if (this.hasErrors()) {
					final String configurationErrorMessages = getConfigurationErrors().values().stream()
							.flatMap(Collection::stream).collect(Collectors.joining(Globals.NEW_LINE));
					final String guid = UUID.randomUUID().toString();
					logger.error("['" + guid + "'] " + configurationErrorMessages);
					throw new ConfigurationException(String.format(ACTION_MAPPING_VALIDATION_FAILED, guid));
				}
			}
		}
	}
}
