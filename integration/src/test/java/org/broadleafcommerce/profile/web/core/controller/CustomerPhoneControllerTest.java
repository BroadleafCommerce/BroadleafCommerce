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

package org.broadleafcommerce.profile.web.core.controller;

import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPhone;
import org.broadleafcommerce.profile.core.service.CustomerPhoneService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.controller.CustomerPhoneController;
import org.broadleafcommerce.profile.web.core.controller.dataprovider.CustomerPhoneControllerTestDataProvider;
import org.broadleafcommerce.profile.web.core.model.PhoneNameForm;
import org.broadleafcommerce.profile.web.core.security.CustomerStateRequestProcessor;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class CustomerPhoneControllerTest extends BaseTest {

    @Resource
    private CustomerPhoneController customerPhoneController;
    @Resource
    private CustomerPhoneService customerPhoneService;
    @Resource
    private CustomerService customerService;
    private final List<Long> createdCustomerPhoneIds = new ArrayList<Long>();
    private final Long userId = 1L;
    private MockHttpServletRequest request;
    private static final String SUCCESS = "customerPhones";

    @Test(groups = "createCustomerPhoneFromController", dataProvider = "setupCustomerPhoneControllerData", dataProviderClass = CustomerPhoneControllerTestDataProvider.class, dependsOnGroups = "readCustomer")
    @Transactional
    @Rollback(false)
    public void createCustomerPhoneFromController(PhoneNameForm phoneNameForm) {
        BindingResult errors = new BeanPropertyBindingResult(phoneNameForm, "phoneNameForm");

        Customer customer = customerService.readCustomerById(userId);
        request = this.getNewServletInstance();
        request.setAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName(), customer);

        String view = customerPhoneController.savePhone(phoneNameForm, errors, request, null, null);
        assert (view.indexOf(SUCCESS) >= 0);

        List<CustomerPhone> phones = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);

        boolean inPhoneList = false;

        Long id = (Long) request.getAttribute("customerPhoneId");
        assert (id != null);

        for (CustomerPhone p : phones) {
            if ((p.getPhoneName() != null) && p.getPhoneName().equals(phoneNameForm.getPhoneName())) {
                inPhoneList = true;
            }
        }
        assert (inPhoneList == true);

        createdCustomerPhoneIds.add(id);
    }

    @Test(groups = "makePhoneDefaultOnCustomerPhoneController", dependsOnGroups = "createCustomerPhoneFromController")
    @Transactional
    public void makePhoneDefaultOnCustomerPhoneController() {
        Long nonDefaultPhoneId = null;
        List<CustomerPhone> phones_1 = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);

        for (CustomerPhone p : phones_1) {
            if (!p.getPhone().isDefault()) {
                nonDefaultPhoneId = p.getId();
                break;
            }
        }

        request = this.getNewServletInstance();

        String view = customerPhoneController.makePhoneDefault(nonDefaultPhoneId, request);
        assert (view.indexOf("viewPhone") >= 0);

        List<CustomerPhone> phones = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);

        for (CustomerPhone p : phones) {
            if (p.getId() == nonDefaultPhoneId) {
                assert (p.getPhone().isDefault());

                break;
            }
        }
    }

    @Test(groups = "readCustomerPhoneFromController", dependsOnGroups = "createCustomerPhoneFromController")
    @Transactional
    public void readCustomerPhoneFromController() {
        List<CustomerPhone> phones_1 = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);
        int phones_1_size = phones_1.size();

        request = this.getNewServletInstance();

        String view = customerPhoneController.deletePhone(createdCustomerPhoneIds.get(0), request);
        assert (view.indexOf("viewPhone") >= 0);

        List<CustomerPhone> phones_2 = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);
        assert ((phones_1_size - phones_2.size()) == 1);
    }

    @Test(groups = "viewCustomerPhoneFromController")
    public void viewCustomerPhoneFromController() {
        PhoneNameForm pnf = new PhoneNameForm();

        BindingResult errors = new BeanPropertyBindingResult(pnf, "phoneNameForm");

        request = this.getNewServletInstance();

        String view = customerPhoneController.viewPhone(null, request, pnf, errors);
        assert (view.indexOf(SUCCESS) >= 0);
        assert (request.getAttribute("customerPhoneId") == null);
    }

    @Test(groups = "viewExistingCustomerPhoneFromController", dependsOnGroups = "createCustomerPhoneFromController")
    @Transactional
    public void viewExistingCustomerPhoneFromController() {
        List<CustomerPhone> phones_1 = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);
        PhoneNameForm pnf = new PhoneNameForm();

        BindingResult errors = new BeanPropertyBindingResult(pnf, "phoneNameForm");

        Customer customer = customerService.readCustomerById(userId);
        request = this.getNewServletInstance();
        request.setAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName(), customer);

        String view = customerPhoneController.viewPhone(phones_1.get(0).getId(), request, pnf, errors);
        assert (view.indexOf(SUCCESS) >= 0);
        assert (request.getAttribute("customerPhoneId").equals(phones_1.get(0).getId()));
    }

    private MockHttpServletRequest getNewServletInstance() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("customer_session", userId); //set customer on session

        return request;
    }
}
