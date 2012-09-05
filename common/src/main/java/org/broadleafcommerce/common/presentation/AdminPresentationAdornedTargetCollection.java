package org.broadleafcommerce.common.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adorned target collections are a variant of the basic collection type (@see AdminPresentationCollection).
 * This type of collection concept comes into play when you want to represent a "ToMany" association, but
 * you also want to capture some additional data around the association. CrossSaleProductImpl is an example of
 * this concept. CrossSaleProductImpl not only contains a product reference, but sequence and
 * promotional message fields as well. We want the admin user to choose the desired product for the association
 * and also specify the order and promotional message information to complete the interaction.
 * The Adorned target concept embodied in this annotation makes this possible.
 *
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationAdornedTargetCollection {

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
     * Optional - only required if the collection grid UI
     * should be in read only mode
     *
     * Whether or not the collection can be edited
     *
     * @return Whether or not the collection can be edited
     */
    boolean mutable() default true;

    /**
     * Optional - only required in the absence of a "mappedBy" property
     * on the JPA annotation
     *
     * This is the field in the adorned target entity that refers
     * back to the parent entity
     *
     * @return the field that refers back to the parent entity
     */
    String parentObjectProperty() default "";

    /**
     * Optional - only required if the primary key property of the
     * parent entity is called something other than "id"
     *
     * This is the field in the parent entity that represents
     * its primary key
     *
     * @return primary key field of the parent entity
     */
    String parentObjectIdProperty() default "id";

    /**
     * This is the field in the adorned target entity that refers
     * to the target entity
     *
     * @return target entity field of the adorned target
     */
    String targetObjectProperty();

    /**
     * Optional - only required if the adorned target has fields
     * (other than the sort property) that should be populated
     * by the user
     *
     * List of fields to include in the add/update form
     * for the adorned target entity.
     *
     * @return user populated fields on the adorned target
     */
    String[] maintainedAdornedTargetFields() default {};

    /**
     * Optional - only required when it is desirable to override
     * the property prominence settings from the adorned target and the
     * target object
     *
     * List of fields visible in the adorned target grid UI in the
     * admin tool. Fields are referenced relative to the adorned target
     * entity, or the target entity. For example, in CrossSaleProductImpl,
     * to show the product name and promotionMesssage fields, the
     * gridVisibleFields value would be : {"defaultSku.name", "promotionMessage"}
     *
     *
     * @return List of fields visible in the adorned target grid UI in the admin tool
     */
    String[] gridVisibleFields() default {};

    /**
     * Optional - only required if the primary key property of the
     * target entity is called something other than "id"
     *
     * This is the field in the target entity that represents
     * its primary key
     *
     * @return primary key field of the target entity
     */
    String targetObjectIdProperty() default "id";

    /**
     * Optional - only required if the adorned target has
     * a field used for sorting
     *
     * This is the field by which the adorned targets are sorted
     *
     * @return the sort field in the adorned target entity
     */
    String sortProperty() default "";

    /**
     * Optional - only required if the sort order should be
     * descending
     *
     * This is the sort direction for the adorned targets
     *
     * @return the sort direction
     */
    boolean sortAscending() default true;

    /**
     * Optional - only required if the system should not query
     * the user for the adorned property values.
     *
     * Defines whether or not the system should prompt the user
     * for the adorned property values (if any) after searching
     * for the target entity. This is an advanced feature and is
     * rarely used.
     *
     * @return whether to ignore the adorned properties
     */
    boolean ignoreAdornedProperties() default false;

    /**
     * Optional - only required if you want to specify ordering for this field
     *
     * The order in which this field will appear in a GUI relative to other collections from the same class
     *
     * @return the display order
     */
    int order() default 99999;

    /**
     * Optional - only required if you want the resulting collection grid element to
     * appear somewhere other than below the main detail form
     *
     * Specify a UI element Id to which the collection grid should be added. This is useful
     * if, for example, you want the resulting collection grid to appear in another tab, or
     * some other location in the admin tool UI.
     *
     * @return UI element Id to which the collection grid should be added
     */
    String targetUIElementId() default "";

    /**
     * Optional - unique name for the backing datasource. If unspecified, the datasource
     * name will be the JPA entity field name with "AdvancedCollectionDS" appended to the end.
     *
     * The datasource can be retrieved programatically in admin code via
     * PresenterSequenceSetupManager.getDataSource(..)
     *
     * @return unique name for the backing datasource
     */
    String dataSourceName() default "";

    /**
     * Optional - only required if you need to specially handle crud operations for this
     * specific collection on the server
     *
     * Custom string values that will be passed to the server during CRUB operations on this
     * collection. These criteria values can be detected in a custom persistence handler
     * (@CustomPersistenceHandler) in order to engage special handling through custom server
     * side code for this collection.
     *
     * @return the custom string array to pass to the server during CRUD operations
     */
    String[] customCriteria() default {};

}
