/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.service.discount.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
            	delegate.setValue(new Money(delegate.getOffer().getValue(), adjustmentPrice.getCurrency(), 5));
            }
            if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
                BigDecimal offerValue = adjustmentPrice.getAmount().subtract(delegate.getOffer().getValue());
            	delegate.setValue(new Money(offerValue, adjustmentPrice.getCurrency(), 5));
            }
            if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
                BigDecimal offerValue = adjustmentPrice.getAmount().multiply(delegate.getOffer().getValue().divide(new BigDecimal("100"), 5, RoundingMode.HALF_EVEN));
            	delegate.setValue(new Money(offerValue, adjustmentPrice.getCurrency(), 5));
            }
            if (adjustmentPrice.lessThan(delegate.getValue())) {
            	delegate.setValue(adjustmentPrice);
            }
        }
    }
    
    public Money getValue() {
		if (delegate.getValue() == null || delegate.getValue().equals(Money.ZERO)) {
			computeAdjustmentValue();
		}
		return delegate.getValue();
	}
}
