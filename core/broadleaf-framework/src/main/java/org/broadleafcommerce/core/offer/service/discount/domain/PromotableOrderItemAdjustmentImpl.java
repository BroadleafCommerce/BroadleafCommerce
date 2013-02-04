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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.AdvancedOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferTier;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PromotableOrderItemAdjustmentImpl implements PromotableOrderItemAdjustment {
    
    private static final long serialVersionUID = 1L;
    
    protected PromotableOrderItem orderItem;
    protected OrderItemAdjustment delegate;
    
    public PromotableOrderItemAdjustmentImpl(OrderItemAdjustment orderItemAdjustment, PromotableOrderItem orderItem) {
        this.delegate = orderItemAdjustment;
        this.orderItem = orderItem;
    }
    
    public void reset() {
        delegate = null;
    }
    
    public OrderItemAdjustment getDelegate() {
        return delegate;
    }
    
    public Offer getOffer() {
        return delegate.getOffer();
    }

    protected boolean roundOfferValues = true;
    protected int roundingScale = 2;
    protected RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    
    /**
     * It is sometimes problematic to offer percentage-off offers with regards to rounding. For example,
     * consider an item that costs 9.99 and has a 50% promotion. To be precise, the offer value is 4.995,
     * but this may be a strange value to display to the user depending on the currency being used.
     */
    public boolean isRoundOfferValues() {
        return roundOfferValues;
    }

    /**
     * @see #isRoundOfferValues()
     * 
     * @param roundingScale
     */
    public void setRoundingScale(int roundingScale) {
        this.roundingScale = roundingScale;
    }
    
    /**
     * @see #isRoundOfferValues()
     * 
     * @param roundingMode
     */
    public void setRoundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }

    /*
     * Calculates the value of the adjustment
     */
    public void computeAdjustmentValues() {

        if (delegate.getOffer() != null && orderItem != null) {
            Money retailAdjustmentPrice = orderItem.getRetailAdjustmentPrice();
            Money salesAdjustmentPrice = orderItem.getSaleAdjustmentPrice();
            
            if (retailAdjustmentPrice == null) {
                retailAdjustmentPrice = orderItem.getRetailPrice();
            }
            
            if (delegate.getOrderItem().getIsOnSale() && salesAdjustmentPrice == null) {
                salesAdjustmentPrice = orderItem.getSalePrice();
            }
            
            // The value of an AMOUNT_OFF type order is the amount specified on the offer.
            if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) { 
                BigDecimal offerItemValue=delegate.getOffer().getValue();
                if (delegate.getOffer() instanceof AdvancedOffer &&  ((AdvancedOffer)delegate.getOffer()).isTieredOffer()) {  
                    offerItemValue= retriveTierForItem((long) orderItem.getQuantity());
                }
                Money amountOff = new Money(offerItemValue, retailAdjustmentPrice.getCurrency(), 5);
                
                // compute value for on sale price
                if (delegate.getOffer().getApplyDiscountToSalePrice() && delegate.getOrderItem().getIsOnSale()) {
                    if (amountOff.lessThan(salesAdjustmentPrice)) {
                        delegate.setSalesPriceValue(amountOff);
                    } else {
                        // Adjustment takes this item down to zero.
                        delegate.setSalesPriceValue(salesAdjustmentPrice);
                    }                    
                } else {
                    // Not applicable to sales price
                    delegate.setSalesPriceValue(Money.ZERO);                    
                }
                
                // compute value for retail price
                if (amountOff.lessThan(retailAdjustmentPrice)) {
                    delegate.setRetailPriceValue(amountOff);                    
                } else {
                    // Adjustment takes this item down to zero.
                    delegate.setRetailPriceValue(retailAdjustmentPrice);
                }                
            }
            
            // The value of FIX_PRICE depends on whether the item was on sale or not. 
            if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
                BigDecimal offerItemValue=delegate.getOffer().getValue();
                if (delegate.getOffer() instanceof AdvancedOffer &&  ((AdvancedOffer)delegate.getOffer()).isTieredOffer()) {
                    offerItemValue= retriveTierForItem((long) orderItem.getQuantity());
                }
                Money fixPriceAmount = new Money(offerItemValue, retailAdjustmentPrice.getCurrency(), 5);
                
                if (fixPriceAmount.lessThan(retailAdjustmentPrice)) {
                    BigDecimal offerValue = retailAdjustmentPrice.getAmount().subtract(fixPriceAmount.getAmount());
                    delegate.setRetailPriceValue(new Money(offerValue, retailAdjustmentPrice.getCurrency(), 5));
                } else {
                    delegate.setRetailPriceValue(Money.ZERO);
                }
                
                if (delegate.getOffer().getApplyDiscountToSalePrice() && delegate.getOrderItem().getIsOnSale()) {
                    if (fixPriceAmount.lessThan(salesAdjustmentPrice)) {
                        delegate.setSalesPriceValue(fixPriceAmount);                        
                    } else {
                        // Sale price is less than the fixed price already
                        delegate.setSalesPriceValue(Money.ZERO);
                    }
                }
            }
            
            // The current logic assume serial execution of percent off promotions.   Parallel logic 
            // would be slightly different. 
            if (delegate.getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
              
                if (delegate.getOffer().getApplyDiscountToSalePrice() && delegate.getOrderItem().getIsOnSale()) {
                    BigDecimal offerValue = salesAdjustmentPrice.getAmount().multiply(delegate.getOffer().getValue().divide(new BigDecimal("100"), 5, RoundingMode.HALF_EVEN));
                    if (isRoundOfferValues()) {
                        offerValue = offerValue.setScale(roundingScale, roundingMode);
                    }
                    delegate.setSalesPriceValue(new Money(offerValue, salesAdjustmentPrice.getCurrency(), 5));
                } else {
                    delegate.setSalesPriceValue(Money.ZERO);
                }
                BigDecimal offerValue=delegate.getOffer().getValue();
                if (delegate.getOffer() instanceof AdvancedOffer &&  ((AdvancedOffer)delegate.getOffer()).isTieredOffer()) {
                    offerValue= retriveTierForItem((long) orderItem.getQuantity());
                }
                  offerValue = retailAdjustmentPrice.getAmount().multiply(offerValue.divide(new BigDecimal("100"), 5, RoundingMode.HALF_EVEN));
              
                if (isRoundOfferValues()) {
                    offerValue = offerValue.setScale(roundingScale, roundingMode);
                }
                delegate.setRetailPriceValue(new Money(offerValue, retailAdjustmentPrice.getCurrency(), 5));
            }

        }
    }


    private BigDecimal retriveTierForItem(Long i) {
        OfferTier maxTier = null;
        //assuming that delegate.getOffer()).getOfferTiers() is sorted already
        for (OfferTier t : ((AdvancedOffer) delegate.getOffer()).getOfferTiers()) {

            if (i <= t.getMinQuantity() - 1) {
                break;
            }
            maxTier = t;
        }
        if (maxTier != null) {
            return maxTier.getAmount();
        }
        return BigDecimal.ZERO;
    }

    public Money getRetailPriceValue() {
        return delegate.getRetailPriceValue();
    }

    public Money getSalesPriceValue() {
        return delegate.getSalesPriceValue();
    }
}
