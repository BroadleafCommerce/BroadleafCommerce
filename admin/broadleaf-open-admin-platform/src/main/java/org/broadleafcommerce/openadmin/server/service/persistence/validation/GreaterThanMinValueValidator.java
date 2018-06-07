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
    public PropertyValidationResult validate(Entity entity,
                                             Serializable instance,
                                             Map<String, FieldMetadata> entityFieldMetadata,
                                             Map<String, String> validationConfiguration,
                                             BasicFieldMetadata propertyMetadata,
                                             String propertyName,
                                             String value) {
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
