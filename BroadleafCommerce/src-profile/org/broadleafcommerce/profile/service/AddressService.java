package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.Address;

public interface AddressService {

    public Address saveAddress(Address address);

    public List<Address> readAddressByUserId(Long userId);

    public Address readAddressById(Long addressId);
}