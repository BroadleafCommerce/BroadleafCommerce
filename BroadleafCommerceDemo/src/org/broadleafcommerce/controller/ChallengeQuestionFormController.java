package org.broadleafcommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.util.PasswordChange;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ChallengeQuestionFormController extends SimpleFormController {
	/*
    protected final Log logger = LogFactory.getLog(getClass());
    private CustomerService customerService;

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    protected Object formBackingObject(HttpServletRequest request)
    throws ServletException {
        String email = request.getParameter("email");
        Customer customer = customerService.readCustomerByEmail(email);
        PasswordChange passwordChange = new PasswordChange();
        passwordChange.setChallengeQuestion(customer.getChallengeQuestion());
        passwordChange.setEmail(email);
        return passwordChange;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
    throws Exception {
        PasswordChange passwordChange = (PasswordChange) command;
        Customer userFromDb = customerService.readCustomerByEmail(request.getParameter("email"));

        if (!userFromDb.getChallengeAnswer().equalsIgnoreCase(passwordChange.getChallengeAnswer())) {
            errors.rejectValue("challengeAnswer", "challengeAnswer.invalid", null, null);
        }

        ModelAndView mav = new ModelAndView("redirect:/forgotPasswordChange.htm", errors.getModel());
        mav.addObject("email", userFromDb.getEmailAddress());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }

        return mav;
    }
    */
}
