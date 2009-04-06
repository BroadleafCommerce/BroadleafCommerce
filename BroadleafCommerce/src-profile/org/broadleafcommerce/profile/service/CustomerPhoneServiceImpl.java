package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.CustomerPhoneDao;
import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("customerPhoneService")
public class CustomerPhoneServiceImpl implements CustomerPhoneService {

    @Resource
    private CustomerPhoneDao customerPhoneDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerPhone saveCustomerPhone(CustomerPhone customerPhone) {
        // if parameter phone is set as default, unset all other default phones
        List<CustomerPhone> activeCustomerPhones = readActiveCustomerPhonesByCustomerId(customerPhone.getCustomerId());
        if (activeCustomerPhones.size() == 0) {
            customerPhone.getPhone().setDefault(true);
        } else {
            if (customerPhone.getPhone().isDefault()) {
                for (CustomerPhone activeCustomerPhone : activeCustomerPhones) {
                    if (activeCustomerPhone.getId() != customerPhone.getId() && activeCustomerPhone.getPhone().isDefault()) {
                        activeCustomerPhone.getPhone().setDefault(false);
                        customerPhoneDao.maintainCustomerPhone(activeCustomerPhone);
                    }
                }
            }
        }
        return customerPhoneDao.maintainCustomerPhone(customerPhone);
    }

    public List<CustomerPhone> readActiveCustomerPhonesByCustomerId(Long customerId) {
        return customerPhoneDao.readActiveCustomerPhonesByCustomerId(customerId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerPhone readCustomerPhoneByIdAndCustomerId(Long customerPhoneId, Long customerId) {
        return customerPhoneDao.readCustomerPhoneByIdAndCustomerId(customerPhoneId, customerId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void makeCustomerPhoneDefault(Long customerPhoneId, Long customerId) {
        customerPhoneDao.makeCustomerPhoneDefault(customerPhoneId, customerId);
    }

    public void deleteCustomerPhoneByIdAndCustomerId(Long customerPhoneId, Long customerId){
        customerPhoneDao.deleteCustomerPhoneByIdAndCustomerId(customerPhoneId, customerId);
    }
}