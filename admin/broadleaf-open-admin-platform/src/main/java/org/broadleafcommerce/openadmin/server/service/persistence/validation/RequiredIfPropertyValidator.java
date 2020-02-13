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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;


/**
 * Makes a field required if the value of another field matches another value.
 * Designed for use where selecting a radio makes another field required.
 * 
 * This validator supports two approaches.   For both approaches, use compareField to indicate
 * the property name you want to compare ...
 * 
 * To compare against a specific value, also provide a "compareFieldValue" attribute.
 * 
 * To compare against a specific fieldName, also provide a "compareFieldName" attribute 
 * 
 * @author Brian Polster
 */
@Component("blRequiredIfPropertyValidator")
public class RequiredIfPropertyValidator extends ValidationConfigurationBasedPropertyValidator {

    protected static final Log LOG = LogFactory.getLog(RequiredIfPropertyValidator.class);

    @Override
    public PropertyValidationResult validate(Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value) {

        String compareFieldName = lookupCompareFieldName(propertyName, validationConfiguration);
        String errorMessage = validationConfiguration.get("errorMessage");
        String compareFieldValue = validationConfiguration.get("compareFieldValue");
        String compareFieldRegEx = validationConfiguration.get("compareFieldRegEx");
        Property compareFieldProperty = null;

        boolean valid = true;
        if (StringUtils.isEmpty(value)) {
            compareFieldProperty = entity.getPMap().get(compareFieldName);

            if (compareFieldProperty != null) {
                if (compareFieldValue != null) {
                    valid = !compareFieldValue.equals(compareFieldProperty.getValue());
                } else if (compareFieldRegEx != null && compareFieldProperty.getValue() != null) {
                    String expression = validationConfiguration.get("compareFieldRegEx");
                    valid = !compareFieldProperty.getValue().matches(expression);
                }

            }
        }

        if (!valid) {
            BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
            MessageSource messages = context.getMessageSource();

            FieldMetadata fmd = entityFieldMetadata.get(compareFieldName);
            String fieldName = messages.getMessage(fmd.getFriendlyName(), null, context.getJavaLocale());

            if (StringUtils.isBlank(errorMessage)) {
                errorMessage = messages.getMessage("requiredIfValidationFailure",
                        new Object[] { fieldName, compareFieldProperty.getValue() },
                        context.getJavaLocale());
            }

        }

        return new PropertyValidationResult(valid, errorMessage);
    }

    protected String lookupCompareFieldName(String currentFieldName, Map<String, String> validationConfiguration) {
        String compareFieldName = validationConfiguration.get("compareField");
        if (currentFieldName.contains(".")) {
            String prefix = currentFieldName.substring(0, currentFieldName.lastIndexOf('.') + 1);
            return prefix + compareFieldName;
        } else {
            return compareFieldName;
        }
    }
}
