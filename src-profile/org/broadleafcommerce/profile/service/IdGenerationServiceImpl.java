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
package org.broadleafcommerce.profile.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.dao.IdGenerationDao;
import org.broadleafcommerce.profile.domain.IdGeneration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("idGenerationService")
public class IdGenerationServiceImpl implements IdGenerationService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private IdGenerationDao idGenerationDao;

    private Map<String, Id> idTypeIdMap = new HashMap<String, Id>();

    @Transactional(propagation = Propagation.REQUIRED)
    public Long findNextId(String idType) {
        Id id = idTypeIdMap.get(idType);
        IdGeneration idGeneration=null;
        if (id == null) {
            synchronized (idTypeIdMap) {
                // recheck, another thread may have added this.
                id = idTypeIdMap.get(idType);
                if (id == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Getting the initial id from the database.");
                    }
                    idGeneration = idGenerationDao.findNextId(idType);
                    id = new Id(idGeneration.getBatchStart(), 0L);
                }
                idTypeIdMap.put(idType, id);
            }
        }

        // Minimize synchronization to the idType we are looking for.
        synchronized(id) {
            if (id.batchSize == 0L) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Updating batch size for idType " + idType);
                }

                Long prevBatchStart = idGeneration.getBatchStart();
                Long batchSize = idGeneration.getBatchSize();
                idGeneration.setBatchStart(prevBatchStart + batchSize);
                idGeneration = idGenerationDao.updateNextId(idGeneration);
                id.nextId = prevBatchStart;
            }
            Long retId = id.nextId++;
            id.batchSize--;
            return retId;
        }
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
