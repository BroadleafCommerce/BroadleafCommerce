package org.broadleafcommerce.common.extensibility.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specify a custom persister.
 *
 */
@java.lang.annotation.Target( { ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Persister {
    /**
     * The custom persister class.
     */
    Class<?> impl();
}
