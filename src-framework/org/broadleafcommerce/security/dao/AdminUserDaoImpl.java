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
package org.broadleafcommerce.security.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.security.domain.AdminUser;
import org.springframework.stereotype.Repository;

@Repository("blAdminUserDao")
public class AdminUserDaoImpl implements AdminUserDao {
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    @Override
    public void deleteAdminUser(AdminUser user) {
        em.remove(user);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AdminUser readAdminUserById(Long id) {
        return (AdminUser) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.security.domain.AdminUser"), id);
    }

    @Override
    public AdminUser saveAdminUser(AdminUser user) {
        if (user.getId() == null) {
            em.persist(user);
        } else {
            user = em.merge(user);
        }
        return user;
    }

}
