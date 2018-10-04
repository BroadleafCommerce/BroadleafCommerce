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
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.broadleafcommerce.util.money.Money;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "BLC_CANDIDATE_ITEM_OFFER")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class CandidateItemOfferImpl implements CandidateItemOffer {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CandidateItemOfferId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CandidateItemOfferId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CandidateItemOfferImpl", allocationSize = 50)
    @Column(name = "CANDIDATE_ITEM_OFFER_ID")
    protected Long id;

    @ManyToOne(targetEntity = OrderItemImpl.class)
    @JoinColumn(name = "ORDER_ITEM_ID")
    protected OrderItem orderItem;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "OFFER_ID")
    protected Offer offer;

    @Column(name = "DISCOUNTED_PRICE")
    private BigDecimal discountedPrice;

    @Transient
    private BigDecimal discountAmount;

    /*public CandidateItemOfferImpl(){
        this(null, null);
    }

    public CandidateItemOfferImpl(OrderItem orderItem, Offer offer){
        this.orderItem = orderItem;
        this.offer = offer;
    }*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
        discountedPrice = null;  // price needs to be recalculated
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        discountedPrice = null;  // price needs to be recalculated
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

    public int getPriority() {
        return offer.getPriority();
    }

    public Offer getOffer() {
        return offer;
    }

    protected void computeDiscountedPriceAndAmount() {
        if (offer != null && orderItem != null){

            Money priceToUse = orderItem.getRetailPrice();
            Money discountAmount = new Money(0);
            if ((offer.getApplyDiscountToSalePrice()) && (orderItem.getSalePrice() != null)) {
                priceToUse = orderItem.getSalePrice();
            }

            if (offer.getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
                discountAmount = offer.getValue();
            } else if (offer.getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
                discountAmount = priceToUse.subtract(offer.getValue());
            } else if (offer.getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
                BigDecimal percentageOff = offer.getValue().getAmount().divide( new BigDecimal("100") );
                discountAmount = priceToUse.multiply(percentageOff);
            }
            if (discountAmount.greaterThan(priceToUse)) {
                discountAmount = priceToUse;
            }
            priceToUse = priceToUse.subtract(discountAmount);
            discountedPrice = priceToUse.getAmount();
            this.discountAmount = discountAmount.getAmount();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((discountedPrice == null) ? 0 : discountedPrice.hashCode());
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        result = prime * result + ((orderItem == null) ? 0 : orderItem.hashCode());
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
        CandidateItemOfferImpl other = (CandidateItemOfferImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (discountedPrice == null) {
            if (other.discountedPrice != null)
                return false;
        } else if (!discountedPrice.equals(other.discountedPrice))
            return false;
        if (offer == null) {
            if (other.offer != null)
                return false;
        } else if (!offer.equals(other.offer))
            return false;
        if (orderItem == null) {
            if (other.orderItem != null)
                return false;
        } else if (!orderItem.equals(other.orderItem))
            return false;
        return true;
    }

}
