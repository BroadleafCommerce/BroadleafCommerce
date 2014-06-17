/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.structure.dao;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentField;
import org.broadleafcommerce.cms.structure.domain.StructuredContentImpl;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.cms.structure.domain.StructuredContentTypeImpl;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, StructuredContentField> readFieldsForStructuredContentItem(StructuredContent sc) {
        Query query = em.createNamedQuery("BC_READ_CONTENT_FIELDS_BY_CONTENT_ID");
        query.setParameter("structuredContent", sc);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        List<StructuredContentField> fields = query.getResultList();
        Map<String, StructuredContentField> fieldMap = new HashMap<String, StructuredContentField>();
        for (StructuredContentField scField : fields) {
            fieldMap.put(scField.getFieldKey(), scField);
        }
        return fieldMap;
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
    public List<StructuredContent> findActiveStructuredContentByType(SandBox sandBox, StructuredContentType type, Locale locale) {
        return findActiveStructuredContentByType(sandBox, type, locale, null);
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByType(SandBox sandBox, StructuredContentType type, Locale fullLocale, Locale languageOnlyLocale) {
        String queryName = null;
        if (languageOnlyLocale == null)  {
            languageOnlyLocale = fullLocale;
        }
        if (sandBox == null) {
            queryName = "BC_ACTIVE_STRUCTURED_CONTENT_BY_TYPE";
        } else if (SandBoxType.PRODUCTION.equals(sandBox)) {
            queryName = "BC_ACTIVE_STRUCTURED_CONTENT_BY_TYPE_AND_PRODUCTION_SANDBOX";
        } else {
            queryName = "BC_ACTIVE_STRUCTURED_CONTENT_BY_TYPE_AND_USER_SANDBOX";
        }

        Query query = em.createNamedQuery(queryName);
        query.setParameter("contentType", type);
        query.setParameter("fullLocale", fullLocale);
        query.setParameter("languageOnlyLocale", languageOnlyLocale);
        if (sandBox != null)  {
            query.setParameter("sandboxId", sandBox.getId());
        }
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        return query.getResultList();
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByNameAndType(SandBox sandBox, StructuredContentType type, String name, Locale locale) {
        return findActiveStructuredContentByNameAndType(sandBox, type, name, locale, null);
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByNameAndType(SandBox sandBox, StructuredContentType type, String name, Locale fullLocale, Locale languageOnlyLocale) {
        String queryName = null;
        if (languageOnlyLocale == null)  {
            languageOnlyLocale = fullLocale;
        }
        final Query query;
        if (sandBox == null) {
            query = em.createNamedQuery("BC_ACTIVE_STRUCTURED_CONTENT_BY_TYPE_AND_NAME");
        } else if (SandBoxType.PRODUCTION.equals(sandBox)) {
            query = em.createNamedQuery("BC_ACTIVE_STRUCTURED_CONTENT_BY_TYPE_AND_NAME_AND_PRODUCTION_SANDBOX");
            query.setParameter("sandbox", sandBox);
        } else {
            query = em.createNamedQuery("BC_ACTIVE_STRUCTURED_CONTENT_BY_TYPE_AND_NAME_AND_USER_SANDBOX");
            query.setParameter("sandboxId", sandBox.getId());
        }

        query.setParameter("contentType", type);
        query.setParameter("contentName", name);
        query.setParameter("fullLocale", fullLocale);
        query.setParameter("languageOnlyLocale", languageOnlyLocale);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        return query.getResultList();
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByName(SandBox sandBox, String name, Locale locale) {
        return findActiveStructuredContentByName(sandBox, name, locale, null);
    }

    @Override
    public List<StructuredContent> findActiveStructuredContentByName(SandBox sandBox, String name, Locale fullLocale, Locale languageOnlyLocale) {
        String queryName = null;
        if (languageOnlyLocale == null)  {
            languageOnlyLocale = fullLocale;
        }
        if (sandBox == null) {
            queryName = "BC_ACTIVE_STRUCTURED_CONTENT_BY_NAME";
        } else if (SandBoxType.PRODUCTION.equals(sandBox)) {
            queryName = "BC_ACTIVE_STRUCTURED_CONTENT_BY_NAME_AND_PRODUCTION_SANDBOX";
        } else {
            queryName = "BC_ACTIVE_STRUCTURED_CONTENT_BY_NAME_AND_USER_SANDBOX";
        }

        Query query = em.createNamedQuery(queryName);
        query.setParameter("contentName", name);
        query.setParameter("fullLocale", fullLocale);
        query.setParameter("languageOnlyLocale", languageOnlyLocale);
        if (sandBox != null) {
            query.setParameter("sandbox", sandBox);
        }
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
