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
package org.springbridge.support.propertyeditors;

import java.text.NumberFormat;
import java.util.Objects;
import java.util.function.IntSupplier;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.util.StringUtils;

/**
 * Convert String request parameters to Numbers.This also convert blank Strings
 * to numeric primitive defaults or default values using an Integer value
 * Supplier.
 * 
 * @author Anoop V S
 *
 */
public class StringToNumberEditor extends CustomNumberEditor {
	// Use IntegerCache
	protected IntSupplier defaultValueGenerator = () -> Integer.valueOf(0);

	public StringToNumberEditor(final Class<? extends Number> numberClass) {
		super(numberClass, false);
	}

	public StringToNumberEditor(final Class<? extends Number> numberClass,final  IntSupplier defaultValueGenerator) {
		this(numberClass);
		this.setDefaultValueGenerator(defaultValueGenerator);
	}

	public StringToNumberEditor(final Class<? extends Number> numberClass,final  NumberFormat numberFormat,
			final IntSupplier defaultValueGenerator) {
		super(numberClass, numberFormat, false);
		this.setDefaultValueGenerator(defaultValueGenerator);
	}

	@Override
	public void setAsText(final String text) {
		if (StringUtils.hasText(text)) {
			super.setAsText(text);
		} else {
			// Provide default value.
			super.setValue(defaultValueGenerator.getAsInt());
		}
	}

	public IntSupplier getDefaultValueGenerator() {
		return defaultValueGenerator;
	}

	public void setDefaultValueGenerator(final IntSupplier defaultValueGenerator) {
		if (Objects.nonNull(defaultValueGenerator)) {
			this.defaultValueGenerator = defaultValueGenerator;
		}
	}
}
