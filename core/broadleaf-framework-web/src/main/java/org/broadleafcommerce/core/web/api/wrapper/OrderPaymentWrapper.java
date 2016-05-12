/*
 * #%L
 * BroadleafCommerce Framework Web
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
 * 
 * @deprecated - use {@link com.broadleafcommerce.core.rest.api.v2.wrapper.OrderPaymentWrapper}
 * 
 * User: Elbert Bautista
 * Date: 4/26/12
 */
@Deprecated
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
     * @return the orderId
     */
    public Long getOrderId() {
        return orderId;
    }

    
    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    
    /**
     * @return the billingAddress
     */
    public AddressWrapper getBillingAddress() {
        return billingAddress;
    }

    
    /**
     * @param billingAddress the billingAddress to set
     */
    public void setBillingAddress(AddressWrapper billingAddress) {
        this.billingAddress = billingAddress;
    }

    
    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    
    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    
    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    
    /**
     * @param currency the currency to set
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    
    /**
     * @return the referenceNumber
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    
    /**
     * @param referenceNumber the referenceNumber to set
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    
    /**
     * @return the gatewayType
     */
    public String getGatewayType() {
        return gatewayType;
    }

    
    /**
     * @param gatewayType the gatewayType to set
     */
    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType;
    }

    
    /**
     * @return the transactions
     */
    public List<PaymentTransactionWrapper> getTransactions() {
        return transactions;
    }

    
    /**
     * @param transactions the transactions to set
     */
    public void setTransactions(List<PaymentTransactionWrapper> transactions) {
        this.transactions = transactions;
    }
}
