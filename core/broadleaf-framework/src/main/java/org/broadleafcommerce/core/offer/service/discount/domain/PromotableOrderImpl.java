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
            createOrderAdjustments();
        }
    }

    /**
     * Bring over the order adjustments.   Intended to be used when processing
     * fulfillment orders.
     */
    protected void createOrderAdjustments() {
        if (order.getOrderAdjustments() != null) {
            for (OrderAdjustment adjustment : order.getOrderAdjustments()) {
                if (adjustment.getOffer() != null) {
                    PromotableCandidateOrderOffer pcoo = itemFactory.createPromotableCandidateOrderOffer(this, adjustment.getOffer());
                    PromotableOrderAdjustment adj = itemFactory.createPromotableOrderAdjustment(pcoo, this);
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
        if (discountableOrderItems == null) {
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
        boolean totalitarianOfferApplied = false;
        for (PromotableOrderAdjustment adjustment : candidateOrderOfferAdjustments) {
            if (adjustment.isTotalitarian()) {
                totalitarianOfferApplied = true;
                break;
            }
        }
        if (!totalitarianOfferApplied) {
            for (PromotableOrderItemPriceDetail itemPriceDetail : getAllPromotableOrderItemPriceDetails()) {
                totalitarianOfferApplied = itemPriceDetail.isTotalitarianOfferApplied();
            }
        }
        if (!totalitarianOfferApplied) {
            for (PromotableFulfillmentGroup fg : getFulfillmentGroups()) {
                if (fg.isTotalitarianOfferApplied()) {
                    return true;
                }
            }
        }
        return totalitarianOfferApplied;
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
        if (isTotalitarianOfferApplied()) {
            return false;
        }
        
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
    public boolean canApplyItemOffer(PromotableCandidateItemOffer offer) {
        // TODO Auto-generated method stub
        return false;
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

    //    protected boolean notCombinableOfferAppliedAtAnyLevel = false;
    //    protected boolean notCombinableOfferApplied = false;    
    //    protected boolean hasOrderAdjustments = false;
    //    protected BigDecimal adjustmentPrice;  // retailPrice with order adjustments (no item adjustments)

    //    protected List<OrderMultishipOption> multiShipOptions = new ArrayList<OrderMultishipOption>();
    //    protected boolean hasMultiShipOptions = false;
    //    
//  @Override
//  public boolean containsNotStackableFulfillmentGroupOffer() {
//      boolean isContainsNotStackableFulfillmentGroupOffer = false;
//      for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
//          for (FulfillmentGroupAdjustment fgAdjustment : fg.getFulfillmentGroupAdjustments()) {
//              if (!fgAdjustment.getOffer().isStackable()) {
//                  isContainsNotStackableFulfillmentGroupOffer = true;
//                  break;
//              }
//          }
//      }
//      return isContainsNotStackableFulfillmentGroupOffer;
//  }
    //    
    //    public void reset() {
    //        delegate = null;
    //        resetFulfillmentGroups();
    //        resetDiscreteOrderItems();
    //    }
    //    
    //    @Override
    //    public void resetFulfillmentGroups() {
    //        if (fulfillmentGroups != null) {
    //            for (PromotableFulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
    //                fulfillmentGroup.reset();
    //            }
    //        }
    //        fulfillmentGroups = null;
    //    }
    //    
    //    @Override
    //    public void resetDiscreteOrderItems() {
    //        if (discreteOrderItems != null) {
    //            for (PromotableOrderItem orderItem : discreteOrderItems) {
    //                orderItem.reset();
    //            }
    //            discreteOrderItems = null;
    //        }
    //        if (discountableDiscreteOrderItems != null) {
    //            for (PromotableOrderItem orderItem : discountableDiscreteOrderItems) {
    //                orderItem.reset();
    //            }
    //            discountableDiscreteOrderItems = null;
    //        }
    //    }


    //    
    //    @Override
    //    public Money getAdjustmentPrice() {
    //        return adjustmentPrice == null ? null : new Money(adjustmentPrice, delegate.getSubTotal().getCurrency(), adjustmentPrice.scale()==0? BankersRounding.DEFAULT_SCALE:adjustmentPrice.scale());
    //    }
    //
    //    @Override
    //    public void setAdjustmentPrice(Money adjustmentPrice) {
    //        this.adjustmentPrice = Money.toAmount(adjustmentPrice);
    //    }
    //    
    //    @Override
    //    public boolean isNotCombinableOfferApplied() {
    //        return notCombinableOfferApplied;
    //    }
    //
    //    @Override
    //    public boolean isHasOrderAdjustments() {
    //        return hasOrderAdjustments;
    //    }
    //    
    //    @Override
    //    public boolean isTotalitarianOfferApplied() {
    //        return totalitarianOfferApplied;
    //    }
    //
    //    @Override
    //    public void setTotalitarianOfferApplied(boolean totalitarianOfferApplied) {
    //        this.totalitarianOfferApplied = totalitarianOfferApplied;
    //    }
    //
    //    @Override
    //    public boolean isNotCombinableOfferAppliedAtAnyLevel() {
    //        return notCombinableOfferAppliedAtAnyLevel;
    //    }
    //
    //    @Override
    //    public void setNotCombinableOfferAppliedAtAnyLevel(boolean notCombinableOfferAppliedAtAnyLevel) {
    //        this.notCombinableOfferAppliedAtAnyLevel = notCombinableOfferAppliedAtAnyLevel;
    //    }
    //
    //    @Override
    //    public List<OrderItemSplitContainer> getSplitItems() {
    //        return splitItems;
    //    }
    //
    //    @Override
    //    public void setSplitItems(List<OrderItemSplitContainer> splitItems) {
    //        this.splitItems = splitItems;
    //    }
    //    
    //    @Override
    //    public List<PromotableOrderItem> searchSplitItems(PromotableOrderItem key) {
    //        for (OrderItemSplitContainer container : splitItems) {
    //            if (container.getKey().equals(key.getDelegate())) {
    //                return container.getSplitItems();
    //            }
    //        }
    //        return null;
    //    }
    //
    //    @Override
    //    public OrderItem searchSplitItemsForKey(OrderItem orderItem) {
    //        for (OrderItemSplitContainer container : splitItems) {
    //            for (PromotableOrderItem splitItem : container.getSplitItems()) {
    //                if (splitItem.getDelegate().equals(orderItem)) {
    //                    return container.getKey();
    //                }
    //            }
    //        }
    //        return null;
    //    }
    //
    //    @Override
    //    public List<BundleOrderItem> searchBundleSplitItems(BundleOrderItem key) {
    //        for (BundleOrderItemSplitContainer container : bundleSplitItems) {
    //            if (container.getKey().equals(key)) {
    //                return container.getSplitItems();
    //            }
    //        }
    //        return null;
    //    }
    //    
    //    @Override
    //    public void removeAllCandidateOffers() {
    //        removeAllCandidateOrderOffers();
    //        for (OrderItem orderItem : getDelegate().getOrderItems()) {
    //            orderItem.removeAllCandidateItemOffers();
    //        }
    //
    //        removeAllCandidateFulfillmentGroupOffers();
    //    }
    //    
    //    @Override
    //    public void removeAllCandidateFulfillmentGroupOffers() {
    //        if (getFulfillmentGroups() != null) {
    //            for (PromotableFulfillmentGroup fg : getFulfillmentGroups()) {
    //                fg.removeAllCandidateOffers();
    //            }
    //        }
    //    }
    //
    //    @Override
    //    public void removeAllCandidateOrderOffers() {
    //        if (delegate.getCandidateOrderOffers() != null) {
    //            for (CandidateOrderOffer candidate : delegate.getCandidateOrderOffers()) {
    //                candidate.setOrder(null);
    //            }
    //            delegate.getCandidateOrderOffers().clear();
    //        }
    //    }
    //    

    //
    //    @Override
    //    public void removeAllAddedOfferCodes() {
    //        if (delegate.getAddedOfferCodes() != null) {
    //            delegate.getAddedOfferCodes().clear();
    //        }
    //    }
    //    
    //    @Override
    //    public void addCandidateOrderOffer(PromotableCandidateOrderOffer candidateOrderOffer) {
    //        delegate.getCandidateOrderOffers().add(candidateOrderOffer.getDelegate());
    //    }
    //    
    //    @Override
    //    public Money calculateOrderItemsCurrentPrice() {
    //        Money calculatedSubTotal = BroadleafCurrencyUtils.getMoney(getDelegate().getCurrency());
    //        for (PromotableOrderItem orderItem : getDiscountableDiscreteOrderItems()) {
    //            Money currentPrice = orderItem.getCurrentPrice();
    //            calculatedSubTotal = calculatedSubTotal.add(currentPrice.multiply(orderItem.getQuantity()));
    //        }
    //        return calculatedSubTotal;
    //    }
    //    
    //    @Override
    //    public Money calculateOrderItemsPriceWithoutAdjustments() {
    //        Money calculatedSubTotal = BroadleafCurrencyUtils.getMoney(getDelegate().getCurrency());
    //        for (OrderItem orderItem : delegate.getOrderItems()) {
    //            Money price = orderItem.getPriceBeforeAdjustments(true);
    //            calculatedSubTotal = calculatedSubTotal.add(price.multiply(orderItem.getQuantity()));
    //        }
    //        return calculatedSubTotal;
    //    }
    //    
    //    @Override
    //    public List<PromotableOrderItem> getAllSplitItems() {
    //        List<PromotableOrderItem> response = new ArrayList<PromotableOrderItem>();
    //        for (OrderItemSplitContainer container : getSplitItems()) {
    //            response.addAll(container.getSplitItems());
    //        }
    //        
    //        return response;
    //    }
    //    
    //    @Override
    //    public Money getSubTotal() {
    //        return delegate.getSubTotal();
    //    }
    //    
    //    @Override
    //    public List<PromotableFulfillmentGroup> getFulfillmentGroups() {
    //        if (fulfillmentGroups == null) {
    //            fulfillmentGroups = new ArrayList<PromotableFulfillmentGroup>();
    //            for (FulfillmentGroup fulfillmentGroup : delegate.getFulfillmentGroups()) {
    //                fulfillmentGroups.add(itemFactory.createPromotableFulfillmentGroup(fulfillmentGroup, this));
    //            }
    //        }
    //        return Collections.unmodifiableList(fulfillmentGroups);
    //    }
    //    
    //    @Override
    //    public void setTotalShipping(Money totalShipping) {
    //        delegate.setTotalShipping(totalShipping);
    //    }
    //    
    //    @Override
    //    public Money calculateTaxableItemTotal() {
    //        return delegate.calculateTaxableItemTotal();
    //    }
    //
    //    @Override
    //    public Money calculateItemTotal() {
    //        return delegate.calculateItemTotal();
    //    }
    //    
    //    @Override
    //    public void setSubTotal(Money subTotal) {
    //        delegate.setSubTotal(subTotal);
    //    }
    //    
    //    @Override
    //    public void assignOrderItemsFinalPrice() {
    //        for (PromotableOrderItem orderItem : getDiscountableDiscreteOrderItems()) {
    //            orderItem.assignFinalPrice();
    //        }
    //        for (OrderItem orderItem : getDelegate().getOrderItems()) {
    //            if (orderItem instanceof BundleOrderItem) {
    //                orderItem.assignFinalPrice();
    //            }
    //        }
    //    }
    //    
    //    @Override
    //    public Customer getCustomer() {
    //        return delegate.getCustomer();
    //    }
    //    
    //    
    //    @Override
    //    public List<PromotableOrderItem> getDiscountableDiscreteOrderItems() {
    //        return getDiscountableDiscreteOrderItems(false);
    //    }
    //    

    //
    //    @Override
    //    public Order getDelegate() {
    //        return delegate;
    //    }
    //
    //    @Override
    //    public void setDelegate(Order order) {
    //        this.delegate = delegate;
    //    }
    //
    //    @Override
    //    public List<BundleOrderItemSplitContainer> getBundleSplitItems() {
    //        return bundleSplitItems;
    //    }
    //
    //    @Override
    //    public void setBundleSplitItems(List<BundleOrderItemSplitContainer> bundleSplitItems) {
    //        this.bundleSplitItems = bundleSplitItems;
    //    }
    //
    //    @Override
    //    public List<OrderMultishipOption> getMultiShipOptions() {
    //        return multiShipOptions;
    //    }
    //
    //    @Override
    //    public void setMultiShipOptions(List<OrderMultishipOption> multiShipOptions) {
    //        this.multiShipOptions = multiShipOptions;
    //    }
    //
    //    @Override
    //    public boolean isHasMultiShipOptions() {
    //        return hasMultiShipOptions;
    //    }
    //
    //    @Override
    //    public void setHasMultiShipOptions(boolean hasMultiShipOptions) {
    //        this.hasMultiShipOptions = hasMultiShipOptions;
    //    }
}
