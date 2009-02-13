package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.domain.Address;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class AddressServiceImpl implements AddressService {

    @Resource(name = "addressDao")
    private AddressDao addressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Address saveAddress(Address address) {
        return addressDao.maintainAddress(address);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Address> readAddressByUserId(Long userId) {
        return addressDao.readAddressByUserId(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Address readAddressById(Long addressId) {
        return addressDao.readAddressById(addressId);
    }
}
