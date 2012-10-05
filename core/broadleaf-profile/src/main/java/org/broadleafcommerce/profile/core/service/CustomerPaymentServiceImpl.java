/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.dao.CustomerPaymentDao;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("blCustomerPaymentService")
public class CustomerPaymentServiceImpl implements CustomerPaymentService {

    @Resource(name="blCustomerPaymentDao")
    protected CustomerPaymentDao customerPaymentDao;

    @Override
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
    public void deleteCustomerPaymentById(Long customerPaymentId) {
        customerPaymentDao.deleteCustomerPaymentById(customerPaymentId);
    }

    @Override
    public CustomerPayment create() {
        return customerPaymentDao.create();
    }

}
