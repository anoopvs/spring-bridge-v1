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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * LabelValueBean as in old Struts 1.x framework.
 * 
 * @author Anoop V S
 *
 */
public class LabelValueBean implements Comparable<LabelValueBean>, Serializable {

	private static final long serialVersionUID = 1L;
	public static final Comparator<LabelValueBean> CASE_INSENSITIVE_ORDER = Comparator
			.comparing(LabelValueBean::getLabel, String.CASE_INSENSITIVE_ORDER);

	private String label = null;
	private String value = null;

	public LabelValueBean() {
		super();
	}

	public LabelValueBean(final String label, final String value) {
		this();
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Compare LabelValueBeans based on the label as in Struts 1.x
	 */
	@Override
	public int compareTo(final LabelValueBean other) {
		return this.getLabel().compareTo(other.getLabel());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LabelValueBean other = (LabelValueBean) obj;
		if (Objects.isNull(other.getValue()) && Objects.isNull(this.getValue())) {
			return true;
		}
		return Objects.equals(value, other.getValue());
	}

	@Override
	public String toString() {
		return String.format("LabelValueBean [label=%s, value=%s]", label, value);
	}
}