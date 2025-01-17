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
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.MinimumTargetsRequired;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferPriceData;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface PromotableCandidateItemOffer extends PromotionRounding, Serializable {

    HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap();

    void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap);

    HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateTargetsMap();

    void setCandidateTargetsMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap);

    Money getPotentialSavings();

    void setPotentialSavings(Money savings);

    Money getPotentialSavingsQtyOne();

    void setPotentialSavingsQtyOne(Money potentialSavingsQtyOne);

    boolean hasQualifyingItemCriteria();

    int calculateMaximumNumberOfUses();

    /**
     * Returns the number of item quantities that qualified as targets for
     * this promotion.
     * <p>
     * Uses {@link PromotableCandidateItemOffer#isUseQtyOnlyTierCalculation()} to determine how to calculate.
     * If set to true, this method will use only the quantity in cart for determining which tier level to apply for
     * a group of qualifying items. This is old and likely undesired behavior, and is disabled by default. To turn
     * it back on, use `use.quantity.only.tier.calculation=true` in your `common-shared.properties`.
     * <p>
     * Otherwise, the default behavior is to factor in the number of iterations that the qualifying items have been
     * applied.
     *
     * @return
     */
    int calculateTargetQuantityForTieredOffer();

    /**
     * Determines the max number of times this itemCriteria might apply.    This calculation does
     * not take into account other promotions.   It is useful only to assist in prioritizing the order to process
     * the promotions.
     *
     * @param itemCriteria
     * @param promotion
     * @return
     */
    int calculateMaxUsesForItemCriteria(OfferItemCriteria itemCriteria, Offer promotion);

    HashMap<OfferPriceData, List<PromotableOrderItem>> getCandidateFixedTargetsMap();

    void setCandidateFixedTargetsMap(HashMap<OfferPriceData, List<PromotableOrderItem>> candidateFixedTargetsMap);

    int getPriority();

    Offer getOffer();

    int getUses();

    void addUse();

    /**
     * Resets the uses for this candidate offer item. This is mainly used in the case where we want to calculate savings
     * and then actually apply the promotion to an item. Both scenarios run through the same logic that add uses in order
     * to determine if various quantities of items can be targeted for a particular promotion.
     *
     * @see {@link ItemOfferProcessor#applyAndCompareOrderAndItemOffers(PromotableOrder, List, List)}
     */
    void resetUses();

    List<PromotableOrderItem> getLegacyCandidateTargets();

    void setLegacyCandidateTargets(List<PromotableOrderItem> candidateTargets);

    BigDecimal getWeightedPercentSaved();

    void setWeightedPercentSaved(BigDecimal weightedPercentSaved);

    Money getOriginalPrice();

    void setOriginalPrice(Money originalPrice);

    /**
     * @see MiniumTargetsRequired
     */
    void setMinimumTargetsRequired(Integer minimumTargetsRequired);

    /**
     * Returns the required target quantity for the offer.
     *
     * @return
     * @see MinimumTargetsRequired
     */
    int getMinimumRequiredTargetQuantity();

    /**
     * Returns whether to use quantity only tier calculation.
     * If set to true, {@link PromotableCandidateItemOffer#calculateTargetQuantityForTieredOffer()} will use only the
     * quantity in cart for determining which tier level to apply for a group of qualifying items. This mode
     * is disabled by default. To turn it back on, use
     * `use.quantity.only.tier.calculation=true` in your `common-shared.properties`.
     * <p>
     * Otherwise, the default behavior is to factor in the number of iterations that the qualifying items have been
     * applied.
     *
     * @return
     * @see {@link PromotableCandidateItemOffer#calculateTargetQuantityForTieredOffer()}
     */
    boolean isUseQtyOnlyTierCalculation();

    /**
     * Sets whether to use quantity only tier calculation.
     * If set to true, {@link PromotableCandidateItemOffer#calculateTargetQuantityForTieredOffer()} will use only the
     * quantity in cart for determining which tier level to apply for a group of qualifying items. This mode
     * is disabled by default. To turn it back on, use
     * `use.quantity.only.tier.calculation=true` in your `common-shared.properties`.
     * <p>
     * Otherwise, the default behavior is to factor in the number of iterations that the qualifying items have been
     * applied.
     *
     * @param useQtyOnlyTierCalculation
     * @see {@link PromotableCandidateItemOffer#calculateTargetQuantityForTieredOffer()}
     */
    void setUseQtyOnlyTierCalculation(boolean useQtyOnlyTierCalculation);

}
