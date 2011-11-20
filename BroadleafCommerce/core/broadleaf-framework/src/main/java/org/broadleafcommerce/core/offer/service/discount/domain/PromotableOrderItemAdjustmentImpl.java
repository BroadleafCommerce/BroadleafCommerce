package org.broadleafcommerce.core.offer.service.discount.domain;

import java.math.BigDecimal;

import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.money.Money;

public class PromotableOrderItemAdjustmentImpl implements PromotableOrderItemAdjustment {
	
	private static final long serialVersionUID = 1L;
	
	protected PromotableOrderItem orderItem;
	protected OrderItemAdjustment delegate;
	
	public PromotableOrderItemAdjustmentImpl(OrderItemAdjustment orderItemAdjustment, PromotableOrderItem orderItem) {
		this.delegate = orderItemAdjustment;
		this.orderItem = orderItem;
	}
	
	public void reset() {
		delegate = null;
	}
	
	public OrderItemAdjustment getDelegate() {
		return delegate;
	}

	/*
     * Calculates the value of the adjustment
     */
    public void computeAdjustmentValue() {
        if (delegate.getOffer() != null && orderItem != null) {
            Money adjustmentPrice = orderItem.getAdjustmentPrice(); // get the current price of the item with all adjustments
            if (adjustmentPrice == null) {
                if ((delegate.getOffer().getApplyDiscountToSalePrice()) && (orderItem.getSalePrice() != null)) {
                    adjustmentPrice = orderItem.getSalePrice();
                } else {
                    adjustmentPrice = orderItem.getRetailPrice();
                }
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
