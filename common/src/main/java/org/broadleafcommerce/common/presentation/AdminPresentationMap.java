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

import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.UnspecifiedBooleanType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.MapKey;

/**
 * This annotation is used to describe a persisted map structure for use in the
 * admin tool
 *
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationMap {

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
    int tabOrder() default 100;

    /**
     * <p>Optional - only required if the type for the key of this map
     * is other than java.lang.String, or if the map is not a generic
     * type from which the key type can be derived</p>
     *
     * <p>The type for the key of this map</p>
     *
     * @return The type for the key of this map
     */
    Class<?> keyClass() default void.class;
    
    /**
     * <p>Optional - only required if you wish to specify a key different from the one on the
     * {@link MapKey} annotation for the same field.
     * 
     * @return the property for the key
     */
    String mapKeyValueProperty() default "";

    /**
     * <p>Optional - only required if the key field title for this
     * map should be translated to another lang, or should be
     * something other than the constant "Key"</p>
     *
     * <p>The friendly name to present to a user for this key field title in a GUI. If supporting i18N,
     * the friendly name may be a key to retrieve a localized friendly name using
     * the GWT support for i18N.</p>
     *
     * @return the friendly name
     */
    String keyPropertyFriendlyName() default "Key";

    /**
     * <p>Optional - only required if the type for the value of this map
     * is other than java.lang.String, or if the map is not a generic
     * type from which the value type can be derived, or if there is
     * not a @ManyToMany annotation used from which a targetEntity
     * can be inferred.</p>
     *
     * <p>The type for the value of this map</p>
     *
     * @return The type for the value of this map
     */
    Class<?> valueClass() default void.class;

    /**
     * <p>Optional - only required if the value class is a
     * JPA managed type and the persisted entity should
     * be deleted upon removal from this map</p>
     *
     * <p>Whether or not a complex (JPA managed) value should
     * be deleted upon removal from this map</p>
     *
     * @return Whether or not a complex value is deleted upon map removal
     */
    boolean deleteEntityUponRemove() default false;

    /**
     * <p>Optional - only required if the value property for this map
     * is simple (Not JPA managed - e.g. java.lang.String) and if the
     * value field title for this map should be translated to another lang, or
     * should be something other than the constant "Value"</p>
     *
     * <p>The friendly name to present to a user for this value field title in a GUI. If supporting i18N,
     * the friendly name may be a key to retrieve a localized friendly name using
     * the GWT support for i18N.</p>
     *
     * @return the friendly name
     */
    String valuePropertyFriendlyName() default "Value";

    /**
     * <p>Optional - only required if the value type cannot be derived from the map
     * declaration in the JPA managed entity and the value type is complex (JPA managed entity)</p>
     *
     * <p>Whether or not the value type for the map is complex (JPA managed entity), rather than an simple
     * type (e.g. java.lang.String). This can usually be inferred from the parameterized type of the map
     * (if available), or from the targetEntity property of a @ManyToMany annotation for the map (if available).</p>
     *
     * @return Whether or not the value type for the map is complex
     */
    UnspecifiedBooleanType isSimpleValue() default UnspecifiedBooleanType.UNSPECIFIED;

    /**
     * <p>Optional - if the intended map value is actually buried inside of a modelled join entity, specify the
     * the path to that value here. For example, SkuImpl.skuMedia uses SkuMediaXrefImpl, but the intended value
     * is Media, so the toOneTargetProperty annotation param is "media"</p>
     *
     * @return the path to the intended map value field in the join entity
     */
    String toOneTargetProperty() default "";

    /**
     * <p>Optional - if the intended map value is actually buried inside of a modelled join entity, specify the
     * the path to that parent here. For example, SkuImpl.skuMedia uses SkuMediaXrefImpl, and the parent reference
     * inside SkuMediaXrefImpl is to Sku, so the toOneParentProperty annotation param is "sku"</p>
     *
     * @return the path to the parent in the join entity
     */
    String toOneParentProperty() default "";

    /**
     * <p>Optional - only required if the value type for the map is complex (JPA managed) and one of the fields
     * of the complex value provides a URL value that points to a resolvable image url.</p>
     *
     * <p>The field name of complex value that provides an image url</p>
     *
     * @return The field name of complex value that provides an image url
     */
    String mediaField() default "";

    /**
     * <p>Optional - only required when the user should select from a list of pre-defined
     * keys when adding/editing this map. Either this value, or the mapKeyOptionEntityClass
     * should be user - not both.</p>
     *
     * <p>Specify the keys available for the user to select from</p>
     *
     * @return the array of keys from which the user can select
     */
    AdminPresentationMapKey[] keys() default {};

    /**
     * <p>Optional - only required when you want to allow the user to create his/her own
     * key value, rather than select from a pre-defined list. The default is to
     * force selection from a pre-defined list.</p>
     *
     * @return whether or not the user will create their own key values.
     */
    boolean forceFreeFormKeys() default false;

    /**
     * <p>Optional - only required with a complex value class that has a bi-directional
     * association back to the parent class containing the map. This can generally
     * be inferred by the system from a "mappedBy" attribute for maps of a OneToMany
     * type. For map configurations without a mappedBy value, or if you wish to
     * explicitly set a bi-directional association field on the complex value, use
     * this property.</p>
     *
     * @return the bi-directional association field on the complex value, if any
     */
    String manyToField() default "";

    /**
     * <p>Optional - only required when the user should select from a list of database
     * persisted values for keys when adding/editing this map. Either this value, or the
     * keys parameter should be user - not both</p>
     *
     * <p>Specify the entity class that represents the table in the database that contains
     * the key values for this map</p>
     *
     * @return the entity class for the map keys
     */
    Class<?> mapKeyOptionEntityClass() default void.class;

    /**
     * <p>Optional - only required when the user should select from a list of database
     * persisted values for keys when adding/editing this map.</p>
     *
     * <p>Specify the field in the option entity class that contains the value that will
     * be shown to the user. This can be the same field as the value field. This option
     * does not support i18n out-of-the-box.</p>
     *
     * @return the display field in the entity class
     */
    String mapKeyOptionEntityDisplayField() default "";

    /**
     * <p>Optional - only required when the user should select from a list of database
     * persisted values for keys when adding/editing this map.</p>
     *
     * <p>Specify the field in the option entity class that contains the value that will
     * actually be saved for the selected key. This can be the same field as the display
     * field.</p>
     *
     * @return the value field in the entity class
     */
    String mapKeyOptionEntityValueField() default "";

    /**
     * <p>Optional - only required if you need to specially handle crud operations for this
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
    AdminPresentationOperationTypes operationTypes() default @AdminPresentationOperationTypes(addType = OperationType.MAP, fetchType = OperationType.MAP, inspectType = OperationType.MAP, removeType = OperationType.MAP, updateType = OperationType.MAP);

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
     * @return the currency property field
     */
    String currencyCodeField() default "";
    
    
    /**
     * <p>Optional - fields are eagerly fetched by default</p>
     *
     * <p>Specify true if this field should be lazily fetched</p>
     *
     * @return whether or not the field should be fetched eagerly
     */
   
    boolean lazyFetch() default true;

}
