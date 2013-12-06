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
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blOrderPaymentDao")
public class OrderPaymentDaoImpl implements OrderPaymentDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public OrderPayment save(OrderPayment paymentInfo) {
        return em.merge(paymentInfo);
    }

    public PaymentResponseItem save(PaymentResponseItem paymentResponseItem) {
        return em.merge(paymentResponseItem);
    }

    public PaymentLog save(PaymentLog log) {
        return em.merge(log);
    }

    public OrderPayment readPaymentInfoById(Long paymentId) {
        return (OrderPayment) em.find(OrderPaymentImpl.class, paymentId);
    }

    @SuppressWarnings("unchecked")
    public List<OrderPayment> readPaymentInfosForOrder(Order order) {
        Query query = em.createNamedQuery("BC_READ_ORDERS_PAYMENTS_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return query.getResultList();
    }

    public OrderPayment create() {
        return ((OrderPayment) entityConfiguration.createEntityInstance("org.broadleafcommerce.core.payment.domain.OrderPayment"));
    }

    public PaymentResponseItem createResponseItem() {
        return ((PaymentResponseItem) entityConfiguration.createEntityInstance("org.broadleafcommerce.core.payment.domain.PaymentResponseItem"));
    }

    public PaymentLog createLog() {
        return ((PaymentLog) entityConfiguration.createEntityInstance("org.broadleafcommerce.core.payment.domain.PaymentLog"));
    }

    public void delete(OrderPayment paymentInfo) {
        if (!em.contains(paymentInfo)) {
            paymentInfo = readPaymentInfoById(paymentInfo.getId());
        }
        em.remove(paymentInfo);
    }
}
