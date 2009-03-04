package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.domain.Address;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("addressServiceImpl")
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressDao addressDao;

    public Address saveAddress(Address address) {
        return addressDao.maintainAddress(address);
    }

    public List<Address> readAddressByUserId(Long userId) {
        return addressDao.readAddressByUserId(userId);
    }

    public Address readAddressById(Long addressId) {
        return addressDao.readAddressById(addressId);
    }

    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
}
