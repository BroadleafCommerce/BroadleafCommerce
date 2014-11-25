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
import org.broadleafcommerce.common.money.util.CurrencyAdapter;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.util.xml.BigDecimalRoundingAdapter;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This is a JAXB wrapper around OrderPayment.
 * <p/>
 * User: Elbert Bautista
 * Date: 4/26/12
 */
@XmlRootElement(name = "payment")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderPaymentWrapper extends BaseWrapper implements APIWrapper<OrderPayment>, APIUnwrapper<OrderPayment> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected Long orderId;

    @XmlElement
    protected String type;

    @XmlElement
    protected AddressWrapper billingAddress;

    @XmlElement
    @XmlJavaTypeAdapter(value = BigDecimalRoundingAdapter.class)
    protected BigDecimal amount;

    @XmlElement
    @XmlJavaTypeAdapter(value = CurrencyAdapter.class)
    protected Currency currency;

    @XmlElement
    protected String referenceNumber;

    @XmlElement
    protected String gatewayType;

    @XmlElement(name = "transaction")
    @XmlElementWrapper(name = "transactions")
    protected List<PaymentTransactionWrapper> transactions;

    @Override
    public void wrapDetails(OrderPayment model, HttpServletRequest request) {
        this.id = model.getId();

        if (model.getOrder() != null) {
            this.orderId = model.getOrder().getId();
        }

        if (model.getType() != null) {
            this.type = model.getType().getType();
        }

        if (model.getGatewayType() != null) {
            this.gatewayType = model.getGatewayType().getType();
        }

        if (model.getBillingAddress() != null) {
            AddressWrapper addressWrapper = (AddressWrapper) context.getBean(AddressWrapper.class.getName());
            addressWrapper.wrapDetails(model.getBillingAddress(), request);
            this.billingAddress = addressWrapper;
        }

        if (model.getAmount() != null) {
            this.amount = model.getAmount().getAmount();
            this.currency = model.getAmount().getCurrency();
        }

        if (model.getTransactions() != null && !model.getTransactions().isEmpty()) {
            this.transactions = new ArrayList<PaymentTransactionWrapper>();
            for (PaymentTransaction transaction : model.getTransactions()) {
                PaymentTransactionWrapper transactionWrapper = (PaymentTransactionWrapper) context.getBean(PaymentTransactionWrapper.class.getName());
                transactionWrapper.wrapSummary(transaction, request);
                this.transactions.add(transactionWrapper);
            }
        }

        this.referenceNumber = model.getReferenceNumber();
    }

    @Override
    public void wrapSummary(OrderPayment model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public OrderPayment unwrap(HttpServletRequest request, ApplicationContext context) {
        OrderPaymentService orderPaymentService = (OrderPaymentService) context.getBean("blOrderPaymentService");
        OrderPayment payment = orderPaymentService.create();

        OrderService orderService = (OrderService) context.getBean("blOrderService");
        Order order = orderService.findOrderById(this.orderId);
        if (order != null) {
            payment.setOrder(order);
        }
        
        payment.setId(this.id);
        payment.setType(PaymentType.getInstance(this.type));
        payment.setPaymentGatewayType(PaymentGatewayType.getInstance(this.gatewayType));
        payment.setReferenceNumber(this.referenceNumber);

        if (this.billingAddress != null) {
            payment.setBillingAddress(this.billingAddress.unwrap(request, context));
        }

        if (this.amount != null) {
            if (this.currency != null) {
                payment.setAmount(new Money(this.amount, this.currency));
            } else {
                payment.setAmount(new Money(this.amount));
            }
        }

        if (this.transactions != null && !this.transactions.isEmpty()) {
            for (PaymentTransactionWrapper transactionWrapper : this.transactions) {
                PaymentTransaction transaction = transactionWrapper.unwrap(request,context);
                transaction.setOrderPayment(payment);
                payment.addTransaction(transaction);
            }
        }

        return payment;
    }
}
