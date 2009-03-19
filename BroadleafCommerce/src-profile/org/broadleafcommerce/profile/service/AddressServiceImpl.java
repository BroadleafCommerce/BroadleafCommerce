package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.StateProvince;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressDao addressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Address saveAddress(Address address) {
        // if parameter address is set as default, unset all other default addresses
        List<Address> activeAddresses = readActiveAddressesByCustomerId(address.getCustomer().getId());
        if (activeAddresses.size() == 0) {
            address.setDefault(true);
        } else {
            if (address.isDefault()) {
                for (Address activeAddress : activeAddresses) {
                    if (activeAddress.getId() != address.getId() && activeAddress.isDefault()) {
                        activeAddress.setDefault(false);
                        addressDao.maintainAddress(activeAddress);
                    }
                }
            }
        }
        return addressDao.maintainAddress(address);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Address> readActiveAddressesByCustomerId(Long customerId) {
        return addressDao.readActiveAddressesByCustomerId(customerId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Address readAddressById(Long addressId) {
        return addressDao.readAddressById(addressId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void makeAddressDefault(Long addressId, Long customerId) {
        addressDao.makeAddressDefault(addressId, customerId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StateProvince findStateProvinces() {
        return addressDao.findStateProvinces();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StateProvince findStateProvinceByAbbreviation(String abbreviation) {
        return addressDao.findStateProvinceByAbbreviation(abbreviation);
    }
}