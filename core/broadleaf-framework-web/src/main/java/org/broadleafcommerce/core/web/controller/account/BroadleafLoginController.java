/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The controller responsible for login, forgot password, forgot username, 
 * and reset password actions.
 * 
 * @author apazzolini
 * @author bpolster
 */
public class BroadleafLoginController extends BroadleafAbstractController {
	
    @Resource(name="blCustomerService")
    protected CustomerService customerService;
	
	private String loginView = "/login";
	private String forgotPasswordView = "/forgotPassword";
	private String forgotUsernameView = "/forgotUsername";	
	private String resetPasswordView = "/resetPassword";
	private String resetPasswordFormName = "resetPasswordForm";
		
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
		return ajaxRender(getLoginView(), request, model);
	}
	
	public String forgotPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
		return ajaxRender(getForgotPasswordView(), request, model);
	}
	
    public String processForgotPassword(String username, HttpServletRequest request, Model model) {
    	GenericResponse errorResponse = customerService.sendResetPasswordNotification(username, getResetPasswordUrl(request));
        if (errorResponse.getHasErrors()) {
        	 String errorCode = errorResponse.getErrorCodesList().get(0);
             request.setAttribute("errorCode", errorCode);
             return ajaxRender(getForgotPasswordView(), request, model);
        } else {
            request.getSession(true).setAttribute("forgot_password_username", username);
            String url = buildRedirectToResetPasswordWithMessage("passwordTokenSent");
        	return ajaxRender(url, request, model);
        }
    }   
    
	public String forgotUsername(HttpServletRequest request, HttpServletResponse response, Model model) {
		return ajaxRender(getForgotUsernameView(), request, model);
	}	
    
    public String processForgotUsername(String email, HttpServletRequest request, HttpServletResponse response, Model model) {
        GenericResponse errorResponse = customerService.sendForgotUsernameNotification(email);
        if (errorResponse.getHasErrors()) {
        	String errorCode = errorResponse.getErrorCodesList().get(0);
            request.setAttribute("errorCode", errorCode);
        	return ajaxRender(getForgotUsernameView(), request, model);
        } else {
        	String url = buildRedirectToLoginWithMessage("usernameSent");
        	return ajaxRender(url, request, model);
        }
     }    
    
	public String resetPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("resetPasswordForm", initResetPasswordForm(request));
		return ajaxRender(getForgotUsernameView(), request, model);
	}	
    
    public String processResetPassword(ResetPasswordForm resetPasswordForm, HttpServletRequest request, HttpServletResponse response, Model model) {
    	GenericResponse errorResponse = customerService.resetPasswordUsingToken(
                resetPasswordForm.getUsername(), 
                resetPasswordForm.getToken(), 
                resetPasswordForm.getPassword(), 
                resetPasswordForm.getConfirmPassword());
        if (errorResponse.getHasErrors()) {
        	String errorCode = errorResponse.getErrorCodesList().get(0);
            request.setAttribute("errorCode", errorCode);
            return ajaxRender(getResetPasswordView(), request, model);
        } else {
        	String url = buildRedirectToLoginWithMessage("passwordReset");
        	return ajaxRender(url, request, model);
        }
     }
    
    protected String buildRedirectToLoginWithMessage(String message) {
        StringBuffer url = new StringBuffer("redirect:").append(getLoginView()).append("?messageCode=").append(message);
        return url.toString();
    }

    protected String buildRedirectToResetPasswordWithMessage(String message) {
        StringBuffer url = new StringBuffer("redirect:").append(getResetPasswordView()).append("?messageCode=").append(message);
        return url.toString();
    } 
    
    public ResetPasswordForm initResetPasswordForm(HttpServletRequest request) {
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        String username = (String) request.getSession(true).getAttribute("forgot_password_username");
        String token = request.getParameter("token");
        resetPasswordForm.setToken(token);
        resetPasswordForm.setUsername(username);
        return resetPasswordForm;
    }

	/**
	 * The view representing the login page.
	 * @return
	 */
    public String getLoginView() {
		return loginView;
	}

	public void setLoginView(String loginView) {
		this.loginView = loginView;
	}	

	/**
	 * The view displayed for the forgot username form.
	 * @return
	 */
	public String getForgotUsernameView() {
		return forgotUsernameView;
	}

	public void setForgotUsernameView(String forgotUsernameView) {
		this.forgotUsernameView = forgotUsernameView;
	}

	/**
	 * The view displayed for the forgot password form.
	 * @return
	 */
	public String getForgotPasswordView() {
		return forgotPasswordView;
	}

	public void setForgotPasswordView(String forgotPasswordView) {
		this.forgotPasswordView = forgotPasswordView;
	}

	/**
	 * The view displayed for the reset password form.
	 * @return
	 */
	public String getResetPasswordView() {
		return resetPasswordView;
	}

	public void setResetPasswordView(String resetPasswordView) {
		this.resetPasswordView = resetPasswordView;
	}

	/**
	 * The form name to use for the reset password model..
	 * @return
	 */
	public String getResetPasswordFormName() {
		return resetPasswordFormName;
	}

	public void setResetPasswordFormName(String resetPasswordFormName) {
		this.resetPasswordFormName = resetPasswordFormName;
	}
	
	public String getResetPasswordUrl(HttpServletRequest request) {
		String url = request.getScheme() + "://" + request.getServerName();
		if (request.getContextPath() != null && ! "".equals(request.getContextPath())) {
			url = url + request.getContextPath() + getResetPasswordView();
		} else {
			url = url + getResetPasswordView();
		}
		return url;
	}
}
