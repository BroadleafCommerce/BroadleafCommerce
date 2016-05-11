/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.controller;

import org.broadleafcommerce.profile.core.domain.ChallengeQuestion;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.ChallengeQuestionService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.controller.validator.RegisterCustomerValidator;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.broadleafcommerce.profile.web.core.service.login.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller("blRegisterCustomerController")
@RequestMapping("/registerCustomer")
/**
 * @Deprecated - Use BroadleafRegisterController instead
 * RegisterCustomerController is used to register a customer.
 *
 * This controller simply calls the RegistrationCustomerValidator which can be extended for custom validation and
 * then calls saveCustomer.
 */
public class RegisterCustomerController {

    // URLs For success and failure
    protected String displayRegistrationFormView = "/account/registration/registerCustomer";
    protected String registrationErrorView = displayRegistrationFormView;
    protected String registrationSuccessView = "redirect:/registerCustomer/registerCustomerSuccess.htm";

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    @Resource(name="blRegisterCustomerValidator")
    protected RegisterCustomerValidator registerCustomerValidator;
    
    @Resource(name="blChallengeQuestionService")
    protected ChallengeQuestionService challengeQuestionService;

    @Resource(name="blLoginService")
    protected LoginService loginService;

    @RequestMapping(value="registerCustomer", method = { RequestMethod.GET })
    public String registerCustomer() {
        return getDisplayRegistrationFormView();
    }

    @RequestMapping(value="registerCustomer", method = { RequestMethod.POST })
    public ModelAndView registerCustomer(@ModelAttribute("registerCustomerForm") RegisterCustomerForm registerCustomerForm,
            BindingResult errors, HttpServletRequest request, HttpServletResponse response) {
        registerCustomerValidator.validate(registerCustomerForm, errors);
        if (! errors.hasErrors()) {
            customerService.registerCustomer(registerCustomerForm.getCustomer(), registerCustomerForm.getPassword(), registerCustomerForm.getPasswordConfirm());
            loginService.loginCustomer(registerCustomerForm.getCustomer());
            return new ModelAndView(getRegistrationSuccessView());
        } else {
            return new ModelAndView(getRegistrationErrorView());
        }
    }
    
    @RequestMapping (value="registerCustomerSuccess", method = { RequestMethod.GET })
    public String registerCustomerSuccess() {
        return "/account/registration/registerCustomerSuccess";
    }

    @ModelAttribute("registerCustomerForm")
    public RegisterCustomerForm initCustomerRegistrationForm() {
        RegisterCustomerForm customerRegistrationForm = new RegisterCustomerForm();
        Customer customer = customerService.createNewCustomer();
        customerRegistrationForm.setCustomer(customer);
        return customerRegistrationForm;
    }
    
    @ModelAttribute("challengeQuestions")
    public List<ChallengeQuestion> getChallengeQuestions() {
        return challengeQuestionService.readChallengeQuestions();
        //return null;
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
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ChallengeQuestion.class, new CustomChallengeQuestionEditor(challengeQuestionService));
    }

}
