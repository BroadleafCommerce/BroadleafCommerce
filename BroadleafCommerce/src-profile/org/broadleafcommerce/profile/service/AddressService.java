package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.StateProvince;

public interface AddressService {

    public Address saveAddress(Address address);

    public List<Address> readActiveAddressesByCustomerId(Long customerId);

    public Address readAddressById(Long addressId);

    public void makeAddressDefault(Long addressId, Long customerId);

    public StateProvince findStateProvinces();

    public StateProvince findStateProvinceByAbbreviation(String abbreviation);
}