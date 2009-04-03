package org.broadleafcommerce.profile.service;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.domain.Address;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressDao addressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Address saveAddress(Address address) {
        return addressDao.maintainAddress(address);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Address readAddressById(Long addressId) {
        return addressDao.readAddressById(addressId);
    }
}