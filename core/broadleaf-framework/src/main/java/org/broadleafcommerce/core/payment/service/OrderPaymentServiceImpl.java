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

import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.dao.OrderPaymentDao;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentLog;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
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
    public CustomerPayment saveOrderPaymentAsCustomerPayment(Customer customer, OrderPayment orderPayment) throws PaymentException {
        CustomerPayment customerPayment = customerPaymentService.create();
        customerPayment.setCustomer(customer);
        customerPayment.setBillingAddress(orderPayment.getBillingAddress());
        customerPayment.setPaymentGatewayType(orderPayment.getGatewayType());
        
        String cardType = null;
        String expDate = null;
        String lastFour = null;
        String token = null;

        for (PaymentTransaction paymentTransaction : orderPayment.getTransactions()) {
            if (cardType == null) {
                cardType = paymentTransaction.getAdditionalFields().get(PaymentAdditionalFieldType.CARD_TYPE.getType());
            }
            if (expDate == null) {
                expDate = paymentTransaction.getAdditionalFields().get(PaymentAdditionalFieldType.EXP_DATE.getType());
            }
            if (lastFour == null) {
                lastFour = paymentTransaction.getAdditionalFields().get(PaymentAdditionalFieldType.LAST_FOUR.getType());
            }
            if (token == null) {
                token = paymentTransaction.getAdditionalFields().get(PaymentAdditionalFieldType.TOKEN.getType());
            }
        }

        if (expDate != null) {
            customerPayment.setExpirationDate(expDate);
        }

        customerPayment.setCardType(cardType);
        customerPayment.setLastFour(lastFour);
        customerPayment.setPaymentToken(token);

        String paymentName = orderPayment.getPaymentName();
        if (paymentName == null || paymentName.isEmpty()) {
            if (customerPayment.getCardType() != null && customerPayment.getLastFour() != null) {
                paymentName = customerPayment.getCardType() + " ending in " + customerPayment.getLastFour();
            } else {
                paymentName = "Payment #" + customer.getCustomerPayments().size();
            }
        }
        customerPayment.setPaymentName(paymentName);

        return customerPayment;
    }
}
