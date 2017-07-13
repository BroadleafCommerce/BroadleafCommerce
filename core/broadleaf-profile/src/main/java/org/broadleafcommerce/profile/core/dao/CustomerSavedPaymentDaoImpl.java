package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.SavedPayment;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Jacob Mitash
 */
@Repository("blCustomerSavedPaymentDao")
public class CustomerSavedPaymentDaoImpl implements CustomerSavedPaymentDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public void save(SavedPayment savedPayment) {
        em.merge(savedPayment);
    }

    @Override
    public List<SavedPayment> readSavedPaymentsByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_SAVED_PAYMENTS");
        return query.getResultList();
    }

    @Override
    public void deleteSavedPayment(SavedPayment savedPayment) {
        em.remove(savedPayment);
    }

    @Override
    public SavedPayment create() {
        return (SavedPayment) entityConfiguration.createEntityInstance(SavedPayment.class.getName());
    }
}
