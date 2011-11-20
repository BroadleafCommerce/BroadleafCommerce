package org.broadleafcommerce.core.offer.service.discount.domain;

import java.math.BigDecimal;

import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.money.Money;

public class PromotableFulfillmentGroupAdjustmentImpl implements PromotableFulfillmentGroupAdjustment {

	private static final long serialVersionUID = 1L;
	
	protected PromotableFulfillmentGroup fulfillmentGroup;
	protected FulfillmentGroupAdjustment delegate;
	
	public PromotableFulfillmentGroupAdjustmentImpl(FulfillmentGroupAdjustment fulfillmentGroupAdjustment, PromotableFulfillmentGroup fulfillmentGroup) {
		this.delegate = fulfillmentGroupAdjustment;
		this.fulfillmentGroup = fulfillmentGroup;
	}
	
	public void reset() {
		delegate = null;
	}
	
	public FulfillmentGroupAdjustment getDelegate() {
		return delegate;
	}
	
	/*
     * Calculates the value of the adjustment
     */
    public void computeAdjustmentValue() {
        if (getOffer() != null && fulfillmentGroup != null) {
            Money adjustmentPrice = fulfillmentGroup.getAdjustmentPrice(); // get the current price of the item with all adjustments
            if (adjustmentPrice == null) {
                if ((getOffer().getApplyDiscountToSalePrice()) && (fulfillmentGroup.getSaleShippingPrice() != null)) {
                    adjustmentPrice = fulfillmentGroup.getSaleShippingPrice();
                } else {
                    adjustmentPrice = fulfillmentGroup.getRetailShippingPrice();
                }
            }
            if (getOffer().getDiscountType().equals(OfferDiscountType.AMOUNT_OFF )) {
            	setValue(new Money(delegate.getOffer().getValue()));
            }
            if (getOffer().getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
            	setValue(adjustmentPrice.subtract(new Money(delegate.getOffer().getValue())));
            }
            if (getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
            	setValue(adjustmentPrice.multiply(delegate.getOffer().getValue().divide(new BigDecimal("100"))));
            }
            if (adjustmentPrice.lessThan(getValue())) {
            	setValue(adjustmentPrice);
            }
        }
    }
    
    // FulfillmentGroupAdjustment methods

	public Long getId() {
		return delegate.getId();
	}

	public void setId(Long id) {
		delegate.setId(id);
	}

	public Offer getOffer() {
		return delegate.getOffer();
	}

	public FulfillmentGroup getFulfillmentGroup() {
		return delegate.getFulfillmentGroup();
	}

	public String getReason() {
		return delegate.getReason();
	}

	public void setReason(String reason) {
		delegate.setReason(reason);
	}

	public void init(FulfillmentGroup fulfillmentGroup, Offer offer,
			String reason) {
		delegate.init(fulfillmentGroup, offer, reason);
	}

	public void setValue(Money value) {
		delegate.setValue(value);
	}

	public Money getValue() {
		if (delegate.getValue() == null) {
            computeAdjustmentValue();
        }
		return delegate.getValue();
	}
    
}
