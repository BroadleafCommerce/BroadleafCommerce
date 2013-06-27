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

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class PromotableCandidateOrderOfferImpl implements PromotableCandidateOrderOffer {

    private static final long serialVersionUID = 1L;
    
    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();
    protected Offer offer;
    protected PromotableOrder promotableOrder;
    protected Money potentialSavings;
    
    public PromotableCandidateOrderOfferImpl(PromotableOrder promotableOrder, Offer offer) {
        assert(offer != null);
        assert(promotableOrder != null);
        this.promotableOrder = promotableOrder;
        this.offer = offer;
        calculatePotentialSavings();
    }
    
    /**
     * Instead of calculating the potential savings, you can specify an override of this value.   
     * This is currently coded only to work if the promotableOrder's isIncludeOrderAndItemAdjustments flag
     * is true.
     *  
     * @param promotableOrder
     * @param offer
     * @param potentialSavings
     */
    public PromotableCandidateOrderOfferImpl(PromotableOrder promotableOrder, Offer offer, Money potentialSavings) {
        this(promotableOrder, offer);
        if (promotableOrder.isIncludeOrderAndItemAdjustments()) {
            this.potentialSavings = potentialSavings;
        }
    }

    @Override
    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap() {
        return candidateQualifiersMap;
    }
    
    protected void calculatePotentialSavings() {
        Money amountBeforeAdjustments = promotableOrder.calculateSubtotalWithoutAdjustments();
        potentialSavings = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        if (getOffer().getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
            potentialSavings = BroadleafCurrencyUtils.getMoney(getOffer().getValue(), getCurrency());
        } else if (getOffer().getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
            potentialSavings = amountBeforeAdjustments.subtract(BroadleafCurrencyUtils.getMoney(getOffer().getValue(), getCurrency()));
        } else if (getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
            potentialSavings = amountBeforeAdjustments.multiply(getOffer().getValue().divide(new BigDecimal("100")));
        }

        if (potentialSavings.greaterThan(amountBeforeAdjustments)) {
            potentialSavings = amountBeforeAdjustments;
        }
    }
    
    @Override
    public Offer getOffer() {
        return this.offer;
    }
    
    @Override
    public PromotableOrder getPromotableOrder() {
        return this.promotableOrder;
    }
    
    public BroadleafCurrency getCurrency() {
        return promotableOrder.getOrderCurrency();
    }

    @Override
    public Money getPotentialSavings() {
        return potentialSavings;
    }

    @Override
    public boolean isCombinable() {
        Boolean combinable = offer.isCombinableWithOtherOffers();
        return (combinable != null && combinable);
    }

    @Override
    public boolean isTotalitarian() {
        Boolean totalitarian = offer.isTotalitarianOffer();
        return (totalitarian != null && totalitarian.booleanValue());
    }

    @Override
    public int getPriority() {
        return offer.getPriority();
    }
}
