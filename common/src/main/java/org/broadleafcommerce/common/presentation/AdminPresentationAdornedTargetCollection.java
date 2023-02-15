/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.presentation;

import org.broadleafcommerce.common.presentation.client.AdornedTargetAddMethodType;
import org.broadleafcommerce.common.presentation.client.OperationType;

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
     * <p>Optional - field name will be used if not specified</p>
     *
     * <p>The friendly name to present to a user for this field in a GUI. If supporting i18N,
     * the friendly name may be a key to retrieve a localized friendly name using
     * the GWT support for i18N.</p>
     *
     * @return the friendly name
     */
    String friendlyName() default "";

    /**
     * <p>Optional - only required if you wish to apply security to this field</p>
     *
     * <p>If a security level is specified, it is registered with the SecurityManager.
     * The SecurityManager checks the permission of the current user to
     * determine if this field should be disabled based on the specified level.</p>
     *
     * @return the security level
     */
    String securityLevel() default "";

    /**
     * <p>Optional - fields are not excluded by default</p>
     *
     * <p>Specify if this field should be excluded from inclusion in the
     * admin presentation layer</p>
     *
     * @return whether or not the field should be excluded
     */
    boolean excluded() default false;
    
    /**
     * <p>Optional - propertyName , only required if you want hide the field based on this property's value</p>
     *
     * <p>If the property is defined and found to be set to false, in the AppConfiguraionService, then this field will be excluded in the
     * admin presentation layer</p>
     *
     * @return name of the property 
     */
    String showIfProperty() default "";

    /**
     * <p>Optional - only required if you want hide the field based on the supplied field's value</p>
     *
     * <p>If the property is defined and found to be equal to one of the values provided
     * then this field will be included in the admin presentation layer</p>
     *
     * @return configuration of the field values
     */
    FieldValueConfiguration[] showIfFieldEquals() default {};

    /**
     * Optional - only required if you want to make the field immutable
     *
     * Explicityly specify whether or not this field is mutable.
     *
     * @return whether or not this field is read only
     */
    boolean readOnly() default false;

    /**
     * <p>Optional - only required if you want to make the field ignore caching</p>
     *
     * <p>Explicitly specify whether or not this field will use server-side
     * caching during inspection</p>
     *
     * @return whether or not this field uses caching
     */
    boolean useServerSideInspectionCache() default true;

    /**
     * <p>Optional - only required in the absence of a "mappedBy" property
     * on the JPA annotation</p>
     *
     * <p>This is the field in the adorned target entity that refers
     * back to the parent entity</p>
     *
     * @return the field that refers back to the parent entity
     */
    String parentObjectProperty() default "";

    /**
     * <p>Optional - only required if the primary key property of the
     * parent entity is called something other than "id"</p>
     *
     * <p>This is the field in the parent entity that represents
     * its primary key</p>
     *
     * @return primary key field of the parent entity
     */
    String parentObjectIdProperty() default "id";

    /**
     * <p>This is the field in the adorned target entity that refers
     * to the target entity</p>
     *
     * @return target entity field of the adorned target
     */
    String targetObjectProperty() default "";

    /**
     * <p>Optional - only required if the adorned target has fields
     * (other than the sort property) that should be populated
     * by the user</p>
     *
     * <p>List of fields to include in the add/update form
     * for the adorned target entity.</p>
     *
     * @return user populated fields on the adorned target
     */
    String[] maintainedAdornedTargetFields() default {};

    /**
     * <p>Optional - only required when it is desirable to override
     * the property prominence settings from the adorned target and the
     * target object</p>
     *
     * <p>List of fields visible in the adorned target grid UI in the
     * admin tool. Fields are referenced relative to the adorned target
     * entity, or the target entity. For example, in CrossSaleProductImpl,
     * to show the product name and promotionMesssage fields, the
     * gridVisibleFields value would be : {"defaultSku.name", "promotionMessage"}</p>
     *
     *
     * @return List of fields visible in the adorned target grid UI in the admin tool
     */
    String[] gridVisibleFields() default {};

    /**
     * <p>Optional - only required if the primary key property of the
     * target entity is called something other than "id"</p>
     *
     * <p>This is the field in the target entity that represents
     * its primary key</p>
     * 
     * <p>Note that this should just be the property name, not the path to the property.
     * For example, if the target object is CountryImpl, then the value for the 
     * targetObjectIdProperty should just be "abbreviation".
     *
     * @return primary key field of the target entity
     */
    String targetObjectIdProperty() default "id";

    /**
     * <p>Optional - only required if there is an entity that is responsible
     * for modeling the join table for this adorned collection.</p>
     * 
     * <p>For example, consider the scenario that a product has many possible 
     * parent categories. Also consider that you might want to sort the parent
     * categories in a specific way. The join entity in this case would hold a
     * link to both a category and a product as well as a sequence field. This
     * property provides the ability to specify that mapping.</p>
     * 
     * @return the join entity class (if any)
     */
    String joinEntityClass() default "";

    /**
     * <p>Optional - only required if the adorned target has
     * a field used for sorting</p>
     *
     * <p>This is the field by which the adorned targets are sorted</p>
     *
     * @return the sort field in the adorned target entity
     */
    String sortProperty() default "";

    /**
     * <p>Optional - only required if the sort order should be
     * descending</p>
     *
     * <p>This is the sort direction for the adorned targets</p>
     *
     * @return the sort direction
     */
    boolean sortAscending() default true;

    /**
     * <p>Optional - only required if the system should not query
     * the user for the adorned property values.</p>
     *
     * <p>Defines whether or not the system should prompt the user
     * for the adorned property values (if any) after searching
     * for the target entity. This is an advanced feature and is
     * rarely used.</p>
     *
     * @return whether to ignore the adorned properties
     */
    boolean ignoreAdornedProperties() default false;

    /**
     * <p>Optional - only required if you want to specify ordering for this field</p>
     *
     * <p>The order in which this field will appear in a GUI relative to other collections from the same class</p>
     *
     * @return the display order
     */
    int order() default 99999;

    /**
     * Optional - only required if you want the field to appear under a different tab
     * 
     * Specify a GUI tab for this field
     * 
     * @return the tab for this field
     */
    @Deprecated
    String tab() default "General";

    /**
     * Optional - only required if you want to order the appearance of the tabs in the UI
     * 
     * Specify an order for this tab. Tabs will be sorted int he resulting form in 
     * ascending order based on this parameter.
     * 
     * The default tab will render with an order of 99999.
     * 
     * @return the order for this tab
     */
    @Deprecated
    int tabOrder() default 99999;

    /**
     * <p>Optional - only required if you need to specially handle crud operations for this
     * specific collection on the server</p>
     *
     * <p>Custom string values that will be passed to the server during CRUB operations on this
     * collection. These criteria values can be detected in a custom persistence handler
     * (@CustomPersistenceHandler) in order to engage special handling through custom server
     * side code for this collection.</p>
     *
     * @return the custom string array to pass to the server during CRUD operations
     */
    String[] customCriteria() default {};

    /**
     * <p>Optional - only required if a special operation type is required for a CRUD operation. This
     * setting is not normally changed and is an advanced setting</p>
     *
     * <p>The operation type for a CRUD operation</p>
     *
     * @return the operation type
     */
    AdminPresentationOperationTypes operationTypes() default @AdminPresentationOperationTypes(addType = OperationType.ADORNEDTARGETLIST, fetchType = OperationType.ADORNEDTARGETLIST, inspectType = OperationType.BASIC, removeType = OperationType.ADORNEDTARGETLIST, updateType = OperationType.ADORNEDTARGETLIST);

    /**
     * Optional - If you have FieldType set to SupportedFieldType.MONEY,      *
     * then you can specify a money currency property field.
     *
     *
     * @return the currency property field
     */
    String currencyCodeField() default "";
    
    /**
     * <p>Optional - fields are eagerly fetched by default</p>
     *
     * <p>Specify true if this field should be lazily fetched</p>
     *
     * @return whether or not the field should be fetched
     */
    boolean lazyFetch() default true;

    /**
     * <p>Optional - fields are manually fetched by default</p>
     *
     * <p>Specify true if this field should be fetched manually</p>
     *
     * @return whether or not the field should be fetched manually
     */
    boolean manualFetch() default false;

    /**
     * Used to map the collection to a group defined in AdminPresentationClass using AdminGroupPresentation.
     *
     * If the group cannot be found in AdminPresentationClass, then the tab specified in
     * AdminPresentationAdornedTargetCollection is used to map the collection to a tab defined in AdminPresentationClass
     * using AdminTabPresentation. If the tab cannot be found, then the collection will be placed in a tab created using
     * the information specified in AdminPresentationAdornedTargetCollection.
     *
     * Optional - only required if you want the field to appear under a specific group
     *
     * Specify a GUI group for this collection
     *
     * @return the group for this collection
     */
    String group() default "";

    /**
     * <p>Optional - only required if you want to lookup an item
     * for this association, rather than creating a new instance of the
     * target item. Note - if the type is changed to LOOKUP, and you
     * do not wish for the lookup entity to be deleted during an admin
     * collection item removal operation, you should specify a removeType
     * of OperationType.NONDESTRUCTIVEREMOVE in {@link #operationTypes()}
     * param for this annotation.</p>
     *
     * <p>If the type is set to LOOKUP_FOR_UPDATE, the system will trigger
     * an update call on the target entity instead of an add. This is typically
     * used when the target entity also has a to-one lookup to this field.</p>
     *
     * <p>Define whether or not added items for this
     * collection are acquired via search or construction.</p>
     *
     * @return the item is acquired via lookup or construction
     */
    AdornedTargetAddMethodType addType() default AdornedTargetAddMethodType.LOOKUP;

    /**
     * <p>Optional - only required when using the "SELECTIZE_LOOKUP" addType for a collection </p>
     *
     * <p>Field visible in the selectize collection UI in the admin tool.
     * Fields are referenced relative to the the target entity. For example, in CrossSaleProductImpl,
     * to show the product name field, the selectizeVisibleField value would be : "name"</p>
     *
     * @return Field visible in the selectize collection UI in the admin tool
     */
    String selectizeVisibleField() default "";
}
