package org.broadleafcommerce.common.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationCollection {

    /**
     * The friendly name to present to a user for this field in a GUI. If supporting i18N,
     * the friendly name may be a key to retrieve a localized friendly name using
     * the GWT support for i18N.
     *
     * @return the friendly name
     */
    String friendlyName() default "";

    /**
     * If a security level is specified, it is registered with org.broadleafcommerce.openadmin.client.security.SecurityManager
     * The SecurityManager checks the permission of the current user to
     * determine if this field should be disabled based on the specified level.
     *
     * @return the security level
     */
    String securityLevel() default "";

    /**
     * Specify if this field should be excluded from inclusion in the
     * admin presentation layer
     *
     * @return whether or not the field should be excluded
     */
    boolean excluded() default false;

    /**
     * Define the operation types for the various CRUD operations for this
     * collection. This is an advanced setting.
     *
     * @return CRUD operation type definition
     */
    OperationTypes operationTypes() default @OperationTypes;

    /**
     * Define whether or not added items for this
     * collection are acquired via search or construction.
     *
     * @return the item is acquired via lookup or construction
     */
    AddType addType();

    /**
     * Define configuration for the operation types.
     * OperationType.ENTITY and OperationType.FOREIGNKEY require annotation properties starting with "entity_"
     * OperationType.JOINSTRUCTURE require annotation properties starting with "joinStructure_"
     * OperationType.MAPSTRUCTURE require annotation properties starting with "mapStructure_"
     *
     * Review the javadocs for PersistencePerspectiveItem for field requirements, as the system under many
     * circumstances will be able to infer the appropriate values, reducing the amount of information you
     * need to specify.
     *
     * @return the configuration to support the operation types
     */
    PersistencePerspectiveItem persistencePerspectiveItem() default @PersistencePerspectiveItem;

    /**
     * The order in which this field will appear in a GUI relative to other fields from the same class
     *
     * @return the display order
     */
    int order() default 99999;

}
