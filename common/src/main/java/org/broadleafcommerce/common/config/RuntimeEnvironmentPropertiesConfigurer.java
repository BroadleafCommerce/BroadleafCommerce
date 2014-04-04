/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.config;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.logging.SupportLogManager;
import org.broadleafcommerce.common.logging.SupportLogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * A property resource configurer that chooses the property file at runtime
 * based on the runtime environment.
 * <p>
 * Used for choosing properties files based on the current runtime environment,
 * allowing for movement of the same application between multiple runtime
 * environments without rebuilding.
 * <p>
 * The property replacement semantics of this implementation are identical to
 * PropertyPlaceholderConfigurer, from which this class inherits. <code>
 * <pre>
 * &lt;bean id=&quot;propertyConfigurator&quot; class=&quot;frilista.framework.RuntimeEnvironmentPropertiesConfigurer&quot;&gt;
 *        &lt;property name=&quot;propertyLocation&quot; value=&quot;/WEB-INF/runtime-properties/&quot; /&gt;
 *        &lt;property name=&quot;environments&quot;&gt;
 *        &lt;set&gt;
 *            &lt;value&gt;production&lt;/value&gt;
 *            &lt;value&gt;staging&lt;/value&gt;
 *            &lt;value&gt;integration&lt;/value&gt;
 *            &lt;value&gt;development&lt;/value&gt;
 *        &lt;/set&gt;
 *        &lt;/property&gt;
 *        &lt;property name=&quot;defaultEnvironment&quot; value=&quot;development&quot;/&gt;
 * &lt;/bean&gt;
 * </code> </pre> The keys of the environment specific properties files are
 * compared to ensure that each property file defines the complete set of keys,
 * in order to avoid environment-specific failures.
 * <p>
 * An optional RuntimeEnvironmentKeyResolver implementation can be provided,
 * allowing for customization of how the runtime environment is determined. If
 * no implementation is provided, a default of
 * SystemPropertyRuntimeEnvironmentKeyResolver is used (which uses the system
 * property 'runtime.environment')
 * @author <a href="mailto:chris.lee.9@gmail.com">Chris Lee</a>
 */
public class RuntimeEnvironmentPropertiesConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean {

    private static final Log LOG = LogFactory.getLog(RuntimeEnvironmentPropertiesConfigurer.class);
    protected SupportLogger logger = SupportLogManager.getLogger("UserOverride", this.getClass());
    
    protected static final String SHARED_PROPERTY_OVERRIDE = "property-shared-override";
    protected static final String PROPERTY_OVERRIDE = "property-override";
    
    protected static Set<String> defaultEnvironments = new LinkedHashSet<String>();
    protected static Set<Resource> blcPropertyLocations = new LinkedHashSet<Resource>();
    protected static Set<Resource> defaultPropertyLocations = new LinkedHashSet<Resource>();
    
    static {
        defaultEnvironments.add("production");
        defaultEnvironments.add("staging");
        defaultEnvironments.add("integrationqa");
        defaultEnvironments.add("integrationdev");
        defaultEnvironments.add("development");
        
        blcPropertyLocations.add(new ClassPathResource("config/bc/"));
        blcPropertyLocations.add(new ClassPathResource("config/bc/admin/"));
        blcPropertyLocations.add(new ClassPathResource("config/bc/cms/"));
        blcPropertyLocations.add(new ClassPathResource("config/bc/web/"));
        blcPropertyLocations.add(new ClassPathResource("config/bc/fw/"));
        
        defaultPropertyLocations.add(new ClassPathResource("runtime-properties/"));
    }

    protected String defaultEnvironment = "development";
    protected String determinedEnvironment = null;
    protected RuntimeEnvironmentKeyResolver keyResolver;
    protected Set<String> environments = Collections.emptySet();
    protected Set<Resource> propertyLocations;
    protected Set<Resource> overridableProperyLocations;
    protected StringValueResolver stringValueResolver;

    public RuntimeEnvironmentPropertiesConfigurer() {
        super();
        setIgnoreUnresolvablePlaceholders(true); // This default will get overriden by user options if present
        setNullValue("@null");
    }

    public void afterPropertiesSet() throws IOException {
        // If no environment override has been specified, used the default environments
        if (environments == null || environments.size() == 0) {
            environments = defaultEnvironments;
        }
        
        // Prepend the default property locations to the specified property locations (if any)
        Set<Resource> combinedLocations = new LinkedHashSet<Resource>();
        if (!CollectionUtils.isEmpty(overridableProperyLocations)) {
            combinedLocations.addAll(overridableProperyLocations);
        }
        combinedLocations.addAll(defaultPropertyLocations);
        if (!CollectionUtils.isEmpty(propertyLocations)) {
            combinedLocations.addAll(propertyLocations);
        }
        propertyLocations = combinedLocations;
    
        if (!environments.contains(defaultEnvironment)) {
            throw new AssertionError("Default environment '" + defaultEnvironment + "' not listed in environment list");
        }

        if (keyResolver == null) {
            keyResolver = new SystemPropertyRuntimeEnvironmentKeyResolver();
        }

        String environment = determineEnvironment();
        ArrayList<Resource> allLocations = new ArrayList<Resource>();
        
        /* Process configuration in the following order (later files override earlier files
         * common-shared.properties
         * [environment]-shared.properties
         * common.properties
         * [environment].properties
         * -Dproperty-override-shared specified value, if any
         * -Dproperty-override specified value, if any  */
        
        for (Resource resource : createBroadleafResource()) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }
        
        for (Resource resource : createSharedCommonResource()) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }

        for (Resource resource : createSharedPropertiesResource(environment)) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }

        for (Resource resource : createCommonResource()) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }

        for (Resource resource : createPropertiesResource(environment)) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }
        
        Resource sharedPropertyOverride = createSharedOverrideResource();
        if (sharedPropertyOverride != null) {
            allLocations.add(sharedPropertyOverride);
        }
        
        Resource propertyOverride = createOverrideResource();
        if (propertyOverride != null) {
            allLocations.add(propertyOverride);
        }
        
        Properties props = new Properties();
        for (Resource resource : allLocations) {
            if (resource.exists()) {
                // We will log source-control managed properties with trace and overrides with info
                if (((resource.equals(sharedPropertyOverride) || resource.equals(propertyOverride)))
                        || LOG.isTraceEnabled()) {
                    props = new Properties(props);
                    props.load(resource.getInputStream());
                    for (Entry<Object, Object> entry : props.entrySet()) {
                        if (resource.equals(sharedPropertyOverride) || resource.equals(propertyOverride)) {
                            logger.support("Read " + entry.getKey() + " from " + resource.getFilename());
                        } else {
                            LOG.trace("Read " + entry.getKey() + " from " + resource.getFilename());
                        }
                    }
                }
            } else {
                LOG.debug("Unable to locate resource: " + resource.getFilename());
            }
        }

        setLocations(allLocations.toArray(new Resource[] {}));
    }
    
    protected Resource[] createSharedPropertiesResource(String environment) throws IOException {
        String fileName = environment.toString().toLowerCase() + "-shared.properties";
        Resource[] resources = new Resource[propertyLocations.size()];
        int index = 0;
        for (Resource resource : propertyLocations) {
            resources[index] = resource.createRelative(fileName);
            index++;
        }
        return resources;
    }
    
    protected Resource[] createBroadleafResource() throws IOException {
        Resource[] resources = new Resource[blcPropertyLocations.size()];
        int index = 0;
        for (Resource resource : blcPropertyLocations) {
            resources[index] = resource.createRelative("common.properties");
            index++;
        }
        return resources;
    }

    protected Resource[] createSharedCommonResource() throws IOException {
        Resource[] resources = new Resource[propertyLocations.size()];
        int index = 0;
        for (Resource resource : propertyLocations) {
            resources[index] = resource.createRelative("common-shared.properties");
            index++;
        }
        return resources;
    }

    protected Resource[] createPropertiesResource(String environment) throws IOException {
        String fileName = environment.toString().toLowerCase() + ".properties";
        Resource[] resources = new Resource[propertyLocations.size()];
        int index = 0;
        for (Resource resource : propertyLocations) {
            resources[index] = resource.createRelative(fileName);
            index++;
        }
        return resources;
    }

    protected Resource[] createCommonResource() throws IOException {
        Resource[] resources = new Resource[propertyLocations.size()];
        int index = 0;
        for (Resource resource : propertyLocations) {
            resources[index] = resource.createRelative("common.properties");
            index++;
        }
        return resources;
    }
    
    protected Resource createSharedOverrideResource() throws IOException {
        String path = System.getProperty(SHARED_PROPERTY_OVERRIDE);
        return StringUtils.isBlank(path) ? null : new FileSystemResource(path);
    }
    
    protected Resource createOverrideResource() throws IOException {
        String path = System.getProperty(PROPERTY_OVERRIDE);
        return StringUtils.isBlank(path) ? null : new FileSystemResource(path);
    }

    public String determineEnvironment() {
        if (determinedEnvironment != null) {
            return determinedEnvironment;
        }
        determinedEnvironment = keyResolver.resolveRuntimeEnvironmentKey();

        if (determinedEnvironment == null) {
            LOG.warn("Unable to determine runtime environment, using default environment '" + defaultEnvironment + "'");
            determinedEnvironment = defaultEnvironment;
        }

        return determinedEnvironment.toLowerCase();
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        stringValueResolver = new PlaceholderResolvingStringValueResolver(props);
    }

    /**
     * Sets the default environment name, used when the runtime environment
     * cannot be determined.
     */
    public void setDefaultEnvironment(String defaultEnvironment) {
        this.defaultEnvironment = defaultEnvironment;
    }

    public String getDefaultEnvironment() {
        return defaultEnvironment;
    }

    public void setKeyResolver(RuntimeEnvironmentKeyResolver keyResolver) {
        this.keyResolver = keyResolver;
    }

    /**
     * Sets the allowed list of runtime environments
     */
    public void setEnvironments(Set<String> environments) {
        this.environments = environments;
    }

    /**
     * Sets the directory from which to read environment-specific properties
     * files; note that it must end with a '/'
     */
    public void setPropertyLocations(Set<Resource> propertyLocations) {
        this.propertyLocations = propertyLocations;
    }

    /**
     * Sets the directory from which to read environment-specific properties
     * files; note that it must end with a '/'. Note, these properties may be
     * overridden by those defined in propertyLocations and any "runtime-properties" directories
     *
     * @param overridableProperyLocations location containing overridable environment properties
     */
    public void setOverridableProperyLocations(Set<Resource> overridableProperyLocations) {
        this.overridableProperyLocations = overridableProperyLocations;
    }

    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final PropertyPlaceholderHelper helper;

        private final PropertyPlaceholderHelper.PlaceholderResolver resolver;

        public PlaceholderResolvingStringValueResolver(Properties props) {
            this.helper = new PropertyPlaceholderHelper("${", "}", ":", true);
            this.resolver = new PropertyPlaceholderConfigurerResolver(props);
        }

        public String resolveStringValue(String strVal) throws BeansException {
            String value = this.helper.replacePlaceholders(strVal, this.resolver);
            return (value.equals("") ? null : value);
        }
    }

    private class PropertyPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final Properties props;

        private PropertyPlaceholderConfigurerResolver(Properties props) {
            this.props = props;
        }

        public String resolvePlaceholder(String placeholderName) {
            return RuntimeEnvironmentPropertiesConfigurer.this.resolvePlaceholder(placeholderName, props, 1);
        }
    }

    public StringValueResolver getStringValueResolver() {
        return stringValueResolver;
    }
}
