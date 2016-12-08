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
import org.broadleafcommerce.openadmin.server.security.domain.AdminModule;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author elbertbautista
 *
 */
@Repository("blAdminNavigationDao")
public class AdminNavigationDaoImpl implements AdminNavigationDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Override
    public AdminSection save(AdminSection adminSection) {
        adminSection = em.merge(adminSection);
        AdminModule module = adminSection.getModule();
        em.refresh(module);
        return adminSection;
    }

    @Override
    public void remove(AdminSection adminSection) {
        AdminModule module = adminSection.getModule();
        em.remove(adminSection);
        em.refresh(module);
    }

    @Override
    public List<AdminModule> readAllAdminModules() {
        Query query = em.createNamedQuery("BC_READ_ALL_ADMIN_MODULES");
        query.setHint(org.hibernate.ejb.QueryHints.HINT_CACHEABLE, true);
        List<AdminModule> modules = query.getResultList();
        return modules;
    }
    
    @Override
    public AdminModule readAdminModuleByModuleKey(String moduleKey) {
        TypedQuery<AdminModule> q = new TypedQueryBuilder<AdminModule>(AdminModule.class, "am")
            .addRestriction("am.moduleKey", "=", moduleKey)
            .toQuery(em);
        return q.getSingleResult();
    }

    @Override
    public List<AdminSection> readAllAdminSections() {
        Query query = em.createNamedQuery("BC_READ_ALL_ADMIN_SECTIONS");
        query.setHint(org.hibernate.ejb.QueryHints.HINT_CACHEABLE, true);
        List<AdminSection> sections = query.getResultList();
        return sections;
    }
    
    @Override
    public AdminSection readAdminSectionByClassAndSectionId(Class<?> clazz, String sectionId) {
        String className = clazz.getName();
        
        // Try to find a section for the exact input received
        List<AdminSection> sections = readAdminSectionForClassName(className);
        if (CollectionUtils.isEmpty(sections)) {
            // If we didn't find a section, and this class ends in Impl, try again without the Impl.
            // Most of the sections should match to the interface
            if (className.endsWith("Impl")) {
                className = className.substring(0, className.length() - 4);
                sections = readAdminSectionForClassName(className);
            }
        }
        
        if (!CollectionUtils.isEmpty(sections)) {
            AdminSection returnSection = sections.get(0);

            if (sectionId != null) {
                if (!sectionId.startsWith("/")) {
                    sectionId = "/" + sectionId;
                }
                for (AdminSection section : sections) {
                    if (sectionId.equals(section.getUrl())) {
                        returnSection = section;
                        break;
                    }
                }
            }
            return returnSection;
        }
        
        return null;
    }
    
    protected List<AdminSection> readAdminSectionForClassName(String className) {
        TypedQuery<AdminSection> q = em.createQuery(
            "select s from " + AdminSection.class.getName() + " s where s.ceilingEntity = :className", AdminSection.class);
        q.setParameter("className", className);
        q.setHint(org.hibernate.ejb.QueryHints.HINT_CACHEABLE, true);
        List<AdminSection> result = q.getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return q.getResultList();
    }

    @Override
    public AdminSection readAdminSectionByURI(String uri) {
        Query query = em.createNamedQuery("BC_READ_ADMIN_SECTION_BY_URI");
        query.setParameter("uri", uri);
        query.setHint(org.hibernate.ejb.QueryHints.HINT_CACHEABLE, true);
        AdminSection adminSection = null;
        try {
             adminSection = (AdminSection) query.getSingleResult();
        } catch (NoResultException e) {
           //do nothing
        }
        return adminSection;
    }

    @Override
    public AdminSection readAdminSectionBySectionKey(String sectionKey) {
        Query query = em.createNamedQuery("BC_READ_ADMIN_SECTION_BY_SECTION_KEY");
        query.setHint(org.hibernate.ejb.QueryHints.HINT_CACHEABLE, true);
        query.setParameter("sectionKey", sectionKey);
        AdminSection adminSection = null;
        try {
            adminSection = (AdminSection) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing
        }
        return adminSection;
    }

}
