package org.broadleafcommerce.config;

public interface RuntimeEnvironmentKeyResolver {
    /**
     * Determine and return the runtime environment; if an implementation is
     * unable to determine the runtime environment, null can be returned to
     * indicate this.
     */
    String resolveRuntimeEnvironmentKey();
}
