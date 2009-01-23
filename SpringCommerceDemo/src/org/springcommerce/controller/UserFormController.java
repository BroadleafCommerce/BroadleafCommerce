package org.springcommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.EmailService;
import org.springcommerce.profile.service.UserService;
import org.springcommerce.util.CreateUser;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class UserFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    //TODO: Should move all these to a property file
    private static final String TEMPLATE = "registration.vm";
    private static final String EMAIL_FROM="SpringCommerce@credera.com";
    private static final String EMAIL_SUBJECT="Your Registration Completed";
    private UserService userService;
    private EmailService emailService;

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void setUserService(UserService userService) {
        this.userService = userService;
    }

    protected Object formBackingObject(HttpServletRequest request)
                                throws ServletException {
        return new CreateUser();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
                             throws Exception {
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
        user.setPassword(createUser.getPassword());
        user.setEmailAddress(createUser.getEmailAddress());
        user.setChallengeQuestion(createUser.getChallengeQuestion());
        user.setChallengeAnswer(createUser.getChallengeAnswer());
        userService.registerUser(user);
        emailService.sendEmail(user, TEMPLATE,EMAIL_FROM, EMAIL_SUBJECT);
        mav.addObject("saved", true);
        return mav;
    }
}
