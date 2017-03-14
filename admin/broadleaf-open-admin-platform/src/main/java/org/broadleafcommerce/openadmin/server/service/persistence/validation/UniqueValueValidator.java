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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * Checks for uniqueness of this field's value among other entities of this type
 * 
 * @author Brandon Smith
 */
@Component("blUniqueValueValidator")
public class UniqueValueValidator implements PropertyValidator {

    protected static final Log LOG = LogFactory.getLog(UniqueValueValidator.class);

    @Override
    public PropertyValidationResult validate(Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value) {

        String instanceClassName = instance.getClass().getName();
        DynamicEntityDao dynamicEntityDao = getDynamicEntityDao(instanceClassName);
        List<Long> responseIds = dynamicEntityDao.readOtherEntitiesWithPropertyValue(instance, propertyName, value);

        String message = validationConfiguration.get(ConfigurationItem.ERROR_MESSAGE);
        if (message == null) {
            message = entity.getType()[0] + " with this value for attribute " +
                    propertyName + " already exists. This attribute's value must be unique.";
        }

        if(responseIds.size() == 0) {
            return new PropertyValidationResult(true, message);
        } else {
            return new PropertyValidationResult(false, message);
        }
    }

    protected DynamicEntityDao getDynamicEntityDao(String className) {
        return PersistenceManagerFactory.getPersistenceManager(className).getDynamicEntityDao();
    }
}
