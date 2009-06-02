/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("paymentInfoDao")
public class PaymentInfoDaoJpa implements PaymentInfoDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @Override
    public PaymentInfo save(PaymentInfo paymentInfo) {
        return em.merge(paymentInfo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PaymentInfo readPaymentInfoById(Long paymentId) {
        return (PaymentInfo) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.PaymentInfo"), paymentId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PaymentInfo> readPaymentInfosForOrder(Order order) {
        Query query = em.createNamedQuery("BC_READ_ORDERS_PAYMENTS_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return query.getResultList();
    }

    @Override
    public PaymentInfo create() {
        return ((PaymentInfo) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.PaymentInfo"));
    }

    @Override
    public void delete(PaymentInfo paymentInfo) {
        em.remove(paymentInfo);
    }
}
