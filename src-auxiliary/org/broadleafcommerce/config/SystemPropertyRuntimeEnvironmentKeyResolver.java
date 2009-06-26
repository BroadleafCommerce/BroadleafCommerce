package org.broadleafcommerce.config;

/**
 * Determines the current runtime environment by reading a system property (specified in
 * environmentKey); if no system property is specified, a (reasonable) default of "runtime.environment"
 * is used.
 *
 */
/*
 * TODO this should be supported directly in Spring 3.0.
 */
public class SystemPropertyRuntimeEnvironmentKeyResolver implements RuntimeEnvironmentKeyResolver
{
    private String m_environmentKey = "runtime.environment";

    public SystemPropertyRuntimeEnvironmentKeyResolver()
    {
        // EMPTY
    }

    public String resolveRuntimeEnvironmentKey()
    {
        return System.getProperty( m_environmentKey );
    }

    public void setEnvironmentKey( String environmentKey )
    {
        m_environmentKey = environmentKey;
    }
}
