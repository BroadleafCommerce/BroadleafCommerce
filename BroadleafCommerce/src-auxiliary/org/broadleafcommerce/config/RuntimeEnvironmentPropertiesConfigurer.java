package org.broadleafcommerce.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;

/**
 *
 * A property resource configurer that chooses the property file at runtime based on the
 * runtime environment.
 * <p>
 * Used for choosing properties files based on the current runtime environment, allowing for movement
 * of the same application between multiple runtime environments without rebuilding.
 * <p>
 * The property replacement semantics of this implementation are identical to PropertyPlaceholderConfigurer, from
 * which this class inherits.
 * <code>
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
 * </code>
 * </pre>
 * The keys of the environment specific properties files are compared to ensure that each property
 * file defines the complete set of keys, in order to avoid environment-specific failures.
 * <p>
 * An optional RuntimeEnvironmentKeyResolver implementation can be provided, allowing for
 * customization of how the runtime environment is determined.  If no implementation is provided,
 * a default of SystemPropertyRuntimeEnvironmentKeyResolver is used (which uses the system property
 * 'runtime.environment')
 *
 * @author <a href="mailto:chris.lee.9@gmail.com">Chris Lee</a>
 *
 */
public class RuntimeEnvironmentPropertiesConfigurer extends PropertyPlaceholderConfigurer implements
InitializingBean
{
    private Log m_log = LogFactory.getLog( getClass() );

    private String m_defaultEnvironment;

    private RuntimeEnvironmentKeyResolver m_keyResolver;

    private Set<String> m_environments = Collections.emptySet();

    private Set<Resource> m_propertyLocations;

    public RuntimeEnvironmentPropertiesConfigurer()
    {
        // EMPTY
    }

    public void afterPropertiesSet() throws IOException
    {
        if( !m_environments.contains( m_defaultEnvironment ) )
        {
            throw new AssertionError( "Default environment '" + m_defaultEnvironment
                    + "' not listed in environment list" );
        }

        if( m_keyResolver == null )
        {
            m_keyResolver = new SystemPropertyRuntimeEnvironmentKeyResolver();
        }

        String environment = determineEnvironment();

        Resource[] propertiesLocation = createPropertiesResource( environment );
        Resource[] commonLocation = createCommonResource();
        Resource[] allLocations = new Resource[propertiesLocation.length + commonLocation.length];
        System.arraycopy(commonLocation, 0, allLocations, 0, commonLocation.length);
        System.arraycopy(propertiesLocation, 0, allLocations, commonLocation.length, propertiesLocation.length);
        setLocations(allLocations);

        validateProperties();
    }

    private boolean compareProperties( Properties props1, Properties props2 ) throws IOException
    {
        Set<Object> outerKeys = props1.keySet();

        boolean missingKeys = false;
        for( Object keyObj : outerKeys )
        {
            String key = (String)keyObj;
            if( !props2.containsKey( key ) )
            {
                missingKeys = true;
                getLog().error(
                        "Property file mismatch: " + key + " missing");
            }
        }

        return missingKeys;
    }

    private Resource[] createPropertiesResource( String environment ) throws IOException
    {
        String fileName = environment.toString().toLowerCase() + ".properties";
        Resource[] resources = new Resource[m_propertyLocations.size()];
        int index = 0;
        for (Resource resource : m_propertyLocations) {
            resources[index] = resource.createRelative(fileName);
            index++;
        }
        return resources;
    }

    private Resource[] createCommonResource() throws IOException {
        Resource[] resources = new Resource[m_propertyLocations.size()];
        int index = 0;
        for (Resource resource : m_propertyLocations) {
            resources[index] = resource.createRelative("common.properties");
            index++;
        }
        return resources;
    }

    private String determineEnvironment()
    {
        String environment = m_keyResolver.resolveRuntimeEnvironmentKey();

        if( environment == null )
        {
            getLog().warn(
                    "Unable to determine runtime environment, using default environment '"
                    + m_defaultEnvironment + "'" );
            return m_defaultEnvironment;
        }

        return environment.toLowerCase();
    }

    protected final Log getLog()
    {
        return m_log;
    }

    /**
     * Sets the allowed list of runtime environments
     */
    public void setEnvironments( Set<String> environments )
    {
        m_environments = environments;
    }

    /**
     * Sets the directory from which to read environment-specific properties files; note
     * that it must end with a '/'
     */
    public void setPropertyLocations( Set<Resource> propertyLocations )
    {
        m_propertyLocations = propertyLocations;
    }

    private void validateProperties() throws IOException
    {
        boolean missingKeys = false;
        for( String envOuter : m_environments )
        {
            for( String envInner : m_environments )
            {
                if( !envOuter.equals( envInner ) )
                {
                    Properties resource1 = mergeProperties(createPropertiesResource( envOuter ));

                    Properties resource2 = mergeProperties(createPropertiesResource( envInner ));

                    missingKeys |= compareProperties( resource1, resource2 );
                }
            }
        }

        if( missingKeys )
        {
            throw new AssertionError(
            "Missing runtime properties keys (log entries above have details)" );
        }
    }

    private Properties mergeProperties(Resource[] locations) throws IOException {
        Properties props = new Properties();
        for (Resource resource : locations) {
            props = new Properties(props);
            props.load(resource.getInputStream());
        }
        return props;
    }

    /**
     * Sets the default environment name, used when the runtime environment
     * cannot be determined.
     */
    public void setDefaultEnvironment( String defaultEnvironment )
    {
        m_defaultEnvironment = defaultEnvironment;
    }

    public void setKeyResolver( RuntimeEnvironmentKeyResolver keyResolver )
    {
        m_keyResolver = keyResolver;
    }
}
