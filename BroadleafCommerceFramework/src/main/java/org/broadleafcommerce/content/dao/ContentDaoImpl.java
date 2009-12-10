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
import org.springframework.stereotype.Repository;

/**
 * @author btaylor
 *
 */
@Repository("blContentDao")
public class ContentDaoImpl implements ContentDao {

	@PersistenceContext(unitName="blPU")
	protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";    
    
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.dao.ContentDao#delete(org.broadleafcommerce.content.domain.Content)
	 */
	public void delete(Content content) {
		if (!em.contains(content)){
			content = readContentById(content.getId());
		}
		em.remove(content);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.dao.ContentDao#readContentById(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public Content readContentById(Long id) {
		return (Content) em.find(entityConfiguration.lookupEntityClass("com.broadleafcommerce.content.domain.Content"), id);
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
		return (List<Content>)query.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.dao.ContentDao#saveContent(org.broadleafcommerce.content.domain.Content)
	 */
	public Content saveContent(Content content) {
		return em.merge(content);
	}
	
    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }	
}
