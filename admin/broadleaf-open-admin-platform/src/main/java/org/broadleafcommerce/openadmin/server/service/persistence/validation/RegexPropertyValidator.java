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
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.util.ApplicationContextHolder;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import jakarta.annotation.Resource;

/**
 * Validates a field against a configured 'regularExpression' item
 *
 * @author Phillip Verheyden
 */
@Component("blRegexPropertyValidator")
public class RegexPropertyValidator extends ValidationConfigurationBasedPropertyValidator {

    protected static final Log LOG = LogFactory.getLog(RegexPropertyValidator.class);

    /**
     * Denotes what should occur when this validator encounters a null value to validate against. Default behavior is to
     * allow them, which means that this validator will always return true with null values
     */
    protected boolean succeedForNullValues = true;

    /**
     * Whether or not this validator should succeed (and thus pass validation) even when the configured regular expression
     * pattern is invalid. While this value defaults to true, it might be beneficial to set this to false in a development
     * environment to debug problems in regular expressions. In either case, this validator will log that there is an invalid
     * regular expression
     */
    protected boolean succeedForInvalidRegex = true;

    @Resource(name = "blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    @Override
    public PropertyValidationResult validate(Entity entity,
                                             Serializable instance,
                                             Map<String, FieldMetadata> entityFieldMetadata,
                                             Map<String, String> validationConfiguration,
                                             BasicFieldMetadata propertyMetadata,
                                             String propertyName,
                                             String value) {
        String expression = validationConfiguration.get("regularExpression");

        if (value == null) {
            return new PropertyValidationResult(succeedForNullValues, validationConfiguration.get(ConfigurationItem.ERROR_MESSAGE));
        }

        if (exploitProtectionService == null) {
            try {
                exploitProtectionService = this.initExploitProtectionService();
            } catch (Exception e) {
                LOG.error("ExploitProtectionService is missing and failed to initialize on fly so results of RegexPropertyValidator can be not accurate, please use bean name blRegexPropertyValidator in your AdminPresentation configuration instead of class name for validator implementation");
                return new PropertyValidationResult(
                        value.matches(expression),
                        validationConfiguration.get(ConfigurationItem.ERROR_MESSAGE)
                );
            }
        }

        try {
            return new PropertyValidationResult(
                    this.exploitProtectionService.htmlDecode(value).matches(expression),
                    validationConfiguration.get(ConfigurationItem.ERROR_MESSAGE)
            );
        } catch (PatternSyntaxException e) {
            String message = "Invalid regular expression pattern '" + StringUtil.sanitize(expression) + "' for "
                    + StringUtil.sanitize(propertyName);
            LOG.error(message, e);
            return new PropertyValidationResult(!succeedForInvalidRegex, "Invalid regular expression pattern for " + propertyName);
        }
    }

    protected ExploitProtectionService initExploitProtectionService() throws ServiceException {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();

        if (applicationContext == null) {
            throw new ServiceException("Can't access application context to initialize blExploitProtectionService bean");
        }

        ExploitProtectionService blExploitProtectionService = applicationContext.getBean(
                "blExploitProtectionService", ExploitProtectionService.class
        );

        if (blExploitProtectionService == null) {
            throw new ServiceException("Bean blExploitProtectionService is missing in spring application context");
        }
        return blExploitProtectionService;
    }

    public boolean isSucceedForNullValues() {
        return succeedForNullValues;
    }

    public void setSucceedForNullValues(boolean succeedForNullValues) {
        this.succeedForNullValues = succeedForNullValues;
    }

    public boolean isSucceedForInvalidRegex() {
        return succeedForInvalidRegex;
    }

    public void setSucceedForInvalidRegex(boolean succeedForInvalidRegex) {
        this.succeedForInvalidRegex = succeedForInvalidRegex;
    }

}
