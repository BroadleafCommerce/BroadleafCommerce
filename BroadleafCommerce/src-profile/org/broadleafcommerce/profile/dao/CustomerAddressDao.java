package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.CustomerAddress;

public interface CustomerAddressDao {

    public List<CustomerAddress> readActiveCustomerAddressesByCustomerId(Long customerId);

    public CustomerAddress maintainCustomerAddress(CustomerAddress customerAddress);

    public CustomerAddress readCustomerAddressByIdAndCustomerId(Long customerAddressId, Long customerId);

    public void makeCustomerAddressDefault(Long customerAddressId, Long customerId);

    public void deleteCustomerAddressByIdAndCustomerId(Long customerAddressId, Long customerId);
}
