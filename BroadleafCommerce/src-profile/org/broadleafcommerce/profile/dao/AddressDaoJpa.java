package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("addressDao")
public class AddressDaoJpa implements AddressDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @SuppressWarnings("unchecked")
    public List<Address> readActiveAddressesByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ACTIVE_ADDRESSES_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public Address maintainAddress(Address address) {
        if (address.getId() == null) {
            em.persist(address);
        } else {
            address = em.merge(address);
        }
        return address;
    }

    @SuppressWarnings("unchecked")
    public Address readAddressById(Long id) {
        return (Address) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Address"), id);
    }

    public void makeAddressDefault(Long addressId, Long customerId) {
        List<Address> addresses = readActiveAddressesByCustomerId(customerId);
        for (Address address : addresses) {
            address.setDefault(address.getId().equals(addressId));
            em.merge(address);
        }
    }
}
