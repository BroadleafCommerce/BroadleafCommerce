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
package org.broadleafcommerce.common.presentation.override;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a non-comprehensive override of admin presentation annotation
 * property values for a target entity field.
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
public @interface AdminPresentationMergeOverrides {

    /**
     * The new configurations for each field targeted for override
     *
     * @return field specific overrides
     */
    AdminPresentationMergeOverride[] value();

}
