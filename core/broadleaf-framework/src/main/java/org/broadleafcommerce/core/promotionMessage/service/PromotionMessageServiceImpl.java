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
package org.broadleafcommerce.core.promotionMessage.service;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.rule.MvelHelper;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferOfferRuleXref;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OfferTargetCriteriaXref;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;
import org.broadleafcommerce.core.promotionMessage.domain.type.PromotionMessageType;
import org.broadleafcommerce.core.promotionMessage.dto.PromotionMessageDTO;
import org.broadleafcommerce.profile.core.dto.CustomerRuleHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blPromotionMessageService")
public class PromotionMessageServiceImpl implements PromotionMessageService {
    
    private static final Log LOG = LogFactory.getLog(PromotionMessageServiceImpl.class);

    @Resource(name="blOfferService")
    protected OfferService offerService;

    @Resource(name = "blOrderItemService")
    private OrderItemService orderItemService;

    @Override
    public Map<String, List<PromotionMessageDTO>> findActivePromotionMessagesForProduct(Product product) {
        Map<String, List<PromotionMessageDTO>> promotionMessages = new MultiValueMap();

        List<Offer> offersWithPromotionMessages = offerService.findActiveOffersWithPromotionMessages();
        for (Offer offer : offersWithPromotionMessages) {
            Set<PromotionMessage> applicablePromotionMessages = findApplicableOfferTargetAndQualifierMessages(product, offer);

            Map<String, List<PromotionMessageDTO>> convertedMessageDTOMap = convertPromotionMessagesToDTOs(applicablePromotionMessages, offer);
            promotionMessages.putAll(convertedMessageDTOMap);
        }

        return promotionMessages;
    }

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

    protected Set<PromotionMessage> findApplicableOfferTargetAndQualifierMessages(Product product, Offer offer) {
        Set<PromotionMessage> promotionMessages = new HashSet<>();

        Set<OfferTargetCriteriaXref> targetItemCriteriaXrefs = offer.getTargetItemCriteriaXref();
        for (OfferTargetCriteriaXref targetCriteriaXref : targetItemCriteriaXrefs) {
            OfferItemCriteria criteria = targetCriteriaXref.getOfferItemCriteria();

            Set<PromotionMessage> applicablePromotionMessages = findApplicablePromotionMessagesByType(product, offer, criteria, PromotionMessageType.TARGETS_ONLY);
            promotionMessages.addAll(applicablePromotionMessages);
        }

        Set<OfferQualifyingCriteriaXref> qualifierItemCriteriaXrefs = offer.getQualifyingItemCriteriaXref();
        for (OfferQualifyingCriteriaXref qualifierCriteriaXref : qualifierItemCriteriaXrefs) {
            OfferItemCriteria criteria = qualifierCriteriaXref.getOfferItemCriteria();

            Set<PromotionMessage> applicablePromotionMessages = findApplicablePromotionMessagesByType(product, offer, criteria, PromotionMessageType.QUALIFIERS_ONLY);
            promotionMessages.addAll(applicablePromotionMessages);
        }

        return promotionMessages;
    }

    protected Set<PromotionMessage> findApplicablePromotionMessagesByType(Product product, Offer offer,
            OfferItemCriteria criteria, PromotionMessageType promotionMessageType) {
        Set<PromotionMessage> promotionMessages = new HashSet<>();

        String matchRule = criteria.getMatchRule();
        if (productPassesMatchRule(product, matchRule)) {
            promotionMessages.addAll(offer.getActivePromotionMessagesByType(promotionMessageType));
        }

        return promotionMessages;
    }

    protected boolean productPassesMatchRule(Product product, String matchRule) {
        if (matchRule == null) return true;

        Map<String, Object> ruleParams = buildRuleParams(product);
        return MvelHelper.evaluateRule(matchRule, ruleParams);
    }

    protected Map<String, Object> buildRuleParams(Product product) {
        HashMap<String, Object> vars = new HashMap<>();
        DiscreteOrderItem orderItem = buildDiscreteOrderItemFromProduct(product);
        vars.put("orderItem", orderItem);
        return vars;
    }

    protected DiscreteOrderItem buildDiscreteOrderItemFromProduct(Product product) {
        DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
        itemRequest.setCategory(product.getCategory());
        itemRequest.setProduct(product);
        itemRequest.setSku(product.getDefaultSku());
        DiscreteOrderItem orderItem = orderItemService.createDiscreteOrderItem(itemRequest);

        return orderItem;
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
