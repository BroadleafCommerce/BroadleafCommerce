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
package org.broadleafcommerce.openadmin.web.form.entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
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
     * Validates the DTO against the map of property errors. Note that this method does not support global validation errors
     * from {@link Entity#getGlobalValidationErrors()} as they might not make sense.
     * 
     * @param form
     * @param propertyErrors
     * @param errors
     * @return
     */
    public boolean validate(EntityForm form, Map<String, List<String>> propertyErrors, Errors errors) {
        return validate(form, propertyErrors, null, errors);
    }
    
    /**
     * Validates the form DTO against the passed in map of propertyErrors along with global validation errors.
     * 
     * @see #validate(EntityForm, Entity, Errors)
     * @param form
     * @param propertyErrors
     * @param globalErrors
     * @param errors
     * @return
     */
    public boolean validate(EntityForm form, Map<String, List<String>> propertyErrors, List<String> globalErrors, Errors errors) {
        if (MapUtils.isEmpty(propertyErrors) && CollectionUtils.isEmpty(globalErrors)) {
            return true;
        }
        
        if (MapUtils.isNotEmpty(propertyErrors)) {
            for (Map.Entry<String, List<String>> pe : propertyErrors.entrySet()) {
                for (String errorMessage : pe.getValue()) {
                    String unserializedFieldName = pe.getKey();
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
                    Field field = null;
                    if (StringUtils.contains(unserializedFieldName, DynamicEntityFormInfo.FIELD_SEPARATOR)) {
                        String[] fieldInfo = unserializedFieldName.split("\\" + DynamicEntityFormInfo.FIELD_SEPARATOR);
                        field = form.getDynamicForm(fieldInfo[0]).findField(fieldInfo[1]);
                    } else if (form.findField(unserializedFieldName) != null) {
                        field = form.findField(unserializedFieldName);
                    }
                    
                    //If the submitted field was a radio button but has validation associated with it, that radio field
                    //will have never been submitted in the POST and thus will not have ever been attached to the EntityForm.
                    //We still want to notate the fact that there was a validation failure on that field
                    String value = (field != null) ? field.getValue() : null;
                    
                    String[] errorCodes = ((AbstractBindingResult) errors).resolveMessageCodes(errorMessage, serializedFieldName);
                    FieldError fieldError = new FieldError("entityForm", String.format("fields[%s].value", serializedFieldName),
                            value, false, errorCodes, null, errorMessage);
                    ((AbstractBindingResult) errors).addError(fieldError);
                }
            }
        }
        
        if (CollectionUtils.isNotEmpty(globalErrors)) {
            for (String errorMessage : globalErrors) {
                errors.reject(errorMessage, errorMessage);
            }
        }
        
        return false;
    }

    /**
     * Validates the form DTO against the passed in entity
     * @param form the form DTO
     * @param entity value obtained after attempting to save via {@link AdminEntityService#updateEntity(EntityForm, String)}
     * @return <b>true</b> if <b>entity</b> does not have any validation errors, <b>false</b> otherwise.
     */
    public boolean validate(EntityForm form, Entity entity, Errors errors) {
        if (entity.isValidationFailure()) {
            return validate(form, entity.getPropertyValidationErrors(), entity.getGlobalValidationErrors(), errors);
        }
        
        return true;
    }

}
