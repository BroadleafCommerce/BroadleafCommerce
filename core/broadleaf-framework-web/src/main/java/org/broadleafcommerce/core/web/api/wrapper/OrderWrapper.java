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
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    protected List<OrderItemWrapper> orderItems = new LinkedList<OrderItemWrapper>();

    @XmlElement(name = "fulfillmentGroup")
    @XmlElementWrapper(name = "fulfillmentGroups")
    protected List<FulfillmentGroupWrapper> fulfillmentGroups = new LinkedList<FulfillmentGroupWrapper>();

    @XmlElement(name = "paymentInfo")
    @XmlElementWrapper(name = "paymentInfos")
    protected List<PaymentInfoWrapper> paymentInfos = new LinkedList<PaymentInfoWrapper>();

    @Override
    public void wrap(Order model, HttpServletRequest request) {
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
                orderItemWrapper.wrap(orderItem, request);
                this.orderItems.add(orderItemWrapper);
            }
        }

        if (model.getFulfillmentGroups() != null && !model.getFulfillmentGroups().isEmpty()) {
            this.fulfillmentGroups = new ArrayList<FulfillmentGroupWrapper>();
            for (FulfillmentGroup fulfillmentGroup : model.getFulfillmentGroups()) {
                FulfillmentGroupWrapper fulfillmentGroupWrapper = (FulfillmentGroupWrapper) context.getBean(FulfillmentGroupWrapper.class.getName());
                fulfillmentGroupWrapper.wrap(fulfillmentGroup, request);
                this.fulfillmentGroups.add(fulfillmentGroupWrapper);
            }
        }

        if (model.getPaymentInfos() != null && !model.getPaymentInfos().isEmpty()) {
            this.paymentInfos = new ArrayList<PaymentInfoWrapper>();
            for (PaymentInfo paymentInfo : model.getPaymentInfos()) {
                PaymentInfoWrapper paymentInfoWrapper = (PaymentInfoWrapper) context.getBean(PaymentInfoWrapper.class.getName());
                paymentInfoWrapper.wrap(paymentInfo, request);
                this.paymentInfos.add(paymentInfoWrapper);
            }
        }

        CustomerWrapper customerWrapper = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        customerWrapper.wrap(model.getCustomer(), request);
        this.customer = customerWrapper;
    }
}
