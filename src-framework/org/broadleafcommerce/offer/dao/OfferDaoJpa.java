package org.broadleafcommerce.offer.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("offerDao")
public class OfferDaoJpa implements OfferDao {

	/** Lookup identifier for Offer bean **/
	private static String beanName = "org.broadleafcommerce.promotion.domain.Offer";
	
	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
	
    @PersistenceContext(unitName="blPU")
    private EntityManager em;
    
    @Resource
    private EntityConfiguration entityConfiguration;
    
    @Override
	public Offer create() {
    	return ((Offer) entityConfiguration.createEntityInstance(beanName));
    }

	@Override
	public void deleteOffer(Offer offer) {
		em.remove(offer);
	}

	@Override
	public Offer maintainOffer(Offer offer) {
		if(offer.getId() == null){
			em.persist(offer);
		}else{
			offer = em.merge(offer);
		}
		return offer;
				
	}

	@Override
	@SuppressWarnings("unchecked")
	public Offer readOfferById(Long offerId) {
		return (Offer) em.find(entityConfiguration.lookupEntityClass(beanName), offerId);
	}

}
