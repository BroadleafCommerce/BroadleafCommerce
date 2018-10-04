package org.broadleafcommerce.config;

/**
 * Determines the current runtime environment by reading a system property
 * (specified in environmentKey); if no system property is specified, a
 * (reasonable) default of "runtime.environment" is used.
 */
public class SystemPropertyRuntimeEnvironmentKeyResolver implements RuntimeEnvironmentKeyResolver {

    protected String environmentKey = "runtime.environment";

    public SystemPropertyRuntimeEnvironmentKeyResolver() {
        // EMPTY
    }

    public String resolveRuntimeEnvironmentKey() {
        return System.getProperty(environmentKey);
    }

    public void setEnvironmentKey(String environmentKey) {
        this.environmentKey = environmentKey;
    }
}
