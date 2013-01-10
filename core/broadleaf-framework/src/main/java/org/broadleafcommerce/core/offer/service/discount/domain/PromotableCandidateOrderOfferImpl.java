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

import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.money.Money;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class PromotableCandidateOrderOfferImpl implements PromotableCandidateOrderOffer {

    private static final long serialVersionUID = 1L;
    
    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();
    protected CandidateOrderOffer delegate;
    protected PromotableOrder order;
    
    public PromotableCandidateOrderOfferImpl(CandidateOrderOffer candidateOrderOffer, PromotableOrder order) {
        this.delegate = candidateOrderOffer;
        this.order = order;
    }
    
    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap() {
        return candidateQualifiersMap;
    }

    public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap) {
        this.candidateQualifiersMap = candidateItemsMap;
    }
    
    public void computeDiscountedPriceAndAmount() {
        if (getOffer() != null && getOrder() != null){
            if (getOrder().getSubTotal() != null) {
                Money priceToUse = getOrder().getSubTotal();
                Money discountAmount = new Money(0);
                if (getOffer().getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
                    discountAmount = new Money(getOffer().getValue());
                } else if (getOffer().getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
                    discountAmount = priceToUse.subtract(new Money(getOffer().getValue()));
                } else if (getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
                    discountAmount = priceToUse.multiply(getOffer().getValue().divide(new BigDecimal("100")));
                }
                if (discountAmount.greaterThan(priceToUse)) {
                    discountAmount = priceToUse;
                }
                priceToUse = priceToUse.subtract(discountAmount);
                setDiscountedPrice(priceToUse);
            }
        }
    }
    
    public void reset() {
        delegate = null;
    }
    
    public CandidateOrderOffer getDelegate() {
        return delegate;
    }
    
    public PromotableOrder getOrder() {
        return this.order;
    }
    
    public Offer getOffer() {
        return delegate.getOffer();
    }
    
    //CandidateOrderOffer methods

    public Money getDiscountedPrice() {
        if (delegate.getDiscountedPrice() == null) {
            computeDiscountedPriceAndAmount();
        }
        return delegate.getDiscountedPrice();
    }
    
    public void setDiscountedPrice(Money discountedPrice) {
        delegate.setDiscountedPrice(discountedPrice);
    }

    public Long getId() {
        return delegate.getId();
    }

    public void setId(Long id) {
        delegate.setId(id);
    }

    

    public void setOrder(Order order) {
        this.order = (PromotableOrder) order;
        delegate.setOrder(this.order.getDelegate());
    }

    public void setOffer(Offer offer) {
        delegate.setOffer(offer);
    }

    public int getPriority() {
        return delegate.getPriority();
    }
    
}
