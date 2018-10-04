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
package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.domain.Country;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerAddress;
import org.broadleafcommerce.profile.service.CountryService;
import org.broadleafcommerce.profile.service.CustomerAddressService;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.test.dataprovider.CustomerAddressDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class CustomerAddressTest extends BaseTest {

    List<Long> customerAddressIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;

    @Resource
    private CustomerAddressService customerAddressService;

    @Resource
    private CustomerService customerService;

    @Resource
    private CountryService countryService;

    @Test(groups = "createCustomerAddress", dataProvider = "setupCustomerAddress", dataProviderClass = CustomerAddressDataProvider.class, dependsOnGroups = {"readCustomer1", "createCountry"})
    @Rollback(false)
    public void createCustomerAddress(CustomerAddress customerAddress) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        assert customerAddress.getId() == null;
        customerAddress.setCustomer(customer);
        Country country = countryService.findCountryByAbbreviation("US");
        customerAddress.getAddress().getState().setCountry(country);
        customerAddress.getAddress().setCountry(country);
        customerAddress = customerAddressService.saveCustomerAddress(customerAddress);
        assert customer.equals(customerAddress.getCustomer());
        userId = customerAddress.getCustomer().getId();
    }

    @Test(groups = "readCustomerAddress", dependsOnGroups = "createCustomerAddress")
    public void readCustomerAddressByUserId() {
        List<CustomerAddress> customerAddressList = customerAddressService.readActiveCustomerAddressesByCustomerId(userId);
        for (CustomerAddress customerAddress : customerAddressList) {
            assert customerAddress != null;
        }
    }
}
