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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PromotableFulfillmentGroupImpl implements PromotableFulfillmentGroup {

    private static final long serialVersionUID = 1L;
    protected FulfillmentGroup fulfillmentGroup;
    protected PromotableOrder promotableOrder;
    protected PromotableItemFactory itemFactory;
    protected List<PromotableOrderItem> discountableOrderItems;
    protected boolean useSaleAdjustments = false;
    protected Money adjustedPrice;
    
    public List<PromotableFulfillmentGroupAdjustment> candidateFulfillmentGroupAdjustments = new ArrayList<PromotableFulfillmentGroupAdjustment>();
    
    public PromotableFulfillmentGroupImpl(FulfillmentGroup fulfillmentGroup,
            PromotableOrder promotableOrder,
            PromotableItemFactory itemFactory) {
        this.fulfillmentGroup = fulfillmentGroup;
        this.promotableOrder = promotableOrder;
        this.itemFactory = itemFactory;
    }

    @Override
    public FulfillmentGroup getFulfillmentGroup() {
        return fulfillmentGroup;
    }

    @Override
    public void updateRuleVariables(Map<String, Object> ruleVars) {
        ruleVars.put("fulfillmentGroup", fulfillmentGroup);
    }

    @Override
    public List<PromotableOrderItem> getDiscountableOrderItems() {
        if (discountableOrderItems != null) {
            return discountableOrderItems;
        }
    
        discountableOrderItems = new ArrayList<PromotableOrderItem>();
        List<Long> discountableOrderItemIds = new ArrayList<Long>();
        for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
            OrderItem orderItem = fgItem.getOrderItem();
            if (orderItem.isDiscountingAllowed()) {
                discountableOrderItemIds.add(fgItem.getOrderItem().getId());
            } else {
                if (orderItem instanceof OrderItemContainer) {
                    OrderItemContainer orderItemContainer = (OrderItemContainer) orderItem;
                    if (orderItemContainer.getAllowDiscountsOnChildItems()) {
                        for (OrderItem containedOrderItem : orderItemContainer.getOrderItems()) {
                            if (!containedOrderItem.isDiscountingAllowed()) {
                                discountableOrderItemIds.add(containedOrderItem.getId());
                            }
                        }
                    }
                }
            }
        }
            
        for (PromotableOrderItem item : promotableOrder.getDiscountableOrderItems()) {
            if (discountableOrderItemIds.contains(item.getOrderItemId())) {
                discountableOrderItems.add(item);
            }
        }
        return discountableOrderItems;
    }

    protected Money getSalePriceBeforeAdjustments() {
        Money salePrice = fulfillmentGroup.getSaleFulfillmentPrice();
        if (salePrice == null) {
            return fulfillmentGroup.getRetailFulfillmentPrice();
        } else {
            return salePrice;
        }
    }

    protected Money calculateSaleAdjustmentPrice() {
        Money returnPrice = getSalePriceBeforeAdjustments();
        for (PromotableFulfillmentGroupAdjustment adjustment : candidateFulfillmentGroupAdjustments) {
            returnPrice = returnPrice.subtract(adjustment.getSaleAdjustmentValue());
        }
        return returnPrice;
    }

    protected Money calculateRetailAdjustmentPrice() {
        Money returnPrice = fulfillmentGroup.getRetailFulfillmentPrice();
        for (PromotableFulfillmentGroupAdjustment adjustment : candidateFulfillmentGroupAdjustments) {
            returnPrice = returnPrice.subtract(adjustment.getRetailAdjustmentValue());
        }
        return returnPrice;
    }

    /**
     * This method will check to see if the salePriceAdjustments or retailPriceAdjustments are better
     * and remove those that should not apply.
     * @return 
     */
    public void chooseSaleOrRetailAdjustments() {
        this.useSaleAdjustments = Boolean.FALSE;
        Money saleAdjustmentPrice = calculateSaleAdjustmentPrice();
        Money retailAdjustmentPrice = calculateRetailAdjustmentPrice();

        if (saleAdjustmentPrice.lessThan(retailAdjustmentPrice)) {
            this.useSaleAdjustments = Boolean.TRUE;
            adjustedPrice = saleAdjustmentPrice;
        } else {
            adjustedPrice = retailAdjustmentPrice;
        }

        if (useSaleAdjustments) {
            removeRetailOnlyAdjustments();
        }

        removeZeroDollarAdjustments(useSaleAdjustments);

        finalizeAdjustments(useSaleAdjustments);

    }

    protected void finalizeAdjustments(boolean useSaleAdjustments) {
        for (PromotableFulfillmentGroupAdjustment adjustment : candidateFulfillmentGroupAdjustments) {
            adjustment.finalizeAdjustment(useSaleAdjustments);
        }
    }

    /**
     * Removes retail only adjustments.
     */
    protected void removeRetailOnlyAdjustments() {
        Iterator<PromotableFulfillmentGroupAdjustment> adjustments = candidateFulfillmentGroupAdjustments.iterator();
        while (adjustments.hasNext()) {
            PromotableFulfillmentGroupAdjustment adjustment = adjustments.next();
            if (adjustment.getPromotableCandidateFulfillmentGroupOffer().getOffer().getApplyDiscountToSalePrice() == false) {
                adjustments.remove();
            }
        }
    }

    /**
     * If removeUnusedAdjustments is s 
     * @param useSaleAdjustments
     */
    protected void removeZeroDollarAdjustments(boolean useSalePrice) {
        Iterator<PromotableFulfillmentGroupAdjustment> adjustments = candidateFulfillmentGroupAdjustments.iterator();
        while (adjustments.hasNext()) {
            PromotableFulfillmentGroupAdjustment adjustment = adjustments.next();
            if (useSalePrice) {
                if (adjustment.getSaleAdjustmentValue().isZero()) {
                    adjustments.remove();
                }
            } else {
                if (adjustment.getRetailAdjustmentValue().isZero()) {
                    adjustments.remove();
                }
            }
        }
    }

    @Override
    public Money getFinalizedPriceWithAdjustments() {
        chooseSaleOrRetailAdjustments();
        return adjustedPrice;
    }

    @Override
    public Money calculatePriceWithoutAdjustments() {
        if (fulfillmentGroup.getSaleFulfillmentPrice() != null) {
            return fulfillmentGroup.getSaleFulfillmentPrice();
        } else {
            return fulfillmentGroup.getRetailFulfillmentPrice();
        }
    }

    @Override
    public void addCandidateFulfillmentGroupAdjustment(PromotableFulfillmentGroupAdjustment adjustment) {
        candidateFulfillmentGroupAdjustments.add(adjustment);
    }

    @Override
    public List<PromotableFulfillmentGroupAdjustment> getCandidateFulfillmentGroupAdjustments() {
        return Collections.unmodifiableList(candidateFulfillmentGroupAdjustments);
    }

    @Override
    public boolean canApplyOffer(PromotableCandidateFulfillmentGroupOffer fulfillmentGroupOffer) {
        if (candidateFulfillmentGroupAdjustments.size() > 0) {
            if (!fulfillmentGroupOffer.getOffer().isCombinableWithOtherOffers()) {
                return false;
            }

            for (PromotableFulfillmentGroupAdjustment adjustment : candidateFulfillmentGroupAdjustments) {
                if (!adjustment.isCombinable()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Money calculatePriceWithAdjustments(boolean useSalePrice) {
        if (useSalePrice) {
            return calculateSaleAdjustmentPrice();
        } else {
            return calculateRetailAdjustmentPrice();
        }
    }

    @Override
    public boolean isTotalitarianOfferApplied() {
        for (PromotableFulfillmentGroupAdjustment adjustment : candidateFulfillmentGroupAdjustments) {
            if (adjustment.getPromotableCandidateFulfillmentGroupOffer().getOffer().isTotalitarianOffer()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeAllCandidateAdjustments() {
        candidateFulfillmentGroupAdjustments.clear();
    }
}
