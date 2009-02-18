package org.broadleafcommerce.profile.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

public class CustomerDaoJpa implements CustomerDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private JpaTemplate jpaTemplate;

    private EntityConfiguration entityConfiguration;

    @SuppressWarnings("unchecked")
    @Override
    public Customer readCustomerById(final Long id) {
        return (Customer) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                return em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Customer"), id);
            }
        });
    }

    public Customer readCustomerByUsername(final String username) {
        return (Customer) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_CUSTOMER_BY_USER_NAME");
                query.setParameter("username", username);
                try {
                    return query.getSingleResult();
                } catch (NoResultException ne) {
                    return null;
                }
            }
        });
    }

    public Customer readCustomerByEmail(final String emailAddress) {
        return (Customer) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_CUSTOMER_BY_EMAIL");
                query.setParameter("email", emailAddress);
                try {
                    return query.getSingleResult();
                } catch (NoResultException ne) {
                    return null;
                }
            }
        });
    }

    public Customer maintainCustomer(final Customer customer) {
        return (Customer) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Customer retCustomer = customer;
                if (customer.getId() == null) {
                    em.persist(retCustomer);
                } else {
                    retCustomer = em.merge(retCustomer);
                }
                return retCustomer;
            }
        });
    }

    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.jpaTemplate = new JpaTemplate(emf);
    }

    public void setEntityConfiguration(EntityConfiguration entityConfiguration) {
        this.entityConfiguration = entityConfiguration;
    }
}
