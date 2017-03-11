/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.id.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.OptimisticLockException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.id.dao.IdGenerationDao;
import org.broadleafcommerce.common.id.domain.IdGeneration;
import org.springframework.stereotype.Service;

@Service("blIdGenerationService")
public class IdGenerationServiceImpl implements IdGenerationService {

    private static final Log LOG = LogFactory.getLog(IdGenerationServiceImpl.class);

    @Resource(name="blIdGenerationDao")
    protected IdGenerationDao idGenerationDao;

    protected Map<String, Id> idTypeIdMap = new HashMap<String, Id>();

    @Override
    public Long findNextId(String idType) {
        return findNextId(idType, null);
    }

    @Override
    public Long findNextId(String idType, Long batchSize) {
        Id id;
        synchronized (idTypeIdMap) {
            id = idTypeIdMap.get(idType);
            if (id == null) {
                // recheck, another thread may have added this.
                id = idTypeIdMap.get(idType);
                if (id == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Getting the initial id from the database.");
                    }
                    IdGeneration idGeneration = getCurrentIdRange(idType, batchSize);
                    id = new Id(idGeneration.getBatchStart(), idGeneration.getBatchSize());
                }
                idTypeIdMap.put(idType, id);
            }
        }

        synchronized(id) {
            if (id.batchSize == 0L) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Updating batch size for idType " + idType);
                }
                IdGeneration idGeneration = getCurrentIdRange(idType, batchSize);
                id.nextId = idGeneration.getBatchStart();
                id.batchSize = idGeneration.getBatchSize();
            }
            Long retId = id.nextId++;
            id.batchSize--;

            return retId;
        }
    }
    
    private IdGeneration getCurrentIdRange(String idType, Long batchSize) {
        IdGeneration idGeneration = null;
        int retryCount = 0;
        boolean stale = true;
        while (stale) {
            try {
                idGeneration = idGenerationDao.findNextId(idType, batchSize);
                stale = false;
            } catch (OptimisticLockException e) {
                //do nothing -- we will try again
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Error saving batch start for " + idType + ".  Requerying table.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to retrieve id range for " + idType, e);
            }
            if (retryCount >= 10) {
                throw new RuntimeException("Unable to retrieve id range for " + idType + ". Tried " + retryCount + " times, but the version for this entity continues to be concurrently modified.");
            }
            retryCount++;
        }
        return idGeneration;
    }

    private class Id {
        public Long nextId;
        public Long batchSize;

        public Id(Long nextId, Long batchSize) {
            this.nextId = nextId;
            this.batchSize = batchSize;
        }
    }
}
