package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

public class AddressDaoJpa implements AddressDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private JpaTemplate jpaTemplate;

    @SuppressWarnings("unchecked")
    public List<Address> readAddressByUserId(final Long customerId) {
        return (List<Address>) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_ADDRESS_BY_CUSTOMER_ID");
                query.setParameter("customerId", customerId);
                return query.getResultList();
            }
        });
    }

    public Address readAddressByUserIdAndName(final Long customerId, final String addressName) {
        return (Address) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_ADDRESS_BY_CUSTOMER_ID_AND_NAME");
                query.setParameter("customerId", customerId);
                query.setParameter("addressName", addressName);
                return query.getSingleResult();
            }
        });
    }

    public Address maintainAddress(final Address address) {
        return (Address) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Address retAddress = address;
                if (retAddress.getId() == null) {
                    em.persist(retAddress);
                } else {
                    retAddress = em.merge(retAddress);
                }
                return retAddress;
            }
        });
    }

    public Address readAddressById(final Long addressId) {
        return (Address) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_ADDRESS_BY_ID");
                query.setParameter("addressId", addressId);
                return query.getSingleResult();
            }
        });
    }

    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.jpaTemplate = new JpaTemplate(emf);
    }
}
