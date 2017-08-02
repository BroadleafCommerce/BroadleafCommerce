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
package org.broadleafcommerce.core.payment.service;

import org.apache.commons.collections4.MapUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.dao.OrderPaymentDao;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentLog;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CustomerPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Service("blOrderPaymentService")
public class OrderPaymentServiceImpl implements OrderPaymentService {

    @Resource(name = "blOrderPaymentDao")
    protected OrderPaymentDao paymentDao;

    @Resource(name = "blCustomerPaymentService")
    protected CustomerPaymentService customerPaymentService;

    @Resource(name = "blAddressService")
    protected AddressService addressService;

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public OrderPayment save(OrderPayment payment) {
        return paymentDao.save(payment);
    }

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public PaymentTransaction save(PaymentTransaction transaction) {
        return paymentDao.save(transaction);
    }

    @Override
    public PaymentLog save(PaymentLog log) {
        return paymentDao.save(log);
    }

    @Override
    public OrderPayment readPaymentById(Long paymentId) {
        return paymentDao.readPaymentById(paymentId);
    }

    @Override
    public List<OrderPayment> readPaymentsForOrder(Order order) {
        return paymentDao.readPaymentsForOrder(order);
    }

    @Override
    public OrderPayment create() {
        return paymentDao.create();
    }

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void delete(OrderPayment payment) {
        paymentDao.delete(payment);
    }

    @Override
    public PaymentLog createLog() {
        return paymentDao.createLog();
    }

    @Override
    public PaymentTransaction createTransaction() {
        PaymentTransaction returnItem = paymentDao.createTransaction();
        
        //TODO: this needs correct timezone conversion, right?
        returnItem.setDate(SystemTime.asDate());
        
        return returnItem;
    }

    @Override
    public PaymentTransaction readTransactionById(Long transactionId) {
        return paymentDao.readTransactionById(transactionId);
    }

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public OrderPayment createOrderPaymentFromCustomerPayment(Order order, CustomerPayment customerPayment, Money amount) {
        OrderPayment orderPayment = create();
        orderPayment.setOrder(order);
        orderPayment.setBillingAddress(addressService.copyAddress(customerPayment.getBillingAddress()));
        
        PaymentGatewayType gatewayType = customerPayment.getPaymentGatewayType();
        PaymentType paymentType = customerPayment.getPaymentType();
        Map<String, String> additionalFields = customerPayment.getAdditionalFields();
        if (gatewayType == null || paymentType == null) {
            if (MapUtils.isEmpty(additionalFields)) {
                additionalFields = new HashMap<>();
            }
            String paymentTypeKey = PaymentAdditionalFieldType.PAYMENT_TYPE.getType();
            if (additionalFields.containsKey(paymentTypeKey)) {
                paymentType = PaymentType.getInstance(additionalFields.get(paymentTypeKey));
            }
            String gatewayTypeKey = PaymentAdditionalFieldType.GATEWAY_TYPE.getType();
            if (additionalFields.containsKey(gatewayTypeKey)) {
                gatewayType = PaymentGatewayType.getInstance(additionalFields.get(gatewayTypeKey));
            }
        }
        orderPayment.setPaymentGatewayType(gatewayType);
        orderPayment.setType(paymentType);

        orderPayment.setAmount(amount);

        PaymentTransaction unconfirmedTransaction = createTransaction();
        unconfirmedTransaction.setAmount(amount);
        unconfirmedTransaction.setType(PaymentTransactionType.UNCONFIRMED);
        unconfirmedTransaction.setOrderPayment(orderPayment);
        unconfirmedTransaction.getAdditionalFields().put(PaymentAdditionalFieldType.TOKEN.getType(), customerPayment.getPaymentToken());
        unconfirmedTransaction.getAdditionalFields().putAll(customerPayment.getAdditionalFields());

        orderPayment.getTransactions().add(unconfirmedTransaction);

        return save(orderPayment);
    }

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public CustomerPayment createCustomerPaymentFromPaymentTransaction(PaymentTransaction transaction) {
        CustomerPayment customerPayment = customerPaymentService.create();
        customerPayment.setCustomer(transaction.getOrderPayment().getOrder().getCustomer());
        customerPayment.setBillingAddress(addressService.copyAddress(transaction.getOrderPayment().getBillingAddress()));
        customerPayment.setPaymentType(transaction.getOrderPayment().getType());
        customerPayment.setPaymentGatewayType(transaction.getOrderPayment().getGatewayType());
        customerPayment.setAdditionalFields(transaction.getAdditionalFields());

        populateCustomerPaymentToken(customerPayment, transaction);

        return customerPaymentService.saveCustomerPayment(customerPayment);
    }

    @Override
    public void populateCustomerPaymentToken(CustomerPayment customerPayment, PaymentTransaction transaction) {
        if (transaction.getAdditionalFields().containsKey(PaymentAdditionalFieldType.TOKEN.getType())) {
            customerPayment.setPaymentToken(transaction.getAdditionalFields().get(PaymentAdditionalFieldType.TOKEN.getType()));
        }
    }

    @Override
    @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void deleteOrderPaymentsByType(Order order, PaymentType paymentType) {
        List<OrderPayment> orderPayments = readPaymentsForOrder(order);

        for (OrderPayment orderPayment : orderPayments) {
            if (orderPayment.isActive() && paymentType.equals(orderPayment.getType())) {
                delete(orderPayment);
            }
        }
    }
}

