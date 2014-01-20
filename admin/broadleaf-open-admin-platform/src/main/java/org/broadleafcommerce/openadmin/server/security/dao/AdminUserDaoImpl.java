/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * 
 * @author jfischer
 *
 */
@Repository("blAdminUserDao")
public class AdminUserDaoImpl implements AdminUserDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public void deleteAdminUser(AdminUser user) {
        if (!em.contains(user)) {
            user = em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.openadmin.server.security.domain.AdminUser", AdminUser.class), user.getId());
        }
        em.remove(user);
    }

    public AdminUser readAdminUserById(Long id) {
        return em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.openadmin.server.security.domain.AdminUser", AdminUser.class), id);
    }
    
    @Override
    public List<AdminUser> readAdminUsersByIds(Set<Long> ids) {
        TypedQuery<AdminUser> query = new TypedQueryBuilder<AdminUser>(AdminUser.class, "au")
                .addRestriction("au.id", "in", ids)
                .toQuery(em);
        return query.getResultList();
    }

    public AdminUser saveAdminUser(AdminUser user) {
        if (em.contains(user) || user.getId() != null) {
            return em.merge(user);
        } else {
            em.persist(user);
            return user;
        }
    }

    public AdminUser readAdminUserByUserName(String userName) {
        TypedQuery<AdminUser> query = em.createNamedQuery("BC_READ_ADMIN_USER_BY_USERNAME", AdminUser.class);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setParameter("userName", userName);
        List<AdminUser> users = query.getResultList();
        if (users != null && !users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    public List<AdminUser> readAllAdminUsers() {
        TypedQuery<AdminUser> query = em.createNamedQuery("BC_READ_ALL_ADMIN_USERS", AdminUser.class);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public List<AdminUser> readAdminUserByEmail(String emailAddress) {
        TypedQuery<AdminUser> query = em.createNamedQuery("BC_READ_ADMIN_USER_BY_EMAIL", AdminUser.class);
        query.setParameter("email", emailAddress);
        return query.getResultList();
    }
}
