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

import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.common.money.Money;

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
                setValue(new Money(delegate.getOffer().getValue(), adjustmentPrice.getCurrency(), 5));
            }
            if (getOffer().getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
                BigDecimal offerValue = adjustmentPrice.getAmount().subtract(delegate.getOffer().getValue());
                setValue(new Money(offerValue, adjustmentPrice.getCurrency(), 5));
            }
            if (getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
                BigDecimal offerValue = adjustmentPrice.getAmount().multiply(delegate.getOffer().getValue().divide(new BigDecimal("100"), 5, RoundingMode.HALF_EVEN));
                setValue(new Money(offerValue, adjustmentPrice.getCurrency(), 5));
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
        if (delegate.getValue() == null || delegate.getValue().equals(Money.ZERO)) {
            computeAdjustmentValue();
        }
        return delegate.getValue();
    }
    
}
