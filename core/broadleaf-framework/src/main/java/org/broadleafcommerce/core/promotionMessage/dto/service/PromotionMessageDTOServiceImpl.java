/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.promotionMessage.dto.service;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferOfferRuleXref;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;
import org.broadleafcommerce.core.promotionMessage.dto.PromotionMessageDTO;
import org.broadleafcommerce.profile.core.dto.CustomerRuleHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blPromotionMessageDTOService")
public class PromotionMessageDTOServiceImpl implements PromotionMessageDTOService {
    
    private static final Log LOG = LogFactory.getLog(PromotionMessageDTOServiceImpl.class);

    @Override
    public Map<String, List<PromotionMessageDTO>> convertPromotionMessagesToDTOs(Set<PromotionMessage> promotionMessages) {
        return convertPromotionMessagesToDTOs(promotionMessages, null);
    }

    @Override
    public Map<String, List<PromotionMessageDTO>> convertPromotionMessagesToDTOs(Set<PromotionMessage> promotionMessages, Offer offer) {
        MultiValueMap promotionMessageDTOs = new MultiValueMap();

        for (PromotionMessage message : promotionMessages) {
            PromotionMessageDTO dto = new PromotionMessageDTO(message);

            CustomerRuleHolder customerRuleHolder = buildCustomerRuleHolder(offer);
            dto.setCustomerRuleHolder(customerRuleHolder);

            promotionMessageDTOs.put(dto.getMessagePlacement(), dto);
        }

        return promotionMessageDTOs;
    }

    protected CustomerRuleHolder buildCustomerRuleHolder(Offer offer) {
        String customerRule = getCustomerRule(offer);
        return new CustomerRuleHolder(customerRule);
    }

    protected String getCustomerRule(Offer offer) {
        if (offer != null) {
            Map<String, OfferOfferRuleXref> offerMatchRuleXrefs = offer.getOfferMatchRulesXref();
            OfferOfferRuleXref customerRuleXref = offerMatchRuleXrefs.get(RuleIdentifier.CUSTOMER_FIELD_KEY);

            if (customerRuleXref != null && customerRuleXref.getOfferRule() != null) {
                OfferRule customerOfferRule = customerRuleXref.getOfferRule();
                return customerOfferRule.getMatchRule();
            }
        }

        return null;
    }
}
