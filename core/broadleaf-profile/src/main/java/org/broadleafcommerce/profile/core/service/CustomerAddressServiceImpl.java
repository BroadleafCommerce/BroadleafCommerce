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

import org.broadleafcommerce.profile.core.dao.CustomerAddressDao;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("blCustomerAddressService")
public class CustomerAddressServiceImpl implements CustomerAddressService {

    @Resource(name="blCustomerAddressDao")
    protected CustomerAddressDao customerAddressDao;

    public CustomerAddress saveCustomerAddress(CustomerAddress customerAddress) {
        // if parameter address is set as default, unset all other default addresses
        List<CustomerAddress> activeCustomerAddresses = readActiveCustomerAddressesByCustomerId(customerAddress.getCustomer().getId());
        if (activeCustomerAddresses != null && activeCustomerAddresses.isEmpty()) {
            customerAddress.getAddress().setDefault(true);
        } else {
            if (customerAddress.getAddress().isDefault()) {
                for (CustomerAddress activeCustomerAddress : activeCustomerAddresses) {
                    if (activeCustomerAddress.getId() != customerAddress.getId() && activeCustomerAddress.getAddress().isDefault()) {
                        activeCustomerAddress.getAddress().setDefault(false);
                        customerAddressDao.save(activeCustomerAddress);
                    }
                }
            }
        }
        return customerAddressDao.save(customerAddress);
    }

    public List<CustomerAddress> readActiveCustomerAddressesByCustomerId(Long customerId) {
        return customerAddressDao.readActiveCustomerAddressesByCustomerId(customerId);
    }

    public CustomerAddress readCustomerAddressById(Long customerAddressId) {
        return customerAddressDao.readCustomerAddressById(customerAddressId);
    }

    public void makeCustomerAddressDefault(Long customerAddressId, Long customerId) {
        customerAddressDao.makeCustomerAddressDefault(customerAddressId, customerId);
    }

    public void deleteCustomerAddressById(Long customerAddressId){
        customerAddressDao.deleteCustomerAddressById(customerAddressId);
    }

    public CustomerAddress findDefaultCustomerAddress(Long customerId) {
        return customerAddressDao.findDefaultCustomerAddress(customerId);
    }

    public CustomerAddress create() {
        return customerAddressDao.create();
    }
}
