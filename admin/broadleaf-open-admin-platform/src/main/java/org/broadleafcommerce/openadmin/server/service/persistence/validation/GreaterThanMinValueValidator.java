/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blGreaterThanMinValueValidator")
public class GreaterThanMinValueValidator extends ValidationConfigurationBasedPropertyValidator {

    protected final String INVALID_VALUE_MESSAGE = "Entered value must be greater than %s.";

    @Override
    public PropertyValidationResult validate(
            Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value
    ) {
        BigDecimal minValue = getMinValue(validationConfiguration);

        if (StringUtils.isBlank(value)) {
            return new PropertyValidationResult(true);
        }

        try {
            BigDecimal newValue = new BigDecimal(value);

            if (minValue.compareTo(newValue) > 0) {
                return new PropertyValidationResult(false, String.format(INVALID_VALUE_MESSAGE, minValue));
            }
        } catch (NumberFormatException e) {
            return new PropertyValidationResult(false);
        }

        return new PropertyValidationResult(true);
    }

    private BigDecimal getMinValue(Map<String, String> validationConfiguration) throws NumberFormatException {
        String minValue = validationConfiguration.get("minValue");

        return new BigDecimal(minValue);
    }

}
