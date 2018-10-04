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

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blOfferCodeDao")
public class OfferCodeDaoImpl implements OfferCodeDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public OfferCode create() {
        return ((OfferCode) entityConfiguration.createEntityInstance(OfferCode.class.getName()));
    }

    public void delete(OfferCode offerCode) {
        em.remove(offerCode);
    }

    public OfferCode save(OfferCode offerCode) {
        if(offerCode.getId()==null){
            em.persist(offerCode);
        }else{
            offerCode = em.merge(offerCode);
        }
        return offerCode;
    }

    @SuppressWarnings("unchecked")
    public OfferCode readOfferCodeById(Long offerCodeId) {
        return (OfferCode) em.find(entityConfiguration.lookupEntityClass(OfferCode.class.getName()), offerCodeId);
    }

    @SuppressWarnings("unchecked")
    public OfferCode readOfferCodeByCode(String code) {
        OfferCode offerCode = null;
        Query query = em.createNamedQuery("BC_READ_OFFER_CODE_BY_CODE");
        query.setParameter("code", code);
        List<OfferCode> result = query.getResultList();
        if (result.size() > 0) {
            offerCode = result.get(0);
        }
        return offerCode;
    }

}
