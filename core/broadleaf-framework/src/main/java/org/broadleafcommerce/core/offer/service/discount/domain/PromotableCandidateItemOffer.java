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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.MinimumTargetsRequired;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface PromotableCandidateItemOffer extends Serializable {

    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap();

    public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap);

    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateTargetsMap();

    public void setCandidateTargetsMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap);

    public Money getPotentialSavings();

    public void setPotentialSavings(Money savings);

    public Money getPotentialSavingsQtyOne();

    public void setPotentialSavingsQtyOne(Money potentialSavingsQtyOne);

    public boolean hasQualifyingItemCriteria();

    /**
     * Public only for unit testing - not intended to be called
     */
    public Money calculateSavingsForOrderItem(PromotableOrderItem orderItem, int qtyToReceiveSavings);

    public int calculateMaximumNumberOfUses();
    
    /**
     * Returns the number of item quantities that qualified as targets for 
     * this promotion.
     */
    public int calculateTargetQuantityForTieredOffer();

    /**
     * Determines the max number of times this itemCriteria might apply.    This calculation does 
     * not take into account other promotions.   It is useful only to assist in prioritizing the order to process
     * the promotions. 
     * 
     * @param itemCriteria
     * @param promotion
     * @return
     */
    public int calculateMaxUsesForItemCriteria(OfferItemCriteria itemCriteria, Offer promotion);
    
    public int getPriority();
    
    public Offer getOffer();

    public int getUses();

    public void addUse();
    
    /**
     * Resets the uses for this candidate offer item. This is mainly used in the case where we want to calculate savings
     * and then actually apply the promotion to an item. Both scenarios run through the same logic that add uses in order
     * to determine if various quantities of items can be targeted for a particular promotion.
     * 
     * @see {@link ItemOfferProcessor#applyAndCompareOrderAndItemOffers(PromotableOrder, List, List)}
     */
    public void resetUses();

    public List<PromotableOrderItem> getLegacyCandidateTargets();

    public void setLegacyCandidateTargets(List<PromotableOrderItem> candidateTargets);

    public BigDecimal getWeightedPercentSaved();

    public void setWeightedPercentSaved(BigDecimal weightedPercentSaved);

    public Money getOriginalPrice();

    public void setOriginalPrice(Money originalPrice);

    /**
     * @see MiniumTargetsRequired
     */
    public void setMinimumTargetsRequired(Integer minimumTargetsRequired);

    /**
     * Returns the required target quantity for the offer.
     * @see MinimumTargetsRequired
     * @return
     */
    public int getMinimumRequiredTargetQuantity();

}
