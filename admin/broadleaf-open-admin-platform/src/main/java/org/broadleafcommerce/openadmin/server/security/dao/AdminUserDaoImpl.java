/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.security.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
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
        TypedQueryBuilder<AdminUser> tqb = new TypedQueryBuilder<AdminUser>(AdminUser.class, "au");

        if (ids != null && !ids.isEmpty()) {
            tqb.addRestriction("au.id", "in", ids);
        }

        TypedQuery<AdminUser> query = tqb.toQuery(em);
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
        query.setHint(QueryHints.HINT_CACHE_REGION, "blAdminSecurityVolatileQuery");
        query.setParameter("userName", userName);
        List<AdminUser> users = query.getResultList();
        //TODO rewrite on streams when upgraded to java 8
        Iterator<AdminUser> iterator = users.iterator();
        while (iterator.hasNext()){
            AdminUser user = iterator.next();
            if(Status.class.isAssignableFrom(user.getClass())) {
                if('Y' == ((Status)user).getArchived()) {
                    iterator.remove();
                }
            }
        }
        if (users != null && !users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    public List<AdminUser> readAllAdminUsers() {
        TypedQuery<AdminUser> query = em.createNamedQuery("BC_READ_ALL_ADMIN_USERS", AdminUser.class);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "blAdminSecurityVolatileQuery");
        return query.getResultList();
    }

    @Override
    public List<AdminUser> readAdminUserByEmail(String emailAddress) {
        TypedQuery<AdminUser> query = em.createNamedQuery("BC_READ_ADMIN_USER_BY_EMAIL", AdminUser.class);
        query.setParameter("email", emailAddress);
        return query.getResultList();
    }
}
