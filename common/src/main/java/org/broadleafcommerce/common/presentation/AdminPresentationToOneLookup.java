/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.presentation;

import org.broadleafcommerce.common.presentation.client.LookupType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationToOneLookup {

    /**
     * <p>Optional - only required if the display property is other than "name"</p>
     *
     * <p>Specify the property on a lookup class that should be used as the value to display to the user in
     * a form in the admin tool UI</p>
     *
     * @return the property on the lookup class containing the displayable value
     */
    String lookupDisplayProperty() default "";

    /**
     * <p>Optional - only required if you need to specially handle crud operations for this
     * specific collection on the server</p>
     *
     * <p>Custom string values that will be passed to the server during Read and Inspect operations on the
     * entity lookup. This allows for the creation of a custom persistence handler to handle both
     * inspect and fetch phase operations. Presumably, one could use this to
     * somehow filter the list of records shown when the user interacts with the lookup widget in the
     * admin UI.</p>
     *
     * @return the custom string array to pass to the server during CRUD operations
     */
    String[] customCriteria() default {};

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
     * <p>Optional - only required if you want to configure the lookup
     * to be driven by a prepopulated dropdown instead of the standard
     * lookup type, which is modal based.</p>
     *
     * <p>Define whether or not the lookup type for this field should be
     * handled through a modal or through a dropdown</p>
     *
     * @return the item is looked up via a modal or dropdown
     */
    LookupType lookupType() default LookupType.STANDARD;
    
    
    /**
     * <p>Optional - by setting this value to true, the admin will identify
     * the properties that are inside the target of this to-one field. </p>
     * 
     * <p>Typically, this is done if you want to expose a certain field as an
     * AdminPresentationToOneLookup but also allow filtering on a property that
     * resides inside of the target of this lookup.</p>
     * 
     * @return whether or not to force population of the child properties
     */
    boolean forcePopulateChildProperties() default false;

}
