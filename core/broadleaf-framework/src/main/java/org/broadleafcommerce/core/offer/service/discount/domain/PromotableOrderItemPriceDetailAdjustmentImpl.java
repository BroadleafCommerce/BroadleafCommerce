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
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;

import java.math.BigDecimal;

public class PromotableOrderItemPriceDetailAdjustmentImpl extends AbstractPromotionRounding implements PromotableOrderItemPriceDetailAdjustment, OfferHolder {

    private static final long serialVersionUID = 1L;

    protected PromotableCandidateItemOffer promotableCandidateItemOffer;
    protected PromotableOrderItemPriceDetail promotableOrderItemPriceDetail;
    protected Money saleAdjustmentValue;
    protected Money retailAdjustmentValue;
    protected Money adjustmentValue;
    protected boolean appliedToSalePrice;
    protected Offer offer;

    public PromotableOrderItemPriceDetailAdjustmentImpl(PromotableCandidateItemOffer promotableCandidateItemOffer,
            PromotableOrderItemPriceDetail orderItemPriceDetail) {
        assert (promotableCandidateItemOffer != null);
        assert (orderItemPriceDetail != null);
        this.promotableCandidateItemOffer = promotableCandidateItemOffer;
        this.promotableOrderItemPriceDetail = orderItemPriceDetail;
        this.offer = promotableCandidateItemOffer.getOffer();
        computeAdjustmentValues();
    }

    public PromotableOrderItemPriceDetailAdjustmentImpl(OrderItemPriceDetailAdjustment itemAdjustment,
            PromotableOrderItemPriceDetail orderItemPriceDetail) {
        assert (orderItemPriceDetail != null);
        adjustmentValue = itemAdjustment.getValue();
        if (itemAdjustment.isAppliedToSalePrice()) {
            saleAdjustmentValue = itemAdjustment.getValue();
            // This will just set to a Money value of zero in the correct currency.
            retailAdjustmentValue = itemAdjustment.getRetailPriceValue();
        } else {
            retailAdjustmentValue = itemAdjustment.getValue();
            // This will just set to a Money value of zero in the correct currency.
            saleAdjustmentValue = itemAdjustment.getSalesPriceValue();
        }
        appliedToSalePrice = itemAdjustment.isAppliedToSalePrice();
        promotableOrderItemPriceDetail = orderItemPriceDetail;
        offer = itemAdjustment.getOffer();
    }

    /*
     * Calculates the value of the adjustment for both retail and sale prices.   
     * If either adjustment is greater than the item value, it will be set to the
     * currentItemValue (e.g. no adjustment can cause a negative value). 
     */
    protected void computeAdjustmentValues() {
        saleAdjustmentValue = new Money(getCurrency());
        retailAdjustmentValue = new Money(getCurrency());

        Money currentPriceDetailRetailValue = promotableOrderItemPriceDetail.calculateItemUnitPriceWithAdjustments(false);
        Money currentPriceDetailSalesValue = promotableOrderItemPriceDetail.calculateItemUnitPriceWithAdjustments(true);
        if (currentPriceDetailSalesValue == null) {
            currentPriceDetailSalesValue = currentPriceDetailRetailValue;
        }
        
        BigDecimal offerUnitValue = PromotableOfferUtility.determineOfferUnitValue(offer, promotableCandidateItemOffer);

        retailAdjustmentValue = PromotableOfferUtility.computeAdjustmentValue(currentPriceDetailRetailValue, offerUnitValue, this, this);
                
        if (offer.getApplyDiscountToSalePrice() == true) {
            saleAdjustmentValue = PromotableOfferUtility.computeAdjustmentValue(currentPriceDetailSalesValue, offerUnitValue, this, this);
        }
    }

    @Override
    public Money getRetailAdjustmentValue() {
        return retailAdjustmentValue;
    }

    @Override
    public Money getSaleAdjustmentValue() {
        return saleAdjustmentValue;
    }
    
    @Override
    public BroadleafCurrency getCurrency() {
        return promotableOrderItemPriceDetail.getPromotableOrderItem().getCurrency();
    }    

    @Override
    public PromotableOrderItemPriceDetail getPromotableOrderItemPriceDetail() {
        return promotableOrderItemPriceDetail;
    }

    @Override
    public Offer getOffer() {
        return offer;
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
    public Long getOfferId() {
        return offer.getId();
    }

    @Override
    public Money getAdjustmentValue() {
        return adjustmentValue;
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

    @Override
    public PromotableOrderItemPriceDetailAdjustment copy() {
        PromotableOrderItemPriceDetailAdjustmentImpl newAdjustment = new PromotableOrderItemPriceDetailAdjustmentImpl(
                promotableCandidateItemOffer, promotableOrderItemPriceDetail);
        newAdjustment.adjustmentValue = adjustmentValue;
        newAdjustment.saleAdjustmentValue = saleAdjustmentValue;
        newAdjustment.retailAdjustmentValue = retailAdjustmentValue;
        newAdjustment.appliedToSalePrice = appliedToSalePrice;
        return newAdjustment;
    }
}
