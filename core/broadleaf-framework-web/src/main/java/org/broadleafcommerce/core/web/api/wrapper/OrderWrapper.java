/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderAttribute;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;
import org.broadleafcommerce.core.payment.domain.OrderPayment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around Order.
 * <p/>
 * User: Elbert Bautista
 * Date: 4/10/12
 */
@XmlRootElement(name = "order")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderWrapper extends BaseWrapper implements APIWrapper<Order> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String status;

    @XmlElement
    protected Money totalTax;

    @XmlElement
    protected Money totalShipping;

    @XmlElement
    protected Money subTotal;

    @XmlElement
    protected Money total;

    @XmlElement
    protected CustomerWrapper customer;

    @XmlElement(name = "orderItem")
    @XmlElementWrapper(name = "orderItems")
    protected List<OrderItemWrapper> orderItems;

    @XmlElement(name = "fulfillmentGroup")
    @XmlElementWrapper(name = "fulfillmentGroups")
    protected List<FulfillmentGroupWrapper> fulfillmentGroups;

    @XmlElement(name = "payment")
    @XmlElementWrapper(name = "payments")
    protected List<OrderPaymentWrapper> payments;

    @XmlElement(name = "orderAdjustment")
    @XmlElementWrapper(name = "orderAdjustments")
    protected List<AdjustmentWrapper> orderAdjustments;

    @XmlElement(name = "orderAttribute")
    @XmlElementWrapper(name = "orderAttributes")
    protected List<OrderAttributeWrapper> orderAttributes;

    @XmlElement(name = "cartMessages")
    @XmlElementWrapper(name = "cartMessages")
    protected List<CartMessageWrapper> cartMessages;

    @Override
    public void wrapDetails(Order model, HttpServletRequest request) {
        this.id = model.getId();

        if (model.getStatus() != null) {
            this.status = model.getStatus().getType();
        }

        this.totalTax = model.getTotalTax();
        this.totalShipping = model.getTotalShipping();
        this.subTotal = model.getSubTotal();
        this.total = model.getTotal();

        if (model.getOrderItems() != null && !model.getOrderItems().isEmpty()) {
            this.orderItems = new ArrayList<OrderItemWrapper>();
            for (OrderItem orderItem : model.getOrderItems()) {
                OrderItemWrapper orderItemWrapper = (OrderItemWrapper) context.getBean(OrderItemWrapper.class.getName());
                orderItemWrapper.wrapSummary(orderItem, request);
                this.orderItems.add(orderItemWrapper);
            }
        }

        if (model.getFulfillmentGroups() != null && !model.getFulfillmentGroups().isEmpty()) {
            this.fulfillmentGroups = new ArrayList<FulfillmentGroupWrapper>();
            for (FulfillmentGroup fulfillmentGroup : model.getFulfillmentGroups()) {
                FulfillmentGroupWrapper fulfillmentGroupWrapper = (FulfillmentGroupWrapper) context.getBean(FulfillmentGroupWrapper.class.getName());
                fulfillmentGroupWrapper.wrapSummary(fulfillmentGroup, request);
                this.fulfillmentGroups.add(fulfillmentGroupWrapper);
            }
        }

        if (model.getPayments() != null && !model.getPayments().isEmpty()) {
            this.payments = new ArrayList<OrderPaymentWrapper>();
            for (OrderPayment payment : model.getPayments()) {
                OrderPaymentWrapper paymentWrapper = (OrderPaymentWrapper) context.getBean(OrderPaymentWrapper.class.getName());
                paymentWrapper.wrapSummary(payment, request);
                this.payments.add(paymentWrapper);
            }
        }

        if (model.getOrderAdjustments() != null && !model.getOrderAdjustments().isEmpty()) {
            this.orderAdjustments = new ArrayList<AdjustmentWrapper>();
            for (OrderAdjustment orderAdjustment : model.getOrderAdjustments()) {
                AdjustmentWrapper orderAdjustmentWrapper = (AdjustmentWrapper) context.getBean(AdjustmentWrapper.class.getName());
                orderAdjustmentWrapper.wrapSummary(orderAdjustment, request);
                this.orderAdjustments.add(orderAdjustmentWrapper);
            }
        }
        if (model.getOrderAttributes() != null && !model.getOrderAttributes().isEmpty()) {
            Map<String, OrderAttribute> itemAttributes = model.getOrderAttributes();
            this.orderAttributes = new ArrayList<OrderAttributeWrapper>();
            Set<String> keys = itemAttributes.keySet();
            for (String key : keys) {
                OrderAttributeWrapper orderAttributeWrapper =
                        (OrderAttributeWrapper) context.getBean(OrderAttributeWrapper.class.getName());
                orderAttributeWrapper.wrapSummary(itemAttributes.get(key), request);
                this.orderAttributes.add(orderAttributeWrapper);
            }
        }
        CustomerWrapper customerWrapper = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        customerWrapper.wrapDetails(model.getCustomer(), request);
        this.customer = customerWrapper;

        if (model.getOrderMessages() != null && !model.getOrderMessages().isEmpty()) {
            for (ActivityMessageDTO dto : model.getOrderMessages()) {

                CartMessageWrapper cartMessageWrapper = (CartMessageWrapper) context.getBean(CartMessageWrapper.class.getName());
                cartMessageWrapper.wrapSummary(dto, request);
                if (cartMessages == null) {
                    cartMessages = new ArrayList<CartMessageWrapper>();
                }
                this.cartMessages.add(cartMessageWrapper);

            }
        }

    }

    @Override
    public void wrapSummary(Order model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    
    /**
     * @return the totalTax
     */
    public Money getTotalTax() {
        return totalTax;
    }

    
    /**
     * @param totalTax the totalTax to set
     */
    public void setTotalTax(Money totalTax) {
        this.totalTax = totalTax;
    }

    
    /**
     * @return the totalShipping
     */
    public Money getTotalShipping() {
        return totalShipping;
    }

    
    /**
     * @param totalShipping the totalShipping to set
     */
    public void setTotalShipping(Money totalShipping) {
        this.totalShipping = totalShipping;
    }

    
    /**
     * @return the subTotal
     */
    public Money getSubTotal() {
        return subTotal;
    }

    
    /**
     * @param subTotal the subTotal to set
     */
    public void setSubTotal(Money subTotal) {
        this.subTotal = subTotal;
    }

    
    /**
     * @return the total
     */
    public Money getTotal() {
        return total;
    }

    
    /**
     * @param total the total to set
     */
    public void setTotal(Money total) {
        this.total = total;
    }

    
    /**
     * @return the customer
     */
    public CustomerWrapper getCustomer() {
        return customer;
    }

    
    /**
     * @param customer the customer to set
     */
    public void setCustomer(CustomerWrapper customer) {
        this.customer = customer;
    }

    
    /**
     * @return the orderItems
     */
    public List<OrderItemWrapper> getOrderItems() {
        return orderItems;
    }

    
    /**
     * @param orderItems the orderItems to set
     */
    public void setOrderItems(List<OrderItemWrapper> orderItems) {
        this.orderItems = orderItems;
    }

    
    /**
     * @return the fulfillmentGroups
     */
    public List<FulfillmentGroupWrapper> getFulfillmentGroups() {
        return fulfillmentGroups;
    }

    
    /**
     * @param fulfillmentGroups the fulfillmentGroups to set
     */
    public void setFulfillmentGroups(List<FulfillmentGroupWrapper> fulfillmentGroups) {
        this.fulfillmentGroups = fulfillmentGroups;
    }

    
    /**
     * @return the payments
     */
    public List<OrderPaymentWrapper> getPayments() {
        return payments;
    }

    
    /**
     * @param payments the payments to set
     */
    public void setPayments(List<OrderPaymentWrapper> payments) {
        this.payments = payments;
    }

    
    /**
     * @return the orderAdjustments
     */
    public List<AdjustmentWrapper> getOrderAdjustments() {
        return orderAdjustments;
    }

    
    /**
     * @param orderAdjustments the orderAdjustments to set
     */
    public void setOrderAdjustments(List<AdjustmentWrapper> orderAdjustments) {
        this.orderAdjustments = orderAdjustments;
    }

    
    /**
     * @return the orderAttributes
     */
    public List<OrderAttributeWrapper> getOrderAttributes() {
        return orderAttributes;
    }

    
    /**
     * @param orderAttributes the orderAttributes to set
     */
    public void setOrderAttributes(List<OrderAttributeWrapper> orderAttributes) {
        this.orderAttributes = orderAttributes;
    }

    
    /**
     * @return the cartMessages
     */
    public List<CartMessageWrapper> getCartMessages() {
        return cartMessages;
    }

    
    /**
     * @param cartMessages the cartMessages to set
     */
    public void setCartMessages(List<CartMessageWrapper> cartMessages) {
        this.cartMessages = cartMessages;
    }


}
