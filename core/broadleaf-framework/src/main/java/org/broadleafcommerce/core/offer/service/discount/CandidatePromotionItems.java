/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.offer.service.discount;

import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferPriceData;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jfischer
 */
public class CandidatePromotionItems {

    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap = new HashMap<>();
    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateTargetsMap = new HashMap<>();
    protected HashMap<OfferPriceData, List<PromotableOrderItem>> candidateFixedTargetsMap = new HashMap<>();
    protected boolean isMatchedQualifier = false;
    protected boolean isMatchedTarget = false;

    public void addQualifier(OfferItemCriteria criteria, PromotableOrderItem item) {
        List<PromotableOrderItem> itemList = candidateQualifiersMap.get(criteria);
        if (itemList == null) {
            itemList = new ArrayList<>();
            candidateQualifiersMap.put(criteria, itemList);
        }
        itemList.add(item);
    }

    public void addTarget(OfferItemCriteria criteria, PromotableOrderItem item) {
        List<PromotableOrderItem> itemList = candidateTargetsMap.get(criteria);
        if (itemList == null) {
            itemList = new ArrayList<>();
            candidateTargetsMap.put(criteria, itemList);
        }
        itemList.add(item);
    }

    public void addFixedTarget(OfferPriceData offerPriceData, PromotableOrderItem item) {
        List<PromotableOrderItem> itemList = candidateFixedTargetsMap.get(offerPriceData);
        if (itemList == null) {
            itemList = new ArrayList<>();
            candidateFixedTargetsMap.put(offerPriceData, itemList);
        }
        itemList.add(item);
    }

    public boolean isMatchedQualifier() {
        return isMatchedQualifier;
    }

    public void setMatchedQualifier(boolean isMatchedCandidate) {
        this.isMatchedQualifier = isMatchedCandidate;
    }

    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap() {
        return candidateQualifiersMap;
    }

    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateTargetsMap() {
        return candidateTargetsMap;
    }

    public HashMap<OfferPriceData, List<PromotableOrderItem>> getCandidateFixedTargetsMap() {
        return candidateFixedTargetsMap;
    }

    public boolean isMatchedTarget() {
        return isMatchedTarget;
    }

    public void setMatchedTarget(boolean isMatchedCandidate) {
        this.isMatchedTarget = isMatchedCandidate;
    }

    public Set<PromotableOrderItem> getAllCandidateTargets() {
        Set<PromotableOrderItem> promotableOrderItemSet = new HashSet<>();
        for (List<PromotableOrderItem> orderItems : getCandidateTargetsMap().values()) {
            promotableOrderItemSet.addAll(orderItems);
        }
        for (List<PromotableOrderItem> orderItems : getCandidateFixedTargetsMap().values()) {
            promotableOrderItemSet.addAll(orderItems);
        }
        return promotableOrderItemSet;
    }

}
