package org.broadleafcommerce.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class ForgotPwdFormController extends SimpleFormController {
	/*
    protected final Log logger = LogFactory.getLog(getClass());
    private static final String TEMPLATE = "forgotPasswordReset.vm";
    private static final String EMAIL_FROM = "BroadleafCommerce@credera.com";
    private static final String EMAIL_SUBJECT = "Email From Broadleaf Commerce Group";

    private CustomerService customerService;

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    private EmailService emailService;

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        return new BroadleafCustomer();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        Customer customer = (Customer) command;
        ModelAndView mav = new ModelAndView();

        Customer customerFromDb = customerService.readCustomerByEmail(customer.getEmailAddress());
        if (customerFromDb != null) {
            if (!StringUtils.isEmpty(customerFromDb.getChallengeQuestion()) && !StringUtils.isEmpty(customerFromDb.getChallengeAnswer())) {
                mav = new ModelAndView("redirect:/challengeQuestion.htm", errors.getModel());
            } else {
                String temporaryPassword = PasswordUtils.generateTemporaryPassword(8);
                customerFromDb.setUnencodedPassword(temporaryPassword);
                customerFromDb.setPasswordChangeRequired(true);
                customerService.saveCustomer(customerFromDb);
                emailService.sendEmail(customerFromDb, TEMPLATE, EMAIL_FROM, EMAIL_SUBJECT);
                mav = new ModelAndView(getSuccessView(), errors.getModel());
            }
            mav.addObject("email", customerFromDb.getEmailAddress());
        } else {
            errors.rejectValue("emailAddress", "emailAddress.notInUse", null, null);
        }

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");
            return showForm(request, response, errors);
        }

        mav.addObject("saved", true);
        return mav;
    }
    */
}
