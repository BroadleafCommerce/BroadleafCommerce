package org.broadleafcommerce.profile.dao;

import org.broadleafcommerce.profile.domain.Address;

public interface AddressDao {

    public Address maintainAddress(Address address);

    public Address readAddressById(Long addressId);
}
