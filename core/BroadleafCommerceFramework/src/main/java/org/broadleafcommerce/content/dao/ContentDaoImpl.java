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
package org.broadleafcommerce.content.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.content.domain.Content;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.util.dao.BatchRetrieveDao;
import org.springframework.stereotype.Repository;

/**
*
* @author btaylor
 */
@Repository("blContentDao")
public class ContentDaoImpl extends BatchRetrieveDao implements ContentDao {
    
    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;
    protected String queryCacheableKey = "org.hibernate.cacheable";

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#delete(org.broadleafcommerce.content.domain.Content)
     */
    public void delete(Content content) {
        if (!em.contains(content)) {
            content = readContentById(content.getId());
        }

        em.remove(content);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#delete(java.util.List)
     */
    public void delete(List<Content> contentList) {
        for (Content content : contentList) {
            this.delete(content);
        }
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#readContentAwaitingApproval()
     */
    public List<Content> readContentAwaitingApproval() {
        Query query = em.createNamedQuery("BC_READ_CONTENT_AWAITING_APPROVAL");

        return (List<Content>) query.getResultList();
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#readContentById(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public Content readContentById(Integer id) {
        return (Content) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.content.domain.Content"), id);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#readContentByIds(java.util.List)
     */
    public List<Content> readContentByIdsAndSandbox(List<Integer> ids, String sandbox) {
        Query query;

        if (sandbox == null) {
            query = em.createNamedQuery("BC_READ_CONTENT_BY_IDS_WHERE_SANDBOX_IS_NULL");
        } else {
            query = em.createNamedQuery("BC_READ_CONTENT_BY_IDS_AND_SANDBOX");
            query.setParameter("sandbox", sandbox);
        }

        query.setHint(getQueryCacheableKey(), true);
        
        return batchExecuteReadQuery(query, ids, "idList");
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#readContentBySandbox(java.lang.String)
     */
    public List<Content> readContentBySandbox(String sandbox) {
        Query query = null;
        if(sandbox!=null && sandbox.endsWith("*"))
            query = em.createNamedQuery("BC_READ_CONTENT_BY_LIKE_SANDBOX");
        else
            query = em.createNamedQuery("BC_READ_CONTENT_BY_SANDBOX");

        query.setParameter("sandbox", sandbox);
        query.setHint(getQueryCacheableKey(), true);

        return (List<Content>) query.getResultList();
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#readContentBySandboxAndType(java.lang.String, java.lang.String)
     */
    public List<Content> readContentBySandboxAndType(String sandbox, String contentType) {
        Query query = null;
        if (sandbox == null){
            query = em.createNamedQuery("BC_READ_CONTENT_BY_NULL_SANDBOX_AND_CONTENT_TYPE");
        } else {
            query = em.createNamedQuery("BC_READ_CONTENT_BY_SANDBOX_AND_CONTENT_TYPE");
            query.setParameter("sandbox", sandbox);
        }

        query.setParameter("contentType", contentType);
        query.setHint(getQueryCacheableKey(), true);

        return (List<Content>) query.getResultList();
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#readContentByVersionSandboxFile(java.lang.Long, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Content> readContentSpecified(String sandbox, String contentType, Date displayDate) {
        Query query = em.createNamedQuery("BC_READ_CONTENT_SPECIFIED");
        query.setParameter("sandbox", sandbox);
        query.setParameter("contentType", contentType);
        query.setParameter("displayDate", displayDate);
        query.setHint(getQueryCacheableKey(), true);

        return (List<Content>) query.getResultList();
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#saveContent(org.broadleafcommerce.content.domain.Content)
     */
    public Content saveContent(Content content) {
        return em.merge(content);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#saveContent(java.util.List)
     */
    public List<Content> saveContent(List<Content> contentList) {
        for (Content content : contentList) {
            this.saveContent(content);
        }

        return contentList;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.content.dao.ContentDao#readStagedContent()
     */
    public List<Content> readStagedContent() {
        Query query = em.createNamedQuery("BC_READ_STAGED_CONTENT");

        return (List<Content>) query.getResultList();
    }

    public List<Content> readAllContent() {
        Query query = em.createNamedQuery("BC_READ_ALL_CONTENT");

        return (List<Content>) query.getResultList();
    }

}
