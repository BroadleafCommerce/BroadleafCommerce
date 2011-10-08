/*
 * Copyright 2008-20011 the original author or authors.
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

import org.broadleafcommerce.cms.locale.domain.Locale;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageFolder;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.broadleafcommerce.openadmin.server.domain.SandBoxType;
import org.broadleafcommerce.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

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
    public PageFolder readPageById(Long id) {
        return (PageFolder) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.cms.page.domain.PageFolder"), id);
    }

    @Override
    public PageTemplate readPageTemplateById(Long id) {
        return (PageTemplate) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.cms.page.domain.PageTemplate"), id);
    }

    @Override
    public Map<String, PageField> readPageFieldsByPage(Page page) {
        Query query = em.createNamedQuery("BC_READ_PAGE_FIELDS_BY_PAGE_ID");
        query.setParameter("page", page);

        List<PageField> pageFields = (List<PageField>) query.getResultList();
        Map<String, PageField> pageFieldMap = new HashMap<String, PageField>();
        for (PageField pageField : pageFields) {
            pageFieldMap.put(pageField.getFieldKey(), pageField);
        }
        return pageFieldMap;
    }

    @Override
    public List<PageFolder> readPageFolderChildren(PageFolder parentFolder, String localeCode, SandBox userSandBox, SandBox productionSandBox) {
        String queryPrefix = "BC_READ_";
        if (parentFolder == null) {
                queryPrefix = "BC_READ_NULL_";
        }
        Query query = em.createNamedQuery(queryPrefix + "PAGE_FOLDER_CHILD_PAGES");
        if (parentFolder != null) {
            query.setParameter("parentFolder", parentFolder);
        }
        query.setParameter("userSandbox", userSandBox == null ? DUMMY_SANDBOX : userSandBox);
        query.setParameter("productionSandbox", productionSandBox == null ? DUMMY_SANDBOX : productionSandBox);
        query.setParameter("localeCode", localeCode);

        List<Page> childPages = query.getResultList();
        filterPagesForSandbox(userSandBox, productionSandBox, childPages);

        Query query2 = em.createNamedQuery(queryPrefix + "PAGE_FOLDER_CHILD_FOLDERS");
        if (parentFolder != null) {
            query2.setParameter("parentFolder", parentFolder);
        }
        List<PageFolder> childFolders = query2.getResultList();
        childFolders.addAll(childPages);

        return childFolders;
    }

    private void filterPagesForSandbox(SandBox userSandBox, SandBox productionSandBox, List<Page> pageList) {
        if (userSandBox != null) {

            List<Long> removeIds = new ArrayList<Long>();
            for (Page page : pageList) {
                if (page.getOriginalPageId() != null) {
                    removeIds.add(page.getOriginalPageId());
                }

                if (page.getDeletedFlag()) {
                    removeIds.add(page.getId());
                }
            }

            Iterator<Page> pageIterator = pageList.iterator();

            while (pageIterator.hasNext()) {
                Page page = pageIterator.next();
                if (removeIds.contains(page.getId())) {
                  pageIterator.remove();
                }
            }
        }
    }

    @Override
    public Page updatePage(Page page, boolean clearLevel1Cache) {
        if (clearLevel1Cache) {
            em.clear();
        }
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
    public PageFolder updatePageFolder(PageFolder pageFolder) {
        return em.merge(pageFolder);
    }

    @Override
    public Page addPage(Page clonedPage) {
        return (Page) em.merge(clonedPage);
    }

    @Override
    public PageFolder addPageFolder(PageFolder pageFolder) {
        return (PageFolder) em.merge(pageFolder);
    }

    @Override
    public Page findPageByURI(SandBox sandBox, Locale locale, String uri) {
        String[] pathElements = uri.split("//");
        String pageName = pathElements[pathElements.length - 1];

        Query query = null;

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
            query.setParameter("sandbox", sandBox);
            query.setParameter("locale", locale);
            query.setParameter("uri", uri);
        }

        List<Page> results = query.getResultList();
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

}
