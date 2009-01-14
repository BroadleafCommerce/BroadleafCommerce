package org.springcommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.UserService;
import org.springcommerce.util.CreateUser;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class UserFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private UserService userService;

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

        if (userService.readUserByUsername(createUser.getUsername()) != null) {
            errors.rejectValue("username", "username.used", null, null);
        }

        userService.readUserByUsername(createUser.getUsername());

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
        mav.addObject("saved", true);

        return mav;
    }
}
