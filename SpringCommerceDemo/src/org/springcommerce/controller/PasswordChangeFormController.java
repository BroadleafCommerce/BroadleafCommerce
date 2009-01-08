package org.springcommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.domain.User;
import org.springcommerce.util.PasswordChange;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class PasswordChangeFormController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return new PasswordChange();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        logger.info("**** in onSubmit");
        PasswordChange pwChange = (PasswordChange) command;
        // TODO: Adding errors.getModel() to our ModelAndView is a "hack" to allow our
        // form to post results back to the same page. We need to get the
        // command from errors and then add our search results to the model.
        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());
        mav.addObject("saved", true);
        return mav;
    }
}
