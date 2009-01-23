package org.springcommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.UserService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ForgotPwdFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private UserService userService;


    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    protected Object formBackingObject(HttpServletRequest request)
                                throws ServletException {
        return new User();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
                             throws Exception {
        User user = (User) command;
        ModelAndView mav=new ModelAndView();

        User userFromDb = userService.readUserByEmail(user.getEmailAddress());

        if (userFromDb != null) {
        	if(! userFromDb.getChallengeQuestion().isEmpty() && ! userFromDb.getChallengeAnswer().isEmpty()){
        		 mav = new ModelAndView("redirect:/challengeQuestion.htm", errors.getModel());
        	}else{
        		mav = new ModelAndView(getSuccessView(), errors.getModel());
        	}
        	mav.addObject("email",userFromDb.getEmailAddress());
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
