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

import org.broadleafcommerce.common.id.domain.IdGeneration;
import org.broadleafcommerce.common.id.domain.IdGenerationImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.dataprovider.CustomerDataProvider;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class CustomerTest extends TestNGSiteIntegrationSetup {
    
    @Resource
    private CustomerService customerService;
    
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    List<Long> userIds = new ArrayList<>();

    List<String> userNames = new ArrayList<>();

    @Test(groups = { "createCustomerIdGeneration" })
    @Commit
    @Transactional
    public void createCustomerIdGeneration() {
        IdGenerationImpl gen = em.find(IdGenerationImpl.class, "org.broadleafcommerce.profile.core.domain.Customer");
        if (gen == null) {
            IdGeneration idGeneration = new IdGenerationImpl();
            idGeneration.setType("org.broadleafcommerce.profile.core.domain.Customer");
            idGeneration.setBatchStart(1L);
            idGeneration.setBatchSize(10L);
            em.persist(idGeneration);
        }
    }

    @Test(groups = "createCustomers", dependsOnGroups="createCustomerIdGeneration", dataProvider = "setupCustomers", dataProviderClass = CustomerDataProvider.class)
    @Rollback(false)
    public void createCustomer(Customer customerInfo) {
        Customer customer = customerService.createCustomerFromId(null);
        customer.setPassword(customerInfo.getPassword());
        customer.setUsername(customerInfo.getUsername());
        Long customerId = customer.getId();
        assert customerId != null;
        customer = customerService.saveCustomer(customer);
        assert customer.getId() == customerId;
        userIds.add(customer.getId());
        userNames.add(customer.getUsername());
    }

    @Test(groups = { "readCustomer" }, dependsOnGroups = { "createCustomers", "createCustomerIdGeneration" })
    public void readCustomersById() {
        for (Long userId : userIds) {
            Customer customer = customerService.readCustomerById(userId);
            assert customer.getId() == userId;
        }
    }

    /*@Test(groups = { "readCustomer1" }, dependsOnGroups = { "createCustomers" })
    public void readCustomersByUsername1() {
        for (String userName : userNames) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            assert userDetails != null && userDetails.getPassword().equals(userDetails.getUsername() + "Password");
        }
    }*/

    @Test(groups = { "changeCustomerPassword" }, dependsOnGroups = { "readCustomer" })
    @Transactional
    @Commit
    public void changeCustomerPasswords() {
        for (String userName : userNames) {
            Customer customer = customerService.readCustomerByUsername(userName);
            customer.setPassword(customer.getPassword() + "-Changed");
            customerService.saveCustomer(customer);
        }
    }

    /*@Test(groups = { "readCustomer2" }, dependsOnGroups = { "changeCustomerPassword" })
    public void readCustomersByUsername2() {
        for (String userName : userNames) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            assert userDetails != null && userDetails.getPassword().equals(userDetails.getUsername() + "Password-Changed");
        }
    }*/
}
