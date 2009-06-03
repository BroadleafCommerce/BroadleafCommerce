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
package org.broadleafcommerce.offer.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.util.money.Money;

@Entity
@Table(name = "BLC_FG_ADJUSTMENT")
public class FulfillmentGroupAdjustmentImpl implements Serializable, FulfillmentGroupAdjustment {
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "FG_ADJUSTMENT_ID")
    private Long id;

    @ManyToOne(targetEntity = FulfillmentGroupAdjustmentImpl.class)
    @JoinColumn(name = "FULFILLMENT_GROUP_ID")
    private FulfillmentGroup fulfillmentGroup;

    @ManyToOne(targetEntity = FulfillmentGroupAdjustmentImpl.class)
    @JoinColumn(name = "OFFER_ID")
    private Offer offer;

    @Column(name = "ADJUSTMENT_REASON")
    private String reason;

    @Column(name = "ADJUSTMENT_VALUE")
    private BigDecimal value;

    public FulfillmentGroupAdjustmentImpl(FulfillmentGroup fulfillmentGroup, Offer offer, String reason){
        this.fulfillmentGroup = fulfillmentGroup;
        this.offer = offer;
        this.reason = reason;
        computeAdjustmentValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FulfillmentGroup getFulfillmentGroup() {
        return fulfillmentGroup;
    }

    public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        this.fulfillmentGroup = fulfillmentGroup;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Money getValue() {
        return value == null ? null : new Money(value);
    }

    /*
     * Calculates the value of the adjustment
     */
    public void computeAdjustmentValue() {
        if (offer != null && fulfillmentGroup != null) {

            Money adjustmentPrice = fulfillmentGroup.getAdjustmentPrice(); // get the current price of the item with all adjustments
            if (adjustmentPrice == null) {
                adjustmentPrice = fulfillmentGroup.getRetailShippingPrice();
            }

            if (offer.getDiscountType() == OfferDiscountType.AMOUNT_OFF ) {
                value = offer.getValue().getAmount();
            }
            if (offer.getDiscountType() == OfferDiscountType.FIX_PRICE) {
                value = adjustmentPrice.subtract(offer.getValue()).getAmount();
            }
            if (offer.getDiscountType() == OfferDiscountType.PERCENT_OFF) {
                value = adjustmentPrice.multiply(offer.getValue().divide(new BigDecimal("100")).getAmount()).getAmount();
            }
        }
    }

}
