/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.profile.web.core.service;

import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPhone;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.broadleafcommerce.profile.core.service.CustomerPhoneService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.dataprovider.CustomerPhoneDataProvider;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

public class CustomerPhoneTest extends TestNGSiteIntegrationSetup {

    List<Long> customerPhoneIds = new ArrayList<>();
    String userName = new String();
    Long userId;

    @Resource
    private CustomerPhoneService customerPhoneService;

    @Resource
    private CustomerService customerService;

    @Test(groups = "createCustomerPhone", dataProvider = "setupCustomerPhone", dataProviderClass = CustomerPhoneDataProvider.class, dependsOnGroups = "readCustomer")
    @Transactional
    @Rollback(false)
    public void createCustomerPhone(CustomerPhone customerPhone) {
        userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        assert customerPhone.getId() == null;
        customerPhone.setCustomer(customer);
        Phone phone = new PhoneImpl();
        phone.setPhoneNumber("214-214-2134");
        customerPhone.setPhone(phone);
        customerPhone = customerPhoneService.saveCustomerPhone(customerPhone);
        assert customer.equals(customerPhone.getCustomer());
        userId = customerPhone.getCustomer().getId();
    }

    @Test(groups = "readCustomerPhone", dependsOnGroups = "createCustomerPhone")
    @Transactional
    public void readCustomerPhoneByUserId() {
        List<CustomerPhone> customerPhoneList = customerPhoneService.readActiveCustomerPhonesByCustomerId(userId);
        for (CustomerPhone customerPhone : customerPhoneList) {
            assert customerPhone != null;
        }
    }
    
    @Test(groups = "readCustomerPhone", dependsOnGroups = "createCustomerPhone")
    @Transactional
    public void readDeafultCustomerPhoneByUserId() {
        CustomerPhone customerPhone = customerPhoneService.findDefaultCustomerPhone(userId);
        assert customerPhone != null;
    }
}
