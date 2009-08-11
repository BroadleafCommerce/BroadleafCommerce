/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.stereotype.Component;

@Component("blCustomerState")
public class CustomerState {

    private static final String DEFAULTSESSIONATTRIBUTENAME = "customer_session";
    private static final String SERIALIZEDSESSIONATTRIBUTENAME = "customer_session_serialized";

    @Resource(name="blCustomerService")
    private CustomerService customerService;

    public Customer getCustomer(HttpServletRequest request) {
        Object sessionReference = request.getSession().getAttribute(DEFAULTSESSIONATTRIBUTENAME);
        Customer customer;
        checkCustomer: {
            if (sessionReference instanceof Long) {
                Long customerId = (Long) sessionReference;
                if (customerId != null) {
                    customer = customerService.readCustomerById(customerId);
                    if (customer == null) {
                        customer = (Customer) request.getSession().getAttribute(SERIALIZEDSESSIONATTRIBUTENAME);
                        if (customer != null) {
                            break checkCustomer;
                        }
                    } else {
                        break checkCustomer;
                    }
                    customer = customerService.createCustomerFromId(customerId);
                    break checkCustomer;
                }
            }
            customer = null;
        }

        return customer;
    }

    public void setCustomer(Customer customer, HttpServletRequest request) {
        request.getSession().setAttribute(DEFAULTSESSIONATTRIBUTENAME, customer.getId());
        if (customerService.readCustomerById(customer.getId()) != null) {
            request.getSession().removeAttribute(SERIALIZEDSESSIONATTRIBUTENAME);
        } else {
            request.getSession().setAttribute(SERIALIZEDSESSIONATTRIBUTENAME, customer);
        }
    }

    public Long getCustomerId(HttpServletRequest request) {
        return getCustomer(request) == null ? null : getCustomer(request).getId();
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }
}
