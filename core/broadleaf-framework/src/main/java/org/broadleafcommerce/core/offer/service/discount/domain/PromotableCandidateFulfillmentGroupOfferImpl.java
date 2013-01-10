/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class PromotableCandidateFulfillmentGroupOfferImpl implements PromotableCandidateFulfillmentGroupOffer {

    private static final long serialVersionUID = 1L;
    
    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();
    protected CandidateFulfillmentGroupOffer delegate;
    protected PromotableFulfillmentGroup promotableFulfillmentGroup;
    protected Money discountedAmount;
    
    public PromotableCandidateFulfillmentGroupOfferImpl(CandidateFulfillmentGroupOffer candidateFulfillmentGroupOffer, PromotableFulfillmentGroup promotableFulfillmentGroup) {
        this.delegate = candidateFulfillmentGroupOffer;
        this.promotableFulfillmentGroup = promotableFulfillmentGroup;
    }
    
    @Override
    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap() {
        return candidateQualifiersMap;
    }

    @Override
    public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap) {
        this.candidateQualifiersMap = candidateItemsMap;
    }
    
    @Override
    public void computeDiscountedPriceAndAmount() {
        if (delegate.getOffer() != null && delegate.getFulfillmentGroup() != null){

            if (delegate.getFulfillmentGroup().getRetailShippingPrice() != null) {
                Money priceToUse = delegate.getFulfillmentGroup().getRetailShippingPrice();
                discountedAmount = new Money(0);
                if ((delegate.getOffer().getApplyDiscountToSalePrice()) && (delegate.getFulfillmentGroup().getSaleShippingPrice() != null)) {
                    priceToUse = delegate.getFulfillmentGroup().getSaleShippingPrice();
                }

                if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
                    discountedAmount = BroadleafCurrencyUtils.getMoney(delegate.getOffer().getValue(), delegate.getFulfillmentGroup().getOrder().getCurrency());
                } else if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
                    discountedAmount = priceToUse.subtract(BroadleafCurrencyUtils.getMoney(delegate.getOffer().getValue(), delegate.getFulfillmentGroup().getOrder().getCurrency()));
                } else if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
                    discountedAmount = priceToUse.multiply(delegate.getOffer().getValue().divide(new BigDecimal("100")));
                }
                if (discountedAmount.greaterThan(priceToUse)) {
                    discountedAmount = priceToUse;
                }
                priceToUse = priceToUse.subtract(discountedAmount);
                delegate.setDiscountedPrice(priceToUse);
            }
        }
    }
    
    @Override
    public void reset() {
        delegate = null;
    }
    
    @Override
    public CandidateFulfillmentGroupOffer getDelegate() {
        return delegate;
    }
    
    @Override
    public Money getDiscountedPrice() {
        if (delegate.getDiscountedPrice() == null) {
            computeDiscountedPriceAndAmount();
        }
        return delegate.getDiscountedPrice();
    }

    @Override
    public Money getDiscountedAmount() {
        if (delegate.getDiscountedPrice() == null) {
            computeDiscountedPriceAndAmount();
        }
        return discountedAmount;
    }

    @Override
    public Offer getOffer() {
        return delegate.getOffer();
    }
    
    @Override
    public PromotableFulfillmentGroup getFulfillmentGroup() {
        return promotableFulfillmentGroup;
    }

    public int getPriority() {
        return delegate.getPriority();
    }
}
