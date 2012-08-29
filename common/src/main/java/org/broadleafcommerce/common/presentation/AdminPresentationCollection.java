package org.broadleafcommerce.common.presentation;

import org.broadleafcommerce.common.presentation.client.AddType;

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
     * Optional - field name will be used if not specified
     *
     * The friendly name to present to a user for this field in a GUI. If supporting i18N,
     * the friendly name may be a key to retrieve a localized friendly name using
     * the GWT support for i18N.
     *
     * @return the friendly name
     */
    String friendlyName() default "";

    /**
     * Optional - only required if you wish to apply security to this field
     *
     * If a security level is specified, it is registered with the SecurityManager.
     * The SecurityManager checks the permission of the current user to
     * determine if this field should be disabled based on the specified level.
     *
     * @return the security level
     */
    String securityLevel() default "";

    /**
     * Optional - fields are not excluded by default
     *
     * Specify if this field should be excluded from inclusion in the
     * admin presentation layer
     *
     * @return whether or not the field should be excluded
     */
    boolean excluded() default false;

    /**
     * Define whether or not added items for this
     * collection are acquired via search or construction.
     *
     * @return the item is acquired via lookup or construction
     */
    AddType addType();

    /**
     * Optional - only required in the absence of a "mappedBy" property
     * on the JPA annotation
     *
     * For the target entity of this collection, specify the field
     * name that refers back to the parent entity.
     *
     * For collection definitions that use the "mappedBy" property
     * of the @OneToMany and @ManyToMany annotations, this value
     * can be safely ignored as the system will be able to infer
     * the proper value from this.
     *
     * @return the parent entity referring field name
     */
    String manyToField() default "";

    /**
     * Optional - Only required if the display value field is called
     * something other than "name"
     *
     * For the target entity of this collection, specify the field
     * name of the field that will provide the display value in
     * the admin gui.
     *
     * @return the display value field name for the collection entity
     */
    String displayValueProperty() default "name";

    /**
     * Optional - only required if you want to specify ordering for this field
     *
     * The order in which this field will appear in a GUI relative to other fields from the same class
     *
     * @return the display order
     */
    int order() default 99999;

}
