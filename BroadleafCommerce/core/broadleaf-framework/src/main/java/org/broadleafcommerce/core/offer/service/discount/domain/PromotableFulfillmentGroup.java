package org.broadleafcommerce.core.offer.service.discount.domain;

import java.util.List;

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.money.Money;

public interface PromotableFulfillmentGroup {

	public void reset();

	public FulfillmentGroup getDelegate();

	public List<PromotableOrderItem> getDiscountableDiscreteOrderItems();

	/*
	 * Adds the adjustment to the order item's adjustment list and discounts the order item's adjustment
	 * price by the value of the adjustment.
	 */
	public void addFulfillmentGroupAdjustment(PromotableFulfillmentGroupAdjustment fulfillmentGroupAdjustment);

	public void removeAllAdjustments();

	public Money getPriceBeforeAdjustments(boolean allowSalesPrice);

	public Money getAdjustmentPrice();

	public void setAdjustmentPrice(Money adjustmentPrice);

	public Money getRetailShippingPrice();

	public Money getSaleShippingPrice();
	
	public void removeAllCandidateOffers();
	
	public void setShippingPrice(Money shippingPrice);
	
	public Money getShippingPrice();
	
	public void addCandidateFulfillmentGroupOffer(PromotableCandidateFulfillmentGroupOffer candidateOffer);
	
}