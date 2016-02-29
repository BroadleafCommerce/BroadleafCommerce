/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.server.service.persistence.validation;

import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidationResult;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * Validator that ensures that a ProductOption with useInSkuGeneration set to TRUE has at least one allowedValue (ProductOptionValue)
 *
 * @author Chris Kittrell (ckittrell)
 */
@Component("blUseInSkuGenerationValidator")
public class UseInSkuGenerationValidator implements PropertyValidator {

    public static String ERROR_MESSAGE = "allowedValuesRequired_ValidationFailure";

    @Override
    public PropertyValidationResult validate(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata, Map<String, String> validationConfiguration, BasicFieldMetadata propertyMetadata, String propertyName, String value) {
        ProductOption productOption = (ProductOption)instance;
        List<ProductOptionValue> allowedValues = productOption.getAllowedValues();
        if (productOption.getUseInSkuGeneration().equals(Boolean.TRUE)
                && (allowedValues == null || allowedValues.isEmpty())) {
            return new PropertyValidationResult(false, ERROR_MESSAGE);
        } else {
            return new PropertyValidationResult(true);
        }
    }
}
