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

import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValueImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationDataDrivenEnumeration {

    /**
     * <p>Optional - only required if the target entity is other than DataDrivenEnumerationValueImpl. The annotated
     * field must be of type String. DataDrivenEnumerationValueImpl is the standard entity for storing data driven enumerations,
     * but a completely different entity (e.g. CountryImpl) could be substituted, if applicable.</p>
     *
     * <p>Specify the target entity that should be queried for the list of options that will be presented to the user in a
     * drop down list. The value selected from the dropdown will become the String value for this field.</p>
     *
     * @return the entity class representing the data to populate a dropdown field in the admin tool
     */
    Class<?> optionListEntity() default DataDrivenEnumerationValueImpl.class;

    /**
     * <p>Optional - only required if it is desirable to filter the list of items returned from the query for the optionListEntity. This is useful if you
     * only want to present part of a table as options in the data driven enumeration. Note, when configuring for optionListEntity
     * equals DataDrivenEnumerationValueImpl, it is generally appropriate to denote:</p>
     *
     * <p>@OptionFilterParam(param="type.key", value="[the key value of the DataDrivenEnumerationImpl instance]", paramType=[your param type])</p>
     *
     * <p>Additional parameters with which to filter the list of options shown to the user in the admin tool</p>
     *
     * @return list of parameters with which to filter the option list
     */
    OptionFilterParam[] optionFilterParams() default {};

    /**
     * <p>Optional - only required if the optionListEntity is not DataDrivenEnumerationValueImpl.</p>
     *
     * <p>Specify the field in the target entity that contains the value that will be persisted into this annotated field.</p>
     *
     * @return the value field in the target entity
     */
    String optionValueFieldName() default "";

    /**
     * <p>Optional - only required if the optionListEntity is not DataDrivenEnumerationValueImpl.</p>
     *
     * <p>Specify the field in the target entity that contains the display value that will be shown to the user in the dropdown field</p>
     *
     * @return the display field in the target entity
     */
    String optionDisplayFieldName() default "";

    /**
     * <p>Optional - only required if you want to allow users to edit (or enter new values) in the dropdown. If true, users will
     * be able to type their own value or select from one of the data-driven values. This is only required when the optionListEntity
     * is not DataDrivenEnumerationValueImpl, since that class already defines this property (i.e. the modifiable property)</p>
     *
     * <p>Whether or not the user can type in the data-driven field</p>
     *
     * @return whether or not the user can type in the data-driven field
     */
    boolean optionCanEditValues() default false;
}
