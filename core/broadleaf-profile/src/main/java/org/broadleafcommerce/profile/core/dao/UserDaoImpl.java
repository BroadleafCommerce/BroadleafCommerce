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

package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.User;
import org.broadleafcommerce.profile.core.domain.UserImpl;
import org.broadleafcommerce.profile.core.domain.UserRole;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

@Repository("blUserDao")
public class UserDaoImpl implements UserDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public User readUserByUsername(String username) {
        Query query = em.createNamedQuery("BC_READ_USER_BY_USER_NAME");
        query.setParameter("username", username);
        @SuppressWarnings("unchecked")
		List<User> users = query.getResultList();
        return users == null || users.isEmpty() ? null : users.get(0);
    }

    public User readUserByEmail(String emailAddress) {
        Query query = em.createNamedQuery("BC_READ_USER_BY_EMAIL");
        query.setParameter("email", emailAddress);
        @SuppressWarnings("unchecked")
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
        return em.merge(user);
    }

    public User readUserById(Long id) {
        return (User) em.find(UserImpl.class, id);
    }
}
