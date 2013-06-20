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
import org.broadleafcommerce.openadmin.server.service.JSCompatibilityHelper;
import org.springframework.stereotype.Component;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

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
                    String unserializedFieldName = propertyErrors.getKey();
                    String serializedFieldName = JSCompatibilityHelper.encode(unserializedFieldName);
                    
                    /**
                     * Rather than just use errors.rejectValue directly, we need to instantiate the FieldError object ourself
                     * and add it to the binding result. This is so that we can resolve the actual rejected value ourselves
                     * rather than rely on Spring to do it for us. If we rely on Spring, Spring will attempt to resolve something
                     * like fields['defaultSku__name'] immediately (at the time of invoking errors.rejectValue). At that point,
                     * the field names within the EntityForm are still in their unserialized state, and thus Spring would only
                     * find fields['defaultSku.name'] and there would be an empty string for the rejected value. Then on the
                     * frontend, Thymeleaf's th:field processor relies on Spring's BindingResult to provide the value that
                     * was actually rejected so you can get blank form fields.
                     * 
                     * With this implementation, we avoid all of those additional problems because we are referencing the
                     * field that is being rejected along with providing our own method for getting the rejected value
                     */
                    String[] errorCodes = ((AbstractBindingResult) errors).resolveMessageCodes(errorMessage, serializedFieldName);
                    FieldError fieldError = new FieldError(
                            "entityForm", String.format("fields[%s].value", serializedFieldName),
                            form.getFields().get(unserializedFieldName).getValue(), false,
                            errorCodes, null, errorMessage);
                    ((AbstractBindingResult) errors).addError(fieldError);
                }
            }
        }
        
        return result;
    }

}
