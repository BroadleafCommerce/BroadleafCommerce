package org.broadleafcommerce.controller;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.service.UserService;
import org.broadleafcommerce.profile.util.PasswordChange;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class PasswordChangeFormController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name = "userService")
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        String username = (String) request.getSession().getAttribute("SPRING_SECURITY_LAST_USERNAME");
        return new PasswordChange(username);
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        logger.info("**** in onSubmit");
        PasswordChange passwordChange = (PasswordChange) command;
        logger.info("Changing password from " + passwordChange.getCurrentPassword() + " to " + passwordChange.getNewPassword());
        // TODO: Adding errors.getModel() to our ModelAndView is a "hack" to allow our
        // form to post results back to the same page. We need to get the
        // command from errors and then add our search results to the model.
        userService.changePassword(passwordChange);
        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());
        return mav;
    }
}
