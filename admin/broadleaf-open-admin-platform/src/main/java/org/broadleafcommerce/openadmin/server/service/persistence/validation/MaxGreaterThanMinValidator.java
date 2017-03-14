/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Validator that checks if a max value field is actually greater than or equal to a min value field.
 *
 * @author Nathan Moore (nathandmoore)
 */
@Component("blMaxGreaterThanMinValidator")
public class MaxGreaterThanMinValidator extends ValidationConfigurationBasedPropertyValidator {

    @Override
    public PropertyValidationResult validate(Entity entity,
                                             Serializable instance,
                                             Map<String, FieldMetadata> entityFieldMetadata,
                                             Map<String, String> validationConfiguration,
                                             BasicFieldMetadata propertyMetadata,
                                             String propertyName,
                                             String value) {
        String otherField = validationConfiguration.get("otherField");
        FieldManager fm = getFieldManager(propertyMetadata);
        boolean valid = true;
        String message = "";
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = min;

        if (StringUtils.isBlank(value) || StringUtils.isBlank(otherField)) {
            return new PropertyValidationResult(true);
        }

        try {
            Object minObj = fm.getFieldValue(instance, otherField);
            if (minObj != null) {
                min = new BigDecimal(minObj.toString());
            }
            max = new BigDecimal(fm.getFieldValue(instance, propertyName).toString());
        } catch (IllegalAccessException | FieldNotAvailableException e) {
            valid = false;
            message = e.getMessage();
        }

        if (valid && max != null && min != null && max.compareTo(min) < 0 ) {
            valid = false;
            message = "minGreaterThanMax";
        }

        return new PropertyValidationResult(valid, message);
    }

    protected FieldManager getFieldManager(BasicFieldMetadata propertyMetadata) {
        PersistenceManager persistenceManager = PersistenceManagerFactory.getPersistenceManager(propertyMetadata.getTargetClass());
        return persistenceManager.getDynamicEntityDao().getFieldManager();
    }

}
