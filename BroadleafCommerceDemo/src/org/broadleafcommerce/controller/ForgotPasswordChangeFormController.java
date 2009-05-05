package org.broadleafcommerce.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class ForgotPasswordChangeFormController extends SimpleFormController {

    /** Logger for this class and subclasses */
    /*protected final Log logger = LogFactory.getLog(getClass());
    private static final String TEMPLATE = "forgotPassword.vm";
    private static final String EMAIL_FROM = "BroadleafCommerce@credera.com";
    private static final String EMAIL_SUBJECT = "Email From Broadleaf Commerce Group";
    private CustomerService customerService;
    private EmailService emailService;

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        return new PasswordChange();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        PasswordChange pwChange = (PasswordChange) command;
        Customer customer = customerService.readCustomerByEmail(request.getParameter("email"));
        customer.setUnencodedPassword(pwChange.getNewPassword());
        customerService.saveCustomer(customer);
        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());
        emailService.sendEmail(customer, TEMPLATE, EMAIL_FROM, EMAIL_SUBJECT);
        mav.addObject("saved", true);
        return mav;
    }
    */
}
