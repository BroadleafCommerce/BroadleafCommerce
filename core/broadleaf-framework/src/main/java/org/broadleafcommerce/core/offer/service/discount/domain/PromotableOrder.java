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

import java.util.List;

import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.manipulation.BundleOrderItemSplitContainer;
import org.broadleafcommerce.core.order.service.manipulation.OrderItemSplitContainer;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.profile.core.domain.Customer;

public interface PromotableOrder {

	public boolean isNotCombinableOfferAppliedAtAnyLevel();

	public boolean isNotCombinableOfferApplied();

	public void resetTotalitarianOfferApplied();

	/**
	 * Adds the adjustment to the order's adjustment list and discounts the
	 * order's adjustment price by the value of the adjustment.
	 * 
	 * @param orderAdjustment
	 */
	public void addOrderAdjustments(PromotableOrderAdjustment orderAdjustment);

	/**
	 * Removes all order, order item, and fulfillment adjustments from the order
	 * and resets the adjustment price.
	 */
	public void removeAllAdjustments();

	/**
	 * Removes all order adjustments from the order and resets the adjustment
	 * price. This method does not remove order item or fulfillment adjustments
	 * from the order.
	 */
	public void removeAllOrderAdjustments();

	/**
	 * Removes all adjustments from the order's order items and resets the
	 * adjustment price for each item. This method does not remove order or
	 * fulfillment adjustments from the order.
	 */
	public void removeAllItemAdjustments();

	public void removeAllFulfillmentAdjustments();

	/**
	 * Returns the price of the order with the order offers applied (item offers
	 * are not applied).
	 * 
	 * @return the order price with the order offers applied (item offers are
	 *         not applied)
	 */
	public Money getAdjustmentPrice();

	public void setAdjustmentPrice(Money adjustmentPrice);

	public boolean isHasOrderAdjustments();

	public boolean isTotalitarianOfferApplied();

	public void setTotalitarianOfferApplied(boolean totalitarianOfferApplied);

	public void setNotCombinableOfferAppliedAtAnyLevel(boolean notCombinableOfferAppliedAtAnyLevel);

	public List<OrderItemSplitContainer> getSplitItems();

	public void setSplitItems(List<OrderItemSplitContainer> splitItems);

	public List<PromotableOrderItem> searchSplitItems(PromotableOrderItem key);
	
	public void removeAllCandidateOffers();

    public void removeAllCandidateOrderOffers();
    
    public void removeAllCandidateFulfillmentGroupOffers();
    
    public boolean containsNotStackableOrderOffer();
    
    public boolean containsNotStackableFulfillmentGroupOffer();
    
    public void removeAllAddedOfferCodes();
    
    public void addCandidateOrderOffer(PromotableCandidateOrderOffer candidateOrderOffer);
    
    public Order getDelegate();
    
    public Money calculateOrderItemsCurrentPrice();
    
    public Money calculateOrderItemsPriceWithoutAdjustments();
    
    public List<PromotableOrderItem> getAllSplitItems();
    
    public List<PromotableOrderItem> getDiscountableDiscreteOrderItems();
    
    public List<PromotableOrderItem> getDiscountableDiscreteOrderItems(boolean applyDiscountToSalePrice);
    
    public void resetFulfillmentGroups();
    
    public void resetDiscreteOrderItems();
    
    public Money getSubTotal();
    
    public List<PromotableFulfillmentGroup> getFulfillmentGroups();
    
    public void setTotalShipping(Money totalShipping);
    
    public Money calculateOrderItemsFinalPrice(boolean includeNonTaxableItems);
    
    public void setSubTotal(Money subTotal);
    
    public void assignOrderItemsFinalPrice();
    
    public Customer getCustomer();
    
    public List<PromotableOrderItem> getDiscreteOrderItems();

    public List<BundleOrderItemSplitContainer> getBundleSplitItems();

    public void setBundleSplitItems(List<BundleOrderItemSplitContainer> bundleSplitItems);

    public List<BundleOrderItem> searchBundleSplitItems(BundleOrderItem key);

}
