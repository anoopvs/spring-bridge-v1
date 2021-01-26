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

/**
 * 
 * @author Anoop V S
 *
 */
public class ActionForward implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null;
	/**
	 * This Path Must be full path as in Struts config XML,That means each path
	 * contain its own prefix and suffix
	 */
	private String path;

	private boolean redirect = false;

	public ActionForward() {
	}

	public ActionForward(final String path) {
		this.path = path;
	}

	public ActionForward(final String name, final String path) {
		this(path);
		this.name = name;
	}

	public ActionForward(final String path, final boolean redirect) {
		this.path = path;
		this.redirect = redirect;
	}

	public ActionForward(final String name, final String path, final boolean redirect) {
		this(name, path);
		this.redirect = redirect;
	}

	public ActionForward(final ActionForward other) {
		this(other.getName(), other.getPath(), other.getRedirect());
	}

	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public boolean getRedirect() {
		return redirect;
	}

	public void setRedirect(final boolean redirect) {
		this.redirect = redirect;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("ActionForward [name='%s', path='%s', redirect=%s]", name, path, redirect);
	}

}
