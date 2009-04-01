package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Country;
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
        return addressDao.maintainAddress(address);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Address readAddressById(Long addressId) {
        return addressDao.readAddressById(addressId);
    }

    public List<StateProvince> findStateProvinces() {
        return addressDao.findStateProvinces();
    }

    public StateProvince findStateProvinceByShortName(String shortName) {
        return addressDao.findStateProvinceByShortName(shortName);
    }

    public List<Country> findCountries() {
        return addressDao.findCountries();
    }

    public Country findCountryByShortName(String shortName) {
        return addressDao.findCountryByShortName(shortName);
    }
}