package org.broadleafcommerce.controller;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;
import org.broadleafcommerce.profile.service.EmailService;
import org.broadleafcommerce.profile.service.UserService;
import org.broadleafcommerce.util.CreateUser;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class UserFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    // TODO: Should move all these to a property file
    private static final String TEMPLATE = "registration.vm";
    private static final String EMAIL_FROM = "BroadleafCommerce@credera.com";
    private static final String EMAIL_SUBJECT = "Your Registration Completed";
    private UserService userService;
    private EmailService emailService;

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        return new CreateUser();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        CreateUser createUser = (CreateUser) command;

        User userFromDb = userService.readUserByUsername(createUser.getUsername());
        if (userFromDb != null) {
            errors.rejectValue("username", "username.used", null, null);
        }

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }
        User user = new User();
        user.setUsername(createUser.getUsername());
        user.setFirstName(createUser.getFirstName());
        user.setLastName(createUser.getLastName());
        user.setUnencodedPassword(createUser.getPassword());
        user.setEmailAddress(createUser.getEmailAddress());
        user.setChallengeQuestion(createUser.getChallengeQuestion());
        user.setChallengeAnswer(createUser.getChallengeAnswer());
        Set<UserRole> roles = new HashSet<UserRole>();
        roles.add(new UserRole(user, "ROLE_USER"));
        user.setUserRoles(roles);
        userService.saveUser(user);
        emailService.sendEmail(user, TEMPLATE, EMAIL_FROM, EMAIL_SUBJECT);
        mav.addObject("saved", true);
        return mav;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
    throws Exception {
        CreateUser createUser = (CreateUser) command;

        return super.processFormSubmission(request, response, createUser, errors);
    }
}
