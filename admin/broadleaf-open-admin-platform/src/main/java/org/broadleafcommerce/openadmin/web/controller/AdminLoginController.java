/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.web.form.ResetPasswordForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller("blAdminLoginController")
@RequestMapping("/blcadmin/*")
/**
 * AdminLoginController handles login related needs for the BLC admin including:
 * <ul>
 *     <li>Forgot Password</li>
 *     <li>Forgot Username</li>
 *     <li>Reset Password</li>
 * </ul>
 *
 */
public class AdminLoginController {

    @Resource(name="blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;

    // Entry URLs
    protected String loginView = "/blcadmin/login";
    protected String forgotPasswordView = "/blcadmin/forgotPassword";
    protected String forgotUsernameView = "/blcadmin/forgotUsername";
    protected String resetPasswordView  = "/blcadmin/resetPassword";

    @RequestMapping(method = { RequestMethod.GET })
    public String login() {
        return getLoginView();
    }

    @RequestMapping(method = { RequestMethod.GET })
    public String forgotPassword() {
        return getForgotPasswordView();
    }
    
    @RequestMapping(method = { RequestMethod.GET })
    public String forgotUsername() {
        return getForgotUsernameView();
    }

    @RequestMapping(method = { RequestMethod.POST })
    public String forgotPassword(@RequestParam("username") String username, HttpServletRequest request) {
        GenericResponse errorResponse = adminSecurityService.sendResetPasswordNotification(username);
        if (errorResponse.getHasErrors()) {
            setErrors(errorResponse, request);
            return getForgotPasswordView();
        } else {
            request.getSession(true).setAttribute("forgot_password_username", username);
            return redirectToResetPasswordWithMessage("passwordTokenSent");
        }
    }

    @RequestMapping(method = { RequestMethod.POST })
    public String forgotUsername(@RequestParam("email") String email, HttpServletRequest request) {
        GenericResponse errorResponse = adminSecurityService.sendForgotUsernameNotification(email);
        if (errorResponse.getHasErrors()) {
            setErrors(errorResponse, request);
            return getForgotUsernameView();
        } else {
            return redirectToLoginWithMessage("usernameSent");
        }
    }

    @RequestMapping(method = { RequestMethod.GET })
    public String resetPassword(HttpServletRequest request) {
        return getResetPasswordView();
    }

    @RequestMapping(method = { RequestMethod.POST })
    public String resetPassword(@ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm,
                                HttpServletRequest request) {
        GenericResponse errorResponse = adminSecurityService.resetPasswordUsingToken(
                resetPasswordForm.getUsername(), 
                resetPasswordForm.getToken(), 
                resetPasswordForm.getPassword(), 
                resetPasswordForm.getConfirmPassword());
        if (errorResponse.getHasErrors()) {
            setErrors(errorResponse, request);
            return getResetPasswordView();
        } else {
            return redirectToLoginWithMessage("passwordReset");
        }
    }

    @ModelAttribute("resetPasswordForm")
    public ResetPasswordForm initResetPasswordForm(HttpServletRequest request) {
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        String username = (String) request.getSession(true).getAttribute("forgot_password_username");
        String token = request.getParameter("token");
        resetPasswordForm.setToken(token);
        resetPasswordForm.setUsername(username);
        return resetPasswordForm;
    }

    protected String redirectToLoginWithMessage(String message) {
        StringBuffer url = new StringBuffer("redirect:").append(getLoginView()).append("?messageCode=").append(message);
        return url.toString();
    }

    protected String redirectToResetPasswordWithMessage(String message) {
        StringBuffer url = new StringBuffer("redirect:").append(getResetPasswordView()).append("?messageCode=").append(message);
        return url.toString();
    }

    protected void setErrors(GenericResponse response, HttpServletRequest request) {
        String errorCode = response.getErrorCodesList().get(0);
        request.setAttribute("errorCode", errorCode);
    }

    public String getLoginView() {
        return loginView;
    }

    public void setLoginView(String loginView) {
        this.loginView = loginView;
    }

    public String getForgotPasswordView() {
        return forgotPasswordView;
    }

    public void setForgotPasswordView(String forgotPasswordView) {
        this.forgotPasswordView = forgotPasswordView;
    }

    public String getForgotUsernameView() {
        return forgotUsernameView;
    }

    public void setForgotUsernameView(String forgotUsernameView) {
        this.forgotUsernameView = forgotUsernameView;
    }

    public String getResetPasswordView() {
        return resetPasswordView;
    }

    public void setResetPasswordView(String resetPasswordView) {
        this.resetPasswordView = resetPasswordView;
    }
}