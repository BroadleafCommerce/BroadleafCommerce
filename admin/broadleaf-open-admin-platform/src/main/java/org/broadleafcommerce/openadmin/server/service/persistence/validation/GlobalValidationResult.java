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

import org.broadleafcommerce.openadmin.server.security.service.RowLevelSecurityService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing a boolean whether or not it passed validation and String error message. An error message is not required
 * if the result is not an error.
 * 
 * This is most suitable for global errors like those from {@link RowLevelSecurityService}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link RowLevelSecurityService}
 * @see {@link PropertyValidationResult}
 */
public class GlobalValidationResult {
    
    protected boolean valid;
    protected List<String> errorMessages = new ArrayList<>();
    
    public GlobalValidationResult(boolean valid, String errorMessage) {
        setValid(valid);
        addErrorMessage(errorMessage);
    }
    
    public GlobalValidationResult(boolean valid) {
        setValid(valid);
    }
    
    /**
     * 
     * @return Whether or not this property passed validation
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Opposite of {@link #isValid()}
     * @return
     */
    public boolean isNotValid() {
        return !valid;
    }
    
    /**
     * Set the validation result for this property
     * @param valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    /**
     * Convenience method to return the first message 
     * @return the error message (or key in a message bundle) for the validation failure
     */
    public String getErrorMessage() {
        return CollectionUtils.isEmpty(errorMessages) ? null : errorMessages.get(0);
    }

    /**
     * Sets the error message (or key in a message bundle) for the validation failure. If you have some sort
     * of custom error message for the validation failure it should be set here
     * @param errorMessage
     * @deprecated - use {@link #addErrorMessage(String)}
     */
    @Deprecated
    public void setErrorMessage(String errorMessage) {
        addErrorMessage(errorMessage);
    }
    
    /**
     * Adds an error message to the list of error messages
     * @param errorMessageOrKey
     */
    public void addErrorMessage(String errorMessageOrKey) {
        errorMessages.add(errorMessageOrKey);
    }
    
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
    
}
