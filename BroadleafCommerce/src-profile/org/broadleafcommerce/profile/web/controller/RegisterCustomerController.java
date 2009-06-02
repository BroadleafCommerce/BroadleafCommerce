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
package org.broadleafcommerce.profile.web.controller;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.controller.validator.RegisterCustomerValidator;
import org.broadleafcommerce.profile.web.form.CustomerRegistrationForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller("blRegisterCustomerFormController")
public class RegisterCustomerController {
    @Resource
    private CustomerService customerService;

    @Resource
    private RegisterCustomerValidator registerCustomerValidator;

    public RegisterCustomerController() { }

    @RequestMapping(method = { RequestMethod.GET })
    public String viewForm() {
        return "registerCustomer";
    }

    @RequestMapping(method = { RequestMethod.POST })
    public ModelAndView saveCustomer(@ModelAttribute("customerForm") CustomerRegistrationForm registerCustomer,
            BindingResult errors) {
        registerCustomerValidator.validate(registerCustomer, errors);

        if (errors.getAllErrors().isEmpty()) {
            createCustomer(registerCustomer);
            new ModelAndView("customerRegistered");
        }

        return new ModelAndView("registerCustomer");
    }


    private void createCustomer(CustomerRegistrationForm registerCustomer) {
        Customer customer = customerService.createCustomerFromId(null);
        customer.setEmailAddress(registerCustomer.getEmailAddress());
        customer.setFirstName(registerCustomer.getFirstName());
        customer.setLastName(registerCustomer.getLastName());
        customer.setPassword(registerCustomer.getPassword());
        customer.setPassword(registerCustomer.getPassword());
        customerService.saveCustomer(customer);
    }

    @ModelAttribute("customerForm")
    public CustomerRegistrationForm initCustomer(WebRequest request) {
        CustomerRegistrationForm customer = new CustomerRegistrationForm();
        return customer;
    }

}
