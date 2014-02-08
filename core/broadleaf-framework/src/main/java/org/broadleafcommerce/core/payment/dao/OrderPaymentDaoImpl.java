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
package org.broadleafcommerce.core.payment.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl;
import org.broadleafcommerce.core.payment.domain.PaymentLog;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.domain.PaymentTransactionImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("blOrderPaymentDao")
public class OrderPaymentDaoImpl implements OrderPaymentDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public OrderPayment save(OrderPayment payment) {
        return em.merge(payment);
    }

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        return em.merge(transaction);
    }

    @Override
    public PaymentLog save(PaymentLog log) {
        return em.merge(log);
    }

    @Override
    public OrderPayment readPaymentById(Long paymentId) {
        return em.find(OrderPaymentImpl.class, paymentId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderPayment> readPaymentsForOrder(Order order) {
        Query query = em.createNamedQuery("BC_READ_ORDERS_PAYMENTS_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return query.getResultList();
    }

    @Override
    public OrderPayment create() {
        return ((OrderPayment) entityConfiguration.createEntityInstance(OrderPayment.class.getName()));
    }

    @Override
    public PaymentTransaction createTransaction() {
        return entityConfiguration.createEntityInstance(PaymentTransaction.class.getName(), PaymentTransaction.class);
    }

    @Override
    public PaymentTransaction readTransactionById(Long transactionId) {
        return em.find(PaymentTransactionImpl.class, transactionId);
    }

    @Override
    public PaymentLog createLog() {
        return entityConfiguration.createEntityInstance(PaymentLog.class.getName(), PaymentLog.class);
    }

    @Override
    public void delete(OrderPayment paymentInfo) {
        if (!em.contains(paymentInfo)) {
            paymentInfo = readPaymentById(paymentInfo.getId());
        }
        em.remove(paymentInfo);
    }
}
