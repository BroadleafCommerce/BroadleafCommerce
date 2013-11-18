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

import org.apache.commons.lang.ClassUtils;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermissionImpl;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
    
    @Override
    public AdminPermission readAdminPermissionByName(String name) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<AdminPermission> criteria = builder.createQuery(AdminPermission.class);
        Root<AdminPermissionImpl> adminPerm = criteria.from(AdminPermissionImpl.class);
        criteria.select(adminPerm);

        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(adminPerm.get("name"), name));

        // Execute the query with the restrictions
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        List<AdminPermission> results = em.createQuery(criteria).getResultList();
        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
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
        //the ceiling may be an impl, which will fail because entity permission is normally specified for the interface
        //try the passed in ceiling first, but also try an interfaces implemented
        List<String> testClasses = new ArrayList<String>();
        testClasses.add(ceilingEntityFullyQualifiedName);
        try {
            for (Object interfaze : ClassUtils.getAllInterfaces(Class.forName(ceilingEntityFullyQualifiedName))) {
                testClasses.add(((Class<?>) interfaze).getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (String testClass : testClasses) {
            Query query = em.createNamedQuery("BC_COUNT_PERMISSIONS_FOR_USER_BY_TYPE_AND_CEILING_ENTITY");
            query.setParameter("adminUser", adminUser);
            query.setParameter("type", permissionType.getType());
            query.setParameter("ceilingEntity", testClass);
            query.setHint(QueryHints.HINT_CACHEABLE, true);

            Long count = (Long) query.getSingleResult();
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean doesOperationExistForCeilingEntity(PermissionType permissionType, String ceilingEntityFullyQualifiedName) {
        //the ceiling may be an impl, which will fail because entity permission is normally specified for the interface
        //try the passed in ceiling first, but also try an interfaces implemented
        List<String> testClasses = new ArrayList<String>();
        testClasses.add(ceilingEntityFullyQualifiedName);
        try {
            for (Object interfaze : ClassUtils.getAllInterfaces(Class.forName(ceilingEntityFullyQualifiedName))) {
                testClasses.add(((Class<?>) interfaze).getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (String testClass : testClasses) {
            Query query = em.createNamedQuery("BC_COUNT_PERMISSIONS_BY_TYPE_AND_CEILING_ENTITY");
            query.setParameter("type", permissionType.getType());
            query.setParameter("ceilingEntity", testClass);
            query.setHint(QueryHints.HINT_CACHEABLE, true);

            Long count = (Long) query.getSingleResult();
            if (count > 0) {
                return true;
            }
        }
        return false;
    }
}
