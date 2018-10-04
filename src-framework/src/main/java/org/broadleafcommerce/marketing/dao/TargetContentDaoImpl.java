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

import org.broadleafcommerce.marketing.domain.TargetContent;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blTargetContentDao")
public class TargetContentDaoImpl implements TargetContentDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    public void delete(Long targetContentId) {
        TargetContent tc = readTargetContentById(targetContentId);
        em.remove(tc);
    }

    public TargetContent save(TargetContent targetContent) {
        if(targetContent.getId() == null) {
            em.persist(targetContent);
        }else {
            targetContent = em.merge(targetContent);
        }
        return targetContent;
    }

    @SuppressWarnings("unchecked")
    public List<TargetContent> readCurrentTargetContentByNameType(String name, String type) {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS_BY_NAME_TYPE");
        query.setParameter("name", name);
        query.setParameter("type", type);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<TargetContent> readCurrentTargetContentsByPriority(int priority) {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS_BY_PRIORITY");
        query.setParameter("priority", priority);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public TargetContent readTargetContentById(Long targetContentId) {
        return (TargetContent) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.marketing.domain.TargetContent"), targetContentId);
    }

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
