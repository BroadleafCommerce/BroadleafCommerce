/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.persistence.validation;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.RuleFieldExtractionUtility;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidationResult;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.ValidationConfigurationBasedPropertyValidator;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Checks to make sure that the TargetItemCriteria is not empty if required
 * 
 * @author Jaci Eckert
 */
@Component("blOfferTargetCriteriaItemValidator")
public class OfferTargetItemCriteriaValidator extends ValidationConfigurationBasedPropertyValidator {

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

        Property offerTypeProperty = entity.findProperty("type");
        if(OfferType.ORDER_ITEM.getType().equals(offerTypeProperty.getValue())) {
            String targetItemCriteriaJson = entity.findProperty("targetItemCriteria").getUnHtmlEncodedValue();
            if (targetItemCriteriaJson == null) {
                targetItemCriteriaJson = entity.findProperty("targetItemCriteriaJson").getUnHtmlEncodedValue();
            }
            DataWrapper dw = ruleFieldExtractionUtility.convertJsonToDataWrapper(targetItemCriteriaJson);

            if (CollectionUtils.isEmpty(dw.getData()) && dw.getRawMvel() == null) {
                return new PropertyValidationResult(false, "requiredValidationFailure");
            }
        }

        return new PropertyValidationResult(true);
    }
}
