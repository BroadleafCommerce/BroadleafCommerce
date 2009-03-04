package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.springframework.stereotype.Repository;

@Repository("addressDao")
public class AddressDaoJpa implements AddressDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<Address> readAddressByUserId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ADDRESS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public Address readAddressByUserIdAndName(Long customerId, String addressName) {
        Query query = em.createNamedQuery("BC_sREAD_ADDRESS_BY_CUSTOMER_ID_AND_NAME");
        query.setParameter("customerId", customerId);
        query.setParameter("addressName", addressName);
        return (Address) query.getSingleResult();
    }

    public Address maintainAddress(Address address) {
        if (address.getId() == null) {
            em.persist(address);
        } else {
            address = em.merge(address);
        }
        return address;
    }

    public Address readAddressById(Long addressId) {
        Query query = em.createNamedQuery("BC_READ_ADDRESS_BY_ID");
        query.setParameter("addressId", addressId);
        return (Address) query.getSingleResult();
    }
}
