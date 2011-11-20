package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.money.Money;

public interface PromotableOrderItemAdjustment {

	public void reset();

	public OrderItemAdjustment getDelegate();

	/*
	 * Calculates the value of the adjustment
	 */
	public void computeAdjustmentValue();

	public Money getValue();
	
}