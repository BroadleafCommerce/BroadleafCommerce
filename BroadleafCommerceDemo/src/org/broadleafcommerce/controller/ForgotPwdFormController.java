package org.broadleafcommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.service.EmailService;
import org.broadleafcommerce.profile.service.UserService;
import org.broadleafcommerce.profile.util.PasswordUtils;
import org.drools.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ForgotPwdFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private static final String TEMPLATE = "forgotPasswordReset.vm";
    private static final String EMAIL_FROM = "BroadleafCommerce@credera.com";
    private static final String EMAIL_SUBJECT = "Email From Spring Commerce Group";

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private EmailService emailService;

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        return new User();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        User user = (User) command;
        ModelAndView mav = new ModelAndView();

        User userFromDb = userService.readUserByEmail(user.getEmailAddress());
        if (userFromDb != null) {
            if (!StringUtils.isEmpty(userFromDb.getChallengeQuestion()) && !StringUtils.isEmpty(userFromDb.getChallengeAnswer())) {
                mav = new ModelAndView("redirect:/challengeQuestion.htm", errors.getModel());
            } else {
                String temporaryPassword = PasswordUtils.generateTemporaryPassword(8);
                userFromDb.setUnencodedPassword(temporaryPassword);
                userFromDb.setPasswordChangeRequired(true);
                userService.saveUser(userFromDb);
                emailService.sendEmail(userFromDb, TEMPLATE, EMAIL_FROM, EMAIL_SUBJECT);
                mav = new ModelAndView(getSuccessView(), errors.getModel());
            }
            mav.addObject("email", userFromDb.getEmailAddress());
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
}
