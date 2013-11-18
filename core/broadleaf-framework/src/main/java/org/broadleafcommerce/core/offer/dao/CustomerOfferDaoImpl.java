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

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.offer.domain.CustomerOffer;
import org.broadleafcommerce.core.offer.domain.CustomerOfferImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blCustomerOfferDao")
public class CustomerOfferDaoImpl implements CustomerOfferDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public CustomerOffer create() {
        return ((CustomerOffer) entityConfiguration.createEntityInstance(CustomerOffer.class.getName()));
    }

    public void delete(CustomerOffer customerOffer) {
        if (!em.contains(customerOffer)) {
            customerOffer = readCustomerOfferById(customerOffer.getId());
        }
        em.remove(customerOffer);
    }

    public CustomerOffer save(final CustomerOffer customerOffer) {
        return em.merge(customerOffer);
    }

    public CustomerOffer readCustomerOfferById(final Long customerOfferId) {
        return (CustomerOffer) em.find(CustomerOfferImpl.class, customerOfferId);
    }

    @SuppressWarnings("unchecked")
    public List<CustomerOffer> readCustomerOffersByCustomer(final Customer customer) {
        final Query query = em.createNamedQuery("BC_READ_CUSTOMER_OFFER_BY_CUSTOMER_ID");
        query.setParameter("customerId", customer.getId());
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

}
