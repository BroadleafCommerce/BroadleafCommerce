/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.structure.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentField;
import org.broadleafcommerce.cms.structure.domain.StructuredContentImpl;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.cms.structure.domain.StructuredContentTypeImpl;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

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
        return query.getResultList();
    }
    
    @Override
    public List<StructuredContent> findAllContentItems() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<StructuredContent> criteria = builder.createQuery(StructuredContent.class);
        Root<StructuredContentImpl> sc = criteria.from(StructuredContentImpl.class);

        criteria.select(sc);

        try {
            return em.createQuery(criteria).getResultList();
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
