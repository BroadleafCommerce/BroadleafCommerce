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
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.MinimumTargetsRequired;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferPriceData;
import org.broadleafcommerce.core.offer.domain.OfferTargetCriteriaXref;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

public class PromotableCandidateItemOfferImpl extends AbstractPromotionRounding implements PromotableCandidateItemOffer, OfferHolder {
    
    private static final long serialVersionUID = 1L;
    protected Offer offer;
    protected PromotableOrder promotableOrder;
    protected Money potentialSavings;
    protected Money potentialSavingsQtyOne;
    protected BigDecimal weightedPercentSaved;
    protected Money originalPrice;
    protected int uses = 0;
    protected boolean useQtyOnlyTierCalculation = false;

    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap =
            new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();

    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateTargetsMap =
            new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();

    protected HashMap<OfferPriceData, List<PromotableOrderItem>> candidateFixedTargetsMap = new HashMap<>();
    
    protected List<PromotableOrderItem> legacyCandidateTargets = new ArrayList<PromotableOrderItem>();

    protected int minimumTargetsRequired = 1;

    public PromotableCandidateItemOfferImpl(PromotableOrder promotableOrder, Offer offer) {
        assert (offer != null);
        assert (promotableOrder != null);
        this.offer = offer;
        this.promotableOrder = promotableOrder;
        if (this.offer instanceof MinimumTargetsRequired) {
            setMinimumTargetsRequired(((MinimumTargetsRequired) offer).getMinimumTargetsRequired());
        }
    }

    public PromotableCandidateItemOfferImpl(PromotableOrder promotableOrder, Offer offer, boolean useQtyOnlyTierCalculation) {
        this(promotableOrder, offer);
        this.useQtyOnlyTierCalculation = useQtyOnlyTierCalculation;
    }

    @Override
    public BroadleafCurrency getCurrency() {
        return promotableOrder.getOrderCurrency();
    }

    /**
     * Returns the number of items that potentially could be targets for the offer.   Due to combination or bogo
     * logic, they may not all get the tiered offer price.
     */
    @Override
    public int calculateTargetQuantityForTieredOffer() {
        if (isUseQtyOnlyTierCalculation()) {
            int returnQty = 0;

            for (OfferItemCriteria itemCriteria : getCandidateTargetsMap().keySet()) {
                List<PromotableOrderItem> candidateTargets = getCandidateTargetsMap().get(itemCriteria);
                for (PromotableOrderItem promotableOrderItem : candidateTargets) {
                    returnQty += promotableOrderItem.getQuantity();
                }
            }

            return returnQty;
        } else {
            Integer returnQty = null;

            for (OfferItemCriteria itemCriteria : getCandidateTargetsMap().keySet()) {
                int iterationQty = 0;

                List<PromotableOrderItem> candidateTargets = getCandidateTargetsMap().get(itemCriteria);
                for (PromotableOrderItem promotableOrderItem : candidateTargets) {
                    iterationQty += promotableOrderItem.getQuantity();
                }

                int tmpReturnQty;

                if (itemCriteria.getQuantity() <= 0) {
                    tmpReturnQty = 0;
                } else {
                    tmpReturnQty = (int)Math.floor(iterationQty / itemCriteria.getQuantity());
                }

                if (returnQty == null) {
                    returnQty = tmpReturnQty;
                } else {
                    returnQty = Math.min(tmpReturnQty, returnQty);
                }
            }

            if (returnQty == null) {
                returnQty = 0;
            }

            return returnQty;
        }
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
        return (offer.getQualifyingItemCriteriaXref() != null && !offer.getQualifyingItemCriteriaXref().isEmpty());
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
        for (OfferTargetCriteriaXref targetXref : getOffer().getTargetItemCriteriaXref()) {
            int temp = calculateMaxUsesForItemCriteria(targetXref.getOfferItemCriteria(), getOffer());
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
    public HashMap<OfferPriceData, List<PromotableOrderItem>> getCandidateFixedTargetsMap() {
        return candidateFixedTargetsMap;
    }

    @Override
    public void setCandidateFixedTargetsMap(HashMap<OfferPriceData, List<PromotableOrderItem>> candidateFixedTargetsMap) {
        this.candidateFixedTargetsMap = candidateFixedTargetsMap;
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
    public List<PromotableOrderItem> getLegacyCandidateTargets() {
        return legacyCandidateTargets;
    }

    @Override
    public void setLegacyCandidateTargets(List<PromotableOrderItem> candidateTargets) {
        this.legacyCandidateTargets = candidateTargets;
    }

    @Override
    public BigDecimal getWeightedPercentSaved() {
        if (weightedPercentSaved == null) {
            return new BigDecimal(0);
        }
        return weightedPercentSaved;
    }

    @Override
    public void setWeightedPercentSaved(BigDecimal weightedPercentSaved) {
        this.weightedPercentSaved = weightedPercentSaved;
    }

    @Override
    public Money getOriginalPrice() {
        return originalPrice;
    }

    @Override
    public void setOriginalPrice(Money originalPrice) {
        this.originalPrice = originalPrice;
    }

    /**
     * @see MiniumTargetsRequired
     */
    public void setMinimumTargetsRequired(Integer minimumTargetsRequired) {
        if (minimumTargetsRequired == null || minimumTargetsRequired < 1) {
            this.minimumTargetsRequired = 1;
        } else {
            this.minimumTargetsRequired = minimumTargetsRequired.intValue();
        }
    }

    /**
     * If the offer has a minimum required number of targets, then the first time this
     * offer is processed, return that number.   On subsequent runs, return 1.
     * 
     * @see MinimumTargetsRequired
     * @return
     */
    public int getMinimumRequiredTargetQuantity() {
        if (uses > 0) {
            return 1;
        } else {
            return minimumTargetsRequired;
        }
    }

    @Override
    public boolean isUseQtyOnlyTierCalculation() {
        return useQtyOnlyTierCalculation;
    }

    @Override
    public void setUseQtyOnlyTierCalculation(boolean useQtyOnlyTierCalculation) {
        this.useQtyOnlyTierCalculation = useQtyOnlyTierCalculation;
    }
}
