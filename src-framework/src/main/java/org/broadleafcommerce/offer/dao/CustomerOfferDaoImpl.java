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

import org.broadleafcommerce.offer.domain.CustomerOffer;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

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
        em.remove(customerOffer);
    }

    public CustomerOffer save(CustomerOffer customerOffer) {
        if(customerOffer.getId() == null){
            em.persist(customerOffer);
        }else{
            customerOffer = em.merge(customerOffer);
        }
        return customerOffer;
    }

    @SuppressWarnings("unchecked")
    public CustomerOffer readCustomerOfferById(Long customerOfferId) {
        return (CustomerOffer) em.find(entityConfiguration.lookupEntityClass(CustomerOffer.class.getName()), customerOfferId);
    }

    @SuppressWarnings("unchecked")
    public List<CustomerOffer> readCustomerOffersByCustomer(Customer customer) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_OFFER_BY_CUSTOMER_ID");
        query.setParameter("customerId", customer.getId());
        List<CustomerOffer> result = query.getResultList();
        return result;
    }

}
