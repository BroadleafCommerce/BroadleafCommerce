/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.apache.commons.beanutils.BeanComparator;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.AdvancedOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferTier;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

/**
 * Provides shared code for the default implementations of PromotableOrderItemPriceDetailAdjustmentImpl and
 * PromotableCandidateItemOfferImpl
 * @author bpolster
 *
 */
public class PromotableOfferUtility {
    
    public static Money computeAdjustmentValue(Money currentPriceDetailValue, BigDecimal offerUnitValue, OfferHolder offerHolder, PromotionRounding rounding) {
        Offer offer = offerHolder.getOffer();
        BroadleafCurrency currency = offerHolder.getCurrency();

        OfferDiscountType discountType = offer.getDiscountType();
        Money adjustmentValue;
        if (currency != null) {
            adjustmentValue = new Money(currency);
        } else {
            adjustmentValue = new Money();
        }
        
        if (OfferDiscountType.AMOUNT_OFF.equals(discountType)) {
            adjustmentValue = new Money(offerUnitValue, currency);
        }
        
        if (OfferDiscountType.FIX_PRICE.equals(discountType)) {
            adjustmentValue = currentPriceDetailValue.subtract(new Money(offerUnitValue, currency));
        }
        
        if (OfferDiscountType.PERCENT_OFF.equals(discountType)) {
            BigDecimal offerValue = currentPriceDetailValue.getAmount().multiply(offerUnitValue.divide(new BigDecimal("100"), 5, RoundingMode.HALF_EVEN));
            
            if (rounding.isRoundOfferValues()) {
                offerValue = offerValue.setScale(rounding.getRoundingScale(), rounding.getRoundingMode());
            }
            adjustmentValue = new Money(offerValue, currency);
        }

        if (currentPriceDetailValue.lessThan(adjustmentValue)) {
            adjustmentValue = currentPriceDetailValue;
        }
        return adjustmentValue;
    }


    
    @SuppressWarnings("unchecked")
    public static BigDecimal determineOfferUnitValue(Offer offer, PromotableCandidateItemOffer promotableCandidateItemOffer) {
        if (offer instanceof AdvancedOffer) {
            AdvancedOffer advancedOffer = (AdvancedOffer) offer;
            if (advancedOffer.isTieredOffer()) {
                int quantity = promotableCandidateItemOffer.calculateTargetQuantityForTieredOffer();
                List<OfferTier> offerTiers = advancedOffer.getOfferTiers();

                Collections.sort(offerTiers, new BeanComparator("minQuantity"));

                OfferTier maxTier = null;
                //assuming that promotableOffer.getOffer()).getOfferTiers() is sorted already
                for (OfferTier currentTier : offerTiers) {

                    if (quantity >= currentTier.getMinQuantity()) {
                        maxTier = currentTier;
                    } else {
                        break;
                    }
                }

                if (maxTier != null) {
                    return maxTier.getAmount();
                }

                if (OfferDiscountType.FIX_PRICE.equals(offer.getDiscountType())) {
                    // Choosing an arbitrary large value.    The retail / sale price will be less than this, 
                    // so the offer will not get selected.
                    return BigDecimal.valueOf(Integer.MAX_VALUE);
                } else {
                    return BigDecimal.ZERO;
                }
            }
        }
        return offer.getValue();
    }

}
