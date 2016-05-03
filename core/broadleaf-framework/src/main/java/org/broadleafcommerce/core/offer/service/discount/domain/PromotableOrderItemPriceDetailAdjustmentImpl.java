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
