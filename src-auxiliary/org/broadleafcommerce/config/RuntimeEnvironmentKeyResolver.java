package org.broadleafcommerce.config;

/*
 * TODO this should be supported directly in Spring 3.0.
 */
public interface RuntimeEnvironmentKeyResolver
{
    /**
     * Determine and return the runtime environment; if an implementation is unable to
     * determine the runtime environment, null can be returned to indicate this.
     */
    String resolveRuntimeEnvironmentKey();
}
