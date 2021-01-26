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
package org.springbridge.support;

import javax.servlet.http.HttpServletRequest;

import org.springbridge.action.ActionMapping;
import org.springbridge.action.Globals;
import org.springframework.core.Ordered;

/**
 * RequestAttributeActionMappingInstanceLocator Strategy assumes ActionMapping
 * is present as a request Attribute.This is set by
 * {@link org.springbridge.web.method.support.ActionMappingConfigMethodProcessor#resolveArgument}.
 * Request attribute name {@link Globals#MAPPING_KEY}
 * 
 * @author Anoop V S
 *
 */
public class RequestAttributeActionMappingInstanceLocator implements ActionMappingInstanceLocator {

	private int order = Ordered.LOWEST_PRECEDENCE;

	@Override
	public final ActionMapping locateActionMapping(final HttpServletRequest httpRequest) {
		return ActionMapping.class.cast(httpRequest.getAttribute(Globals.MAPPING_KEY));
	}

	@Override
	public String toString() {
		return String.format("RequestAttributeActionMappingInstanceLocator [order=%s]", order);
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
