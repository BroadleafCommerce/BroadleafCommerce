package org.broadleafcommerce.profile.service;

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

    private Long nextId = null;

    private Long batchSize = null;

    @Transactional(propagation = Propagation.REQUIRED)
    public synchronized Long findNextId(String idType) {
        if (nextId == null || batchSize == null || batchSize.equals(0L)) {
            if (logger.isDebugEnabled()) {
                logger.debug("I don't have the next id, going to the database");
            }
            IdGeneration idGeneration = idGenerationDao.findNextId(idType);
            nextId = idGeneration.getBatchStart();
            batchSize = idGeneration.getBatchSize();
            idGeneration.setBatchStart(nextId + batchSize);
            idGenerationDao.updateNextId(idGeneration);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("I already have the next id");
            }
        }
        Long retId = nextId++;
        batchSize--;
        return retId;
    }
}
