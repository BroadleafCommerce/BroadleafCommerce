package org.broadleafcommerce.profile.web.controller;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.email.domain.AbstractEmailTarget;
import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.service.EmailService;
import org.broadleafcommerce.email.service.info.EmailInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.controller.validator.RegisterCustomerValidator;
import org.broadleafcommerce.profile.web.util.RegisterCustomer;
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
    private EmailService emailService;

    @Resource(name="blRegistrationEmailInfo")
    EmailInfo emailInfo;

    @Resource
    private RegisterCustomerValidator registerCustomerValidator;

    public RegisterCustomerController() { }

    @RequestMapping(method = { RequestMethod.GET })
    public String viewForm() {
        return "registerCustomer";
    }

    @RequestMapping(method = { RequestMethod.POST })
    public ModelAndView saveCustomer(@ModelAttribute("customerForm") RegisterCustomer registerCustomer,
            BindingResult errors,
            HttpServletRequest request) {
        registerCustomerValidator.validate(registerCustomer, errors);

        if (errors.getAllErrors().isEmpty()) {
            createCustomer(registerCustomer);
            this.sendConfirmationEmail(request, registerCustomer);
            new ModelAndView("customerRegistered");
        }

        return new ModelAndView("registerCustomer");
    }

    private void sendConfirmationEmail(HttpServletRequest request, RegisterCustomer customer) {
        EmailTarget target = new AbstractEmailTarget(){};
        target.setEmailAddress(customer.getEmailAddress());

        HashMap<String, Object> props = new HashMap<String, Object>();

        emailService.sendTemplateEmail(emailInfo, target, props);
    }

    private void createCustomer(RegisterCustomer registerCustomer) {
        Customer customer = customerService.createCustomerFromId(null);
        customer.setEmailAddress(registerCustomer.getEmailAddress());
        customer.setFirstName(registerCustomer.getFirstName());
        customer.setLastName(registerCustomer.getLastName());
        customer.setPassword(registerCustomer.getPassword());
        customer.setPassword(registerCustomer.getPassword());
        customerService.saveCustomer(customer);
    }

    @ModelAttribute("customerForm")
    public RegisterCustomer initCustomer(WebRequest request) {
        RegisterCustomer customer = new RegisterCustomer();
        return customer;
    }

}
