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
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PromotableFulfillmentGroupAdjustmentImpl extends AbstractPromotionRounding implements PromotableFulfillmentGroupAdjustment, OfferHolder {
    
    private static final long serialVersionUID = 1L;

    protected PromotableCandidateFulfillmentGroupOffer promotableCandidateFulfillmentGroupOffer;
    protected PromotableFulfillmentGroup promotableFulfillmentGroup;
    protected Money saleAdjustmentValue;
    protected Money retailAdjustmentValue;
    protected Money adjustmentValue;
    protected boolean appliedToSalePrice;

    public PromotableFulfillmentGroupAdjustmentImpl(
            PromotableCandidateFulfillmentGroupOffer promotableCandidateFulfillmentGroupOffer,
            PromotableFulfillmentGroup fulfillmentGroup) {
        this.promotableCandidateFulfillmentGroupOffer = promotableCandidateFulfillmentGroupOffer;
        this.promotableFulfillmentGroup = fulfillmentGroup;
        computeAdjustmentValues();
    }

    public Offer getOffer() {
        return promotableCandidateFulfillmentGroupOffer.getOffer();
    }

    /*
     * Calculates the value of the adjustment for both retail and sale prices.   
     * If either adjustment is greater than the item value, it will be set to the
     * currentItemValue (e.g. no adjustment can cause a negative value). 
     */
    protected void computeAdjustmentValues() {
        saleAdjustmentValue = new Money(getCurrency());
        retailAdjustmentValue = new Money(getCurrency());
        Offer offer = promotableCandidateFulfillmentGroupOffer.getOffer();

        Money currentPriceDetailSalesValue = promotableFulfillmentGroup.calculatePriceWithAdjustments(true);
        Money currentPriceDetailRetailValue = promotableFulfillmentGroup.calculatePriceWithAdjustments(false);

        retailAdjustmentValue = PromotableOfferUtility.computeAdjustmentValue(currentPriceDetailRetailValue, offer.getValue(), this, this);

        if (offer.getApplyDiscountToSalePrice() == true) {
            saleAdjustmentValue = PromotableOfferUtility.computeAdjustmentValue(currentPriceDetailSalesValue, offer.getValue(), this, this);
        }
    }

    protected Money computeAdjustmentValue(Money currentPriceDetailValue) {
        Offer offer = promotableCandidateFulfillmentGroupOffer.getOffer();
        OfferDiscountType discountType = offer.getDiscountType();
        Money adjustmentValue = new Money(getCurrency());

        if (OfferDiscountType.AMOUNT_OFF.equals(discountType)) {
            adjustmentValue = new Money(offer.getValue(), getCurrency());
        }

        if (OfferDiscountType.FIX_PRICE.equals(discountType)) {
            adjustmentValue = currentPriceDetailValue;
        }

        if (OfferDiscountType.PERCENT_OFF.equals(discountType)) {
            BigDecimal offerValue = currentPriceDetailValue.getAmount().multiply(offer.getValue().divide(new BigDecimal("100"), 5, RoundingMode.HALF_EVEN));

            if (isRoundOfferValues()) {
                offerValue = offerValue.setScale(roundingScale, roundingMode);
            }
            adjustmentValue = new Money(offerValue, getCurrency(), 5);
        }

        if (currentPriceDetailValue.lessThan(adjustmentValue)) {
            adjustmentValue = currentPriceDetailValue;
        }
        return adjustmentValue;
    }
    
    @Override
    public PromotableFulfillmentGroup getPromotableFulfillmentGroup() {
        return promotableFulfillmentGroup;
    }

    @Override
    public PromotableCandidateFulfillmentGroupOffer getPromotableCandidateFulfillmentGroupOffer() {
        return promotableCandidateFulfillmentGroupOffer;
    }

    @Override
    public Money getAdjustmentValue() {
        return adjustmentValue;
    }

    public BroadleafCurrency getCurrency() {
        return promotableFulfillmentGroup.getFulfillmentGroup().getOrder().getCurrency();
    }

    @Override
    public boolean isCombinable() {
        Boolean combinable = getOffer().isCombinableWithOtherOffers();
        return (combinable != null && combinable);
    }

    @Override
    public boolean isTotalitarian() {
        Boolean totalitarian = getOffer().isTotalitarianOffer();
        return (totalitarian != null && totalitarian.booleanValue());
    }

    @Override
    public Money getSaleAdjustmentValue() {
        return saleAdjustmentValue;
    }

    @Override
    public Money getRetailAdjustmentValue() {
        return retailAdjustmentValue;
    }

    @Override
    public boolean isAppliedToSalePrice() {
        return appliedToSalePrice;
    }

    @Override
    public void finalizeAdjustment(boolean useSalePrice) {
        appliedToSalePrice = useSalePrice;
        if (useSalePrice) {
            adjustmentValue = saleAdjustmentValue;
        } else {
            adjustmentValue = retailAdjustmentValue;
        }
    }

}
