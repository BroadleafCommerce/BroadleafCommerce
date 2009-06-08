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
package org.broadleafcommerce.profile.dao;

import javax.annotation.Resource;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.IdGeneration;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blIdGenerationDao")
public class IdGenerationDaoJpa implements IdGenerationDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private Long defaultBatchSize = 100L;
    private Long defaultBatchStart = 1L;

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource
    protected EntityConfiguration entityConfiguration;

    private String queryCacheableKey = "org.hibernate.cacheable";

    public IdGeneration findNextId(String idType) {
        IdGeneration idGeneration;

        Query query = em.createNamedQuery("BC_FIND_NEXT_ID");
        query.setParameter("idType", idType);
        query.setHint(getQueryCacheableKey(), false);
        try {
            idGeneration =  (IdGeneration) query.getSingleResult();
        } catch (NoResultException nre) {
            // No result not found.
            if (logger.isDebugEnabled()) {
                logger.debug("No row found in idGenerator table for " + idType + " creating row.");
            }
            idGeneration =  (IdGeneration) entityConfiguration.createEntityInstance("org.broadleafcommerce.profile.domain.IdGeneration");
            idGeneration.setType(idType);
            idGeneration.setBatchStart(getDefaultBatchStart());
            idGeneration.setBatchSize(getDefaultBatchSize());
            try {
                em.persist(idGeneration);
            } catch (EntityExistsException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Error inserting row id generation for idType " + idType + ".  Requerying table.");
                }
                return findNextId(idType);
            }
        }
        return idGeneration;
    }

    public IdGeneration updateNextId(IdGeneration idGeneration) {
        return em.merge(idGeneration);
    }

    public Long getDefaultBatchSize() {
        return defaultBatchSize;
    }

    public void setDefaultBatchSize(Long defaultBatchSize) {
        this.defaultBatchSize = defaultBatchSize;
    }

    public Long getDefaultBatchStart() {
        return defaultBatchStart;
    }

    public void setDefaultBatchStart(Long defaultBatchStart) {
        this.defaultBatchStart = defaultBatchStart;
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
}
