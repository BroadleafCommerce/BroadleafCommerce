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

import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.broadleafcommerce.profile.service.CustomerPhoneService;
import org.broadleafcommerce.profile.test.dataprovider.CustomerPhoneControllerTestDataProvider;
import org.broadleafcommerce.profile.web.controller.CustomerPhoneController;
import org.broadleafcommerce.profile.web.model.PhoneNameForm;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.testng.annotations.Test;

public class CustomerPhoneControllerTest extends BaseTest {

    @Resource
    private CustomerPhoneController customerPhoneController;
    @Resource
    private CustomerPhoneService customerPhoneService;
    private List<Long> createdCustomerPhoneIds = new ArrayList<Long>();
    private Long userId = 1L;
    private MockHttpServletRequest request;
    private static final String SUCCESS = "customerPhones";

    @Test(groups = "createCustomerPhoneFromController", dataProvider = "setupCustomerPhoneControllerData", dataProviderClass = CustomerPhoneControllerTestDataProvider.class)
    @Rollback(false)
    public void createCustomerPhoneFromController(PhoneNameForm phoneNameForm) {
        BindingResult errors = new BeanPropertyBindingResult(phoneNameForm, "phoneNameForm");

        request = this.getNewServletInstance();
        request.getSession().setAttribute("customer_session", userId); //set customer on session

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
    public void viewExistingCustomerPhoneFromController() {
        List<CustomerPhone> phones_1 = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);
        PhoneNameForm pnf = new PhoneNameForm();

        BindingResult errors = new BeanPropertyBindingResult(pnf, "phoneNameForm");

        request = this.getNewServletInstance();

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
