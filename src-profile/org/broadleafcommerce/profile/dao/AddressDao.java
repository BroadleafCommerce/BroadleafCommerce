package org.broadleafcommerce.profile.dao;

import org.broadleafcommerce.profile.domain.Address;

public interface AddressDao {

    public Address save(Address address);

    public Address readAddressById(Long addressId);
}
