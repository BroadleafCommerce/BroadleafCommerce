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
                return BigDecimal.ZERO;
            }
        }
        return offer.getValue();
    }

}
