/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.profile.web.core.expression;

import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.CustomerPaymentService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;


/**
 * This Thymeleaf variable expression class serves to expose elements from the BroadleafRequestContext
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Service("blCustomerVariableExpression")
@ConditionalOnTemplating
public class CustomerVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Resource(name = "blCustomerPaymentService")
    protected CustomerPaymentService customerPaymentService;

    @Autowired
    protected Environment env;
    
    @Override
    public String getName() {
        return "customer";
    }
    
    public Customer getCurrent() {
        return CustomerState.getCustomer();
    }

    public List<CustomerAddress> getCustomerAddresses() {
        Customer customer = CustomerState.getCustomer();

        return customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId());
    }

    public boolean savedPaymentsAreEnabled() {
        return env.getProperty("saved.customer.payments.enabled", boolean.class, true);
    }

    public List<CustomerPayment> getCustomerPayments() {
        Customer customer = CustomerState.getCustomer();
        List<CustomerPayment> customerPayments = new ArrayList<>();

        if (savedPaymentsAreEnabled()) {
            customerPayments = customerPaymentService.readCustomerPaymentsByCustomerId(customer.getId());
            sortCustomerPaymentsByDefault(customerPayments);
        }

        return customerPayments;
    }

    protected void sortCustomerPaymentsByDefault(List<CustomerPayment> savedPayments) {
        Collections.sort(savedPayments, new Comparator<CustomerPayment>() {
            @Override
            public int compare(CustomerPayment sp1, CustomerPayment sp2) {
                if (sp1.isDefault()) {
                    return -1;
                } else if (sp2.isDefault()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }
    
}
