/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.dao.CustomerPaymentDao;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("blCustomerPaymentService")
public class CustomerPaymentServiceImpl implements CustomerPaymentService {

    /** Services */
    @Resource(name="blCustomerPaymentDao")
    protected CustomerPaymentDao customerPaymentDao;

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    @Override
    @Transactional("blTransactionManager")
    public CustomerPayment saveCustomerPayment(CustomerPayment customerPayment) {
        return customerPaymentDao.save(customerPayment);
    }

    @Override
    public List<CustomerPayment> readCustomerPaymentsByCustomerId(Long customerId) {
        return customerPaymentDao.readCustomerPaymentsByCustomerId(customerId);
    }

    @Override
    public CustomerPayment readCustomerPaymentById(Long customerPaymentId) {
        return customerPaymentDao.readCustomerPaymentById(customerPaymentId);
    }

    @Override
    public CustomerPayment readCustomerPaymentByToken(String token) {
        return customerPaymentDao.readCustomerPaymentByToken(token);
    }

    @Override
    @Transactional("blTransactionManager")
    public void deleteCustomerPaymentById(Long customerPaymentId) {
        customerPaymentDao.deleteCustomerPaymentById(customerPaymentId);
    }

    @Override
    @Transactional("blTransactionManager")
    public CustomerPayment create() {
        return customerPaymentDao.create();
    }

    public CustomerPayment findDefaultPaymentForCustomer(Customer customer) {
        if (customer == null) { return null; }
        List<CustomerPayment> payments = readCustomerPaymentsByCustomerId(customer.getId());
        for (CustomerPayment payment : payments) {
            if (payment.isDefault()) {
                return payment;
            }
        }
        return null;
    }

    @Override
    @Transactional("blTransactionManager")
    public CustomerPayment setAsDefaultPayment(CustomerPayment payment) {
        CustomerPayment oldDefault = findDefaultPaymentForCustomer(payment.getCustomer());
        if (oldDefault != null) {
            oldDefault.setIsDefault(false);
            saveCustomerPayment(oldDefault);
        }
        payment.setIsDefault(true);
        return saveCustomerPayment(payment);
    }

    @Override
    @Transactional("blTransactionManager")
    public Customer deleteCustomerPaymentFromCustomer(Customer customer, CustomerPayment payment) {
        List<CustomerPayment> payments = customer.getCustomerPayments();
        for (CustomerPayment customerPayment : payments) {
            if (customerPayment.getId().equals(payment.getId())) {
                customer.getCustomerPayments().remove(customerPayment);
                break;
            }
        }
       return customerService.saveCustomer(customer);
    }

}
