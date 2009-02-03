package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.Address;

public interface AddressDao {
    public List<Address> readAddressByUserId(Long userId);
    
    public Address readAddressByUserIdAndName(Long userId, String addressName);
    
    public Address maintainAddress(Address address);
    
    public Address readAddressById(Long addressId);
}
