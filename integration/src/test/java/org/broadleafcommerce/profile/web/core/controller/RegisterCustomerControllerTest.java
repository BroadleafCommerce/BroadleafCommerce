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
package org.broadleafcommerce.profile.web.core.controller;

import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.controller.RegisterCustomerController;
import org.broadleafcommerce.profile.web.core.controller.dataprovider.RegisterCustomerDataProvider;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import javax.annotation.Resource;

public class RegisterCustomerControllerTest extends TestNGSiteIntegrationSetup {

    @Resource
    private RegisterCustomerController registerCustomerController;

    @Resource
    private CustomerService customerService;

    private GreenMail greenMail;

    @BeforeClass
    protected void setupControllerTest() {
        greenMail = new GreenMail(
                new ServerSetup[] {
                        new ServerSetup(30000, "127.0.0.1", ServerSetup.PROTOCOL_SMTP)
                }
        );
        greenMail.start();
    }

    @AfterClass
    protected void tearDownControllerTest() {
        greenMail.stop();
    }

    @Test(groups = "createCustomerFromController", dataProvider = "setupCustomerControllerData", dataProviderClass = RegisterCustomerDataProvider.class, enabled=false)
    @Transactional
    @Rollback(false)
    public void createCustomerFromController(RegisterCustomerForm registerCustomer) {
        BindingResult errors = new BeanPropertyBindingResult(registerCustomer, "registerCustomer");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        registerCustomerController.registerCustomer(registerCustomer, errors, request, response);
        assert(errors.getErrorCount() == 0);
        Customer customerFromDb = customerService.readCustomerByUsername(registerCustomer.getCustomer().getUsername());
        assert(customerFromDb != null);
    }

    @Test(groups = "viewRegisterCustomerFromController")
    public void viewRegisterCustomerFromController() {
        String view = registerCustomerController.registerCustomer();
        assert (view.equals("/account/registration/registerCustomer"));
    }

}
