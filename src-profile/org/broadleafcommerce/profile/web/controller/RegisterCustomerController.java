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
