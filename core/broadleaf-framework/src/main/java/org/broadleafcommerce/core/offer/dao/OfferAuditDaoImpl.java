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

package org.broadleafcommerce.core.offer.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.offer.domain.OfferAuditImpl;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository("blOfferAuditDao")
public class OfferAuditDaoImpl implements OfferAuditDao {
    
    protected static final Log LOG = LogFactory.getLog(OfferAuditDaoImpl.class);

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public OfferAudit create() {
        return ((OfferAudit) entityConfiguration.createEntityInstance(OfferAudit.class.getName()));
    }

    @Override
    public void delete(final OfferAudit offerAudit) {
        OfferAudit loa = offerAudit;
        if (!em.contains(loa)) {
            loa = readAuditById(offerAudit.getId());
        }
        em.remove(loa);
    }

    @Override
    public OfferAudit save(final OfferAudit offerAudit) {
        return em.merge(offerAudit);
    }

    @Override
    public OfferAudit readAuditById(final Long offerAuditId) {
        return em.find(OfferAuditImpl.class, offerAuditId);
    }

    @Override
    public Long countUsesByCustomer(Long customerId, Long offerId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(OfferAuditImpl.class)));

        Root<OfferAuditImpl> from = cq.from(OfferAuditImpl.class);
        Predicate customerIdClause = cb.equal(from.get("customerId"), customerId);
        
        Predicate offerIdClause = cb.equal(from.get("offerId"), offerId);
        cq.where(cb.and(customerIdClause, offerIdClause));

        Long result =  em.createQuery(cq).getSingleResult();
        return result;
    }
    
    @Override
    public Long countOfferCodeUses(Long offerCodeId) {
        OfferAudit check = new OfferAuditImpl();
        try {
            check.getOfferCodeId();
        } catch (UnsupportedOperationException e) {
            LOG.warn("Checking for offer code max usage has not been enabled in your Broadleaf installation. This warning" +
            		" will only appear in Broadleaf prior to the 3.1 line. In order to fix your" +
            		" version of Broadleaf to enable this functionality, refer to the OfferAuditWeaveImpl or directly to" +
            		" https://github.com/BroadleafCommerce/BroadleafCommerce/pull/195.");
            LOG.warn("Returning unlimited usage for offer code ID " + offerCodeId);
            return -1l;
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(OfferAuditImpl.class)));

        Root<OfferAuditImpl> from = cq.from(OfferAuditImpl.class);
        cq.where(cb.equal(from.get("offerCodeId"), offerCodeId));

        Long result =  em.createQuery(cq).getSingleResult();
        return result;
    }

}
