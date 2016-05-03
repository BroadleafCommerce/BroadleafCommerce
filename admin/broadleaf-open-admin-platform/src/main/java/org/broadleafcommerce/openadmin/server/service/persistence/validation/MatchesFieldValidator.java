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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;


/**
 * Checks for equality between this field and a configured 'otherField'
 * 
 * @author Phillip Verheyden
 */
@Component("blMatchesFieldValidator")
public class MatchesFieldValidator extends ValidationConfigurationBasedPropertyValidator implements FieldNamePropertyValidator {

    @Override
    public boolean validateInternal(Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value) {
        String otherField = validationConfiguration.get("otherField");
        return StringUtils.equals(entity.getPMap().get(otherField).getValue(), value);
    }

}
