/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;


/**
 * 
 * @author jfischer
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentation {
	
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
	 * The order in which this field will appear in a GUI relative to other fields from the same class
	 * 
	 * @return the display order
	 */
	int order() default 99999;

    /**
     * Describes how the field is shown in admin GUI.
     *
     * @return whether or not to hide the form field.
     */
    VisibilityEnum visibility() default VisibilityEnum.VISIBLE_ALL;
	
	/**
	 * Explicity specify the type the GUI should consider this field
	 * Specifying UNKNOWN will cause the system to make its best guess
	 * 
	 * @return the field type
	 */
	SupportedFieldType fieldType() default SupportedFieldType.UNKNOWN;
	
	/**
	 * Specify a GUI grouping for this field
	 * Fields in the same group will be visually grouped together in the GUI
	 * 
	 * @return the group for this field
	 */
	String group() default "General";
	
	/**
	 * Specify an order for this group. Groups will be sorted in the resulting
	 * form in ascending order based on this parameter.
	 * 
	 * @return the order for this group
	 */
	int groupOrder() default 99999;

    /**
     * Specify whether a group is collapsed by default in the admin UI.
     *
     * @return whether or not the group is collapsed by default
     */
    boolean groupCollapsed() default false;
	
	/**
	 * If the field is a string, specify that the GUI
	 * provide a text area
	 * 
	 * @return is a text area field
	 */
	boolean largeEntry() default false;
	
	/**
	 * Provide a hint to the GUI about the prominence of this field.
	 * For example, prominent fields will show up as a column in any
	 * list grid in the admin that displays this entity.
	 * 
	 * @return whether or not this is a prominent field
	 */
	boolean prominent() default false;
	
	/**
	 * Specify the column space this field will occupy in grid widgets.
	 * This value can be an absolute integer or a percentage. A value
	 * of "*" will make this field use up equally distributed space.
	 * 
	 * @return the space utilized in grids for this field
	 */
	String columnWidth() default "*";
	
	/**
	 * For fields with a SupportedFieldType of BROADLEAF_ENUMERATION,
	 * you must specify the fully qualified class name of the Broadleaf Enumeration here.
	 * 
	 * @return Broadleaf enumeration class name
	 */
	String broadleafEnumeration() default "";
	
	/**
	 * Explicityly specify whether or not this field is mutable.
	 * 
	 * @return whether or not this field is read only
	 */
	boolean readOnly() default false;
	
	/**
	 * Specify the validation to use for this field in the admin, if any
	 * 
	 * @return the configuration for the validation
	 */
	ValidationConfiguration[] validationConfigurations() default {};


    /**
     * Specify whether you would like the admin to require this field,
     * even if it is not required by the ORM.
     *
     * @return the required override enumeration
     */
    RequiredOverride requiredOverride() default RequiredOverride.IGNORED;

    /**
     * Specify if this field should be excluded from inclusion in the
     * admin presentation layer
     *
     * @return whether or not the field should be excluded
     */
    boolean excluded() default false;
    
    /**
     * Helpful tooltip to be displayed when the admin user hovers over the field.
     * This can be localized by providing a key which will use the GWT
     * support for i18N.
     * 
     */
    String tooltip() default "";
    
}
