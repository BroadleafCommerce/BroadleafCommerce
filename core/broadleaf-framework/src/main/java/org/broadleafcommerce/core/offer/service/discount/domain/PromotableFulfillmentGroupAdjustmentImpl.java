/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
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
            PromotableFulfillmentGroup fulfillmentGroup,
            Money retailAdjustmentValue,
            Money saleAdjustmentValue) {
        this.promotableCandidateFulfillmentGroupOffer = promotableCandidateFulfillmentGroupOffer;
        this.promotableFulfillmentGroup = fulfillmentGroup;
        this.retailAdjustmentValue = retailAdjustmentValue;
        this.saleAdjustmentValue = saleAdjustmentValue;
    }

    public Offer getOffer() {
        return promotableCandidateFulfillmentGroupOffer.getOffer();
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
            BigDecimal offerValue = currentPriceDetailValue.getAmount().multiply(
                    offer.getValue().divide(new BigDecimal("100"), 5, RoundingMode.HALF_EVEN));

            int scale = 2;
            if (getCurrency() != null) {
                // default scale to currency if currency is not null
                scale = getCurrency().getJavaCurrency().getDefaultFractionDigits();
            }
            if (roundingScale != null) {
                // override scale from rounding settings if set
                scale = roundingScale;
            }
            adjustmentValue = new Money(offerValue, getCurrency(), scale);
            if (roundingScale != null) {
                adjustmentValue = Money.trimUnnecessaryScaleToCurrency(adjustmentValue);
            }
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
