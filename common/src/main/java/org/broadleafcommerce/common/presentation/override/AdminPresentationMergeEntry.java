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
package org.broadleafcommerce.common.presentation.override;

/**
 * Represents a override value for a specific {@link org.broadleafcommerce.common.presentation.AdminPresentation} property.
 *
 * @author Jeff Fischer
 */
public @interface AdminPresentationMergeEntry {

    /**
     * The type for this property override. Each type enum member correlates to a annotation property in
     * {@link org.broadleafcommerce.common.presentation.AdminPresentation}.
     *
     * @return the property override type
     */
    AdminPresentationPropertyType propertyType();

    /**
     * The string representation of the override value. Any property can be specified as a string (int, boolean, enum).
     * The backend override system will be responsible for converting the string representation back
     * to the appropriate type for use by the admin. The type specific override value properties are provided
     * for convenience (e.g. doubleOverrideValue()).
     *
     * @return The string representation of the {@link org.broadleafcommerce.common.presentation.AdminPresentation}
     * property value
     */
    String overrideValue() default "";

    /**
     * Convenience property for specifying a double value override. The target {@link org.broadleafcommerce.common.presentation.AdminPresentation} property
     * must be of this type.
     *
     * @return the property override value in the form of a double
     */
    double doubleOverrideValue() default Double.MIN_VALUE;

    /**
     * Convenience property for specifying a float value override. The target {@link org.broadleafcommerce.common.presentation.AdminPresentation} property
     * must be of this type.
     *
     * @return the property override value in the form of a float
     */
    float floatOverrideValue() default Float.MIN_VALUE;

    /**
     * Convenience property for specifying a boolean value override. The target {@link org.broadleafcommerce.common.presentation.AdminPresentation} property
     * must be of this type.
     *
     * @return the property override value in the form of a boolean
     */
    boolean booleanOverrideValue() default false;

    /**
     * Convenience property for specifying a int value override. The target {@link org.broadleafcommerce.common.presentation.AdminPresentation} property
     * must be of this type.
     *
     * @return the property override value in the form of a int
     */
    int intOverrideValue() default Integer.MIN_VALUE;

    /**
     * Convenience property for specifying a long value override. The target {@link org.broadleafcommerce.common.presentation.AdminPresentation} property
     * must be of this type.
     *
     * @return the property override value in the form of a long
     */
    long longOverrideValue() default Long.MIN_VALUE;

}
