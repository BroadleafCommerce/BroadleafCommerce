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

package org.broadleafcommerce.profile.core.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.IdGeneration;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("blIdGenerationDao")
public class IdGenerationDaoImpl implements IdGenerationDao {

    private static final Log LOG = LogFactory.getLog(IdGenerationDaoImpl.class);

    protected Long defaultBatchSize = 100L;
    protected Long defaultBatchStart = 1L;

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public IdGeneration findNextId(String idType) throws OptimisticLockException, Exception {
        IdGeneration response;
        Query query = em.createNamedQuery("BC_FIND_NEXT_ID");
        query.setParameter("idType", idType);
        try {
            IdGeneration idGeneration =  (IdGeneration) query.getSingleResult();
            response =  (IdGeneration) entityConfiguration.createEntityInstance("org.broadleafcommerce.profile.core.domain.IdGeneration");
            response.setBatchSize(idGeneration.getBatchSize());
            response.setBatchStart(idGeneration.getBatchStart());
            Long originalBatchStart = idGeneration.getBatchStart();
            idGeneration.setBatchStart(originalBatchStart + idGeneration.getBatchSize());
            if (idGeneration.getBegin() != null) {
                response.setBegin(idGeneration.getBegin());
                if (idGeneration.getBatchStart() < idGeneration.getBegin()) {
                    idGeneration.setBatchStart(idGeneration.getBegin());
                    response.setBatchStart(idGeneration.getBatchStart());
                }
            }
            if (idGeneration.getEnd() != null) {
                response.setEnd(idGeneration.getEnd());
                if (idGeneration.getBatchStart() > idGeneration.getEnd()) {
                    response.setBatchSize(idGeneration.getEnd() - originalBatchStart + 1);
                    if (idGeneration.getBegin() != null) {
                        idGeneration.setBatchStart(idGeneration.getBegin());
                    } else {
                        idGeneration.setBatchStart(getDefaultBatchStart());
                    }
                }
            }
            response.setType(idGeneration.getType());
            em.merge(idGeneration);
            em.flush();
        } catch (NoResultException nre) {
            // No result not found.
            if (LOG.isDebugEnabled()) {
                LOG.debug("No row found in idGenerator table for " + idType + " creating row.");
            }
            response =  (IdGeneration) entityConfiguration.createEntityInstance("org.broadleafcommerce.profile.core.domain.IdGeneration");
            response.setType(idType);
            response.setBegin(null);
            response.setEnd(null);
            response.setBatchStart(getDefaultBatchStart());
            response.setBatchSize(getDefaultBatchSize());
            try {
                em.persist(response);
                em.flush();
            } catch (EntityExistsException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Error inserting row id generation for idType " + idType + ".  Requerying table.");
                }
                return findNextId(idType);
            }
        }
        
        return response;
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

}
