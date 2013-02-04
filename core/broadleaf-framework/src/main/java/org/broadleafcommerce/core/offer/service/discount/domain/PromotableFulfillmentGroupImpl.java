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

import java.util.ArrayList;
import java.util.List;

public class PromotableFulfillmentGroupImpl implements PromotableFulfillmentGroup {

    private static final long serialVersionUID = 1L;
    
    public List<PromotableFulfillmentGroupAdjustment> candidateFulfillmentGroupAdjustment = new ArrayList<PromotableFulfillmentGroupAdjustment>();
    
    public List<PromotableFulfillmentGroupAdjustment> getCandidateFulfillmentGroupAdjustments() {
        return candidateFulfillmentGroupAdjustment;
    }

    public void addFulfillmentGroupAdjustment(PromotableFulfillmentGroupAdjustment adjustment) {

    }
    
    //    protected BigDecimal adjustmentPrice;  // retailPrice with adjustments
    //    protected FulfillmentGroup delegate;
    //    protected PromotableOrder order;
    //    protected PromotableItemFactory itemFactory;
    //    
    //    public PromotableFulfillmentGroupImpl(FulfillmentGroup fulfillmentGroup, PromotableOrder order, PromotableItemFactory itemFactory) {
    //        this.delegate = fulfillmentGroup;
    //        this.order = order;
    //        this.itemFactory = itemFactory;
    //    }
    //    
    //    @Override
    //    public void reset() {
    //        delegate = null;
    //    }
    //    
    //    @Override
    //    public FulfillmentGroup getDelegate() {
    //        return delegate;
    //    }
    //    
    //    @Override
    //    public List<PromotableOrderItem> getDiscountableDiscreteOrderItems() {
    //        List<PromotableOrderItem> discreteOrderItems = new ArrayList<PromotableOrderItem>();
    //        for (FulfillmentGroupItem fgItem : delegate.getFulfillmentGroupItems()) {
    //            OrderItem orderItem = fgItem.getOrderItem();
    //            if (orderItem instanceof BundleOrderItemImpl) {
    //                BundleOrderItemImpl bundleOrderItem = (BundleOrderItemImpl)orderItem;
    //                for (DiscreteOrderItem discreteOrderItem : bundleOrderItem.getDiscreteOrderItems()) {
    //                    if (discreteOrderItem.getSku().isDiscountable()) {
    //                        discreteOrderItems.add(itemFactory.createPromotableOrderItem(discreteOrderItem, order));
    //                    }
    //                }
    //            } else {
    //                DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem)orderItem;
    //                if (discreteOrderItem.getSku().isDiscountable() == null || discreteOrderItem.getSku().isDiscountable()) {
    //                    //use the decorator patter to return the quantity for this fgItem, not the quantity on the discrete order item
    //                    discreteOrderItems.add(itemFactory.createPromotableOrderItem(new DiscreteOrderItemDecorator(discreteOrderItem, fgItem.getQuantity()), order));
    //                }
    //            }
    //        }
    //        return discreteOrderItems;
    //    }
    //    
    //    /*
    //     * Adds the adjustment to the order item's adjustment list and discounts the order item's adjustment
    //     * price by the value of the adjustment.
    //     */
    //    @Override
    //    public void addFulfillmentGroupAdjustment(PromotableFulfillmentGroupAdjustment fulfillmentGroupAdjustment) {
    //        if (delegate.getFulfillmentGroupAdjustments().size() == 0) {
    //            adjustmentPrice = delegate.getRetailShippingPrice().getAmount();
    //        }
    //        adjustmentPrice = adjustmentPrice.subtract(fulfillmentGroupAdjustment.getValue().getAmount());
    //        delegate.getFulfillmentGroupAdjustments().add(fulfillmentGroupAdjustment.getDelegate());
    //        order.resetTotalitarianOfferApplied();
    //    }
    //
    //    @Override
    //    public void removeAllAdjustments() {
    //        delegate.removeAllAdjustments();
    //        order.resetTotalitarianOfferApplied();
    //        adjustmentPrice = null;
    //    }
    //    
    //    @Override
    //    public Money getPriceBeforeAdjustments(boolean allowSalesPrice) {
    //        Money currentPrice;
    //        if (delegate.getSaleShippingPrice() != null && allowSalesPrice) {
    //            currentPrice = delegate.getSaleShippingPrice();
    //        } else {
    //            currentPrice = delegate.getRetailShippingPrice();
    //        }
    //        return currentPrice;
    //    }
    //    
    //    @Override
    //    public Money getAdjustmentPrice() {
    //        return adjustmentPrice == null ? null : new Money(adjustmentPrice, delegate.getRetailShippingPrice().getCurrency(), adjustmentPrice.scale()==0? BankersRounding.DEFAULT_SCALE:adjustmentPrice.scale());
    //    }
    //
    //    @Override
    //    public void setAdjustmentPrice(Money adjustmentPrice) {
    //        this.adjustmentPrice = Money.toAmount(adjustmentPrice);
    //    }
    //    
    //    @Override
    //    public Money getRetailShippingPrice() {
    //        return delegate.getRetailShippingPrice();
    //    }
    //
    //    @Override
    //    public Money getSaleShippingPrice() {
    //        return delegate.getSaleShippingPrice();
    //    }
    //    
    //    @Override
    //    public void removeAllCandidateOffers() {
    //        delegate.removeAllCandidateOffers();
    //    }
    //    
    //    @Override
    //    public Money getShippingPrice() {
    //        return delegate.getShippingPrice();
    //    }
    //    
    //    @Override
    //    public void setShippingPrice(Money shippingPrice) {
    //        delegate.setShippingPrice(shippingPrice);
    //    }
    //    
    //    @Override
    //    public void addCandidateFulfillmentGroupOffer(PromotableCandidateFulfillmentGroupOffer candidateOffer) {
    //        delegate.addCandidateFulfillmentGroupOffer(candidateOffer.getDelegate());
    //    }
    //    
    //    // FulfillmentGroup methods
    //
    //    public Long getId() {
    //        return delegate.getId();
    //    }
    //
    //    public void setId(Long id) {
    //        delegate.setId(id);
    //    }
    //
    //    public Order getOrder() {
    //        return delegate.getOrder();
    //    }
    //
    //    public void setOrder(Order order) {
    //        delegate.setOrder(order);
    //    }
    //
    //    public Address getAddress() {
    //        return delegate.getAddress();
    //    }
    //
    //    public void setAddress(Address address) {
    //        delegate.setAddress(address);
    //    }
    //
    //    public Phone getPhone() {
    //        return delegate.getPhone();
    //    }
    //
    //    public void setPhone(Phone phone) {
    //        delegate.setPhone(phone);
    //    }
    //
    //    public List<FulfillmentGroupItem> getFulfillmentGroupItems() {
    //        return delegate.getFulfillmentGroupItems();
    //    }
    //
    //    public void setFulfillmentGroupItems(
    //            List<FulfillmentGroupItem> fulfillmentGroupItems) {
    //        delegate.setFulfillmentGroupItems(fulfillmentGroupItems);
    //    }
    //
    //    public void addFulfillmentGroupItem(
    //            FulfillmentGroupItem fulfillmentGroupItem) {
    //        delegate.addFulfillmentGroupItem(fulfillmentGroupItem);
    //    }
    //
    //    public String getMethod() {
    //        return delegate.getMethod();
    //    }
    //
    //    public void setMethod(String fulfillmentMethod) {
    //        delegate.setMethod(fulfillmentMethod);
    //    }
    //
    //    public String getReferenceNumber() {
    //        return delegate.getReferenceNumber();
    //    }
    //
    //    public void setReferenceNumber(String referenceNumber) {
    //        delegate.setReferenceNumber(referenceNumber);
    //    }
    //
    //    public FulfillmentType getType() {
    //        return delegate.getType();
    //    }
    //
    //    public void setType(FulfillmentType type) {
    //        delegate.setType(type);
    //    }
    //
    //    public List<CandidateFulfillmentGroupOffer> getCandidateFulfillmentGroupOffers() {
    //        return delegate.getCandidateFulfillmentGroupOffers();
    //    }
    //
    //    public void setCandidateFulfillmentGroupOffer(
    //            List<CandidateFulfillmentGroupOffer> candidateOffers) {
    //        delegate.setCandidateFulfillmentGroupOffer(candidateOffers);
    //    }
    //
    //    public List<FulfillmentGroupAdjustment> getFulfillmentGroupAdjustments() {
    //        return delegate.getFulfillmentGroupAdjustments();
    //    }
    //
    //    public void setFulfillmentGroupAdjustments(
    //            List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments) {
    //        delegate
    //                .setFulfillmentGroupAdjustments(fulfillmentGroupAdjustments);
    //    }
    //
    //    public Money getTotalTax() {
    //        return delegate.getTotalTax();
    //    }
    //
    //    public void setTotalTax(Money totalTax) {
    //        delegate.setTotalTax(totalTax);
    //    }
    //
    //    public String getDeliveryInstruction() {
    //        return delegate.getDeliveryInstruction();
    //    }
    //
    //    public void setDeliveryInstruction(String deliveryInstruction) {
    //        delegate.setDeliveryInstruction(deliveryInstruction);
    //    }
    //
    //    public PersonalMessage getPersonalMessage() {
    //        return delegate.getPersonalMessage();
    //    }
    //
    //    public void setPersonalMessage(PersonalMessage personalMessage) {
    //        delegate.setPersonalMessage(personalMessage);
    //    }
    //
    //    public boolean isPrimary() {
    //        return delegate.isPrimary();
    //    }
    //
    //    public void setPrimary(boolean primary) {
    //        delegate.setPrimary(primary);
    //    }
    //
    //    public Money getMerchandiseTotal() {
    //        return delegate.getMerchandiseTotal();
    //    }
    //
    //    public void setMerchandiseTotal(Money merchandiseTotal) {
    //        delegate.setMerchandiseTotal(merchandiseTotal);
    //    }
    //
    //    public Money getTotal() {
    //        return delegate.getTotal();
    //    }
    //
    //    public void setTotal(Money orderTotal) {
    //        delegate.setTotal(orderTotal);
    //    }
    //
    //    public FulfillmentGroupStatusType getStatus() {
    //        return delegate.getStatus();
    //    }
    //
    //    public List<FulfillmentGroupFee> getFulfillmentGroupFees() {
    //        return delegate.getFulfillmentGroupFees();
    //    }
    //
    //    public void setFulfillmentGroupFees(
    //            List<FulfillmentGroupFee> fulfillmentGroupFees) {
    //        delegate.setFulfillmentGroupFees(fulfillmentGroupFees);
    //    }
    //
    //    public void addFulfillmentGroupFee(FulfillmentGroupFee fulfillmentGroupFee) {
    //        delegate.addFulfillmentGroupFee(fulfillmentGroupFee);
    //    }
    //
    //    public void removeAllFulfillmentGroupFees() {
    //        delegate.removeAllFulfillmentGroupFees();
    //    }
    //
    //    public String getService() {
    //        return delegate.getService();
    //    }
    //
    //    public void setService(String service) {
    //        delegate.setService(service);
    //    }
    //
    //    public List<DiscreteOrderItem> getDiscreteOrderItems() {
    //        return delegate.getDiscreteOrderItems();
    //    }
    //
    //    public Money getFulfillmentGroupAdjustmentsValue() {
    //        return delegate.getFulfillmentGroupAdjustmentsValue();
    //    }
    
}
