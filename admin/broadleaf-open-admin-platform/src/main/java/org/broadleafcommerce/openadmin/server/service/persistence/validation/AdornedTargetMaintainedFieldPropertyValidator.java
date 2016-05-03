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

import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.extension.AdornedTargetAutoPopulateExtensionManager;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;


/**
 * A basic entity persistence validation hook into validation provided by instances of
 * {@link org.broadleafcommerce.openadmin.server.service.persistence.extension.AdornedTargetAutoPopulateExtensionHandler}
 *
 * @author Jeff Fischer
 */
@Component("blAdornedTargetMaintainedFieldPropertyValidator")
public class AdornedTargetMaintainedFieldPropertyValidator implements GlobalPropertyValidator {

    public static String ERROR_MESSAGE = "adornedTargetMaintainedFieldValidationFailure";

    @Resource(name = "blAdornedTargetAutoPopulateExtensionManager")
    protected AdornedTargetAutoPopulateExtensionManager adornedTargetAutoPopulateExtensionManager;
    
    @Override
    public PropertyValidationResult validate(Entity entity,
                            Serializable instance,
                            Map<String, FieldMetadata> entityFieldMetadata,
                            BasicFieldMetadata propertyMetadata,
                            String propertyName,
                            String value) {
        ExtensionResultHolder<Boolean> validationResult = new ExtensionResultHolder<Boolean>();
        ExtensionResultStatusType status = adornedTargetAutoPopulateExtensionManager.getProxy().validateSubmittedAdornedTargetManagedFields(entity, instance,
                entityFieldMetadata, propertyMetadata, propertyName, value, validationResult);
        Boolean valid = true;
        if (ExtensionResultStatusType.NOT_HANDLED != status && validationResult.getResult() != null) {
            valid = validationResult.getResult();
        }
        return new PropertyValidationResult(valid, ERROR_MESSAGE);
    }

}
