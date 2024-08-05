/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.structure.dao;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentImpl;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.cms.structure.domain.StructuredContentTypeImpl;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by bpolster.
 */
@Repository("blStructuredContentDao")
public class StructuredContentDaoImpl implements StructuredContentDao {

    private static SandBox DUMMY_SANDBOX = new SandBoxImpl();
    {
        DUMMY_SANDBOX.setId(-1l);
    }

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public StructuredContent findStructuredContentById(Long contentId) {
        return em.find(StructuredContentImpl.class, contentId);
    }

    @Override
    public StructuredContentType findStructuredContentTypeById(Long contentTypeId) {
        return em.find(StructuredContentTypeImpl.class, contentTypeId);
    }

    @Override
    public List<StructuredContentType> retrieveAllStructuredContentTypes() {
        Query query = em.createNamedQuery("BC_READ_ALL_STRUCTURED_CONTENT_TYPES");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }
    
    @Override
    public List<StructuredContent> findAllContentItems() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<StructuredContent> criteria = builder.createQuery(StructuredContent.class);
        Root<StructuredContentImpl> sc = criteria.from(StructuredContentImpl.class);

        criteria.select(sc);

        try {
            TypedQuery<StructuredContent> query = em.createQuery(criteria);
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<StructuredContent>();
        }
    }

    @Override
    public StructuredContent addOrUpdateContentItem(StructuredContent content) {
        return em.merge(content);
    }

    @Override
    public void delete(StructuredContent content) {
        if (! em.contains(content)) {
            content = findStructuredContentById(content.getId());
        }
        em.remove(content);
    }
    
    @Override
    public StructuredContentType saveStructuredContentType(StructuredContentType type) {
        return em.merge(type);
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByType(StructuredContentType type, Locale locale) {
        return findActiveStructuredContentByType(type, locale, null);
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByType(StructuredContentType type, Locale fullLocale, Locale languageOnlyLocale) {
        String queryName = "BC_ACTIVE_STRUCTURED_CONTENT_BY_TYPE";
        if (languageOnlyLocale == null)  {
            languageOnlyLocale = fullLocale;
        }

        Query query = em.createNamedQuery(queryName);
        query.setParameter("contentType", type);
        query.setParameter("fullLocale", fullLocale);
        query.setParameter("languageOnlyLocale", languageOnlyLocale);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        return query.getResultList();
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByNameAndType(StructuredContentType type, String name, Locale locale) {
        return findActiveStructuredContentByNameAndType(type, name, locale, null);
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByNameAndType(StructuredContentType type, String name, Locale fullLocale, Locale languageOnlyLocale) {
        if (languageOnlyLocale == null)  {
            languageOnlyLocale = fullLocale;
        }
        final Query query = em.createNamedQuery("BC_ACTIVE_STRUCTURED_CONTENT_BY_TYPE_AND_NAME");
        query.setParameter("contentType", type);
        query.setParameter("contentName", name);
        query.setParameter("fullLocale", fullLocale);
        query.setParameter("languageOnlyLocale", languageOnlyLocale);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        return query.getResultList();
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByName(String name, Locale locale) {
        return findActiveStructuredContentByName(name, locale, null);
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByName(String name, Locale fullLocale, Locale languageOnlyLocale) {
        String queryName = "BC_ACTIVE_STRUCTURED_CONTENT_BY_NAME";
        if (languageOnlyLocale == null)  {
            languageOnlyLocale = fullLocale;
        }

        Query query = em.createNamedQuery(queryName);
        query.setParameter("contentName", name);
        query.setParameter("fullLocale", fullLocale);
        query.setParameter("languageOnlyLocale", languageOnlyLocale);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        return query.getResultList();
    }

    @Override
    public StructuredContentType findStructuredContentTypeByName(String name) {
        Query query = em.createNamedQuery("BC_READ_STRUCTURED_CONTENT_TYPE_BY_NAME");
        query.setParameter("name",name);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        List<StructuredContentType> results = query.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void detach(StructuredContent sc) {
        em.detach(sc);
    }
}
