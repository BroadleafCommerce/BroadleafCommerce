package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.money.Money;

public interface PromotableOrderAdjustment {

	public void reset();

	public OrderAdjustment getDelegate();

	/*
	 * Calculates the value of the adjustment
	 */
	public void computeAdjustmentValue();

	public Money getValue();
	
}