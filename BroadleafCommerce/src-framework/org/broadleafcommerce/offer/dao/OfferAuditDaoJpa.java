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
package org.broadleafcommerce.offer.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blOfferAuditDao")
public class OfferAuditDaoJpa implements OfferAuditDao {

    /** Lookup identifier for Offer bean **/
    private static String beanName = "org.broadleafcommerce.offer.domain.OfferAudit";

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource
    protected EntityConfiguration entityConfiguration;

    @Override
    public OfferAudit create() {
        return ((OfferAudit) entityConfiguration.createEntityInstance(beanName));
    }

    @Override
    public void delete(OfferAudit offerAudit) {
        em.remove(offerAudit);
    }

    @Override
    public OfferAudit save(OfferAudit offerAudit) {
        if(offerAudit.getId() == null){
            em.persist(offerAudit);
        }else{
            offerAudit = em.merge(offerAudit);
        }
        return offerAudit;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OfferAudit readAuditById(Long offerAuditId) {
        return (OfferAudit) em.find(entityConfiguration.lookupEntityClass(beanName), offerAuditId);
    }

}
