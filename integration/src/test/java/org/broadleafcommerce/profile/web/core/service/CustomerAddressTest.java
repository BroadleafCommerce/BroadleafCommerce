/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.profile.web.core.service;

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerAddressImpl;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.dataprovider.CustomerAddressDataProvider;
import org.broadleafcommerce.test.CommonSetupBaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.List;

public class CustomerAddressTest extends CommonSetupBaseTest {

    private String userName;
    private Long userId;
    
    @Resource
    private CustomerAddressService customerAddressService;

    @Test(groups = "testCustomerAddress")
    @Transactional
    public void readCustomerAddresses() {
        Customer customer = createCustomerWithAddresses();
        List<CustomerAddress> customerAddressList = customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId());
        for (CustomerAddress ca : customerAddressList) {
            assert ca != null;
        }
    }
    
    @Test(groups = "testCustomerAddress")
    @Transactional
    public void createNewDefaultAddress() {
        Customer customer = createCustomerWithAddresses();
        CustomerAddress ca = new CustomerAddressImpl();
        Address address = new AddressImpl();
        address.setAddressLine1("123 Main");
        address.setCity("Dallas");
        address.setPostalCode("75201");
        address.setDefault(true);
        ca.setAddress(address);
        ca.setCustomer(customer);
        ca.setAddressName("address3");
        CustomerAddress savedAddress = saveCustomerAddress(ca);
        
        List<CustomerAddress> customerAddressList = customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId());
        for (CustomerAddress customerAddress : customerAddressList) {
            if (customerAddress.getId().equals(savedAddress.getId())) {
                assert customerAddress.getAddress().isDefault();
            } else {
                assert !customerAddress.getAddress().isDefault();
            }
        }
    }
    
    
    /**
     * This method only exists because so many other tests depend on it, but should be removed once tests are more isolated
     * @param customerAddress
     */
    @Deprecated
    @Test(groups = "createCustomerAddress", dataProvider = "setupCustomerAddress", dataProviderClass = CustomerAddressDataProvider.class, dependsOnGroups = {"readCustomer", "createCountry", "createState"})
    @Transactional
    @Rollback(false)
    public void createCustomerAddress(CustomerAddress customerAddress) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        assert customerAddress.getId() == null;
        customerAddress.setCustomer(customer);
        State state = stateService.findStateByAbbreviation("KY");
        customerAddress.getAddress().setState(state);
        Country country = countryService.findCountryByAbbreviation("US");
        customerAddress.getAddress().setCountry(country);
        customerAddress.getAddress().setIsoCountrySubdivision("US-KY");
        ISOCountry isoCountry = isoService.findISOCountryByAlpha2Code("US");
        customerAddress.getAddress().setIsoCountryAlpha2(isoCountry);

        customerAddress = customerAddressService.saveCustomerAddress(customerAddress);
        assert customer.equals(customerAddress.getCustomer());
        userId = customerAddress.getCustomer().getId();
    }

    /**
     * TThis method only exists because so many other tests depend on it, but should be removed once tests are more isolated
     */
    @Deprecated
    @Test(groups = "readCustomerAddress", dependsOnGroups = "createCustomerAddress")
    @Transactional
    public void readCustomerAddressByUserId() {
        List<CustomerAddress> customerAddressList = customerAddressService.readActiveCustomerAddressesByCustomerId(userId);
        for (CustomerAddress customerAddress : customerAddressList) {
            assert customerAddress != null;
        }
    }
    
}
