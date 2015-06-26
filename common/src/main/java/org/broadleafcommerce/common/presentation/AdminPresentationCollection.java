/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.presentation;

import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to describe a simple persistent collection
 * for use by the admin tool.
 *
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationCollection {

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
     * <p>Optional - only required if you want to make the field immutable</p>
     *
     * <p>Explicityly specify whether or not this field is mutable.</p>
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
    AddMethodType addType() default AddMethodType.PERSIST;

    /**
     * <p>Optional - only required in the absence of a "mappedBy" property
     * on the JPA annotation</p>
     *
     * <p>For the target entity of this collection, specify the field
     * name that refers back to the parent entity.</p>
     *
     * <p>For collection definitions that use the "mappedBy" property
     * of the @OneToMany and @ManyToMany annotations, this value
     * can be safely ignored as the system will be able to infer
     * the proper value from this.</p>
     *
     * @return the parent entity referring field name
     */
    String manyToField() default "";

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
     * The default tab will render with an order of 100.
     * 
     * @return the order for this tab
     */
    @Deprecated
    int tabOrder() default 100;

    /**
     * <p>Optional - only required if you need to specially handle CRUD operations for this
     * specific collection on the server</p>
     *
     * <p>Custom string values that will be passed to the server during CRUD operations on this
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
    AdminPresentationOperationTypes operationTypes() default @AdminPresentationOperationTypes(addType = OperationType.BASIC, fetchType = OperationType.BASIC, inspectType = OperationType.BASIC, removeType = OperationType.BASIC, updateType = OperationType.BASIC);

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
     * Optional - If you have FieldType set to SupportedFieldType.MONEY,      *
     * then you can specify a money currency property field.
     *
     *
     * @return the currency property field
     */
    String currencyCodeField() default "";

    /**
     * <p>Optional - only required if the collection
     * a field used for sorting</p>  Enables the reorder functionality in list grids.
     * 
     *
     * @return the sort field in the target entity
     */
    String sortProperty() default "";

    /**
     * <p>Optional - only required if the sort order should be
     * descending</p>
     *
     * <p>This is the sort direction for the targets</p>
     *
     * @return the sort direction
     */
    boolean sortAscending() default true;

    /**
     * Used to map the collection to a group defined in AdminPresentationClass using AdminGroupPresentation.
     *
     * If the group cannot be found in AdminPresentationClass, then the tab specified in AdminPresentationCollection
     * is used to map the collection to a tab defined in AdminPresentationClass using AdminTabPresentation.
     * If the tab cannot be found, then the collection will be placed in a tab created using the information
     * specified in AdminPresentationCollection.
     *
     * Optional - only required if you want the field to appear under a specific group
     *
     * Specify a GUI group for this collection
     *
     * @return the group for this collection
     */
    String group() default "";
}
