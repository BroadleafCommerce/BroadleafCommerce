/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */
package org.broadleafcommerce.common.presentation;

/**
 * This annotation is used to describe a member of a Map structure that should be
 * displayed as a regular field in the admin tool.
 *
 * @author Jeff Fischer
 */
public @interface AdminPresentationMapField {

    /**
     * <p>Represents the field name for this field. The keyName property of <tt>AdminPresentationMapKey</tt> will
     * be used as the field name in the admin form, as well as the key value for the Map structure. The
     * friendlyKeyName property of <tt>AdminPresentationKey</tt> will be used as the display name for the field
     * in the admin tool form.</p>
     *
     * @return the name and friendly name for this field
     */
    AdminPresentationMapKey fieldName();

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
     * inferred, you can explicitly set the Map structure value type here.</p>
     *
     * @return the concrete type for the Map structure value
     */
    Class<?> targetClass() default Void.class;

}
