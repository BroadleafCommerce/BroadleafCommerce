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
package org.broadleafcommerce.marketing.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.marketing.domain.TargetContent;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("targetContentDao")
public class TargetContentDaoJpa implements TargetContentDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource
    protected EntityConfiguration entityConfiguration;

    private String queryCacheableKey = "org.hibernate.cacheable";

    @Override
    public void delete(Long targetContentId) {
        TargetContent tc = readTargetContentById(targetContentId);
        em.remove(tc);
    }

    @Override
    public TargetContent save(TargetContent targetContent) {
        if(targetContent.getId() == null) {
            em.persist(targetContent);
        }else {
            targetContent = em.merge(targetContent);
        }
        return targetContent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TargetContent> readCurrentTargetContentByNameType(String name, String type) {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS_BY_NAME_TYPE");
        query.setParameter("name", name);
        query.setParameter("type", type);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TargetContent> readCurrentTargetContentsByPriority(int priority) {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS_BY_PRIORITY");
        query.setParameter("priority", priority);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TargetContent readTargetContentById(Long targetContentId) {
        return (TargetContent) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.marketing.domain.TargetContent"), targetContentId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TargetContent> readTargetContents() {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS");
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
}
