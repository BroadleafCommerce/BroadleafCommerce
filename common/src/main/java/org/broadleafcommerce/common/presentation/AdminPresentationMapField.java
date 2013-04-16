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

import org.broadleafcommerce.common.presentation.client.CustomFieldSearchableTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to describe a member of a Map structure that should be
 * displayed as a regular field in the admin tool.
 *
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationMapField {

    /**
     * <p>Represents the field name for this field.</p>
     *
     * @return the name for this field
     */
    String fieldName();

    /**
     * <p>Represents the metadata for this field. The <tt>AdminPresentation</tt> properties will be used
     * by the system to determine how this field should be treated in the admin tool (e.g. date fields get
     * a date picker in the UI)</p>
     *
     * @return the descriptive metadata for this field
     */
    AdminPresentation fieldPresentation();

    /**
     * <p>Optional - if the Map structure is using generics, then the system can usually infer the concrete
     * type for the Map value. However, if not using generics for the Map, or if the value cannot be clearly
     * inferred, you can explicitly set the Map structure value type here. Map fields can only understand
     * maps whose values are basic types (String, Long, Date, etc...). Complex types require additional
     * support. Support is provided out-of-the-box for complex types <tt>ValueAssignable</tt>,
     * <tt>Searchable</tt> and <tt>SimpleRule</tt>.</p>
     *
     * @return the concrete type for the Map structure value
     */
    Class<?> targetClass() default Void.class;

    /**
     * <p>Optional - if the map field value contains searchable information and should be included in Broadleaf
     * search engine indexing and searching. If set, the map value class must implement the <tt>Searchable</tt> interface.
     * Note, support for indexing and searching this field must be explicitly added to the Broadleaf search service
     * as well.</p>
     *
     * @return Whether or not this field is searchable with the Broadleaf search engine
     */
    CustomFieldSearchableTypes searchable() default CustomFieldSearchableTypes.NOT_SPECIFIED;

    /**
     * <p>Optional - if the value is not primitive and contains a bi-directional reference back to the entity containing
     * this map structure, you can declare the field name in the value class for this reference. Note, if the map
     * uses the JPA mappedBy property, the system will try to infer the manyToField value so you don't have to set
     * it here.</p>
     *
     * @return the parent entity referring field name
     */
    String manyToField() default "";

}
