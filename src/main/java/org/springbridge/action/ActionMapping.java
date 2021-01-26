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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * A Simple POJO holds action-mappings corresponding to one specific path. This
 * internally represents all the data in one &lt;action&gt; tag. Commonly used
 * struts action xml-tags attributes are <b>
 * path,type,scope,validate,attribute,name,input etc</b>.You can extend this
 * class to capture additional attributes as in Apache Struts 1.x.
 * 
 * @implNote All mutating methods must call <b>checkIsConfigured();</b> before
 *           making the state change,especially in case of child
 *           ActionMappings.This enable us to freeze the configurations once
 *           ActionMapping is loaded by Spring Container and avoids accidental
 *           runtime modifications by developers.
 * 
 * @author Anoop V S
 *
 */
public class ActionMapping implements InitializingBean, BeanNameAware, Serializable {

	private static final long serialVersionUID = 1L;

	protected static final Log logger = LogFactory.getLog(ActionMapping.class);

	public static final String SPLIT_REGEX = "\\s*,\\s*";

	public static final String[] NO_ROLES = new String[] {};
	/**
	 * Values are request or session {@code WebApplicationContext.SCOPE_SESSION} is
	 * the default Struts Value. {@code WebApplicationContext.SCOPE_APPLICATION}
	 * which is not supported as in Apache Struts.
	 * 
	 * @See {@link WebApplicationContext#SCOPE_REQUEST}
	 * @See {@link WebApplicationContext#SCOPE_SESSION}
	 * 
	 */
	private String scope = WebApplicationContext.SCOPE_SESSION;

	private String input;

	private boolean validate = true;

	private boolean cancellable = false;

	/**
	 * Name of the form bean.
	 */
	private String name = null;

	/**
	 * Class of the form bean, If any, associated With this Action.
	 */
	private Class<?> formBeanClass = null;

	/**
	 * Class name of the form bean.Eventhough formBeanType is redundant info It will
	 * provide some additional Flexibility,In case of multi module Maven Projects
	 */
	private String formBeanType = null;

	/**
	 * Context—relative path of the submitted request, starting With a slash ("/")
	 * character, and contains any filename extension If extension mapping is being
	 * used.
	 */
	private String path = null;

	/**
	 * Fully qualified Java class name of the {@code Action} class to be used to
	 * process requests for this mapping if the {@code forward} and {@code include}
	 * properties are not set.
	 */
	private String type = null;

	/**
	 * Indicates if configuration of this component been completed. Since this
	 * component is configured through Spring always putting it as True
	 * 
	 * @see this{@link #afterPropertiesSet()}
	 */
	private volatile boolean configured = false;

	/**
	 * <p>
	 * The request-scope or session-scope attribute name under Which our form bean
	 * is accessed, If it is different from the form bean's Specified {@code name}.
	 * </p>
	 */
	protected String attribute = null;

	/**
	 * Collection to Hold Forwards Associated with an Action.
	 */
	private Map<String, ActionForward> forwards;

	/**
	 * Collection to Hold Global Forwards Associated with an Action.
	 */
	private Map<String, ActionForward> globalForwards;

	/**
	 * A map of arbitrary properties configured for this component
	 *
	 */
	private Map<String, String> properties;
	/**
	 * Config parameter that can be used by Dispatch and its child
	 * actions.Additionaly this can be used to hold additional info about action
	 * Which can be used at Runtime
	 */
	private String parameter = null;
	/**
	 * Comma-delimited list of security role names allowed to request this Action.
	 */
	private String roles = null;
	/**
	 * The set of security role names used to authorize access to this Action, as an
	 * array for faster access.
	 */
	private String[] roleNames = NO_ROLES;
	/**
	 * Internal configuration parameter which helps to provide precise information
	 * about configuration errors.This value is populated by Spring Container. So
	 * developers can concentrate on specific ActionMapping bean in Spring Context
	 * and improves the configuration errors traceability of current ActionMapping .
	 */
	private String beanName;
	/**
	 * Cached input Forward corresponding to current ActionMapping
	 */
	private ActionForward inputForward;

	/**
	 * Context-relative path of the web application resource that will process this
	 * request via RequestDispatcher.forward(), instead of instantiating and calling
	 * the Action.Exactly one of forward, include, or type must be specified. In
	 * spring-bridge we need to create separate Controllers to achieve include or
	 * forward functionality.
	 */
	private String forward = null;

	/**
	 * Context-relative path of the web application resource that will process this
	 * request via RequestDispatcher.include(), instead of instantiating and calling
	 * the Action class specified by type. Exactly one of forward, include, or type
	 * must be specified.
	 */
	private String include = null;

	public ActionMapping() {
		super();
	}

	/**
	 * Based on given Key initially Query the forwards Map, If Not Found Query
	 * globalForwards Map.
	 *
	 * @param forwardName
	 * @return
	 */
	public final ActionForward findForward(final String forwardName) {
		ActionForward forwardConfig = null;
		if (Objects.nonNull(this.forwards)) {
			forwardConfig = forwards.get(forwardName);
		}
		if (Objects.isNull(forwardConfig) && Objects.nonNull(this.globalForwards)) {
			// Try to find a matching forward from Global Forwards.
			forwardConfig = globalForwards.get(forwardName);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Lookup for ActionForward with name ['" + forwardName + "'] resulted in ['" + forwardConfig + "]'");
		}
		return forwardConfig;
	}

	public ActionForward getInputForward() {
		return inputForward;
	}

	public boolean addForward(final String name, final String fwdPath) {
		return this.addForward(name, fwdPath, false);
	}

	public boolean addForward(final String name, final String fwdPath, final boolean redirect) {
		return this.addForward(name, new ActionForward(name, fwdPath, redirect));
	}

	public final boolean addForward(final String name, final ActionForward fwdPath) {
		checkIsConfigured();
		if (Objects.nonNull(fwdPath)) {
			createForwardsIfNull();
			forwards.put(name, fwdPath);
		}
		return true;
	}

	public final boolean addForward(final ActionForward fwdPath) {
		if (Objects.nonNull(fwdPath)) {
			final String fwdName = StringUtils.hasText(fwdPath.getName()) ? fwdPath.getName() : fwdPath.getPath();
			this.addForward(fwdName, fwdPath);
		}
		return true;
	}

	public String getScope() {
		return this.scope;
	}

	public void setScope(final String scope) {
		checkIsConfigured();
		this.scope = scope;
	}

	public String getForward() {
		return this.forward;
	}

	public void setForward(final String forward) {
		checkIsConfigured();
		this.forward = forward;
	}

	public String getInclude() {
		return this.include;
	}

	public void setInclude(final String include) {
		checkIsConfigured();
		this.include = include;
	}

	public String getInput() {
		return input;
	}

	public void setInput(final String input) {
		checkIsConfigured();
		this.input = input;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(final boolean validate) {
		checkIsConfigured();
		this.validate = validate;
	}

	public boolean getCancellable() {
		return cancellable;
	}

	public void setCancellable(final boolean cancellable) {
		checkIsConfigured();
		this.cancellable = cancellable;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		checkIsConfigured();
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		checkIsConfigured();
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		checkIsConfigured();
		this.type = type;
	}

	public String getFormBeanType() {
		return formBeanType;
	}

	public void setFormBeanType(final String formBeanType) {
		checkIsConfigured();
		this.formBeanType = formBeanType;
		this.setFormBeanClass(loadClass(formBeanType));
	}

	protected Class<?> loadClass(final String className) {
		return ClassUtils.resolveClassName(className, ClassUtils.getDefaultClassLoader());
	}

	/**
	 *
	 * @param key   the key by Which this value Will be retrieved
	 * @param value the value to store With the Supplied key
	 * @throws IllegalstateExceptlon If this module configuration has been frozen
	 * 
	 */
	public void setProperty(final String key, final String value) {
		checkIsConfigured();
		createPropertiesIfNull();
		properties.put(key, value);
	}

	/**
	 * Lazily create Collections based on application usage pattern.
	 */
	public final void createPropertiesIfNull() {
		if (Objects.isNull(this.properties)) {
			this.properties = new HashMap<>();
		}
	}

	public final void createForwardsIfNull() {
		if (Objects.isNull(this.forwards)) {
			this.forwards = new HashMap<>();
		}
	}

	public final void createGlobalForwardsIfNull() {
		if (Objects.isNull(this.globalForwards)) {
			this.globalForwards = new HashMap<>();
		}
	}

	/**
	 * Return the property—value for the specified key if present, Otherwise return
	 * {@code null}
	 *
	 * @param key a key specified in the {@code struts—config} file
	 * @return the value stored with the Supplied key
	 * 
	 */
	public final String getProperty(final String key) {
		if (Objects.isNull(this.properties)) {
			return null;
		}
		return this.properties.get(key);
	}

	public final Map<String, String> getProperties() {
		if (Objects.isNull(this.properties)) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(this.properties);
	}

	/**
	 * Set the entire set of properties configured for this Object.
	 */
	public final void setProperties(final Map<String, String> properties) {
		checkIsConfigured();
		if (Objects.nonNull(properties) && !properties.isEmpty()) {
			createPropertiesIfNull();
			// Properties means non null key & value
			this.properties.putAll(properties);
		}
	}

	public void setAttribute(final String attribute) {
		checkIsConfigured();
		this.attribute = attribute;
	}

	/**
	 * <p>
	 * Returns the {@code request} scope or {@code session} scope attribute name
	 * under Which our form bean is accessed, If it is different from the form
	 * bean's specified {@code name}.
	 *
	 * @return attribute name under which our form bean is accessed.
	 */
	public String getAttribute() {
		if (StringUtils.hasText(this.attribute)) {
			return this.attribute;
		} else {
			return this.name;
		}
	}

	public Class<?> getFormBeanClass() {
		if (Objects.isNull(this.formBeanClass) && StringUtils.hasText(formBeanType)) {
			this.formBeanClass = ClassUtils.resolveClassName(formBeanType, ClassUtils.getDefaultClassLoader());
		}
		// Do we really need to propagate NPE??
		Objects.requireNonNull(this.formBeanClass, this::getMissingConfigurationErrorMessage);
		return this.formBeanClass;
	}

	protected String getMissingConfigurationErrorMessage() {
		return String.format("Required configuration is missing.Please check ActionMapping Configuration.%s.", this);
	}

	/**
	 * Sets globalForwards
	 * 
	 * @param globalForwards
	 */

	public final void setGlobalForwards(final Map<String, ActionForward> globalForwards) {
		if (Objects.nonNull(globalForwards) && !globalForwards.isEmpty()) {
			createGlobalForwardsIfNull();
			this.globalForwards.putAll(globalForwards);
		}
	}

	public void setFormBeanClass(final Class<?> formBeanClass) {
		checkIsConfigured();
		this.formBeanClass = formBeanClass;

	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(final String parameter) {
		checkIsConfigured();
		this.parameter = parameter;
	}

	public final void freeze() {
		configured = true;
	}

	public final void setForwards(final Map<String, ActionForward> forwards) {
		checkIsConfigured();
		if (!CollectionUtils.isEmpty(forwards)) {
			createForwardsIfNull();
			this.forwards.putAll(forwards);
		}
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(final String roles) {
		checkIsConfigured();
		if (Objects.isNull(roles) || roles.isBlank()) {
			return;
		}
		this.roles = roles;
		roleNames = roles.split(SPLIT_REGEX);
	}

	public String[] getRoleNames() {
		return roleNames;
	}

	@Override
	public final void afterPropertiesSet() {
		// Freeze Config changes automatically once ActionMapping bean is created and
		// configured by Spring.
		this.createInputForward();
		this.freeze();
	}

	protected final void createInputForward() {
		// If no input defined for specific ActionMapping then inputForward must be Null
		if (StringUtils.hasText(input)) {
			inputForward = new ActionForward(input);
		}
	}

	public final void checkIsConfigured() {
		if (configured) {
			throw new IllegalStateException(
					String.format("%s ('%s') configuration is frozen.", getClass().getSimpleName(), beanName));
		}
	}

	@Override
	public final void setBeanName(final String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Return the ActionMapping's name.
	 */
	public final String getBeanName() {
		return this.beanName;
	}

	@Override
	public String toString() {
		return String.format(
				"%s ('%s') [scope=%s, input=%s, validate=%s, cancellable=%s, name=%s, formBeanClass=%s, formBeanType=%s, path='%s', type=%s, configured=%s, attribute=%s, forwards=%s, globalForwards=%s, properties=%s, parameter='%s', forward=%s, include=%s, roleNames=%s]",
				getClass().getSimpleName(), beanName, scope, input, validate, cancellable, name, formBeanClass,
				formBeanType, path, type, configured, attribute, forwards, globalForwards, properties, parameter,
				forward, include, Arrays.toString(roleNames));
	}
}