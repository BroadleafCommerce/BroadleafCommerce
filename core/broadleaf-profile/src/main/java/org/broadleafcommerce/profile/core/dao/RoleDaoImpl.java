/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.CustomerRole;
import org.broadleafcommerce.profile.core.domain.Role;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blRoleDao")
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public Address readAddressById(Long id) {
        return (Address) em.find(AddressImpl.class, id);
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
}
