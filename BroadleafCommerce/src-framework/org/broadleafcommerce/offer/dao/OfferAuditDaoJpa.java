package org.broadleafcommerce.offer.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("offerAuditDao")
public class OfferAuditDaoJpa implements OfferAuditDao {

    /** Lookup identifier for Offer bean **/
    private static String beanName = "org.broadleafcommerce.promotion.domain.OfferAudit";

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @Override
    public OfferAudit create() {
        return ((OfferAudit) entityConfiguration.createEntityInstance(beanName));
    }

    @Override
    public void delete(OfferAudit offerAudit) {
        em.remove(offerAudit);
    }

    @Override
    public OfferAudit save(OfferAudit offerAudit) {
        if(offerAudit.getId() == null){
            em.persist(offerAudit);
        }else{
            offerAudit = em.merge(offerAudit);
        }
        return offerAudit;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OfferAudit readAuditById(Long offerAuditId) {
        return (OfferAudit) em.find(entityConfiguration.lookupEntityClass(beanName), offerAuditId);
    }

}
