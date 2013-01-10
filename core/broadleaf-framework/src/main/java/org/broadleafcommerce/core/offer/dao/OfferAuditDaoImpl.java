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

package org.broadleafcommerce.core.offer.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blOfferAuditDao")
public class OfferAuditDaoImpl implements OfferAuditDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public OfferAudit create() {
        return ((OfferAudit) entityConfiguration.createEntityInstance(OfferAudit.class.getName()));
    }

    public void delete(final OfferAudit offerAudit) {
        OfferAudit loa = offerAudit;
        if (!em.contains(loa)) {
            loa = readAuditById(offerAudit.getId());
        }
        em.remove(loa);
    }

    public OfferAudit save(final OfferAudit offerAudit) {
        return em.merge(offerAudit);
    }

    public OfferAudit readAuditById(final Long offerAuditId) {
        return (OfferAudit) em.find(entityConfiguration.lookupEntityClass(OfferAudit.class.getName()), offerAuditId);
    }

}
