package org.broadleafcommerce.offer.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.offer.domain.OfferCustomer;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

//TODO: should rename to CustomerOfferDaoJpa
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
    public void delete(OfferCustomer offerCustomer) {
        em.remove(offerCustomer);
    }

    @Override
    public OfferCustomer save(OfferCustomer offerCustomer) {
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

    @Override
    public List<OfferCustomer> readOffersByCustomer(Customer customer) {
        // TODO: add code to query database
        return new ArrayList<OfferCustomer>();
    }

}
