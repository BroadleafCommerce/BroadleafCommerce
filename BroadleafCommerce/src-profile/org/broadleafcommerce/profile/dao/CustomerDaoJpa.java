package org.broadleafcommerce.profile.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.BroadleafCustomer;
import org.broadleafcommerce.profile.domain.Customer;
import org.springframework.stereotype.Repository;

@Repository("customerDao")
public class CustomerDaoJpa implements CustomerDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    @Override
    public Customer readCustomerById(Long id) {
        return em.find(BroadleafCustomer.class, id);
    }

    public Customer readCustomerByUsername(String username) {
        Query query = em.createNamedQuery("READ_CUSTOMER_BY_USER_NAME");
        query.setParameter("username", username);
        try {
            return (Customer) query.getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    public Customer readCustomerByEmail(String emailAddress) {
        Query query = em.createNamedQuery("READ_CUSTOMER_BY_EMAIL");
        query.setParameter("email", emailAddress);
        try {
            return (Customer) query.getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    public Customer maintainCustomer(Customer customer) {
        if (customer.getId() == null) {
            em.persist(customer);
        } else {
            customer = em.merge(customer);
        }
        return customer;
    }
}
