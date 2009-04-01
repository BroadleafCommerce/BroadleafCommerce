package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.CustomerAddress;

public interface CustomerAddressService {

    public CustomerAddress saveCustomerAddress(CustomerAddress customerAddress);

    public List<CustomerAddress> readActiveCustomerAddressesByCustomerId(Long customerId);

    public CustomerAddress readCustomerAddressById(Long customerAddressId);

    public void makeCustomerAddressDefault(Long customerAddressId, Long customerId);

    public void deleteCustomerAddressByIdAndCustomerId(Long customerAddressId, Long customerId);
}