/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PromotableCandidateItemOfferImpl extends AbstractPromotionRounding implements PromotableCandidateItemOffer, OfferHolder {
    
    private static final long serialVersionUID = 1L;
    protected Offer offer;
    protected PromotableOrder promotableOrder;
    protected Money potentialSavings;
    protected Money potentialSavingsQtyOne;
    protected int uses = 0;
    
    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap =
            new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();

    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateTargetsMap =
            new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();
    
    protected List<PromotableOrderItem> legacyCandidateTargets = new ArrayList<PromotableOrderItem>();

    public PromotableCandidateItemOfferImpl(PromotableOrder promotableOrder, Offer offer) {
        assert (offer != null);
        assert (promotableOrder != null);
        this.offer = offer;
        this.promotableOrder = promotableOrder;
    }

    @Override
    public BroadleafCurrency getCurrency() {
        return promotableOrder.getOrderCurrency();
    }

    @Override
    public Money calculateSavingsForOrderItem(PromotableOrderItem orderItem, int qtyToReceiveSavings) {
        Money savings = new Money(promotableOrder.getOrderCurrency());
        Money price = orderItem.getPriceBeforeAdjustments(getOffer().getApplyDiscountToSalePrice());

        BigDecimal offerUnitValue = PromotableOfferUtility.determineOfferUnitValue(offer, this);
        savings = PromotableOfferUtility.computeAdjustmentValue(price, offerUnitValue, this, this);
        return savings.multiply(qtyToReceiveSavings);
    }

    /**
     * Returns the number of items that potentially could be targets for the offer.   Due to combination or bogo
     * logic, they may not all get the tiered offer price.
     */
    @Override
    public int calculateTargetQuantityForTieredOffer() {
        int returnQty = 0;

        for (OfferItemCriteria itemCriteria : getCandidateTargetsMap().keySet()) {
            List<PromotableOrderItem> candidateTargets = getCandidateTargetsMap().get(itemCriteria);
            for (PromotableOrderItem promotableOrderItem : candidateTargets) {
                returnQty += promotableOrderItem.getQuantity();
            }
        }

        return returnQty;
    }

    @Override
    public Money getPotentialSavings() {
        if (potentialSavings == null) {
            return new Money(promotableOrder.getOrderCurrency());
        }
        return potentialSavings;
    }

    @Override
    public void setPotentialSavings(Money potentialSavings) {
        this.potentialSavings = potentialSavings;
    }

    @Override
    public Money getPotentialSavingsQtyOne() {
        if (potentialSavingsQtyOne == null) {
            return new Money(promotableOrder.getOrderCurrency());
        }
        return potentialSavingsQtyOne;
    }

    @Override
    public void setPotentialSavingsQtyOne(Money potentialSavingsQtyOne) {
        this.potentialSavingsQtyOne = potentialSavingsQtyOne;
    }

    @Override
    public boolean hasQualifyingItemCriteria() {
        return (offer.getQualifyingItemCriteria() != null && !offer.getQualifyingItemCriteria().isEmpty());
    }

    /**
     * Determines the maximum number of times this promotion can be used based on the
     * ItemCriteria and promotion's maxQty setting.
     */
    @Override
    public int calculateMaximumNumberOfUses() {     
        int maxMatchesFound = 9999; // set arbitrarily high / algorithm will adjust down

        //iterate through the target criteria and find the least amount of max uses. This will be the overall
        //max usage, since the target criteria are grouped together in "and" style.
        int numberOfUsesForThisItemCriteria = maxMatchesFound;
        for (OfferItemCriteria targetCriteria : getOffer().getTargetItemCriteria()) {
            int temp = calculateMaxUsesForItemCriteria(targetCriteria, getOffer());
            numberOfUsesForThisItemCriteria = Math.min(numberOfUsesForThisItemCriteria, temp);
        }

        maxMatchesFound = Math.min(maxMatchesFound, numberOfUsesForThisItemCriteria);
        int offerMaxUses = getOffer().isUnlimitedUsePerOrder() ? maxMatchesFound : getOffer().getMaxUsesPerOrder();

        return Math.min(maxMatchesFound, offerMaxUses);
    }
    
    @Override
    public int calculateMaxUsesForItemCriteria(OfferItemCriteria itemCriteria, Offer promotion) {
        int numberOfTargets = 0;
        int numberOfUsesForThisItemCriteria = 9999;
        
        if (itemCriteria != null) {
            List<PromotableOrderItem> candidateTargets = getCandidateTargetsMap().get(itemCriteria);
            for(PromotableOrderItem potentialTarget : candidateTargets) {
                numberOfTargets += potentialTarget.getQuantity();
            }
            numberOfUsesForThisItemCriteria = numberOfTargets / itemCriteria.getQuantity();
        }
        
        return numberOfUsesForThisItemCriteria;
    }
    
    @Override
    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap() {
        return candidateQualifiersMap;
    }

    @Override
    public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap) {
        this.candidateQualifiersMap = candidateItemsMap;
    }

    @Override
    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateTargetsMap() {
        return candidateTargetsMap;
    }

    @Override
    public void setCandidateTargetsMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap) {
        this.candidateTargetsMap = candidateItemsMap;
    }

    @Override
    public int getPriority() {
        return offer.getPriority();
    }
    
    @Override
    public Offer getOffer() {
        return offer;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public void addUse() {
        uses++;
    }
    
    @Override
    public void resetUses() {
        uses = 0;
    }

    @Override
    public boolean isLegacyOffer() {
        return offer.getQualifyingItemCriteria().isEmpty() && offer.getTargetItemCriteria().isEmpty();
    }

    @Override
    public List<PromotableOrderItem> getLegacyCandidateTargets() {
        return legacyCandidateTargets;
    }

    @Override
    public void setLegacyCandidateTargets(List<PromotableOrderItem> candidateTargets) {
        this.legacyCandidateTargets = candidateTargets;
    }
}
