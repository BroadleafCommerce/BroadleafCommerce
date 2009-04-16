package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.CustomerPhone;

public interface CustomerPhoneService {

    public CustomerPhone saveCustomerPhone(CustomerPhone customerPhone);

    public List<CustomerPhone> readActiveCustomerPhonesByCustomerId(Long customerId);

    public CustomerPhone readCustomerPhoneByIdAndCustomerId(Long customerPhoneId, Long customerId);

    public void makeCustomerPhoneDefault(Long customerPhoneId, Long customerId);

    public void deleteCustomerPhoneByIdAndCustomerId(Long customerPhoneId, Long customerId);

    public CustomerPhone findDefaultCustomerPhone(Long customerId);
}