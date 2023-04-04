/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

@Component("blAdminUserRegexValidator")
public class AdminUserRegexValidator extends ValidationConfigurationBasedPropertyValidator{

    protected static final Log LOG = LogFactory.getLog(AdminUserRegexValidator.class);

    String regex;

    @Value("${admin.password.regex.validator.enabled:false}")
    protected Boolean enableRegexForAdminUser = false;

    @Value("${admin.password.regex.validation}")
    String adminPasswordRegexPattern;


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
            return new PropertyValidationResult(enableRegexForAdminUser, validationConfiguration.get(ConfigurationItem.ERROR_MESSAGE));
        }

        try {
            return new PropertyValidationResult(
                    regex.matches(expression),
                    validationConfiguration.get(ConfigurationItem.ERROR_MESSAGE)
            );
        } catch (PatternSyntaxException e) {
            String message = "Invalid regular expression pattern '" + StringUtil.sanitize(expression) + "' for "
                    + StringUtil.sanitize(propertyName);
            LOG.error(message, e);
            return new PropertyValidationResult(!enableRegexForAdminUser, "Invalid regular expression pattern for " + propertyName);
        }
    }

}
