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

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderAttribute;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;

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

    @XmlElement(name = "paymentInfo")
    @XmlElementWrapper(name = "paymentInfos")
    protected List<PaymentInfoWrapper> paymentInfos;

    @XmlElement(name = "orderAdjustment")
    @XmlElementWrapper(name = "orderAdjustments")
    protected List<AdjustmentWrapper> orderAdjustments;

    @XmlElement(name = "orderAttribute")
    @XmlElementWrapper(name = "orderAttributes")
    protected List<OrderAttributeWrapper> orderAttributes;

    @Override
    public void wrapDetails(Order model, HttpServletRequest request) {
        this.id = model.getId();
        this.status = model.getStatus().getType();
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

        if (model.getPaymentInfos() != null && !model.getPaymentInfos().isEmpty()) {
            this.paymentInfos = new ArrayList<PaymentInfoWrapper>();
            for (PaymentInfo paymentInfo : model.getPaymentInfos()) {
                PaymentInfoWrapper paymentInfoWrapper = (PaymentInfoWrapper) context.getBean(PaymentInfoWrapper.class.getName());
                paymentInfoWrapper.wrapSummary(paymentInfo, request);
                this.paymentInfos.add(paymentInfoWrapper);
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
    }

    @Override
    public void wrapSummary(Order model, HttpServletRequest request) {
        wrapDetails(model, request);
    }
}
