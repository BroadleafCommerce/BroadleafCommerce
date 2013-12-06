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

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.dao.OrderPaymentDao;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentLog;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

@Service("blOrderPaymentService")
public class OrderPaymentServiceImpl implements OrderPaymentService {

    @Resource(name = "blOrderPaymentDao")
    protected OrderPaymentDao paymentDao;

    @Override
    @Transactional(value = "blTransactionManager")
    public OrderPayment save(OrderPayment paymentInfo) {
        return paymentDao.save(paymentInfo);
    }

    @Override
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
        return paymentDao.readPaymentInfosForOrder(order);
    }

    @Override
    public OrderPayment create() {
        return paymentDao.create();
    }

    @Override
    public void delete(OrderPayment paymentInfo) {
        paymentDao.delete(paymentInfo);
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

}
