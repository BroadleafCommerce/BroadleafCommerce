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

import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blUserDao")
public class UserDaoImpl implements UserDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @SuppressWarnings("unchecked")
    public User readUserByUsername(String username) {
        Query query = em.createNamedQuery("BC_sREAD_USER_BY_USER_NAME");
        query.setParameter("username", username);
        List<User> users = query.getResultList();
        return users == null || users.isEmpty() ? null : users.get(0);
    }

    @SuppressWarnings("unchecked")
    public User readUserByEmail(String emailAddress) {
        Query query = em.createNamedQuery("BC_READ_USER_BY_EMAIL");
        query.setParameter("email", emailAddress);
        List<User> users = query.getResultList();
        return users == null || users.isEmpty() ? null : users.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<UserRole> readUserRolesByUserId(Long userId) {
        Query query = em.createNamedQuery("BC_READ_ROLES_BY_USER_ID");
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public User save(User user) {
        User retUser = user;
        if (retUser.getId() == null) {
            em.persist(retUser);
        } else {
            retUser = em.merge(retUser);
        }
        return retUser;
    }

    @SuppressWarnings("unchecked")
    public User readUserById(Long id) {
        return (User) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.User"), id);
    }
}
