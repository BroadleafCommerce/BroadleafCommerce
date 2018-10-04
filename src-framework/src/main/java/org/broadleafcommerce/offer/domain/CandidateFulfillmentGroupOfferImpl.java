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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.util.money.Money;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "BLC_CANDIDATE_FG_OFFER")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class CandidateFulfillmentGroupOfferImpl implements CandidateFulfillmentGroupOffer {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CandidateFGOfferId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CandidateFGOfferId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CandidateFulfillmentGroupOfferImpl", allocationSize = 50)
    @Column(name = "CANDIDATE_FG_OFFER_ID")
    protected Long id;

    @ManyToOne(targetEntity = FulfillmentGroupImpl.class)
    @JoinColumn(name = "FULFILLMENT_GROUP_ID")
    protected FulfillmentGroup fulfillmentGroup;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "OFFER_ID")
    protected Offer offer;

    @Column(name = "DISCOUNTED_PRICE")
    protected BigDecimal discountedPrice;

    @Transient
    private BigDecimal discountAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        discountedPrice = null;
    }

    public Money getDiscountedPrice() {
        if (discountedPrice == null) {
            computeDiscountedPriceAndAmount();
        }
        return discountedPrice == null ? null : new Money(discountedPrice);
    }

    public Money getDiscountAmount() {
        if (discountAmount == null) {
            computeDiscountedPriceAndAmount();
        }
        return discountAmount == null ? null : new Money(discountAmount);
    }

    public FulfillmentGroup getFulfillmentGroup() {
        return fulfillmentGroup;
    }

    public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        this.fulfillmentGroup = fulfillmentGroup;
        discountedPrice = null;
    }

    public int getPriority() {
        return offer.getPriority();
    }

    protected void computeDiscountedPriceAndAmount() {
        if (offer != null && fulfillmentGroup != null){

            if (fulfillmentGroup.getRetailShippingPrice() != null) {
                Money priceToUse = fulfillmentGroup.getRetailShippingPrice();
                Money discountAmount = new Money(0);
                if ((offer.getApplyDiscountToSalePrice()) && (fulfillmentGroup.getSaleShippingPrice() != null)) {
                    priceToUse = fulfillmentGroup.getSaleShippingPrice();
                }

                if (offer.getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
                    discountAmount = offer.getValue();
                } else if (offer.getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
                    discountAmount = priceToUse.subtract(offer.getValue());
                } else if (offer.getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
                    discountAmount = priceToUse.multiply(offer.getValue().divide(new BigDecimal("100")).getAmount());
                }
                if (discountAmount.greaterThan(priceToUse)) {
                    discountAmount = priceToUse;
                }
                priceToUse = priceToUse.subtract(discountAmount);
                discountedPrice = priceToUse.getAmount();
                this.discountAmount = discountAmount.getAmount();
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((discountedPrice == null) ? 0 : discountedPrice.hashCode());
        result = prime * result + ((fulfillmentGroup == null) ? 0 : fulfillmentGroup.hashCode());
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CandidateFulfillmentGroupOfferImpl other = (CandidateFulfillmentGroupOfferImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (discountedPrice == null) {
            if (other.discountedPrice != null)
                return false;
        } else if (!discountedPrice.equals(other.discountedPrice))
            return false;
        if (fulfillmentGroup == null) {
            if (other.fulfillmentGroup != null)
                return false;
        } else if (!fulfillmentGroup.equals(other.fulfillmentGroup))
            return false;
        if (offer == null) {
            if (other.offer != null)
                return false;
        } else if (!offer.equals(other.offer))
            return false;
        return true;
    }

}
