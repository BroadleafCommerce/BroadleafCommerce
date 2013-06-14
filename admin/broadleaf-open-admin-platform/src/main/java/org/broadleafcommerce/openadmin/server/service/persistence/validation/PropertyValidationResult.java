/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.service.persistence.validation;


/**
 * DTO representing a boolean whether or not it passed validation and String error message. An error message is not required
 * if the result is not an error
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link PropertyValidator}
 */
public class PropertyValidationResult {

    protected boolean valid;
    protected String errorMessage;
    
    public PropertyValidationResult(boolean valid, String errorMessage) {
        setValid(valid);
        setErrorMessage(errorMessage);
    }
    
    public PropertyValidationResult(boolean valid) {
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
     * Set the validation result for this property
     * @param valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    /**
     *
     * @return the error message (or key in a message bundle) for the validation failure
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message (or key in a message bundle) for the validation failure. If you have some sort
     * of custom error message for the validation failure it should be set here
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
}
