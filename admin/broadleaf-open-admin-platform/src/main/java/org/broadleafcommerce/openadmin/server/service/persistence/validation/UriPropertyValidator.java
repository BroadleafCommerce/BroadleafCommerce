/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validates a field as being a valid URI to ensure compatibility with Broadleaf handlers including
 * PageHandler, ProductHandler, and CategoryHandlers.
 * <p>
 * Component can be overridden with the following properties:
 * <p>
 * This component was introduced instead of using RegEx because most site have simple url needs and BLC out of
 * box simply requires that the URL start with a / and use valid url characters.
 * <p>
 * Replace if needed for your implementation.
 *
 * @author Brian Polster
 */
@Component("blUriPropertyValidator")
public class UriPropertyValidator extends ValidationConfigurationBasedPropertyValidator {

    protected static final Log LOG = LogFactory.getLog(UriPropertyValidator.class);
    protected static final Pattern URI_PATTERN = Pattern.compile("[\\w-_/]*");

    protected String ERROR_KEY_BEGIN_WITH_SLASH = "uriPropertyValidatorMustBeginWithSlashError";
    protected String ERROR_KEY_CANNOT_END_WITH_SLASH = "uriPropertyValidatorCannotEndWithSlashError";
    protected String ERROR_KEY_CANNOT_CONTAIN_SPACES = "uriPropertyValidatorCannotContainSpacesError";
    protected String ERROR_KEY_CANNOT_CONTAIN_SPECIAL_CHARACTERS = "uriPropertyValidatorCannotContainSpecialCharactersError";
    /**
     * Denotes what should occur when this validator encounters a null value to validate against. Default behavior is to
     * allow them, which means that this validator will always return true with null values
     */
    protected boolean succeedForNullValues = true;

    protected boolean getIgnoreFullUrls() {
        return BLCSystemProperty.resolveBooleanSystemProperty("uriPropertyValidator.ignoreFullUrls");
    }

    protected boolean getRequireLeadingSlash() {
        return BLCSystemProperty.resolveBooleanSystemProperty("uriPropertyValidator.requireLeadingSlash");
    }

    protected boolean getAllowTrailingSlash() {
        return BLCSystemProperty.resolveBooleanSystemProperty("uriPropertyValidator.allowTrailingSlash");
    }

    public boolean isFullUrl(String url) {
        return (url.startsWith("http") || url.startsWith("ftp"));
    }

    @Override
    public PropertyValidationResult validate(
            Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value
    ) {

        if (value == null) {
            return new PropertyValidationResult(succeedForNullValues);
        }

        if (value.contains(" ")) {
            return new PropertyValidationResult(false, ERROR_KEY_CANNOT_CONTAIN_SPACES);
        }

        if (isFullUrl(value) && getIgnoreFullUrls()) {
            return new PropertyValidationResult(true);
        }

        if (getRequireLeadingSlash() && !value.startsWith("/")) {
            return new PropertyValidationResult(false, ERROR_KEY_BEGIN_WITH_SLASH);
        }

        if (!getAllowTrailingSlash() && value.endsWith("/") && value.length() > 1) {
            return new PropertyValidationResult(false, ERROR_KEY_CANNOT_END_WITH_SLASH);
        }

        if (!URI_PATTERN.matcher(value).matches()) {
            return new PropertyValidationResult(false, ERROR_KEY_CANNOT_CONTAIN_SPECIAL_CHARACTERS);
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
