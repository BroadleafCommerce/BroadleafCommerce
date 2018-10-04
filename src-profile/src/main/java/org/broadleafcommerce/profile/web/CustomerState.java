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
import org.broadleafcommerce.util.BLCRequestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component("blCustomerState")
public class CustomerState {

    private static final String DEFAULTSESSIONATTRIBUTENAME = "customer_session";
    private static final String SERIALIZEDSESSIONATTRIBUTENAME = "customer_session_serialized";
    private static final String SERIALIZEDREQUESTATTRIBUTENAME = "customer_request_serialized";

    @Resource(name="blCustomerService")
    private CustomerService customerService;

    public Customer getCustomer(HttpServletRequest request) {
        Object sessionReference = request.getSession().getAttribute(DEFAULTSESSIONATTRIBUTENAME);
        Customer customer;
        checkCustomer: {
            if (sessionReference instanceof Long) {
                Long customerId = (Long) sessionReference;
                if (customerId != null) {
                    customer = (Customer) request.getAttribute(SERIALIZEDREQUESTATTRIBUTENAME);
                    if (customer == null) {
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
                    } else {
                        break checkCustomer;
                    }
                }
            }
            customer = null;
        }
        if (customer != null) {
            request.setAttribute(SERIALIZEDREQUESTATTRIBUTENAME, customer);
        }
        return customer;
    }

    public Customer getCustomer(WebRequest request) {
        Object sessionReference = request.getAttribute(DEFAULTSESSIONATTRIBUTENAME, WebRequest.SCOPE_REQUEST);
        Customer customer;
        checkCustomer: {
            if (sessionReference instanceof Long) {
                Long customerId = (Long) sessionReference;
                if (customerId != null) {
                    customer = (Customer) request.getAttribute(SERIALIZEDREQUESTATTRIBUTENAME, WebRequest.SCOPE_REQUEST);
                    if (customer == null) {
                        customer = customerService.readCustomerById(customerId);
                        if (customer == null) {
                            customer = (Customer) request.getAttribute(SERIALIZEDSESSIONATTRIBUTENAME, WebRequest.SCOPE_REQUEST);
                            if (customer != null) {
                                break checkCustomer;
                            }
                        } else {
                            break checkCustomer;
                        }
                        customer = customerService.createCustomerFromId(customerId);
                        break checkCustomer;
                    } else {
                        break checkCustomer;
                    }
                }
            }
            customer = null;
        }
        if (customer != null) {
            request.setAttribute(SERIALIZEDREQUESTATTRIBUTENAME, customer, WebRequest.SCOPE_REQUEST);
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

    public Customer getAnonymousCustomer(WebRequest request) {
        if (BLCRequestUtils.isOKtoUseSession(request)) {
            Customer anonymousCustomer = (Customer) request.getAttribute("_blc_anonymousCustomer",
                                                                         WebRequest.SCOPE_GLOBAL_SESSION);
            if (anonymousCustomer == null) {
                //Customer is not in session, see if we have just a customer ID in session (the anonymous customer might have
                //already been persisted)
                Long customerId = (Long) request.getAttribute("_blc_anonymousCustomerId", WebRequest.SCOPE_GLOBAL_SESSION);
                if (customerId != null) {
                    //we have a customer ID in session, look up the customer from the database to ensure we have an up-to-date
                    //customer to store in CustomerState
                    anonymousCustomer = customerService.readCustomerById(customerId);
                }
            }
            return anonymousCustomer;
        }
        return null;
    }

}
