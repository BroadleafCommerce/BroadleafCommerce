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


/**
 * 
 * @author jfischer
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AdminPresentationClass {
	
	/**
	 * Specify whether or not the open admin module persistence mechanism
     * should traverse ManyToOne or OneToOne field boundaries in the entity
     * when retrieving and populating field values.
	 *
	 * @return whether or not to populate ManyToOne or OneToOne fields
	 */
	PopulateToOneFieldsEnum populateToOneFields() default PopulateToOneFieldsEnum.NOT_SPECIFIED;

    /**
	 * The friendly name to present to a user for this field in a GUI. If supporting i18N,
	 * the friendly name may be a key to retrieve a localized friendly name using
	 * the GWT support for i18N. This name will be presented to users when they add a
     * new entity in the GUI and select the polymorphic type for that new added entity.
	 *
	 * @return the friendly name
	 */
	String friendlyName() default "";

    /**
     * Specify the fully qualified class name of the ceiling entity for this inheritance hierarchy. This
     * value affects the list of polymorphic types presented to the administrative user in the admin
     * UI. By specifying a class lower in the inheritance hierarchy, you can cause only a subset of
     * the entire JPA inheritance hierarchy to be presented to the user as options when creating new
     * entities. This value will override any previous settings for this inheritance hierarchy.
     *
     * @return the fully qualified classname of the new top-level member of this inheritance hierarchy
     * to be displayed to the admin user
     */
    String ceilingDisplayEntity() default "";

}
