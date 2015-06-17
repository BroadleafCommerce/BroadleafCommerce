/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
