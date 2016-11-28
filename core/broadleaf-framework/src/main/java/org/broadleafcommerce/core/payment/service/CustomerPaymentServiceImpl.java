/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.core.order.domain.OrderCustomer;
import org.broadleafcommerce.core.order.service.OrderCustomerService;
import org.broadleafcommerce.core.payment.dao.CustomerPaymentDao;
import org.broadleafcommerce.core.payment.domain.CustomerPayment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

@Service("blCustomerPaymentService")
public class CustomerPaymentServiceImpl implements CustomerPaymentService {

    /** Services */
    @Resource(name="blCustomerPaymentDao")
    protected CustomerPaymentDao customerPaymentDao;

    @Resource(name="blCustomerService")
    protected OrderCustomerService orderCustomerService;

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

    @Override
    public CustomerPayment findDefaultPaymentForCustomer(OrderCustomer customer) {
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
        CustomerPayment oldDefault = findDefaultPaymentForCustomer(payment.getOrderCustomer());
        if (oldDefault != null) {
            oldDefault.setIsDefault(false);
            saveCustomerPayment(oldDefault);
        }
        payment.setIsDefault(true);
        return saveCustomerPayment(payment);
    }

    @Override
    @Transactional("blTransactionManager")
    public OrderCustomer deleteCustomerPaymentFromCustomer(OrderCustomer customer, CustomerPayment payment) {
        List<CustomerPayment> payments = customer.getCustomerPayments();
        for (CustomerPayment customerPayment : payments) {
            if (customerPayment.getId().equals(payment.getId())) {
                customer.getCustomerPayments().remove(customerPayment);
                break;
            }
        }
       return orderCustomerService.save(customer);
    }

}
