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

import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;

import java.io.Serializable;
import java.util.Map;


/**
 * Provides a default validate method that uses the validation configuration map to pull out the error key and pre-populate
 * the {@link PropertyValidationResult} based on {@link ConfigurationItem#ERROR_MESSAGE}.
 * 
 * This class should be used as your base if you are writing a validator based on a {@link ValidationConfiguration}
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public abstract class ValidationConfigurationBasedPropertyValidator implements PropertyValidator {

    @Override
    public PropertyValidationResult validate(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value) {
        return new PropertyValidationResult(validateInternal(entity,
                instance,
                entityFieldMetadata,
                validationConfiguration,
                propertyMetadata,
                propertyName,
                value), validationConfiguration.get(ConfigurationItem.ERROR_MESSAGE));
    }
    
    /**
     * Delegate method for {@link ValidationConfiguration}-based processors that don't need to return an error message
     */
    public boolean validateInternal(Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value) {
        return false;
    }

    
}
