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
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.server.security.domain.AdminModule;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UrlPathHelper;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

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
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "blAdminSecurityQuery");
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
        query.setHint(org.hibernate.jpa.QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "blAdminSecurityQuery");
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
            if (sectionId == null) {
                // if no sectionId was passed, ensure we are returning the correct section based on the request's sectionkey
                sectionId = getSectionKey(true);
            }

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
        q.setHint(org.hibernate.jpa.QueryHints.HINT_CACHEABLE, true);
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
        query.setHint(org.hibernate.jpa.QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "blAdminSecurityQuery");
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
        query.setHint(org.hibernate.jpa.QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "blAdminSecurityQuery");
        query.setParameter("sectionKey", sectionKey);
        AdminSection adminSection = null;
        try {
            adminSection = (AdminSection) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing
        }
        return adminSection;
    }

    @Override
    public String getSectionKey(boolean withTypeKey) {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

        if (request != null) {
            String originatingUri = new UrlPathHelper().getOriginatingRequestUri(request);
            int startIndex = request.getContextPath().length();

            String sectionKey = originatingUri.substring(startIndex);
            int endIndex = sectionKey.indexOf("/", 1);
            if (endIndex > 0) {
                // If we want a 'typeKey', grab a new end index
                if (withTypeKey) {
                    endIndex = sectionKey.indexOf("/", endIndex);
                }
                // check again to make sure there is an end index
                if (endIndex > 0) {
                    sectionKey = sectionKey.substring(0, endIndex);
                }
            }
            return sectionKey;
        }
        return null;
    }
}
