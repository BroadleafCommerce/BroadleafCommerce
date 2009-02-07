package org.broadleafcommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.BroadleafCustomer;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.service.EmailService;
import org.broadleafcommerce.util.RegisterCustomer;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class RegisterCustomerFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    // TODO: Should move all these to a property file
    private static final String TEMPLATE = "registration.vm";
    private static final String EMAIL_FROM = "BroadleafCommerce@credera.com";
    private static final String EMAIL_SUBJECT = "Your Registration Completed";
    private CustomerService customerService;
    private EmailService emailService;

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        return new RegisterCustomer();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        RegisterCustomer registerCustomer = (RegisterCustomer) command;

        Customer customerFromDb = customerService.readCustomerByUsername(registerCustomer.getUsername());
        if (customerFromDb != null) {
            errors.rejectValue("username", "username.used", null, null);
        }

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }
        Customer customer = new BroadleafCustomer();
        customer.setUsername(registerCustomer.getUsername());
        customer.setFirstName(registerCustomer.getFirstName());
        customer.setLastName(registerCustomer.getLastName());
        customer.setUnencodedPassword(registerCustomer.getPassword());
        customer.setEmailAddress(registerCustomer.getEmailAddress());
        customer.setChallengeQuestion(registerCustomer.getChallengeQuestion());
        customer.setChallengeAnswer(registerCustomer.getChallengeAnswer());
        customerService.saveCustomer(customer);
        emailService.sendEmail(customer, TEMPLATE, EMAIL_FROM, EMAIL_SUBJECT);
        mav.addObject("saved", true);
        return mav;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
    throws Exception {
        RegisterCustomer registerCustomer = (RegisterCustomer) command;

        return super.processFormSubmission(request, response, registerCustomer, errors);
    }
}
