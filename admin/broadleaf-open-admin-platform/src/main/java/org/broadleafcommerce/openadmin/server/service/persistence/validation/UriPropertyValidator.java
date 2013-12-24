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

package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;


/**
 * Validates a field as being a valid URI to ensure compatibility with Broadleaf handlers including
 * PageHandler, ProductHandler, and CategoryHandlers.
 * 
 * Component can be overridden with the following properties:
 * 
 * This component was introduced instead of using RegEx because most site have simple url needs and BLC out of 
 * box simply requires that the URL start with a / and use valid url characters.
 * 
 * Replace if needed for your implementation.
 * 
 * 
 * @author Brian Polster
 */
@Component("blUriPropertyValidator")
public class UriPropertyValidator extends ValidationConfigurationBasedPropertyValidator {

    protected static final Log LOG = LogFactory.getLog(UriPropertyValidator.class);

    protected String ERROR_KEY_BEGIN_WITH_SLASH = "uriPropertyValidatorMustBeginWithSlashError";
    protected String ERROR_KEY_CANNOT_END_WITH_SLASH = "uriPropertyValidatorCannotEndWithSlashError";

    @Value("${uriPropertyValidator.ignoreFullUrls}")
    protected boolean ignoreFullUrls = true;

    @Value("${uriPropertyValidator.requireLeadingSlash}")
    protected boolean requireLeadingSlash = true;

    @Value("${uriPropertyValidator.allowTrailingSlash}")
    protected boolean allowTrailingSlash = false;
    

    public boolean isFullUrl(String url) {
        return (url.startsWith("http") || url.startsWith("ftp"));
    }

    /**
     * Denotes what should occur when this validator encounters a null value to validate against. Default behavior is to
     * allow them, which means that this validator will always return true with null values
     */
    protected boolean succeedForNullValues = true;
    
    @Override
    public PropertyValidationResult validate(Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value) {
        
        if (value == null) {
            return new PropertyValidationResult(succeedForNullValues);
        }
        
        if (isFullUrl(value) && ignoreFullUrls) {
            return new PropertyValidationResult(true);
        }

        if (requireLeadingSlash && !value.startsWith("/")) {
            return new PropertyValidationResult(false, ERROR_KEY_BEGIN_WITH_SLASH);
        }

        if (!allowTrailingSlash && value.endsWith("/")) {
            return new PropertyValidationResult(false, ERROR_KEY_CANNOT_END_WITH_SLASH);
        }

        return new PropertyValidationResult(true);
    }
    
    public boolean isSucceedForNullValues() {
        return succeedForNullValues;
    }

    public void setSucceedForNullValues(boolean succeedForNullValues) {
        this.succeedForNullValues = succeedForNullValues;
    }
}
