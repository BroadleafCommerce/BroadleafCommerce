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
package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.CustomerRole;
import org.broadleafcommerce.profile.domain.Role;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blRoleDao")
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    @SuppressWarnings("unchecked")
    public Address readAddressById(Long id) {
        return (Address) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Address"), id);
    }

    @SuppressWarnings("unchecked")
    public List<CustomerRole> readCustomerRolesByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_ROLES_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }
    
    public Role readRoleByName(String name) {
        Query query = em.createNamedQuery("BC_READ_ROLE_BY_NAME");
        query.setParameter("name", name);
        return (Role) query.getSingleResult();
    }

    public void addRoleToCustomer(CustomerRole customerRole) {
        em.persist(customerRole);
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
}
