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

import org.broadleafcommerce.common.presentation.AdminPresentationMapKey;
import org.broadleafcommerce.common.presentation.AdminPresentationOperationTypes;
import org.broadleafcommerce.common.presentation.OptionFilterParam;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a override value for a specific admin presentation property.
 *
 * @see org.broadleafcommerce.common.presentation.AdminPresentation
 * @see org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup
 * @see org.broadleafcommerce.common.presentation.AdminPresentationDataDrivenEnumeration
 * @see org.broadleafcommerce.common.presentation.AdminPresentationCollection
 * @see org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection
 * @see org.broadleafcommerce.common.presentation.AdminPresentationMap
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AdminPresentationMergeEntry {

    /**
     * The type for this property override. The types available are specific to the properties available in
     * the various admin presentation annotations. See {@link PropertyType} for a comprehensive list of
     * available values organized by admin presentation annotation type.
     *
     * @see PropertyType
     * @return the property override type
     */
    String propertyType();

    /**
     * The string representation of the override value. Any primitive property can be specified as a string (int, boolean, enum).
     * The backend override system will be responsible for converting the string representation back
     * to the appropriate type for use by the admin. The type specific override value properties are provided
     * for convenience (e.g. doubleOverrideValue()).
     *
     * @return The string representation of the property value
     */
    String overrideValue() default "";

    /**
     * Convenience property for specifying a double value override. The target property
     * must be of this type. This type can also be specified using overrideValue() and the backend will convert.
     *
     * @return the property override value in the form of a double
     */
    double doubleOverrideValue() default Double.MIN_VALUE;

    /**
     * Convenience property for specifying a float value override. The target property
     * must be of this type. This type can also be specified using overrideValue() and the backend will convert.
     *
     * @return the property override value in the form of a float
     */
    float floatOverrideValue() default Float.MIN_VALUE;

    /**
     * Convenience property for specifying a boolean value override. The target property
     * must be of this type. This type can also be specified using overrideValue() and the backend will convert.
     *
     * @return the property override value in the form of a boolean
     */
    boolean booleanOverrideValue() default false;

    /**
     * Convenience property for specifying a int value override. The target property
     * must be of this type. This type can also be specified using overrideValue() and the backend will convert.
     *
     * @return the property override value in the form of a int
     */
    int intOverrideValue() default Integer.MIN_VALUE;

    /**
     * Convenience property for specifying a long value override. The target property
     * must be of this type. This type can also be specified using overrideValue() and the backend will convert.
     *
     * @return the property override value in the form of a long
     */
    long longOverrideValue() default Long.MIN_VALUE;

    /**
     * Convenience property for specifying a string array value override. The target property
     * must be of this type.
     *
     * @return the property override value in the form of a string array
     */
    String[] stringArrayOverrideValue() default {};

    /**
     * Convenience property for specifying a double array value override. The target property
     * must be of this type.
     *
     * @return the property override value in the form of a double array
     */
    double[] doubleArrayOverrideValue() default {};

    /**
     * Convenience property for specifying a float array value override. The target property
     * must be of this type.
     *
     * @return the property override value in the form of a float array
     */
    float[] floatArrayOverrideValue() default {};

    /**
     * Convenience property for specifying a boolean array value override. The target property
     * must be of this type.
     *
     * @return the property override value in the form of a boolean array
     */
    boolean[] booleanArrayOverrideValue() default {};

    /**
     * Convenience property for specifying a int array value override. The target property
     * must be of this type.
     *
     * @return the property override value in the form of a int array
     */
    int[] intArrayOverrideValue() default {};

    /**
     * Convenience property for specifying a long array value override. The target property
     * must be of this type.
     *
     * @return the property override value in the form of a long array
     */
    long[] longArrayOverrideValue() default {};

    /**
     * Property for overriding the validation configuration for a field annotated with the
     * {@link org.broadleafcommerce.common.presentation.AdminPresentation} annotation.
     *
     * @return the validation config override
     */
    ValidationConfiguration[] validationConfigurations() default {};

    /**
     * Property for overriding the operationTypes for an advanced collection. See
     * {@link org.broadleafcommerce.common.presentation.AdminPresentationCollection},
     * {@link org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection} and
     * {@link org.broadleafcommerce.common.presentation.AdminPresentationMap} for default values for each type.
     *
     * @return the operationType override
     */
    AdminPresentationOperationTypes operationTypes() default @AdminPresentationOperationTypes(addType = OperationType.BASIC,
            fetchType = OperationType.BASIC, inspectType = OperationType.BASIC, removeType = OperationType.BASIC,
            updateType = OperationType.BASIC);

    /**
     * Property for overriding the filter configuration for a field annotated with the
     * {@link org.broadleafcommerce.common.presentation.AdminPresentationDataDrivenEnumeration} annotation.
     *
     * @return the option filter configuration
     */
    OptionFilterParam[] optionFilterParams() default {};

    /**
     * Property for overriding the map key configuration for a field annotated with the
     * {@link org.broadleafcommerce.common.presentation.AdminPresentationMap} annotation.
     *
     * @return the map key configuration
     */
    AdminPresentationMapKey[] keys() default {};
}
