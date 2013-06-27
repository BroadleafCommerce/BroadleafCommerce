/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
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
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class PromotableCandidateFulfillmentGroupOfferImpl implements PromotableCandidateFulfillmentGroupOffer {

    private static final long serialVersionUID = 1L;
    
    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();
    protected Offer offer;
    protected PromotableFulfillmentGroup promotableFulfillmentGroup;
    
    public PromotableCandidateFulfillmentGroupOfferImpl(PromotableFulfillmentGroup promotableFulfillmentGroup, Offer offer) {
        assert(offer != null);
        assert(promotableFulfillmentGroup != null);
        this.offer = offer;
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
    
    protected Money getBasePrice() {
        Money priceToUse = null;
        if (promotableFulfillmentGroup.getFulfillmentGroup().getRetailFulfillmentPrice() != null) {
            priceToUse = promotableFulfillmentGroup.getFulfillmentGroup().getRetailFulfillmentPrice();
            if ((offer.getApplyDiscountToSalePrice()) && (promotableFulfillmentGroup.getFulfillmentGroup().getSaleFulfillmentPrice() != null)) {
                priceToUse = promotableFulfillmentGroup.getFulfillmentGroup().getSaleFulfillmentPrice();
            }
        }
        return priceToUse;
    }
    
    @Override
    public Money computeDiscountedAmount() {
        Money discountedAmount = new Money(0);
        Money priceToUse = getBasePrice();
        if (priceToUse != null) {
            if (offer.getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
                discountedAmount = BroadleafCurrencyUtils.getMoney(offer.getValue(), promotableFulfillmentGroup.getFulfillmentGroup().getOrder().getCurrency());
            } else if (offer.getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
                discountedAmount = priceToUse.subtract(BroadleafCurrencyUtils.getMoney(offer.getValue(), promotableFulfillmentGroup.getFulfillmentGroup().getOrder().getCurrency()));
            } else if (offer.getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
                discountedAmount = priceToUse.multiply(offer.getValue().divide(new BigDecimal("100")));
            }
            if (discountedAmount.greaterThan(priceToUse)) {
                discountedAmount = priceToUse;
            }
        }

        return discountedAmount;
    }
    
    @Override
    public Money getDiscountedPrice() {
        return getBasePrice().subtract(computeDiscountedAmount());
    }

    @Override
    public Money getDiscountedAmount() {
        return computeDiscountedAmount();
    }

    @Override
    public Offer getOffer() {
        return offer;
    }
    
    @Override
    public PromotableFulfillmentGroup getFulfillmentGroup() {
        return promotableFulfillmentGroup;
    }

    public int getPriority() {
        return offer.getPriority();
    }
}
