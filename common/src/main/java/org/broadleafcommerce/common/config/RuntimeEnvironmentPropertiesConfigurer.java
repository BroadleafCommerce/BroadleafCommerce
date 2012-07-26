/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
    
    protected static Set<String> defaultEnvironments = new HashSet<String>();
    protected static Set<Resource> blcPropertyLocations = new HashSet<Resource>();
    protected static Set<Resource> defaultPropertyLocations = new HashSet<Resource>();

    
    static {
    	defaultEnvironments.add("production");
    	defaultEnvironments.add("staging");
    	defaultEnvironments.add("integrationqa");
    	defaultEnvironments.add("integrationdev");
    	defaultEnvironments.add("development");
    	defaultEnvironments.add("local");
    	
		blcPropertyLocations.add(new ClassPathResource("config/bc/admin/"));
		blcPropertyLocations.add(new ClassPathResource("config/bc/"));
		blcPropertyLocations.add(new ClassPathResource("config/bc/cms/"));
		
		defaultPropertyLocations.add(new ClassPathResource("runtime-properties/"));
    }

    protected String defaultEnvironment = "development";
    protected RuntimeEnvironmentKeyResolver keyResolver;
    protected Set<String> environments = Collections.emptySet();
    protected Set<Resource> propertyLocations;
    protected StringValueResolver stringValueResolver;

    public RuntimeEnvironmentPropertiesConfigurer() {
    	super();
    	setIgnoreUnresolvablePlaceholders(true); // This default will get overriden by user options if present
    }

    public void afterPropertiesSet() throws IOException {
    	// If no environment override has been specified, used the default environments
    	if (environments == null || environments.size() == 0) {
    		environments = defaultEnvironments;
    	}
    	
    	// Prepend the default property locations to the specified property locations (if any)
		Set<Resource> combinedLocations = new HashSet<Resource>();
		combinedLocations.addAll(defaultPropertyLocations);
    	if (propertyLocations != null && propertyLocations.size() > 0) {
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
        
        Resource[] blcPropertiesLocation = createBroadleafResource();
        
        Resource[] sharedPropertiesLocation = createSharedPropertiesResource(environment);
        Resource[] sharedCommonLocation = createSharedCommonResource();

        Resource[] propertiesLocation = createPropertiesResource(environment);
        Resource[] commonLocation = createCommonResource();
        
        ArrayList<Resource> allLocations = new ArrayList<Resource>();
        
        /* Process configuration in the following order (later files override earlier files
         * common-shared.properties
         * [environment]-shared.properties
         * common.properties
         * [environment].properties */
        
        for (Resource resource : blcPropertiesLocation) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }
        
        for (Resource resource : sharedCommonLocation) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }

        for (Resource resource : sharedPropertiesLocation) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }

        for (Resource resource : commonLocation) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }

        for (Resource resource : propertiesLocation) {
            if (resource.exists()) {
                allLocations.add(resource);
            }
        }
        
        if (LOG.isDebugEnabled()) {
	        Properties props = new Properties();
	        for (Resource resource : allLocations) {
	            if (resource.exists()) {
	                props = new Properties(props);
	                props.load(resource.getInputStream());
	                for (Entry<Object, Object> entry : props.entrySet()) {
	                	LOG.debug("Read " + entry.getKey() + " as " + entry.getValue());
	                }
	            } else {
	                LOG.debug("Unable to locate resource: " + resource.getFilename());
	            }
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

    public String determineEnvironment() {
        String environment = keyResolver.resolveRuntimeEnvironmentKey();

        if (environment == null) {
            LOG.warn("Unable to determine runtime environment, using default environment '" + defaultEnvironment + "'");
            return defaultEnvironment;
        }

        return environment.toLowerCase();
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
