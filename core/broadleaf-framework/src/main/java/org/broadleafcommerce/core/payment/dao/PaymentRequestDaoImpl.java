/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.payment.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentRequest;
import org.broadleafcommerce.core.payment.domain.PaymentRequestImpl;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

@Repository("blPaymentRequestDao")
public class PaymentRequestDaoImpl implements PaymentRequestDao {

//    @PersistenceContext(unitName = "blSecurePU")
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.dao.PaymentRequestDao#save(org.broadleafcommerce.core.payment.domain.PaymentRequest)
     */
    @Override
    public PaymentRequest save(PaymentRequest paymentRequest) {
        return em.merge(paymentRequest);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.dao.PaymentRequestDao#readPaymentInfoById(java.lang.Long)
     */
    @Override
    public PaymentRequest readPaymentRequestById(Long paymentId) {
        return em.find(PaymentRequestImpl.class, paymentId);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.dao.PaymentRequestDao#readPaymentRequestByCurrency(org.broadleafcommerce.common.currency.domain.BroadleafCurrency)
     */
    @Override
    @SuppressWarnings("unchecked")
    public PaymentRequest readPaymentRequestByKey(String key) {
        Query query = em.createNamedQuery("BC_READ_PAYMENT_REQUEST_BY_KEY");
        query.setParameter("key", key);
      
       List x = query.getResultList();
       if (x==null ||x.isEmpty()) {
           return null;
       }
       return (PaymentRequest) x.get(0);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.dao.PaymentRequestDao#create()
     */
    @Override
    public PaymentInfo create() {
        return ((PaymentInfo) entityConfiguration.createEntityInstance("org.broadleafcommerce.core.payment.domain.PaymentRequest"));
    }


    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.dao.PaymentRequestDao#delete(org.broadleafcommerce.core.payment.domain.PaymentRequest)
     */
    @Override
    public void delete(PaymentRequest paymentRequest) {
    	if (!em.contains(paymentRequest)) {
    	    paymentRequest = readPaymentRequestById(paymentRequest.getId());
    	}
        em.remove(paymentRequest);
    }
}
