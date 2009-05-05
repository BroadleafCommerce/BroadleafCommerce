package org.broadleafcommerce.offer.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("offerCodeDao")
public class OfferCodeDaoJpa implements OfferCodeDao {

    /** Lookup identifier for Offer bean **/
    private static String beanName = "org.broadleafcommerce.promotion.domain.OfferCode";

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;



    @Override
    public OfferCode create() {
        return ((OfferCode) entityConfiguration.createEntityInstance(beanName));
    }

    @Override
    public void delete(OfferCode offerCode) {
        em.remove(offerCode);
    }

    @Override
    public OfferCode save(OfferCode offerCode) {
        if(offerCode.getId()==null){
            em.persist(offerCode);
        }else{
            offerCode = em.merge(offerCode);
        }
        return offerCode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OfferCode readOfferCodeById(Long offerCodeId) {
        return (OfferCode) em.find(entityConfiguration.lookupEntityClass(beanName), offerCodeId);
    }

}
