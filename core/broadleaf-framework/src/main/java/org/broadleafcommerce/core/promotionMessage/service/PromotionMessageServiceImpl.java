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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferTargetCriteriaXref;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;
import org.broadleafcommerce.core.promotionMessage.domain.type.PromotionMessageType;
import org.broadleafcommerce.core.promotionMessage.service.extension.PromotionMessageServiceExtensionManager;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blPromotionMessageService")
public class PromotionMessageServiceImpl implements PromotionMessageService {
    
    private static final Log LOG = LogFactory.getLog(PromotionMessageServiceImpl.class);

    @Resource(name = "blPromotionMessageServiceExtensionManager")
    protected PromotionMessageServiceExtensionManager extensionManager;

    @Resource(name="blOfferService")
    protected OfferService offerService;

    @Override
    public Set<PromotionMessage> findActivePromotionMessagesForProduct(Product product) {
        Comparator<? super PromotionMessage> promotionMessageComparator = buildPromotionMessageComparator();
        Set<PromotionMessage> promotionMessages = new TreeSet<>(promotionMessageComparator);

        List<Offer> offersWithPromotionMessages = offerService.findActiveOffersWithPromotionMessages();
        for (Offer offer : offersWithPromotionMessages) {
            Set<PromotionMessage> applicablePromotionMessages = findApplicableOfferTargetAndQualifierMessages(product, offer);
            promotionMessages.addAll(applicablePromotionMessages);
        }

        return promotionMessages;
    }

    protected Comparator<? super PromotionMessage> buildPromotionMessageComparator() {
        return new Comparator<PromotionMessage>() {
            @Override
            public int compare(PromotionMessage pm1, PromotionMessage pm2) {
                return ObjectUtils.compare(pm1.getPriority(), pm2.getPriority());
            }
        };
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

        if (isCategoryRule(criteria) && productQualifiesForCategoryCriteria(product, criteria)) {
            promotionMessages.addAll(offer.getPromotionMessagesByType(promotionMessageType));
        } else if (isProductGroupRule(criteria) && productQualifiesForProductGroupCriteria(product, criteria)) {
            promotionMessages.addAll(offer.getPromotionMessagesByType(promotionMessageType));
        } else if (isProductRule(criteria) && productQualifiesForProductCriteria(product, criteria)) {
            promotionMessages.addAll(offer.getPromotionMessagesByType(promotionMessageType));
        }

        return promotionMessages;
    }

    /**
     * Expected rule format:
     * CollectionUtils.intersection(orderItem.?embeddableMerchandisingGroupOrderItem.?applicableMerchandisingCategory, ["1"]).size()>0
     *
     * @param criteria
     * @return whether or not the rule applies to a {@link org.broadleafcommerce.core.catalog.domain.Category}
     */
    protected boolean isCategoryRule(OfferItemCriteria criteria) {
        String matchRule = criteria.getMatchRule();
        if (matchRule == null) return false;

        String simplifiedMatchRule = simplifyMatchRule(matchRule);
        return simplifiedMatchRule.startsWith("CollectionUtils.intersection(orderItem.embeddableMerchandisingGroupOrderItem.applicableMerchandisingCategory,");
    }

    protected boolean productQualifiesForCategoryCriteria(Product product, OfferItemCriteria criteria) {
        String matchRule = criteria.getMatchRule();
        List<String> matchRuleValues = gatherValueListFromMatchRule(matchRule);
        List<Long> parentCategoryHierarchyIds = product.getParentCategoryHierarchyIds();

        if (CollectionUtils.isNotEmpty(matchRuleValues) && CollectionUtils.isNotEmpty(parentCategoryHierarchyIds)) {
            for (String matchRuleValue : matchRuleValues) {
                for (Long categoryId : parentCategoryHierarchyIds) {
                    if (StringUtils.equals(matchRuleValue, categoryId.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Expected rule format:
     * CollectionUtils.intersection(orderItem.?embeddableMerchandisingGroupOrderItem.?applicableMerchandisingProductGroups,["3"]).size()>0
     *
     * @param criteria
     * @return whether or not the rule applies to a ProductGroup
     */
    protected boolean isProductGroupRule(OfferItemCriteria criteria) {
        String matchRule = criteria.getMatchRule();
        if (matchRule == null) return false;

        String simplifiedMatchRule = simplifyMatchRule(matchRule);
        return simplifiedMatchRule.startsWith("CollectionUtils.intersection(orderItem.embeddableMerchandisingGroupOrderItem.applicableMerchandisingProductGroups,");
    }

    protected boolean productQualifiesForProductGroupCriteria(Product product, OfferItemCriteria criteria) {
        String matchRule = criteria.getMatchRule();
        List<String> matchRuleValues = gatherValueListFromMatchRule(matchRule);

        ExtensionResultHolder<List<Long>> erh = new ExtensionResultHolder<>();
        if (extensionManager != null) {
            extensionManager.getProxy().findProductGroupIdsForProduct(erh, product);
        }
        List<Long> productGroupIds = erh.getResult();

        if (CollectionUtils.isNotEmpty(matchRuleValues) && CollectionUtils.isNotEmpty(productGroupIds)) {
            for (String matchRuleValue : matchRuleValues) {
                for (Long productGroupId : productGroupIds) {
                    if (StringUtils.equals(matchRuleValue, productGroupId.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Expected rule format:
     * CollectionUtils.intersection(orderItem.?name,["Sudden Death Sauce"]).size()>0
     *
     * @param criteria
     * @return whether or not the rule applies to a Product
     */
    protected boolean isProductRule(OfferItemCriteria criteria) {
        String matchRule = criteria.getMatchRule();
        if (matchRule == null) return false;

        String simplifiedMatchRule = simplifyMatchRule(matchRule);
        return simplifiedMatchRule.startsWith("CollectionUtils.intersection(orderItem.");
    }

    protected boolean productQualifiesForProductCriteria(Product product, OfferItemCriteria criteria) {
        return false;
    }

    protected String simplifyMatchRule(String matchRule) {
        return matchRule.replace("?", "");
    }

    protected List<String> gatherValueListFromMatchRule(String matchRule) {
        int startOfValuesList = matchRule.indexOf("\"") + 1;
        int endOfValuesList = matchRule.lastIndexOf("\"");
        String[] values = matchRule.substring(startOfValuesList, endOfValuesList).replaceAll("\\s", "").split(",");

        return Arrays.asList(values);
    }
}
