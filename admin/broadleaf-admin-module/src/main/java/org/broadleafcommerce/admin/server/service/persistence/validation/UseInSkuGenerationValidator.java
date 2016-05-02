/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
