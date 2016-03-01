/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Validator that checks if a max value field is actually greater than a min value field.
 *
 * @author Nathan Moore (nathandmoore)
 */
@Component("blMaxGreaterThanMinValidator")
public class MaxGreaterThanMinValidator extends ValidationConfigurationBasedPropertyValidator {

    private static final String MIN_GREATER_THAN_MAX = "Max value must be greater than min";

    @Override
    public PropertyValidationResult validate(Entity entity,
                                             Serializable instance,
                                             Map<String, FieldMetadata> entityFieldMetadata,
                                             Map<String, String> validationConfiguration,
                                             BasicFieldMetadata propertyMetadata,
                                             String propertyName,
                                             String value) {
        String otherField = validationConfiguration.get("otherField");
        FieldManager fm = PersistenceManagerFactory.getPersistenceManager().getDynamicEntityDao().getFieldManager();
        boolean valid = true;
        String message = "";
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = min;

        if (StringUtils.isBlank(value) || StringUtils.isBlank(otherField)) {
            return new PropertyValidationResult(true);
        }

        try {
            min = (BigDecimal) fm.getFieldValue(instance, otherField);
            max = (BigDecimal) fm.getFieldValue(instance, propertyName);
        } catch (IllegalAccessException | FieldNotAvailableException e) {
            valid = false;
            message = e.getMessage();
        }

        if (valid && max != null && min != null && max.compareTo(min) < 0 ) {
            valid = false;
            message = MIN_GREATER_THAN_MAX;
        }

        return new PropertyValidationResult(valid, message);
    }

}
