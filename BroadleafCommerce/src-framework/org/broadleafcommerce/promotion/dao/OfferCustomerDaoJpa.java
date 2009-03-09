package org.broadleafcommerce.promotion.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.promotion.domain.OfferCustomer;
import org.springframework.stereotype.Repository;

@Repository("offerCustomerDao")
public class OfferCustomerDaoJpa implements OfferCustomerDao {

	/** Lookup identifier for Offer bean **/
	private static String beanName = "org.broadleafcommerce.promotion.domain.OfferCustomer";
	
	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
	
    @PersistenceContext(unitName="blPU")
    private EntityManager em;
    
    @Resource
    private EntityConfiguration entityConfiguration;
    	
	
	@Override
	public OfferCustomer create() {
		return ((OfferCustomer) entityConfiguration.createEntityInstance(beanName));
	}

	@Override
	public void deleteOfferCustomer(OfferCustomer offerCustomer) {
		em.remove(offerCustomer);
	}

	@Override
	public OfferCustomer maintainOfferCustomer(OfferCustomer offerCustomer) {
		if(offerCustomer.getId() == null){
			em.persist(offerCustomer);
		}else{
			offerCustomer = em.merge(offerCustomer);
		}
		return offerCustomer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public OfferCustomer readOfferCustomerById(Long offerCustomerId) {
		return (OfferCustomer) em.find(entityConfiguration.lookupEntityClass(beanName), offerCustomerId);
	}

}
