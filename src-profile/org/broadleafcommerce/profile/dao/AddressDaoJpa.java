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

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<Address> readAddressByUserId(Long userId) {
        Query query = em.createQuery("SELECT address FROM org.broadleafcommerce.profile.domain.Address address WHERE address.user.id = :userId ORDER BY address.id");
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public Address readAddressByUserIdAndName(Long userId, String addressName) {
        Query query = em.createQuery("SELECT address FROM org.broadleafcommerce.profile.domain.Address address WHERE address.user.id = :userId AND address.addressName = :addressName");
        query.setParameter("userId", userId);
        query.setParameter("addressName", addressName);
        return (Address)query.getSingleResult();
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
        Query query = em.createQuery("SELECT address FROM org.broadleafcommerce.profile.domain.Address address WHERE address.id = :addressId");
        query.setParameter("addressId", addressId);
        return (Address)query.getSingleResult();
    }
}
