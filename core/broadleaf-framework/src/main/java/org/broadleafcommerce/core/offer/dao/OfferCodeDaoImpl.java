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
package org.broadleafcommerce.core.offer.dao;

import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferCodeImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("blOfferCodeDao")
public class OfferCodeDaoImpl implements OfferCodeDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blOfferCodeDaoExtensionManager")
    protected OfferCodeDaoExtensionManager extensionManager;

    @Override
    public OfferCode create() {
        return ((OfferCode) entityConfiguration.createEntityInstance(OfferCode.class.getName()));
    }

    @Override
    public void delete(OfferCode offerCode) {
        if (!em.contains(offerCode)) {
            offerCode = readOfferCodeById(offerCode.getId());
        }
        em.remove(offerCode);
    }

    @Override
    public OfferCode save(OfferCode offerCode) {
        return em.merge(offerCode);
    }

    @Override
    public OfferCode readOfferCodeById(Long offerCodeId) {
        return em.find(OfferCodeImpl.class, offerCodeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public OfferCode readOfferCodeByCode(String code) {
        OfferCode offerCode = null;
        Query query = null;

        ExtensionResultHolder<Query> resultHolder = new ExtensionResultHolder<Query>();
        ExtensionResultStatusType extensionResult =
                extensionManager.getProxy().createReadOfferCodeByCodeQuery(em, resultHolder, code, true, "query.Offer");

        if (extensionResult != null && ExtensionResultStatusType.HANDLED.equals(extensionResult)) {
            query = resultHolder.getResult();
        } else {
            query = em.createNamedQuery("BC_READ_OFFER_CODE_BY_CODE");
            query.setParameter("code", code);
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            query.setHint(QueryHints.HINT_CACHE_REGION, "query.Offer");
        }

        List<OfferCode> result = query.getResultList();
        if (result.size() > 0) {
            offerCode = result.get(0);
        }

        return offerCode;
    }

}
