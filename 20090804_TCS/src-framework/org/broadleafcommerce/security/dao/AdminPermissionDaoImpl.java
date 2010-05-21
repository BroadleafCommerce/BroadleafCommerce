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

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.security.domain.AdminPermission;
import org.springframework.stereotype.Repository;

@Repository("blAdminPermissionDao")
public class AdminPermissionDaoImpl implements AdminPermissionDao {
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    @SuppressWarnings("unchecked")
    public void deleteAdminPermission(AdminPermission permission) {
        AdminPermission persisted = (AdminPermission) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.security.domain.AdminPermission"), permission.getId());
        em.remove(persisted);
    }

    @SuppressWarnings("unchecked")
    public AdminPermission readAdminPermissionById(Long id) {
        return (AdminPermission) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.security.domain.AdminPermission"), id);
    }

    public AdminPermission saveAdminPermission(AdminPermission permission) {
        if (permission.getId() == null) {
            em.persist(permission);
        } else {
            permission = em.merge(permission);
        }
        return permission;
    }

    @SuppressWarnings("unchecked")
    public List<AdminPermission> readAllAdminPermissions() {
        Query query = em.createNamedQuery("BC_READ_ALL_ADMIN_PERMISSIONS");
        List<AdminPermission> permissions = query.getResultList();
        return permissions;
    }
}
