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
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.dao.OrderPaymentDao;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentLog;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CustomerPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        orderPayment.setType(customerPayment.getPaymentType());
        orderPayment.setBillingAddress(addressService.copyAddress(customerPayment.getBillingAddress()));
        orderPayment.setPaymentGatewayType(customerPayment.getPaymentGatewayType());
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

}
