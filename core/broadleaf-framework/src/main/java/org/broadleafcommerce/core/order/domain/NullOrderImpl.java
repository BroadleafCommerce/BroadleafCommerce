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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferInfo;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * NullOrderImpl is a class that represents an unmodifiable, empty order. This class is used as the default order
 * for a customer. It is a shared class between customers, and serves as a placeholder order until an item 
 * is initially added to cart, at which point a real Order gets created. This prevents creating individual orders
 * for customers that are just browsing the site.
 * 
 * @author apazzolini
 */
public class NullOrderImpl implements Order {

    private static final long serialVersionUID = 1L;

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Auditable getAuditable() {
        return null;
    }

    @Override
    public void setAuditable(Auditable auditable) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Money getSubTotal() {
        return Money.ZERO;
    }

    @Override
    public void setSubTotal(Money subTotal) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public void assignOrderItemsFinalPrice() {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Money getTotal() {
        return null;
    }

    @Override
    public Money getTotalAfterAppliedPayments() {
        return null;
    }

    @Override
    public void setTotal(Money orderTotal) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Customer getCustomer() {
        return null;
    }

    @Override
    public void setCustomer(Customer customer) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public OrderStatus getStatus() {
        return null;
    }

    @Override
    public void setStatus(OrderStatus status) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public List<OrderItem> getOrderItems() {
        return null;
    }

    @Override
    public void setOrderItems(List<OrderItem> orderItems) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public void addOrderItem(OrderItem orderItem) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public List<FulfillmentGroup> getFulfillmentGroups() {
        return null;
    }

    @Override
    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations."); 
    }

    @Override
    public void setCandidateOrderOffers(List<CandidateOrderOffer> candidateOrderOffers) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public List<CandidateOrderOffer> getCandidateOrderOffers() {
        return null;
    }

    @Override
    public Date getSubmitDate() {
        return null;
    }

    @Override
    public void setSubmitDate(Date submitDate) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Money getTotalTax() {
        return null;
    }

    @Override
    public void setTotalTax(Money totalTax) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Money getTotalShipping() {
        return null;
    }

    @Override
    public void setTotalShipping(Money totalShipping) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public List<OrderPayment> getPayments() {
        return null;
    }

    @Override
    public void setPayments(List<OrderPayment> paymentInfos) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public boolean hasCategoryItem(String categoryName) {
        return false;
    }

    @Override
    public List<OrderAdjustment> getOrderAdjustments() {
        return null;
    }

    @Override
    public List<DiscreteOrderItem> getDiscreteOrderItems() {
        return null;
    }

    @Override
    public boolean containsSku(Sku sku) {
        return false;
    }

    @Override
    public List<OfferCode> getAddedOfferCodes() {
        return null;
    }

    @Override
    public String getFulfillmentStatus() {
        return null;
    }

    @Override
    public String getOrderNumber() {
        return null;
    }

    @Override
    public void setOrderNumber(String orderNumber) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public String getEmailAddress() {
        return null;
    }

    @Override
    public void setEmailAddress(String emailAddress) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Map<Offer, OfferInfo> getAdditionalOfferInformation() {
        return null;
    }

    @Override
    public void setAdditionalOfferInformation(Map<Offer, OfferInfo> additionalOfferInformation) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Money getItemAdjustmentsValue() {
        return null;
    }

    @Override
    public Money getOrderAdjustmentsValue() {
        return Money.ZERO;
    }

    @Override
    public Money getTotalAdjustmentsValue() {
        return null;
    }

    @Override
    public boolean updatePrices() {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Money getFulfillmentGroupAdjustmentsValue() {
        return null;
    }

    @Override
    public void addOfferCode(OfferCode addedOfferCode) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public void addAddedOfferCode(OfferCode offerCode) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Map<String, OrderAttribute> getOrderAttributes() {
        return null;
    }

    @Override
    public void setOrderAttributes(Map<String, OrderAttribute> orderAttributes) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public BroadleafCurrency getCurrency() {
        return null;
    }

    @Override
    public void setCurrency(BroadleafCurrency currency) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void setLocale(Locale locale) {
    }

    @Override
    public Money calculateSubTotal() {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public Money getTotalFulfillmentCharges() {
        return null;
    }

    @Override
    public void setTotalFulfillmentCharges(Money totalFulfillmentCharges) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public boolean finalizeItemPrices() {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

    @Override
    public boolean getHasOrderAdjustments() {
        return false;
    }

    @Override
    public List<ActivityMessageDTO> getOrderMessages() {
        return null;
    }

    @Override
    public void setOrderMessages(List<ActivityMessageDTO> orderMessages) {
        throw new UnsupportedOperationException("NullOrder does not support any modification operations.");
    }

}
