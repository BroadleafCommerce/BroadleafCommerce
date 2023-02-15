/*-
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.persistence.validation;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.RuleFieldExtractionUtility;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidationResult;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.ValidationConfigurationBasedPropertyValidator;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Map;

@Component("blOfferQualifyingCriteriaValidator")
public class OfferQualifyingCriteriaValidator extends ValidationConfigurationBasedPropertyValidator {

    public static final String BOGO_TEMPLATE = "-14105";
    @Resource(name = "blRuleFieldExtractionUtility")
    protected RuleFieldExtractionUtility ruleFieldExtractionUtility;


    @Override
    public PropertyValidationResult validate(Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value) {

        Property offerTemplateProperty = entity.findProperty("embeddableAdvancedOffer.offerTemplate");
        if (offerTemplateProperty != null &&
                offerTemplateProperty.getValue().equals(BOGO_TEMPLATE)) {
            String qualifyingItemCriteriaJson = entity.findProperty("qualifyingItemCriteria").getUnHtmlEncodedValue();
            if (qualifyingItemCriteriaJson == null) {
                qualifyingItemCriteriaJson = entity.findProperty("qualifyingItemCriteriaJson").getUnHtmlEncodedValue();
            }
            DataWrapper dw = ruleFieldExtractionUtility.convertJsonToDataWrapper(qualifyingItemCriteriaJson);

            if (dw == null || (CollectionUtils.isEmpty(dw.getData()) && dw.getRawMvel() == null)) {
                return new PropertyValidationResult(false, "requiredValidationFailure");
            }
        }

        return new PropertyValidationResult(true);
    }
}
