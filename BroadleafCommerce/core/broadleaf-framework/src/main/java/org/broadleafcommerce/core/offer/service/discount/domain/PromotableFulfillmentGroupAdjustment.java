package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.money.Money;

public interface PromotableFulfillmentGroupAdjustment {

	public void reset();

	public FulfillmentGroupAdjustment getDelegate();

	/*
	 * Calculates the value of the adjustment
	 */
	public void computeAdjustmentValue();

	public Money getValue();
	
}