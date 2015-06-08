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

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.springframework.stereotype.Component;

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
        FieldManager fm = PersistenceManagerFactory.getPersistenceManager().getDynamicEntityDao().getFieldManager();
        boolean valid = true;
        String message = "";
        Date startDate = null;
        Date endDate = null;
        
        
        if (value == null || value.equals("") || otherField == null || otherField.equals("")) {
            return new PropertyValidationResult(true);
        }
        

            try {
                startDate = (Date) fm.getFieldValue(instance, otherField);
                endDate = (Date) fm.getFieldValue(instance, propertyName);
            } catch (IllegalAccessException iae) {
                valid = false;
                message = iae.getMessage();
            } catch (FieldNotAvailableException fnae) {
                valid = false;
                message = fnae.getMessage();
            }
        

        
        if (valid && endDate != null && startDate != null && endDate.before(startDate)) {
            valid = false;
            message = END_DATE_BEFORE_START;
        }
                    
        if (valid)
            return new PropertyValidationResult(true);
        else
            return new PropertyValidationResult(false, message);
    }


}
