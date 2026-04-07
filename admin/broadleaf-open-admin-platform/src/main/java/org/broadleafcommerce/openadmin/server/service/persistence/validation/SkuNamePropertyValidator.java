/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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
import org.apache.commons.text.StringEscapeUtils;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component("blSkuNamePropertyValidator")
public class SkuNamePropertyValidator extends ValidationConfigurationBasedPropertyValidator {

    protected static final Log LOG = LogFactory.getLog(SkuNamePropertyValidator.class);

    protected String ERROR_KEY_CANNOT_CONTAIN_SEMICOLON = "skuNamePropertyValidatorCannotContainSemicolonError";
    protected String ERROR_KEY_CANNOT_CONTAIN_SPECIAL_CHARACTERS = "skuNamePropertyValidatorCannotContainSpecialCharactersError";
    protected String ERROR_KEY_CANNOT_END_WITH_COMMA = "skuNamePropertyValidatorCannotEndWithCommaError";

    @Override
    public PropertyValidationResult validate(
            Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value) {
        if (value == null) {
            return new PropertyValidationResult(true);
        }

        if (getExploitProtection()) {
            value = StringEscapeUtils.unescapeHtml4(value);
        }

        if (value.contains(";")) {
            return new PropertyValidationResult(false, ERROR_KEY_CANNOT_CONTAIN_SEMICOLON);
        }

        if (value.contains("\"") && value.contains(",")) {
            return new PropertyValidationResult(false, ERROR_KEY_CANNOT_CONTAIN_SPECIAL_CHARACTERS);
        }

        if (value.endsWith(",")) {
            return new PropertyValidationResult(false, ERROR_KEY_CANNOT_END_WITH_COMMA);
        }

        return new PropertyValidationResult(true);
    }

    protected static boolean getExploitProtection() {
        return BLCSystemProperty.resolveBooleanSystemProperty("exploitProtection.xssEnabled", false);
    }

}
