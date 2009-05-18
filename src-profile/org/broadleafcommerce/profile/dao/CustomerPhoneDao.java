package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.CustomerPhone;

public interface CustomerPhoneDao {

    public List<CustomerPhone> readActiveCustomerPhonesByCustomerId(Long customerId);

    public CustomerPhone save(CustomerPhone customerPhone);

    public CustomerPhone readCustomerPhoneByIdAndCustomerId(Long customerPhoneId, Long customerId);

    public void makeCustomerPhoneDefault(Long customerPhoneId, Long customerId);

    public void deleteCustomerPhoneByIdAndCustomerId(Long customerPhoneId, Long customerId);

    public CustomerPhone findDefaultCustomerPhone(Long customerId);

    public List<CustomerPhone> readAllCustomerPhonesByCustomerId(Long customerId);
}
