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

import javax.annotation.Resource;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.test.dataprovider.RegisterCustomerDataProvider;
import org.broadleafcommerce.profile.web.controller.RegisterCustomerController;
import org.broadleafcommerce.profile.web.form.RegisterCustomerForm;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class RegisterCustomerControllerTest extends BaseTest {

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

    @Test(groups = "createCustomerFromController", dataProvider = "setupCustomerControllerData", dataProviderClass = RegisterCustomerDataProvider.class)
    @Rollback(false)
    public void createCustomerFromController(RegisterCustomerForm registerCustomer) {
        BindingResult errors = new BeanPropertyBindingResult(registerCustomer, "registerCustomer");
        MockHttpServletRequest request = new MockHttpServletRequest();
        registerCustomerController.registerCustomer(registerCustomer, errors, request);
        assert(errors.getErrorCount() == 0);
        Customer customerFromDb = customerService.readCustomerByUsername(registerCustomer.getCustomer().getUsername());
        assert(customerFromDb != null);
    }

    @Test(groups = "viewRegisterCustomerFromController")
    public void viewRegisterCustomerFromController() {
        String view = registerCustomerController.viewForm();
        assert (view.equals("/account/registration/registerCustomer"));
    }

}
