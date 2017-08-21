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

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.rule.QuantityBasedRule;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.RuleFieldExtractionUtility;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.RuleFieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTOToMVELTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

/**
 * Validates that a rule can be parsed out successfully. Most of this comes from {@link RuleFieldPersistenceProvider}.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blRuleFieldValidator")
public class RuleFieldValidator implements PopulateValueRequestValidator {

    @Resource(name = "blRuleFieldExtractionUtility")
    protected RuleFieldExtractionUtility ruleFieldExtractionUtility;
    
    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;
    
    @Override
    public PropertyValidationResult validate(PopulateValueRequest populateValueRequest, Serializable instance) {
        if (canHandleValidation(populateValueRequest)) {
            DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
            EntityManager em = populateValueRequest.getPersistenceManager().getDynamicEntityDao().getStandardEntityManager();
            if (SupportedFieldType.RULE_SIMPLE.equals(populateValueRequest.getMetadata().getFieldType()) ||
                    SupportedFieldType.RULE_SIMPLE_TIME.equals(populateValueRequest.getMetadata().getFieldType())) {
                
                //AntiSamy HTML encodes the rule JSON - pass the unHTMLEncoded version
                DataWrapper dw = ruleFieldExtractionUtility.convertJsonToDataWrapper(populateValueRequest.getProperty().getUnHtmlEncodedValue());
                if (dw != null && StringUtils.isNotEmpty(dw.getError())) {
                    return new PropertyValidationResult(false, "Could not serialize JSON from rule builder: " + dw.getError());
                }
                if (dw == null || StringUtils.isEmpty(dw.getError())) {
                    try {
                        String mvel = ruleFieldExtractionUtility.convertSimpleMatchRuleJsonToMvel(translator, RuleIdentifier.ENTITY_KEY_MAP.get(populateValueRequest.getMetadata().getRuleIdentifier()),
                                populateValueRequest.getMetadata().getRuleIdentifier(), dw);
                    } catch (MVELTranslationException e) {
                        return new PropertyValidationResult(false, getMvelParsingErrorMesage(dw, e));
                    }
                }
            }
            
            if (SupportedFieldType.RULE_WITH_QUANTITY.equals(populateValueRequest.getMetadata().getFieldType())) {
                Collection<QuantityBasedRule> existingRules;
                try {
                    existingRules = (Collection<QuantityBasedRule>) populateValueRequest.getFieldManager().getFieldValue
                            (instance, populateValueRequest.getProperty().getName());
                } catch (FieldNotAvailableException e) {
                    return new PropertyValidationResult(false, "Could not access rule field on Java object to set values");
                } catch (IllegalAccessException e) {
                    return new PropertyValidationResult(false, "Could not access rule field on Java object to set values");
                }
                
                String entityKey = RuleIdentifier.ENTITY_KEY_MAP.get(populateValueRequest.getMetadata().getRuleIdentifier());
                String jsonPropertyValue = populateValueRequest.getProperty().getUnHtmlEncodedValue();
                String fieldService = populateValueRequest.getMetadata().getRuleIdentifier();
                if (!StringUtils.isEmpty(jsonPropertyValue)) {
                    DataWrapper dw = ruleFieldExtractionUtility.convertJsonToDataWrapper(jsonPropertyValue);
                    if (dw != null && StringUtils.isNotEmpty(dw.getError())) {
                        return new PropertyValidationResult(false, "Could not serialize JSON from rule builder: " + dw.getError());
                    }
                    if (dw != null && StringUtils.isEmpty(dw.getError())) {
                        for (DataDTO dto : dw.getData()) {
                            if (dto.getPk() != null) {
                                boolean foundIdToUpdate = false;
                                for (QuantityBasedRule quantityBasedRule : existingRules) {
                                    Long sandBoxVersionId = sandBoxHelper.getSandBoxVersionId(quantityBasedRule.getClass(), dto.getPk());
                                    if (sandBoxVersionId == null) {
                                        sandBoxVersionId = dto.getPk();
                                    }
                                    if (sandBoxVersionId.equals(quantityBasedRule.getId()) || sandBoxHelper.isRelatedToParentCatalogIds(quantityBasedRule, dto.getPk())) {
                                        foundIdToUpdate = true;
                                        try {
                                            String mvel = ruleFieldExtractionUtility.convertDTOToMvelString(translator, entityKey, dto, fieldService);
                                        } catch (MVELTranslationException e) {
                                            return new PropertyValidationResult(false, getMvelParsingErrorMesage(dw, e));
                                        }
                                    }
                                }
                                if (!foundIdToUpdate) {
                                    return new PropertyValidationResult(false, "Tried to update QuantityBasedRule with ID " + dto.getPk() + " but that rule does not exist");
                                }
                            } else {
                                // This is a new rule, just validate that it parses successfully
                                try {
                                    ruleFieldExtractionUtility.convertDTOToMvelString(translator, entityKey, dto, fieldService);
                                } catch (MVELTranslationException e) {
                                    return new PropertyValidationResult(false, getMvelParsingErrorMesage(dw, e));
                                }
                            }
                        }
                    }
                }
            }
        }
        return new PropertyValidationResult(true);
    }
    
    protected String getMvelParsingErrorMesage(DataWrapper dw, MVELTranslationException e) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Problem translating rule builder, error code ");
        errorMessage.append(e.getCode());
        errorMessage.append(": ");
        errorMessage.append(e.getMessage());
        return errorMessage.toString();
    }
    
    protected boolean canHandleValidation(PopulateValueRequest populateValueRequest) {
        return populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY ||
                populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE ||
                populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE_TIME;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }
}
