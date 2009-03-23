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
    public synchronized Long findNextId(String idType) {
        Id id = idTypeIdMap.get(idType);
        if (id == null || id.batchSize.equals(0L)) {
            if (logger.isDebugEnabled()) {
                logger.debug("I don't have the next id, going to the database");
            }
            IdGeneration idGeneration = idGenerationDao.findNextId(idType);
            Long nextId = idGeneration.getBatchStart();
            Long batchSize = idGeneration.getBatchSize();
            idGeneration.setBatchStart(nextId + batchSize);
            idGenerationDao.updateNextId(idGeneration);
            id = new Id(nextId, batchSize);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("I already have the next id");
            }
        }
        Long retId = id.nextId++;
        id.batchSize--;
        idTypeIdMap.put(idType, id);
        return retId;
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
