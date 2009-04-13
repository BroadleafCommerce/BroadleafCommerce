package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.CustomerAddressDao;
import org.broadleafcommerce.profile.domain.CustomerAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("customerAddressService")
public class CustomerAddressServiceImpl implements CustomerAddressService {

    @Resource
    private CustomerAddressDao customerAddressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddress saveCustomerAddress(CustomerAddress customerAddress) {
        // if parameter address is set as default, unset all other default addresses
        List<CustomerAddress> activeCustomerAddresses = readActiveCustomerAddressesByCustomerId(customerAddress.getCustomerId());
        if (activeCustomerAddresses.size() == 0) {
            customerAddress.getAddress().setDefault(true);
        } else {
            if (customerAddress.getAddress().isDefault()) {
                for (CustomerAddress activeCustomerAddress : activeCustomerAddresses) {
                    if (activeCustomerAddress.getId() != customerAddress.getId() && activeCustomerAddress.getAddress().isDefault()) {
                        activeCustomerAddress.getAddress().setDefault(false);
                        customerAddressDao.maintainCustomerAddress(activeCustomerAddress);
                    }
                }
            }
        }
        return customerAddressDao.maintainCustomerAddress(customerAddress);
    }

    public List<CustomerAddress> readActiveCustomerAddressesByCustomerId(Long customerId) {
        return customerAddressDao.readActiveCustomerAddressesByCustomerId(customerId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddress readCustomerAddressByIdAndCustomerId(Long customerAddressId, Long customerId) {
        return customerAddressDao.readCustomerAddressByIdAndCustomerId(customerAddressId, customerId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void makeCustomerAddressDefault(Long customerAddressId, Long customerId) {
        customerAddressDao.makeCustomerAddressDefault(customerAddressId, customerId);
    }

    public void deleteCustomerAddressByIdAndCustomerId(Long customerAddressId, Long customerId){
        customerAddressDao.deleteCustomerAddressByIdAndCustomerId(customerAddressId, customerId);
    }

    @Override
    public CustomerAddress findDefaultCustomerAddress(Long customerId) {
        return customerAddressDao.findDefaultCustomerAddress(customerId);
    }
}