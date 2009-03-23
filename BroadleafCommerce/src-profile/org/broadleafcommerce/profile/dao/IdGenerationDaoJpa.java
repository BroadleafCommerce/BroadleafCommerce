package org.broadleafcommerce.profile.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.IdGeneration;
import org.springframework.stereotype.Repository;

@Repository("idGenerationDao")
public class IdGenerationDaoJpa implements IdGenerationDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    public IdGeneration findNextId(String idType) {
        Query query = em.createNamedQuery("BC_FIND_NEXT_ID");
        query.setParameter("idType", idType);
        query.setHint("org.hibernate.cacheable", false);
        return (IdGeneration) query.getSingleResult();
    }

    public IdGeneration updateNextId(IdGeneration idGeneration) {
        return em.merge(idGeneration);
    }
}
