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

package org.broadleafcommerce.cms.page.dao;

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageImpl;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.cms.page.domain.PageTemplateImpl;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
@Repository("blPageDao")
public class PageDaoImpl implements PageDao {

    private static SandBox DUMMY_SANDBOX = new SandBoxImpl();
    {
        DUMMY_SANDBOX.setId(-1l);
    }

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public Page readPageById(Long id) {
        return (Page) em.find(PageImpl.class, id);
    }

    @Override
    public PageTemplate readPageTemplateById(Long id) {
        return (PageTemplate) em.find(PageTemplateImpl.class, id);
    }

    @Override
    public Map<String, PageField> readPageFieldsByPage(Page page) {
        Query query = em.createNamedQuery("BC_READ_PAGE_FIELDS_BY_PAGE_ID");
        query.setParameter("page", page);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        List<PageField> pageFields = (List<PageField>) query.getResultList();
        Map<String, PageField> pageFieldMap = new HashMap<String, PageField>();
        for (PageField pageField : pageFields) {
            pageFieldMap.put(pageField.getFieldKey(), pageField);
        }
        return pageFieldMap;
    }

    @Override
    public Page updatePage(Page page) {
        return em.merge(page);
    }

    @Override
    public void delete(Page page) {
        if (!em.contains(page)) {
            page = (Page) readPageById(page.getId());
        }
        em.remove(page);
    }

    @Override
    public Page addPage(Page clonedPage) {
        return em.merge(clonedPage);
    }

    @Override
    public List<Page> findPageByURI(SandBox sandBox, Locale locale, String uri) {
        Query query;
        if (sandBox == null) {
            query = em.createNamedQuery("BC_READ_PAGE_BY_URI");
            query.setParameter("uri", uri);
            query.setParameter("locale", locale);
        } else if (SandBoxType.PRODUCTION.equals(sandBox)) {
            query = em.createNamedQuery("BC_READ_PAGE_BY_URI_AND_PRODUCTION_SANDBOX");
            query.setParameter("sandbox", sandBox);
            query.setParameter("locale", locale);
            query.setParameter("uri", uri);
        } else {
            query = em.createNamedQuery("BC_READ_PAGE_BY_URI_AND_USER_SANDBOX");
            query.setParameter("sandboxId", sandBox.getId());
            query.setParameter("locale", locale);
            query.setParameter("uri", uri);
        }

        return query.getResultList();
    }

    @Override
    public void detachPage(Page page) {
        em.detach(page);
    }

}
