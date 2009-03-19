package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.StateProvince;

public interface AddressDao {

    public List<Address> readActiveAddressesByCustomerId(Long userId);

    public Address maintainAddress(Address address);

    public Address readAddressById(Long addressId);

    public void makeAddressDefault(Long addressId, Long customerId);

    public List<StateProvince> findStateProvinces();

    public StateProvince findStateProvinceByAbbreviation(String abbreviation);
}
