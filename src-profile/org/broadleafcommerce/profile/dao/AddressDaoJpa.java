package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Country;
import org.broadleafcommerce.profile.domain.StateProvince;
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

    @SuppressWarnings("unchecked")
    public Address readAddressByIdAndCustomerId(Long addressId, Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ADDRESS_BY_ID_AND_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        query.setParameter("addressId", addressId);
        List<Address> addresses = query.getResultList();
        return addresses.isEmpty() ? null : addresses.get(0);
    }

    public void makeAddressDefault(Long addressId, Long customerId) {
        List<Address> addresses = readActiveAddressesByCustomerId(customerId);
        for (Address address : addresses) {
            address.setDefault(address.getId().equals(addressId));
            em.merge(address);
        }
    }

    @SuppressWarnings("unchecked")
    public StateProvince findStateProvinceByShortName(String shortName) {
        return (StateProvince) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.StateProvince"), shortName);
    }

    @SuppressWarnings("unchecked")
    public List<StateProvince> findStateProvinces() {
        Query query = em.createNamedQuery("BC_FIND_STATE_PROVINCES");
        query.setHint("org.hibernate.cacheable", true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Country findCountryByShortName(String shortName) {
        return (Country) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Country"), shortName);
    }

    @SuppressWarnings("unchecked")
    public List<Country> findCountries() {
        Query query = em.createNamedQuery("BC_FIND_COUNTRIES");
        query.setHint("org.hibernate.cacheable", true);
        return query.getResultList();
    }

    @Override
    public void deleteAddressByIdAndCustomerId(Long addressId, Long customerId) {
        // TODO: determine if hard delete or deactivate, and consider throwing exception if read fails
        Address address = readAddressByIdAndCustomerId(addressId, customerId);
        em.remove(address.getId());
    }
}
