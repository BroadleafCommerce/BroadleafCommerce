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

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blPhoneDao")
public class PhoneDaoImpl implements PhoneDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public Phone save(Phone phone) {
        if (phone.getId() == null) {
            em.persist(phone);
        } else {
            phone = em.merge(phone);
        }
        return phone;
    }

    @SuppressWarnings("unchecked")
    public Phone readPhoneById(Long phoneId) {
        return (Phone) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Phone"), phoneId);
    }

    public Phone create() {
        return (Phone) entityConfiguration.createEntityInstance(Phone.class.getName());
    }
}
