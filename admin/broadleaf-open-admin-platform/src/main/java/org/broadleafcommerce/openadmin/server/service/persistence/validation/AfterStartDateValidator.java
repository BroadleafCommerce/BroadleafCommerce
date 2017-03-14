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
import java.util.Date;
import java.util.Map;

/**
 * Checks to make sure that the end date being updated is after the start date
 * 
 * @author Jay Aisenbrey
 */
@Component("blAfterStartDateValidator")
public class AfterStartDateValidator extends ValidationConfigurationBasedPropertyValidator {
    
    private static final String END_DATE_BEFORE_START = "End date cannot be before the start date";   
    
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
        Date startDate = null;
        Date endDate = null;
        
        if (StringUtils.isBlank(value) || StringUtils.isBlank(otherField)) {
            return new PropertyValidationResult(true);
        }

        try {
            startDate = (Date) fm.getFieldValue(instance, otherField);
            endDate = (Date) fm.getFieldValue(instance, propertyName);
        } catch (IllegalAccessException | FieldNotAvailableException e) {
            valid = false;
            message = e.getMessage();
        }
        
        if (valid && endDate != null && startDate != null && endDate.before(startDate)) {
            valid = false;
            message = END_DATE_BEFORE_START;
        }

        return new PropertyValidationResult(valid, message);
    }

    protected FieldManager getFieldManager(BasicFieldMetadata propertyMetadata) {
        PersistenceManager persistenceManager = PersistenceManagerFactory.getPersistenceManager(propertyMetadata.getTargetClass());
        return persistenceManager.getDynamicEntityDao().getFieldManager();
    }


}
