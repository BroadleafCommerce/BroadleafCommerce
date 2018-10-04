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
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.controller.validator.RegisterCustomerValidator;
import org.broadleafcommerce.profile.web.form.RegisterCustomerForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller("blRegisterCustomerController")

/**
 * RegisterCustomerController is used to register a customer.
 *
 * This controller simply calls the RegistrationCustomerValidator which can be extended for custom validation and
 * then calls saveCustomer.
 */
public class RegisterCustomerController {

    // URLs For success and failure
    private String displayRegistrationFormView = "/account/registration/registerCustomer";
    private String registrationErrorView = displayRegistrationFormView;
    private String registrationSuccessView = "redirect:/account/registration/registrationSuccess";

    @Resource(name="blCustomerService")
    private CustomerService customerService;

    @Resource(name="blRegisterCustomerValidator")
    private RegisterCustomerValidator registerCustomerValidator;

    @RequestMapping(method = { RequestMethod.GET })
    public String viewForm() {
        return getDisplayRegistrationFormView();
    }

    @RequestMapping(method = { RequestMethod.POST })
    public ModelAndView registerCustomer(@ModelAttribute("registerCustomerForm") RegisterCustomerForm registerCustomerForm,
            BindingResult errors, HttpServletRequest request) {
        registerCustomerValidator.validate(registerCustomerForm, errors);
        if (! errors.hasErrors()) {
            customerService.registerCustomer(registerCustomerForm.getCustomer(), registerCustomerForm.getPassword(), registerCustomerForm.getPasswordConfirm());
            return new ModelAndView(getRegistrationSuccessView());
        } else {
            return new ModelAndView(getRegistrationErrorView());
        }
    }

    @ModelAttribute("registerCustomerForm")
    public RegisterCustomerForm initCustomerRegistrationForm() {
        RegisterCustomerForm customerRegistrationForm = new RegisterCustomerForm();
        Customer customer = customerService.createCustomerFromId(null);
        customerRegistrationForm.setCustomer(customer);
        return customerRegistrationForm;
    }

    public String getRegistrationErrorView() {
        return registrationErrorView;
    }

    public void setRegistrationErrorView(String registrationErrorView) {
        this.registrationErrorView = registrationErrorView;
    }

    public String getRegistrationSuccessView() {
        return registrationSuccessView;
    }

    public void setRegistrationSuccessView(String registrationSuccessView) {
        this.registrationSuccessView = registrationSuccessView;
    }

    public RegisterCustomerValidator getRegisterCustomerValidator() {
        return registerCustomerValidator;
    }

    public void setRegisterCustomerValidator(RegisterCustomerValidator registerCustomerValidator) {
        this.registerCustomerValidator = registerCustomerValidator;
    }

    public String getDisplayRegistrationFormView() {
        return displayRegistrationFormView;
    }

    public void setDisplayRegistrationFormView(String displayRegistrationFormView) {
        this.displayRegistrationFormView = displayRegistrationFormView;
    }

}