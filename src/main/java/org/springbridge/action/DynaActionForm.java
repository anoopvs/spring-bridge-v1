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

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.DynaClass;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * Common base class for {@code DynaActionForm}.This implementation access and
 * manipulate bean properties through Springs {@code BeanWrapper} Interface.In
 * spring-bridge DynaActionForm all subclasses are POJOs which has all the
 * properties defined in your struts-config.xml file and corresponding getters
 * and setters. Base class provides generic get and set method to manipulate the
 * properties.
 * 
 * @implNote <b>Never implement org.apache.commons.beanutils.DynaBean in your
 *           DynaActionForms sub classes</b>.Caution ,If your java bean
 *           attributes not following proper java bean naming convention then
 *           data binding won't work as expected.
 *           <p>
 *           Base DynaActionForm doen't do anything to set the default initial
 *           values for variables.It is developer responsibility to set the
 *           default values/state as in old Struts 1.x DynaActionForm eg:-For
 *           String type it will be blank and for ArrayList you have to create
 *           empty ArrayList and for HashMap you have to create empty HashMap
 *           and for Map,List,Set (all interfaces types) default value will be
 *           null.In case of custom types default value is created using no-arg
 *           default Constructor.
 *           </p>
 * @author Anoop V S
 *
 */
public class DynaActionForm extends ActionForm implements Serializable {

	protected static final long serialVersionUID = 1L;

	public static final String NO_MAPPED_VALUE = "No mapped value for '%s (%s)'";

	public static final String NO_MAPPED_PROPERTY = "No mapped property for '%s (%s)'";

	public static final String NO_INDEXED_VALUE = "No indexed value for '%s [%s]'";

	public static final String NO_INDEXED_PROPERTY = "No indexed property for '%s [%s]'";

	private transient volatile boolean initialized = false;
	/**
	 * Access and Manipulate bean properties through Springs BeanWrapper Interface
	 */
	private transient BeanWrapper beanWrapper;

	/**
	 * Get the value for given Property
	 *
	 * @param propertyName
	 * @return
	 */
	public Object get(final String propertyName) {
		return this.getBeanWrapper().getPropertyValue(propertyName);
	}

	/**
	 * Check the specified mapped property contain a value for the specified key
	 * value.
	 * 
	 * @param name Name of the property to check
	 * @param key  Name of the key to check
	 * @return {@code true} if the specified property contains a value for the
	 *         specified key value
	 */
	public boolean contains(final String name, final String key) {
		final Object value = this.get(name);
		Objects.requireNonNull(value, String.format(NO_MAPPED_VALUE, name, key));
		if (value instanceof Map) {
			return (((Map<?, ?>) value).containsKey(key));
		} else {
			throw new IllegalArgumentException(String.format(NO_MAPPED_PROPERTY, name, key));
		}
	}

	/**
	 * Return the value of an indexed property with the specified name.
	 * 
	 * @param name  Name of the property whose value is to be retrieved
	 * @param index Index of the value to be retrieved
	 * @return The value of an indexed property with the specified name.
	 */
	public Object get(final String name, final int index) {
		final Object value = this.get(name);
		Objects.requireNonNull(value, String.format(NO_INDEXED_VALUE, name, index));
		if (value.getClass().isArray()) {
			return (Array.get(value, index));
		} else if (value instanceof List) {
			return ((List<?>) value).get(index);
		} else {
			throw new IllegalArgumentException(String.format(NO_INDEXED_PROPERTY, name, index));
		}
	}

	/**
	 * Return the value of a mapped property with the specified name, or
	 * {@code null} if there is no value for the specified key.
	 * 
	 * @param name Name of the property whose value is to be retrieved
	 * @param key  Key of the value to be retrieved
	 * @return If the specified property contains a value for the specified key then
	 *         value or {@code null} if there is no value for the specified key.
	 */
	public Object get(final String name, final String key) {
		final Object value = this.get(name);
		Objects.requireNonNull(value, String.format(NO_MAPPED_VALUE, name, key));
		if (value instanceof Map) {
			return (((Map<?, ?>) value).get(key));
		} else {
			throw new IllegalArgumentException(String.format(NO_MAPPED_PROPERTY, name, key));
		}
	}

	/**
	 * Return the {@code String} value of a property with the specified name
	 * 
	 * @param name Name of the property whose value is to be retrieved.
	 * @return The value of a {@code String} property with the specified name.
	 */
	public String getString(final String name) {
		return (String) this.get(name);
	}

	/**
	 * 
	 * Return the value of a {@code String[]} property with the specified name.
	 *
	 * @param name Name of the property whose value is to be retrieved.
	 * @return The value of a {@code String[]} property with the specified name.
	 */
	public String[] getStrings(final String name) {
		return (String[]) this.get(name);
	}

	/**
	 * Set the value of a simple property with the specified name.
	 *
	 * @param propertyName Name of the property whose value is to be set
	 * @param value        Value to which this property is to be set
	 * 
	 */
	public void set(final String propertyName, final Object value) {
		this.getBeanWrapper().setPropertyValue(propertyName, value);
	}

	/**
	 * Set the value of an indexed property with the specified name.
	 * 
	 * @param name  Name of the property whose value is to be set
	 * @param index Index of the property to be set
	 * @param value Value to which this property is to be set
	 */
	@SuppressWarnings("unchecked")
	public void set(final String name, final int index, final Object value) {
		final Object prop = this.get(name);
		Objects.requireNonNull(value, String.format(NO_INDEXED_VALUE, name, index));
		if (prop.getClass().isArray()) {
			Array.set(prop, index, value);
		} else if (prop instanceof List) {
			try {
				((List<? super Object>) prop).set(index, value);
			} catch (final ClassCastException ccExc) {
				throw new IllegalArgumentException(ccExc.getMessage(), ccExc);
			}
		} else {
			throw new IllegalArgumentException(String.format(NO_INDEXED_PROPERTY, name, index));
		}
	}

	/**
	 * Set the value of a mapped property with the specified name
	 * 
	 * @param name  Name of the property whose value is to be set
	 * @param key   Key of the property to be set
	 * @param value Value to which this property is to be set
	 */
	@SuppressWarnings({ "unchecked" })
	public void set(final String name, final String key, final Object value) {
		final Object prop = this.get(name);
		Objects.requireNonNull(value, String.format(NO_MAPPED_VALUE, name, key));
		if (prop instanceof Map) {
			((Map<String, ? super Object>) prop).put(key, value);
		} else {
			throw new IllegalArgumentException(String.format(NO_MAPPED_PROPERTY, name, key));
		}
	}

	/**
	 * @return Map containing all bean properties
	 */
	public Map<String, Object> getMap() {
		final PropertyDescriptor[] pds = this.getBeanWrapper().getPropertyDescriptors();
		final Map<String, Object> propertyMap = new HashMap<>(pds.length);
		for (PropertyDescriptor pd : pds) {
			final String propertyName = pd.getName();
			// Below means a proper field exposed as per Java Bean standard
			if (isValidBeanProperty(propertyName)) {
				propertyMap.put(propertyName, this.get(propertyName));
			}
		}
		return propertyMap;
	}

	protected final boolean isValidBeanProperty(final String propertyName) {
		return getBeanWrapper().isReadableProperty(propertyName) && getBeanWrapper().isWritableProperty(propertyName);
	}

	/**
	 * Remove any existing value for the specified key on the specified mapped
	 * property.
	 * 
	 * @param name Name of the property for which a value is to be removed
	 * @param key  Key of the value to be removed
	 */
	public void remove(final String name, final String key) {
		final Object value = this.get(name);
		Objects.requireNonNull(value, String.format(NO_MAPPED_VALUE, name, key));
		if (value instanceof Map) {
			((Map<?, ?>) value).remove(key);
		} else {
			throw new IllegalArgumentException(String.format(NO_MAPPED_PROPERTY, name, key));
		}
	}

	/**
	 * Checks given property is an Array Property.This is spring-bridge specific
	 * method
	 * 
	 * @param propertyName
	 * @return {@code true} if the given property is an Array Property.
	 */
	public boolean isArrayProperty(final String propertyName) {
		final Class<?> propertyType=getBeanWrapper().getPropertyType(propertyName);
		return getBeanWrapper().isReadableProperty(propertyName) && Objects.nonNull(propertyType)
				&& propertyType.isArray();
	}

	/**
	 * Create BeanWrapper for accessing or manipulating bean properties
	 * 
	 * @return BeanWrapper
	 */
	protected final BeanWrapper getBeanWrapper() {
		if (!this.initialized) {
			synchronized (this) {
				if (!this.initialized) {
					this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(this);
					this.initialized = true;
				}
			}
		}
		return this.beanWrapper;
	}

	/**
	 * This method Comes from DynaBean,This method is only invoked when we implement
	 * DynaBean and access properties using commons PropertyUtils
	 * 
	 * @return
	 * @throws Always throws {@code UnsupportedOperationException} to identify the
	 *                usage patterns.
	 */
	public DynaClass getDynaClass() {
		throw new UnsupportedOperationException(
				"getDynaClass() method is not implemented for Spring-Bridge DynaActionForm");
	}

	@Override
	public String toString() {
		return String.format("%s [%s]", getClass().getName(), getMap());
	}

}
