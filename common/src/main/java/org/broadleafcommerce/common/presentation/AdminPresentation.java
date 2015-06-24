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

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 * @author jfischer
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentation {
    
    /**
     * <p>Optional - only required if you want to display a friendly name to the user</p>
     *
     * <p><The friendly name to present to a user for this field in a GUI. If supporting i18N,
     * the friendly name may be a key to retrieve a localized friendly name using
     * the GWT support for i18N.</p>
     *
     * @return the friendly name
     */
    String friendlyName() default "";
    
    /**
     * Optional - only required if you want to restrict this field
     *
     * If a security level is specified, it is registered with org.broadleafcommerce.openadmin.client.security.SecurityManager
     * The SecurityManager checks the permission of the current user to 
     * determine if this field should be disabled based on the specified level.
     * 
     * @return the security level
     * @deprecated not supported
     */
    String securityLevel() default "";
    
    /**
     * Optional - only required if you want to order the appearance of this field in the UI
     *
     * The order in which this field will appear in a GUI relative to other fields from the same class
     * 
     * @return the display order
     */
    int order() default 99999;
    
    /**
     * Optional - required only if you want to order the appearance of this field as it relates to other fields in a grid.
     * Note that this field will only be relevant if {@link #prominent()} is also set to true.
     * 
     * @return
     */
    int gridOrder() default 9999;

    /**
     * Optional - only required if you want to restrict the visibility of this field in the admin tool
     *
     * Describes how the field is shown in admin GUI.
     *
     * @return whether or not to hide the form field.
     */
    VisibilityEnum visibility() default VisibilityEnum.VISIBLE_ALL;
    
    /**
     * Optional - only required if you want to explicitly specify the field type. This
     * value is normally inferred by the system based on the field type in the entity class.
     *
     * Explicity specify the type the GUI should consider this field
     * Specifying UNKNOWN will cause the system to make its best guess
     * 
     * @return the field type
     */
    SupportedFieldType fieldType() default SupportedFieldType.UNKNOWN;
    
    /**
     * Optional - only required if you want to specify a grouping for this field
     *
     * Specify a GUI grouping for this field
     * Fields in the same group will be visually grouped together in the GUI
     * <br />
     * <br />
     * Note: for support I18N, this can also be a key to retrieve a localized String
     * 
     * @return the group for this field
     */
    String group() default "General";
    
    /**
     * Optional - only required if you want to order the appearance of groups in the UI
     *
     * Specify an order for this group. Groups will be sorted in the resulting
     * form in ascending order based on this parameter.
     * 
     * @return the order for this group
     */
    int groupOrder() default 99999;

    /**
     * Optional - only required if you want to control the initial collapsed state of the group
     *
     * Specify whether a group is collapsed by default in the admin UI.
     *
     * @return whether or not the group is collapsed by default
     * @deprecated not supported
     */
    boolean groupCollapsed() default false;
    
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
     * Optional - only required if you want to give the user extra room to enter a value
     * for this field in the UI
     *
     * If the field is a string, specify that the GUI
     * provide a text area
     * 
     * @return is a text area field
     */
    boolean largeEntry() default false;
    
    /**
     * Optional - only required if you want this field to appear as one of the default
     * columns in a grid in the admin tool
     *
     * Provide a hint to the GUI about the prominence of this field.
     * For example, prominent fields will show up as a column in any
     * list grid in the admin that displays this entity.
     * 
     * @return whether or not this is a prominent field
     */
    boolean prominent() default false;
    
    /**
     * Optional - only required if you want to explicitly control column width
     * for this field in a grid in the admin tool
     *
     * Specify the column space this field will occupy in grid widgets.
     * This value can be an absolute integer or a percentage. A value
     * of "*" will make this field use up equally distributed space.
     * 
     * @return the space utilized in grids for this field
     */
    String columnWidth() default "*";
    
    /**
     * Optional - only required for BROADLEAF_ENUMERATION field types
     *
     * For fields with a SupportedFieldType of BROADLEAF_ENUMERATION,
     * you must specify the fully qualified class name of the Broadleaf Enumeration here.
     * 
     * @return Broadleaf enumeration class name
     */
    String broadleafEnumeration() default "";
    
    /**
     * Optional - only required if you want to make the field immutable
     *
     * Explicityly specify whether or not this field is mutable.
     * 
     * @return whether or not this field is read only
     */
    boolean readOnly() default false;
    
    /**
     * Optional - only required if you want to provide validation for this field
     *
     * Specify the validation to use for this field in the admin, if any
     * 
     * @return the configuration for the validation
     */
    ValidationConfiguration[] validationConfigurations() default {};

    /**
     * Optional - only required if you want to explicitly make a field required. This
     * setting is normally inferred by the JPA annotations on the field.
     *
     * Specify whether you would like the admin to require this field,
     * even if it is not required by the ORM.
     *
     * @return the required override enumeration
     */
    RequiredOverride requiredOverride() default RequiredOverride.IGNORED;

    /**
     * Optional - only required if you want to explicitly exclude this field from
     * dynamic management by the admin tool
     *
     * Specify if this field should be excluded from inclusion in the
     * admin presentation layer
     *
     * @return whether or not the field should be excluded
     */
    boolean excluded() default false;
    
    /**
     * Optional - only required if you want to provide a tooltip for the field
     *
     * Helpful tooltip to be displayed when the admin user hovers over the field.
     * This can be localized by providing a key which will use the GWT
     * support for i18N.
     * 
     */
    String tooltip() default "";
    
    /**
     * Optional - only required if you want to provide help text for this field
     *
     * On the form for this entity, this will show a question
     * mark icon next to the field. When the user clicks on the icon, whatever
     * HTML that is specified in this helpText is shown in a popup.
     * 
     * For i18n support, this can also be a key to a localized version of the text
     * 
     * Reference implementation: http://www.smartclient.com/smartgwt/showcase/#form_details_hints
     * 
     */
    String helpText() default "";
    
    /**
     * Optional - only required if you want to provide a hint for this field
     *
     * Text to display immediately to the right of a form field. For instance, if the user needs
     * to put in a date, the hint could be the format the date needs to be in like 'MM/YYYY'.
     * 
     * For i18n support, this can also be a key to a localized version of the text
     * 
     * Reference implementation: http://www.smartclient.com/smartgwt/showcase/#form_details_hints
     */
    String hint() default "";

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
     * Optional - If you have FieldType set to SupportedFieldType.MONEY,
     * then you can specify a money currency property field.
     * 
     * @return the currency property field
     */
    String currencyCodeField() default "";

    /**
     * <p>Optional - only required if the fieldType is SupportedFieldType.RULE_SIMPLE or SupportedFieldType.RULE_COMPLEX</p>
     *
     * <p>Identifies the type for which this rule builder is targeted. See <tt>RuleIdentifier</tt> for a list of
     * identifier types supported out-of-the-box. Note - one of the main uses of this value is to help identify
     * the proper <tt>RuleBuilderService</tt> instance to generate the correct field value options for this rule builder.</p>
     *
     * @return The identifier value that denotes what type of rule builder this is - especially influences the fields that are available in the UI
     */
    String ruleIdentifier() default "";
    
    /**
     * <p>Optional - marks this field as being translatable, which will render the translations modal in the admin UI</p>
     * 
     * @return whether or not this field is translatable
     */
    boolean translatable() default false;

    /**
     * <p>Optional - only required if you want to display a default value to the user when adding a new entity</p>
     *
     * <p>The default value to present to a user for this field when adding a new entity.</p>
     *
     * <p>Default values on <code>Boolean</code> require {@code "true"} or {@code "false"}.</p>
     * <p>Default values on <code>Date</code> support the string {@code "today"} and strings with the
     * format of <b>{@code yyyy.MM.dd HH:mm:ss}</b> (e.g. {@code "2020.02.05 22:11:05"}).</p>
     *
     * @return the defaultValue set for the field.
     */
    String defaultValue() default "";
}
