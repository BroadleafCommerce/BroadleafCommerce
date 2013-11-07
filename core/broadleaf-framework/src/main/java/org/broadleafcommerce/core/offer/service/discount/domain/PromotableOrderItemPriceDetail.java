/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;

import java.util.List;
import java.util.Set;


public interface PromotableOrderItemPriceDetail {
    
    /**
     * Adds the adjustment to the item's adjustment list and discounts the
     * item's prices by the value of the adjustment.
     * 
     * @param itemAdjustment
     */
    void addCandidateItemPriceDetailAdjustment(PromotableOrderItemPriceDetailAdjustment itemAdjustment);

    /**
     * 
     * @return
     */
    List<PromotableOrderItemPriceDetailAdjustment> getCandidateItemAdjustments();

    /**
     * Returns true if this detail has nonCombinable adjustments.   Used primarily with legacy offers (prior to 2.0)
     * 
     * @return
     */
    boolean hasNonCombinableAdjustments();


    /**
     * Returns true if a notCombinableOffer or totalitarian offer was applied to this priceDetail.
     * @return
     */
    boolean isTotalitarianOfferApplied();
    
    /**
     * Returns true if a non-combinable offer has been applied to this item.
     */
    boolean isNonCombinableOfferApplied();

    /**
     * This method will check to see if the salePriceAdjustments or retailPriceAdjustments are better
     * and remove those that should not apply.
     * @return 
     */
    void chooseSaleOrRetailAdjustments();

    /**
     * Removes all adjustments from this detail.   Typically called when it has been determined
     * that another "totalitarian" offer has been applied.
     */
    void removeAllAdjustments();

    /**
     * Returns the promotion discounts applied to this detail object.
     * @return 
     */
    List<PromotionDiscount> getPromotionDiscounts();
    
    /**
     * Returns the times this item is being used as a promotionQualifier
     * @return 
     */
    List<PromotionQualifier> getPromotionQualifiers();

    /**
     * Returns the quantity associated with this priceDetail.
     * @return
     */
    public int getQuantity();

    /**
     * Sets the quantity for this price detail.
     * @param quantity
     */
    public void setQuantity(int quantity);

    /**
     * Return the parent promotableOrderItem
     */
    PromotableOrderItem getPromotableOrderItem();

    /**
     * Returns the quantity of this item that can be used as a qualifier for the passed in itemOffer
     * @param itemOffer
     * @return
     */
    int getQuantityAvailableToBeUsedAsQualifier(PromotableCandidateItemOffer itemOffer);

    /**
     * Returns the quantity of this item that can be used as a target for the passed in itemOffer
     * @param itemOffer
     * @return
     */
    int getQuantityAvailableToBeUsedAsTarget(PromotableCandidateItemOffer itemOffer);

    /**
     * Adds a promotionQualifier entry to this itemDetail.  PromotionQualifiers record the fact that this item has been 
     * marked to be used as a qualifier for other items to receive a discount.   
     * 
     * If other conditions are met this qualifier will be finalized.
     * @param itemOffer
     * @param itemCriteria
     * @param qtyToMarkAsQualifier
     * @return 
     */
    PromotionQualifier addPromotionQualifier(PromotableCandidateItemOffer itemOffer, OfferItemCriteria itemCriteria, int qtyToMarkAsQualifier);

    /**
     * Adds a promotionDiscount entry to this itemDetail.    PromotionDiscounts record the fact that this item has been 
     * targeted to receive a discount.   If other conditions are met this discount will be finalized so that it can
     * then be set on the underlying orderItem.
     * 
     * @param itemOffer
     * @param itemCriteria
     * @param qtyToMarkAsTarget
     */
    void addPromotionDiscount(PromotableCandidateItemOffer itemOffer, Set<OfferItemCriteria> itemCriteria, int qtyToMarkAsTarget);

    /**
     * Returns the price to be used for this priceDetail taking into account whether or not the
     * sales price can be used. 
     * 
     * @param allowSalePrice
     * @return
     */
    Money calculateItemUnitPriceWithAdjustments(boolean allowSalePrice);

    /**
     * Updates the target and qualifier quantities to indicate the number that are being used.
     */
    void finalizeQuantities();

    /**
     * Clears target and qualifier quantities that were marked for a promotion that did not have
     * enough qualifiers or targets to get applied.
     */
    void clearAllNonFinalizedQuantities();

    /**
     * Creates a key that represents a unique priceDetail
     * @return
     */
    String buildDetailKey();

    /**
     * Returns the final total for this item taking into account the finalized
     * adjustments.    Intended to be called after the adjustments have been
     * finalized with a call {@link #chooseSaleOrRetailAdjustments()}.
     */
    Money getFinalizedTotalWithAdjustments();

    /**
     * Returns the total adjustment value as the sum of the adjustments times the
     * quantity represented by this PriceDetail
     * @return
     */
    Money calculateTotalAdjustmentValue();

    /**
     * Checks to see that the discount quantities match the target quantities.   If not, splits this item 
     * into two.
     * 
     * @return
     */
    PromotableOrderItemPriceDetail splitIfNecessary();
    
    /**
     * Returns true if the sale adjustments should be used.
     * @return
     */
    boolean useSaleAdjustments();

    boolean isAdjustmentsFinalized();

    void setAdjustmentsFinalized(boolean adjustmentsFinalized);

}
