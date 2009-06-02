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

@Repository("idGenerationDao")
public class IdGenerationDaoJpa implements IdGenerationDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private Long defaultBatchSize = 100L;
    private Long defaultBatchStart = 1L;

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;
    
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

    public void setDefaultBatchStart(Long defaultBatchSize) {
        this.defaultBatchStart = defaultBatchStart;
    }
    
    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }    
}
