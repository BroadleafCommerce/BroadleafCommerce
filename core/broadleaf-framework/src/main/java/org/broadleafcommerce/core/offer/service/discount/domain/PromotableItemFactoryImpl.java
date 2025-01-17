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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessorImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Enums;

import java.math.RoundingMode;
import java.util.Arrays;
import javax.annotation.PostConstruct;

@Service("blPromotableItemFactory")
public class PromotableItemFactoryImpl implements PromotableItemFactory {

    protected static final Log LOG = LogFactory.getLog(PromotableItemFactoryImpl.class);

    @Value("${use.quantity.only.tier.calculation:false}")
    protected boolean useQtyOnlyTierCalculation = false;

    @Value("${item.offer.percent.rounding.scale:-1}")
    protected Integer itemOfferPercentRoundingScale;


    @Value("${item.offer.percent.rounding.mode}")
    protected String itemOfferPercentRoundingModeStr;

    protected RoundingMode itemOfferPercentRoundingMode;

    @PostConstruct
    public void init() {
        if (itemOfferPercentRoundingModeStr != null) {
            try {
                itemOfferPercentRoundingMode =
                        RoundingMode.valueOf(itemOfferPercentRoundingModeStr);
            } catch (RuntimeException rte) {
                LOG.info("Unable to initialize rounding mode, using default. Value set for " +
                        "item.offer.percent.rounding.mode was " + itemOfferPercentRoundingModeStr);
            }
        }
    }

    protected final PromotableOfferUtility promotableOfferUtility;
    /**
     * It is sometimes problematic to offer percentage-off offers with regards to rounding. For example,
     * consider an item that costs 9.99 and has a 50% promotion. To be precise, the offer value is 4.995,
     * but this may be a strange value to display to the user depending on the currency being used.
     */    /**
     * It is sometimes problematic to offer percentage-off offers with regards to rounding. For example,
     * consider an item that costs 9.99 and has a 50% promotion. To be precise, the offer value is 4.995,
     * but this may be a strange value to display to the user depending on the currency being used.
     */
    public PromotableItemFactoryImpl(PromotableOfferUtility promotableOfferUtility) {
        this.promotableOfferUtility = promotableOfferUtility;
    }
    
    public PromotableOrder createPromotableOrder(Order order, boolean includeOrderAndItemAdjustments) {
        return new PromotableOrderImpl(order, this, includeOrderAndItemAdjustments);
    }

    @Override
    public PromotableCandidateOrderOffer createPromotableCandidateOrderOffer(PromotableOrder promotableOrder, Offer offer) {
        return new PromotableCandidateOrderOfferImpl(promotableOrder, offer);
    }
    
    @Override
    public PromotableCandidateOrderOffer createPromotableCandidateOrderOffer(PromotableOrder promotableOrder,
            Offer offer, Money potentialSavings) {
        return new PromotableCandidateOrderOfferImpl(promotableOrder, offer, potentialSavings);
    }

    @Override
    public PromotableOrderAdjustment createPromotableOrderAdjustment(
            PromotableCandidateOrderOffer promotableCandidateOrderOffer, PromotableOrder order) {
        return new PromotableOrderAdjustmentImpl(promotableCandidateOrderOffer, order);
    }
    
    @Override
    public PromotableOrderAdjustment createPromotableOrderAdjustment(
            PromotableCandidateOrderOffer promotableCandidateOrderOffer,
            PromotableOrder order, Money adjustmentValue) {
        return new PromotableOrderAdjustmentImpl(promotableCandidateOrderOffer, order, adjustmentValue);
    }

    @Override
    public PromotableOrderItem createPromotableOrderItem(OrderItem orderItem, PromotableOrder order,
            boolean includeAdjustments) {
        return new PromotableOrderItemImpl(orderItem, order, this, includeAdjustments);
    }
    
    @Override
    public PromotableOrderItemPriceDetail createPromotableOrderItemPriceDetail(PromotableOrderItem promotableOrderItem,
            int quantity) {
        return new PromotableOrderItemPriceDetailImpl(promotableOrderItem, quantity);
    }

    @Override
    public PromotableCandidateItemOffer createPromotableCandidateItemOffer(PromotableOrder promotableOrder, Offer offer) {
        PromotableCandidateItemOfferImpl pcio = new PromotableCandidateItemOfferImpl(
                promotableOrder, offer, useQtyOnlyTierCalculation);

        // Range enforcement
        if (itemOfferPercentRoundingScale != null && itemOfferPercentRoundingScale >= 0) {
            itemOfferPercentRoundingScale = Math.max(0,itemOfferPercentRoundingScale);
            itemOfferPercentRoundingScale = Math.min(itemOfferPercentRoundingScale, 5);
            pcio.setRoundingScale(itemOfferPercentRoundingScale);
        }

        if (itemOfferPercentRoundingMode != null) {
            pcio.setRoundingMode(itemOfferPercentRoundingMode);
        }
        return pcio;
    }
    
    @Override
    public PromotableOrderItemPriceDetailAdjustment createPromotableOrderItemPriceDetailAdjustment(
            PromotableCandidateItemOffer promotableCandidateItemOffer,
            PromotableOrderItemPriceDetail orderItemPriceDetail) {
        return new PromotableOrderItemPriceDetailAdjustmentImpl(promotableCandidateItemOffer, orderItemPriceDetail,
                promotableOfferUtility.computeRetailAdjustmentValue(promotableCandidateItemOffer, orderItemPriceDetail),
                promotableOfferUtility.computeSalesAdjustmentValue(promotableCandidateItemOffer, orderItemPriceDetail));
    }
    
    @Override
    public PromotableFulfillmentGroup createPromotableFulfillmentGroup(
            FulfillmentGroup fulfillmentGroup,
            PromotableOrder order) {
        return new PromotableFulfillmentGroupImpl(fulfillmentGroup, order, this);
    }
    
    @Override
    public PromotableCandidateFulfillmentGroupOffer createPromotableCandidateFulfillmentGroupOffer(
            PromotableFulfillmentGroup fulfillmentGroup, Offer offer) {
        return new PromotableCandidateFulfillmentGroupOfferImpl(fulfillmentGroup, offer);
    }
    
    @Override
    public PromotableFulfillmentGroupAdjustment createPromotableFulfillmentGroupAdjustment(
            PromotableCandidateFulfillmentGroupOffer promotableCandidateFulfillmentGroupOffer,
            PromotableFulfillmentGroup fulfillmentGroup) {
        return new PromotableFulfillmentGroupAdjustmentImpl(promotableCandidateFulfillmentGroupOffer, fulfillmentGroup,
                promotableOfferUtility.computeRetailAdjustmentValue(promotableCandidateFulfillmentGroupOffer, fulfillmentGroup),
                promotableOfferUtility.computeSalesAdjustmentValue(promotableCandidateFulfillmentGroupOffer, fulfillmentGroup));
    }
}
