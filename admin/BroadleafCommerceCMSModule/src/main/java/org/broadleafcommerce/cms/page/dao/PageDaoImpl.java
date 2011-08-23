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

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageFolder;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by bpolster.
 */
@Repository("blPageDao")
public class PageDaoImpl implements PageDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    @Override
    public Page readPageById(Long id) {
        return (Page) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.cms.page.domain.Page"), id);
    }

    @Override
    public Map<String, PageField> readPageFieldsByPageId(Long pageId) {
        // TODO: query
        return null;
    }

    @Override
    public List<PageFolder> readPageFolderChildren(PageFolder parentFolder) {
        Query query = em.createNamedQuery("BC_READ_PAGE_FOLDER_CHILDREN");
        query.setParameter("parentFolder", parentFolder);
        return query.getResultList();
    }

    @Override
    public Page updatePage(Page page) {
        return em.merge(page);
    }

    @Override
    public void delete(Page page) {
        if (!em.contains(page)) {
            page = readPageById(page.getId());
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
        return (Page) em.merge(pageFolder);
    }
}
