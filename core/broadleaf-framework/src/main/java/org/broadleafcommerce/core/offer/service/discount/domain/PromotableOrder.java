/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.Order;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface PromotableOrder extends Serializable {
    
    /**
     * Sets the order subTotal to the sum of item total prices without
     * adjustments.     
     */
    public void setOrderSubTotalToPriceWithoutAdjustments();

    /**
     * Returns all OrderItems for the order wrapped with PromotableOrderItem
     * @return
     */
    public List<PromotableOrderItem> getAllOrderItems();

    /**
     * Returns all OrderItems that can recieve discounts.
     * @param applyDiscountToSalePrice
     * @return
     */
    public List<PromotableOrderItem> getDiscountableOrderItems(boolean applyDiscountToSalePrice);

    /**
     * Returns the fulfillmentGroups associated with the order after converting them to 
     * promotableFulfillmentGroups.
     * 
     * @return
     */
    public List<PromotableFulfillmentGroup> getFulfillmentGroups();

    /**
     * Returns true if this promotableOrder has any order adjustments.
     * @return
     */
    public boolean isHasOrderAdjustments();

    /**
     * Returns the list of orderAdjustments being proposed for the order.
     * This will be converted to actual order adjustments on completion of the offer processing.
     * @return
     */
    public List<PromotableOrderAdjustment> getCandidateOrderAdjustments();

    /**
     * Adds the adjustment to the order's adjustment list and discounts the
     * order's adjustment price by the value of the adjustment.
     * 
     * @param orderAdjustment
     */
    public void addOrderAdjustments(PromotableOrderAdjustment orderAdjustment);

    /**
     * Returns true if this order contains a notStackable order offer.    
     * @return
     */
    public boolean containsNotStackableOrderOffer();

    /**
     * Adds the underlying order to the rule variable map.
     */
    public void updateRuleVariables(Map<String, Object> ruleVars);

    /**
     * Returns the associated order.
     */
    public Order getOrder();

    /**
     * Loops through adjustments and sets the totalitarian and notCombinableAtAnyLevel indicators.
     */
    void resetTotalitarianOfferApplied();

    /**
     * Returns the subtotal for this promotable order with all order adjustments applied
     */
    Money getSubtotalWithAdjustments();

    //    /**
    //     * Returns true if this order contains a notStackable fulfillmentGroup offer.    
    //     * @return
    //     */
    //    public boolean containsNotStackableFulfillmentGroupOffer();

    //    public Money calculateTaxableItemTotal();
    //
    //    public Money calculateItemTotal();

    //    public boolean isNotCombinableOfferAppliedAtAnyLevel();
    //
    //    public boolean isNotCombinableOfferApplied();
    //
    //    public void resetTotalitarianOfferApplied();
    //
    //    /**
    //     * Adds the adjustment to the order's adjustment list and discounts the
    //     * order's adjustment price by the value of the adjustment.
    //     * 
    //     * @param orderAdjustment
    //     */
    //    public void addOrderAdjustments(PromotableOrderAdjustment orderAdjustment);
    //
    //    /**
    //     * Removes all order, order item, and fulfillment adjustments from the order
    //     * and resets the adjustment price.
    //     */
    //    public void removeAllAdjustments();
    //
    //    /**
    //     * Removes all order adjustments from the order and resets the adjustment
    //     * price. This method does not remove order item or fulfillment adjustments
    //     * from the order.
    //     */
    //    public void removeAllOrderAdjustments();
    //
    //    /**
    //     * Removes all adjustments from the order's order items and resets the
    //     * adjustment price for each item. This method does not remove order or
    //     * fulfillment adjustments from the order.
    //     */
    //    public void removeAllItemAdjustments();
    //
    //    public void removeAllFulfillmentAdjustments();
    //
    //    /**
    //     * Returns the price of the order with the order offers applied (item offers
    //     * are not applied).
    //     * 
    //     * @return the order price with the order offers applied (item offers are
    //     *         not applied)
    //     */
    //    public Money getAdjustmentPrice();
    //
    //    public void setAdjustmentPrice(Money adjustmentPrice);
    //

    //
    //    public boolean isTotalitarianOfferApplied();
    //
    //    public void setTotalitarianOfferApplied(boolean totalitarianOfferApplied);
    //
    //    public void setNotCombinableOfferAppliedAtAnyLevel(boolean notCombinableOfferAppliedAtAnyLevel);
    //    
    //    public void removeAllCandidateOffers();
    //
    //    public void removeAllCandidateOrderOffers();
    //    
    //    public void removeAllCandidateFulfillmentGroupOffers();

    //    
    //    public void removeAllAddedOfferCodes();
    //    
    //    public void addCandidateOrderOffer(PromotableCandidateOrderOffer candidateOrderOffer);
    //    

    //
    //    public void setDelegate(Order order);
    //    
    //    public Money calculateOrderItemsCurrentPrice();
    //    
    //    public Money calculateOrderItemsPriceWithoutAdjustments();
    //    
    //    public void resetFulfillmentGroups();
    //    
    //    public void resetDiscreteOrderItems();
    //    
    //    public Money getSubTotal();
    //    

    //    
    //    public void setTotalShipping(Money totalShipping);
    //    
    //
    //    
    //    public void setSubTotal(Money subTotal);
    //    
    //    public void assignOrderItemsFinalPrice();
    //    
    //    public Customer getCustomer();
    //    
    //    public List<PromotableOrderItem> getDiscreteOrderItems();
    //
    //    public List<BundleOrderItemSplitContainer> getBundleSplitItems();
    //
    //    public void setBundleSplitItems(List<BundleOrderItemSplitContainer> bundleSplitItems);
    //
    //    public List<BundleOrderItem> searchBundleSplitItems(BundleOrderItem key);
    //
    //    public OrderItem searchSplitItemsForKey(OrderItem orderItem);
    //
    //    public List<OrderMultishipOption> getMultiShipOptions();
    //
    //    public void setMultiShipOptions(List<OrderMultishipOption> multiShipOptions);
    //
    //    public boolean isHasMultiShipOptions();
    //
    //    public void setHasMultiShipOptions(boolean hasMultiShipOptions);
}
