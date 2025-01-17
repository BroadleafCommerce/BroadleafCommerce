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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

import jakarta.annotation.Resource;

@Component("blAdminRegexValidator")
public class AdminRegexValidator extends ValidationConfigurationBasedPropertyValidator {

    public static final String REGEX_CONFIG_PROPERTY = "regex";
    public static final String REGEX_PROPERTY_NAME_CONFIG_PROPERTY = "regexPropertyName";
    protected static final Log LOG = LogFactory.getLog(AdminRegexValidator.class);
    @Resource(name = "blSystemPropertiesService")
    protected SystemPropertiesService propertiesService;

    @Override
    public boolean validateInternal(
            Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value
    ) {
        //if value is empty allow, if someone doesn't want to allow empty values there is RequiredPropertyValidator as an option
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        String regexExpression = validationConfiguration.get(REGEX_CONFIG_PROPERTY);
        if (propertiesService == null && StringUtils.isEmpty(regexExpression)) {
            LOG.error("regex validator for field " + propertyName + " is not applied because dependency is not injected, most probably because you used validationImplementation = " +
                    "\"FULLY_QUALIFIED_CLASSNAME\" instead of validationImplementation = \"SPRING_BEAN_NAME\", and regex was not specified");
            return true;
        }
        if (StringUtils.isNotEmpty(validationConfiguration.get(REGEX_PROPERTY_NAME_CONFIG_PROPERTY))
                && StringUtils.isNotEmpty(propertiesService.resolveSystemProperty(validationConfiguration.get(REGEX_PROPERTY_NAME_CONFIG_PROPERTY)))) {
            regexExpression = propertiesService.resolveSystemProperty(validationConfiguration.get(REGEX_PROPERTY_NAME_CONFIG_PROPERTY));
        }
        if (StringUtils.isEmpty(regexExpression)) {
            LOG.error("regex validator for field " + propertyName + " is not applied because regex not specified neither via regex property nor via regexPropertyName(or property is empty)");
            return true;
        }

        try {
            return value != null && value.matches(regexExpression);
        } catch (Exception e) {
            LOG.error("An error has occurred ", e);
            return false;
        }
    }

}
