/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.marketing.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.marketing.domain.TargetContent;
import org.broadleafcommerce.core.marketing.domain.TargetContentImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

@Repository("blTargetContentDao")
/**
 *
 * @deprecated  No longer used as of Broadleaf 1.5
 */
@Deprecated
public class TargetContentDaoImpl implements TargetContentDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public void delete(Long targetContentId) {
        TargetContent tc = readTargetContentById(targetContentId);
        em.remove(tc);
    }

    public TargetContent save(TargetContent targetContent) {
        return em.merge(targetContent);
    }

    @SuppressWarnings("unchecked")
    public List<TargetContent> readCurrentTargetContentByNameType(String name, String type) {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS_BY_NAME_TYPE");
        query.setParameter("name", name);
        query.setParameter("type", type);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<TargetContent> readCurrentTargetContentsByPriority(int priority) {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS_BY_PRIORITY");
        query.setParameter("priority", priority);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    public TargetContent readTargetContentById(Long targetContentId) {
        return (TargetContent) em.find(TargetContentImpl.class, targetContentId);
    }

    @SuppressWarnings("unchecked")
    public List<TargetContent> readTargetContents() {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }
}
