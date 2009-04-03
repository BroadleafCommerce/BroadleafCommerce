package org.broadleafcommerce.profile.service;

import org.broadleafcommerce.profile.domain.Address;

public interface AddressService {

    public Address saveAddress(Address address);

    public Address readAddressById(Long addressId);
}