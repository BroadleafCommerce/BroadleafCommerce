/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.form.entity;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Map;

/**
 * Validator used at the controller level to ensure that an Entity has passed validation from the PersistenceModule or
 * CustomPersistenceHandler. This should be used as a final validation step after attempting the save
 * 
 * @author Phillip Verheyden
 */
@Component("blEntityFormValidator")
public class EntityFormValidator {

    /**
     * Validates the form DTO against the passed in entity
     * @param form the form DTO
     * @param entity value obtained after attempting to save via {@link AdminEntityService#updateEntity(EntityForm, String)}
     * @return <b>true</b> if <b>entity</b> does not have any validation errors, <b>false</b> otherwise.
     */
    public boolean validate(EntityForm form, Entity entity, Errors errors) {
        boolean result = true;
        if (entity.isValidationFailure()) {
            result = false;
            for (Map.Entry<String, List<String>> propertyErrors : entity.getValidationErrors().entrySet()) {
                for (String errorMessage : propertyErrors.getValue()) {
                    errors.rejectValue(String.format("fields[%s].value", propertyErrors.getKey()), errorMessage, errorMessage);
                }
            }
        }
        
        return result;
    }

}
