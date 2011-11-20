package org.broadleafcommerce.core.offer.service.discount.domain;

import java.math.BigDecimal;

import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.money.Money;

public class PromotableOrderAdjustmentImpl implements PromotableOrderAdjustment {
	
	private static final long serialVersionUID = 1L;
	
	protected OrderAdjustment delegate;
	protected PromotableOrder order;
	
	public PromotableOrderAdjustmentImpl(OrderAdjustment orderAdjustment, PromotableOrder order) {
		this.delegate = orderAdjustment;
		this.order = order;
	}
	
	public void reset() {
		delegate = null;
	}
	
	public OrderAdjustment getDelegate() {
		return delegate;
	}

	/*
     * Calculates the value of the adjustment
     */
    public void computeAdjustmentValue() {
        if (delegate.getOffer() != null && order != null) {
            Money adjustmentPrice = order.getAdjustmentPrice(); // get the current price of the item with all adjustments
            if (adjustmentPrice == null) {
                adjustmentPrice = order.getSubTotal();
            }
            if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
            	delegate.setValue(new Money(delegate.getOffer().getValue()));
            }
            if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
            	delegate.setValue(adjustmentPrice.subtract(new Money(delegate.getOffer().getValue())));
            }
            if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
            	delegate.setValue(adjustmentPrice.multiply(delegate.getOffer().getValue().divide(new BigDecimal("100"))));
            }
            if (adjustmentPrice.lessThan(delegate.getValue())) {
            	delegate.setValue(adjustmentPrice);
            }
        }
    }
    
    public Money getValue() {
		if (delegate.getValue() == null) {
			computeAdjustmentValue();
		}
		return delegate.getValue();
	}
}
