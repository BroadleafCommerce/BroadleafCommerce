/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.service.discount.OrderItemPriceComparator;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PromotableOrderImpl implements PromotableOrder {


    private static final long serialVersionUID = 1L;
    
    protected PromotableItemFactory itemFactory;
    protected Order order;
    protected List<PromotableOrderItem> allOrderItems;
    protected List<PromotableOrderItem> discountableOrderItems;
    protected boolean currentSortParam = false;
    protected List<PromotableFulfillmentGroup> fulfillmentGroups;
    protected List<PromotableOrderAdjustment> candidateOrderOfferAdjustments = new ArrayList<PromotableOrderAdjustment>();
    protected boolean includeOrderAndItemAdjustments = false;

    public PromotableOrderImpl(Order order, PromotableItemFactory itemFactory, boolean includeOrderAndItemAdjustments) {
        this.order = order;
        this.itemFactory = itemFactory;
        this.includeOrderAndItemAdjustments = includeOrderAndItemAdjustments;

        if (includeOrderAndItemAdjustments) {
            createExistingOrderAdjustments();
        }
    }

    /**
     * Bring over the order adjustments.   Intended to be used when processing
     * fulfillment orders.
     */
    protected void createExistingOrderAdjustments() {
        if (order.getOrderAdjustments() != null) {
            for (OrderAdjustment adjustment : order.getOrderAdjustments()) {
                if (adjustment.getOffer() != null) {
                    PromotableCandidateOrderOffer pcoo = itemFactory.createPromotableCandidateOrderOffer(this, adjustment.getOffer(), adjustment.getValue());
                    PromotableOrderAdjustment adj = itemFactory.createPromotableOrderAdjustment(pcoo, this, adjustment.getValue());
                    candidateOrderOfferAdjustments.add(adj);
                }
            }
        }
    }

    @Override
    public void setOrderSubTotalToPriceWithoutAdjustments() {
        Money calculatedSubTotal = calculateOrderSubTotalWithoutOrderAdjustments();
        order.setSubTotal(calculatedSubTotal);
    }
    
    @Override
    public void setOrderSubTotalToPriceWithAdjustments() {
        Money calculatedSubTotal = calculateSubtotalWithAdjustments();
        order.setSubTotal(calculatedSubTotal);
    }    

    private Money calculateOrderSubTotalWithoutOrderAdjustments() {
        Money calculatedSubTotal = BroadleafCurrencyUtils.getMoney(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            calculatedSubTotal = calculatedSubTotal.add(orderItem.getTotalPrice());
        }
        return calculatedSubTotal;
    }

    @Override
    public List<PromotableOrderItem> getAllOrderItems() {
        if (allOrderItems == null) {
            allOrderItems = new ArrayList<PromotableOrderItem>();

            for (OrderItem orderItem : order.getOrderItems()) {
                addPromotableOrderItem(orderItem, allOrderItems);
            }
        }

        return allOrderItems;
    }

    // Return the discountableOrderItems in the current order.
    public List<PromotableOrderItem> getDiscountableOrderItems() {
        return getDiscountableOrderItems(currentSortParam);
    }


    @Override
    public List<PromotableOrderItem> getDiscountableOrderItems(boolean applyDiscountToSalePrice) {
        if (discountableOrderItems == null || discountableOrderItems.isEmpty()) {
            discountableOrderItems = buildPromotableOrderItemsList();

            OrderItemPriceComparator priceComparator = new OrderItemPriceComparator(applyDiscountToSalePrice);
            // Sort the items so that the highest priced ones are at the top
            Collections.sort(discountableOrderItems, priceComparator);
            currentSortParam = applyDiscountToSalePrice;
        }

        if (currentSortParam != applyDiscountToSalePrice) {
            // Resort
            OrderItemPriceComparator priceComparator = new OrderItemPriceComparator(applyDiscountToSalePrice);
            Collections.sort(discountableOrderItems, priceComparator);

            currentSortParam = applyDiscountToSalePrice;
        }

        return discountableOrderItems;
    }

    protected List<PromotableOrderItem> buildPromotableOrderItemsList() {
        List<PromotableOrderItem> discountableOrderItems = new ArrayList<PromotableOrderItem>();

        for (PromotableOrderItem promotableOrderItem : getAllOrderItems()) {
            if (promotableOrderItem.isDiscountingAllowed()) {
                discountableOrderItems.add(promotableOrderItem);
            } else {
                if (promotableOrderItem.isOrderItemContainer()) {
                    OrderItemContainer orderItemContainer = promotableOrderItem.getOrderItemContainer();
                    if (orderItemContainer.getAllowDiscountsOnChildItems()) {
                        for (OrderItem containedOrderItem : orderItemContainer.getOrderItems()) {
                            if (containedOrderItem.isDiscountingAllowed()) {
                                addPromotableOrderItem(containedOrderItem, discountableOrderItems);
                            }
                        }
                    }
                }
            }
        }

        return discountableOrderItems;
    }

    protected void addPromotableOrderItem(OrderItem orderItem, List<PromotableOrderItem> discountableOrderItems) {
        PromotableOrderItem item = itemFactory.createPromotableOrderItem(orderItem, PromotableOrderImpl.this, includeOrderAndItemAdjustments);
        discountableOrderItems.add(item);
    }

    @Override
    public List<PromotableFulfillmentGroup> getFulfillmentGroups() {
        if (fulfillmentGroups == null) {
            fulfillmentGroups = new ArrayList<PromotableFulfillmentGroup>();
            for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                fulfillmentGroups.add(itemFactory.createPromotableFulfillmentGroup(fulfillmentGroup, this));
            }
        }
        return Collections.unmodifiableList(fulfillmentGroups);
    }

    @Override
    public boolean isHasOrderAdjustments() {
        return candidateOrderOfferAdjustments.size() > 0;
    }
    
    @Override
    public List<PromotableOrderAdjustment> getCandidateOrderAdjustments() {
        return candidateOrderOfferAdjustments;
    }

    @Override
    public void addCandidateOrderAdjustment(PromotableOrderAdjustment orderAdjustment) {
        candidateOrderOfferAdjustments.add(orderAdjustment);
    }

    @Override
    public void removeAllCandidateOfferAdjustments() {
        removeAllCandidateItemOfferAdjustments();
        removeAllCandidateFulfillmentOfferAdjustments();
        removeAllCandidateOrderOfferAdjustments();
    }

    @Override
    public void removeAllCandidateOrderOfferAdjustments() {
        candidateOrderOfferAdjustments.clear();
    }

    @Override
    public void removeAllCandidateItemOfferAdjustments() {
        for (PromotableOrderItem promotableOrderItem : getDiscountableOrderItems()) {
            promotableOrderItem.removeAllItemAdjustments();
        }
    }

    @Override
    public void removeAllCandidateFulfillmentOfferAdjustments() {
        for (PromotableFulfillmentGroup fulfillmentGroup : getFulfillmentGroups()) {
            fulfillmentGroup.removeAllCandidateAdjustments();
        }
    }

    public void updateRuleVariables(Map<String, Object> ruleVars) {
        ruleVars.put("order", order);
    }

    @Override
    public Order getOrder() {
        return order;
    }
    

    @Override
    public boolean isTotalitarianOfferApplied() {
        return isTotalitarianFgOfferApplied() || isTotalitarianItemOfferApplied() || isTotalitarianOrderOfferApplied();
    }
    
    @Override
    public boolean isTotalitarianOrderOfferApplied() {
        for (PromotableOrderAdjustment adjustment : candidateOrderOfferAdjustments) {
            if (adjustment.isTotalitarian()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isTotalitarianItemOfferApplied() {
        for (PromotableOrderItemPriceDetail itemPriceDetail : getAllPromotableOrderItemPriceDetails()) {
            if (itemPriceDetail.isTotalitarianOfferApplied()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isTotalitarianFgOfferApplied() {
        for (PromotableFulfillmentGroup fg : getFulfillmentGroups()) {
            if (fg.isTotalitarianOfferApplied()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Money calculateOrderAdjustmentTotal() {
        Money orderAdjustmentTotal = BroadleafCurrencyUtils.getMoney(order.getCurrency());
        for (PromotableOrderAdjustment adjustment : candidateOrderOfferAdjustments) {
            orderAdjustmentTotal = orderAdjustmentTotal.add(adjustment.getAdjustmentValue());
        }
        return orderAdjustmentTotal;
    }

    @Override
    public Money calculateItemAdjustmentTotal() {
        Money itemAdjustmentTotal = BroadleafCurrencyUtils.getMoney(order.getCurrency());

        for (PromotableOrderItem item : getDiscountableOrderItems()) {
            itemAdjustmentTotal = itemAdjustmentTotal.add(item.calculateTotalAdjustmentValue());
        }
        return itemAdjustmentTotal;
    }

    public List<PromotableOrderItemPriceDetail> getAllPromotableOrderItemPriceDetails() {
        List<PromotableOrderItemPriceDetail> allPriceDetails = new ArrayList<PromotableOrderItemPriceDetail>();
        for (PromotableOrderItem item : getDiscountableOrderItems()) {
            allPriceDetails.addAll(item.getPromotableOrderItemPriceDetails());
        }
        return allPriceDetails;
    }

    public BroadleafCurrency getOrderCurrency() {
        return this.order.getCurrency();
    }

    public void setTotalFufillmentCharges(Money totalFulfillmentCharges) {
        order.setTotalFulfillmentCharges(totalFulfillmentCharges);
    }

    protected boolean isNotCombinableOrderOfferApplied() {
        for (PromotableOrderAdjustment adjustment : candidateOrderOfferAdjustments) {
            if (!adjustment.isCombinable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canApplyOrderOffer(PromotableCandidateOrderOffer offer) {
        if (isNotCombinableOrderOfferApplied()) {
            return false;
        }
        
        if (!offer.isCombinable() || offer.isTotalitarian()) {
            // Only allow a combinable or totalitarian offer if this is the first adjustment.
            return candidateOrderOfferAdjustments.size() == 0;
        }

        return true;
    }
    
    @Override
    public Money calculateSubtotalWithoutAdjustments() {
        Money calculatedSubTotal = BroadleafCurrencyUtils.getMoney(order.getCurrency());
        for (PromotableOrderItem orderItem : getAllOrderItems()) {
            calculatedSubTotal = calculatedSubTotal.add(orderItem.calculateTotalWithoutAdjustments());
        }
        return calculatedSubTotal;
    }

    @Override
    public Money calculateSubtotalWithAdjustments() {
        Money calculatedSubTotal = BroadleafCurrencyUtils.getMoney(order.getCurrency());
        for (PromotableOrderItem orderItem : getAllOrderItems()) {
            calculatedSubTotal = calculatedSubTotal.add(orderItem.calculateTotalWithAdjustments());
        }
        return calculatedSubTotal;
    }

    @Override
    public boolean isIncludeOrderAndItemAdjustments() {
        return includeOrderAndItemAdjustments;
    }
}
