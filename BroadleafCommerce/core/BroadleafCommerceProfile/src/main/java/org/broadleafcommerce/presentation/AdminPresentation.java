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
package org.broadleafcommerce.presentation;

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
	 * The friendly name to present to a user for this field in a GUI
	 * 
	 * @return the friendly name
	 */
	String friendlyName();
	
	/**
	 * The order in which this field will appear in a GUI relative to other fields from the same class
	 * 
	 * @return the display order
	 */
	int order() default 99999;
	
	/**
	 * Whether or not the field should be hidden in the GUI
	 * 
	 * @return whether or not to hide
	 */
	boolean hidden() default false;
	
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
}
