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

import java.util.Objects;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Specific ActionForward which is normally used in redirect scenario to pass
 * additional information as Query parameters .
 * 
 * @author Anoop V S
 *
 */
public class ActionRedirect extends ActionForward {

	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_BUFFER_SIZE = 128;

	private MultiValueMap<String, String> queryParams;

	private String anchorValue = null;

	public ActionRedirect() {
		setRedirect(true);
	}

	public ActionRedirect(final String path) {
		super(path);
		setRedirect(true);
	}

	public ActionRedirect(final String name, final String path) {
		super(name, path, true);
	}

	public ActionRedirect addParameter(final String paramName, final Object valueObj) {
		Objects.requireNonNull(paramName, "paramName cannot be null.");
		final String paramValue = Objects.nonNull(valueObj) ? valueObj.toString() : "";
		this.getQueryParams().add(paramName, paramValue);
		return this;
	}

	public ActionRedirect setAnchor(final String anchorValue) {
		this.anchorValue = anchorValue;
		return this;
	}

	protected final MultiValueMap<String, String> getQueryParams() {
		if (Objects.isNull(this.queryParams)) {
			this.queryParams = new LinkedMultiValueMap<>();
		}
		return this.queryParams;
	}

	public String getOriginalPath() {
		return super.getPath();
	}

	@Override
	public String getPath() {
		// get the original path and query string that was formed
		final String originalPath = getOriginalPath();
		final String anchorString = getAnchorString();
		final StringBuilder result = new StringBuilder(UriComponentsBuilder.newInstance().path(originalPath)
				.queryParams(queryParams).build().encode().toString());
		result.append(anchorString);
		return result.toString();

	}

	public String getAnchorString() {
		String retVal = "";
		if (Objects.nonNull(anchorValue)) {
			retVal = "#" + anchorValue;
		}
		return retVal;
	}

	public String getParameterString() {
		return UriComponentsBuilder.newInstance().queryParams(queryParams).build().encode().toString();
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder(DEFAULT_BUFFER_SIZE);
		result.append("ActionRedirect [");
		result.append("originalPath=").append(getOriginalPath()).append(";");
		result.append("parameterString=").append(getParameterString()).append(";");
		result.append("anchorString=").append(getAnchorString()).append("]");
		return result.toString();
	}
}
