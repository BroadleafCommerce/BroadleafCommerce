/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.security.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
@Repository("blAdminPermissionDao")
public class AdminPermissionDaoImpl implements AdminPermissionDao {
    
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public void deleteAdminPermission(AdminPermission permission) {
        if (!em.contains(permission)) {
            permission = readAdminPermissionById(permission.getId());
        }
        em.remove(permission);
    }

    public AdminPermission readAdminPermissionById(Long id) {
        return (AdminPermission) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.openadmin.server.security.domain.AdminPermission"), id);
    }

    public AdminPermission saveAdminPermission(AdminPermission permission) {
        return em.merge(permission);
    }

    @SuppressWarnings("unchecked")
    public List<AdminPermission> readAllAdminPermissions() {
        Query query = em.createNamedQuery("BC_READ_ALL_ADMIN_PERMISSIONS");
        List<AdminPermission> permissions = query.getResultList();
        return permissions;
    }

    public boolean isUserQualifiedForOperationOnCeilingEntity(AdminUser adminUser, PermissionType permissionType, String ceilingEntityFullyQualifiedName) {
        Query query = em.createNamedQuery("BC_COUNT_PERMISSIONS_FOR_USER_BY_TYPE_AND_CEILING_ENTITY");
        query.setParameter("adminUser", adminUser);
        query.setParameter("type", permissionType.getType());
        query.setParameter("ceilingEntity", ceilingEntityFullyQualifiedName);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    public boolean doesOperationExistForCeilingEntity(PermissionType permissionType, String ceilingEntityFullyQualifiedName) {
        Query query = em.createNamedQuery("BC_COUNT_PERMISSIONS_BY_TYPE_AND_CEILING_ENTITY");
        query.setParameter("type", permissionType.getType());
        query.setParameter("ceilingEntity", ceilingEntityFullyQualifiedName);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        Long count = (Long) query.getSingleResult();
        return count > 0;
    }
}
