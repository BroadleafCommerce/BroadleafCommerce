/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;

import java.util.List;

public interface CustomerPaymentService {

    public CustomerPayment saveCustomerPayment(CustomerPayment customerPayment);

    public List<CustomerPayment> readCustomerPaymentsByCustomerId(Long customerId);

    public CustomerPayment readCustomerPaymentById(Long customerPaymentId);

    public CustomerPayment readCustomerPaymentByToken(String token);

    public void deleteCustomerPaymentById(Long customerPaymentId);

    public CustomerPayment create();

    public CustomerPayment findDefaultPaymentForCustomer(Customer customer);

    public CustomerPayment setAsDefaultPayment(CustomerPayment payment);

    public Customer deleteCustomerPaymentFromCustomer(Customer customer, CustomerPayment payment);

}
