/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.openadmin.server.security.domain.AdminModule;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.web.form.ResetPasswordForm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AdminLoginController handles login related needs for the BLC admin including:
 * <ul>
 *     <li>Forgot Password</li>
 *     <li>Forgot Username</li>
 *     <li>Reset Password</li>
 * </ul>
 *
 */
public class BroadleafAdminLoginController extends BroadleafAbstractController {

    private static final String ANONYMOUS_USER_NAME = "anonymousUser";

    @Resource(name="blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;

    @Resource(name="blAdminNavigationService")
    protected AdminNavigationService adminNavigationService;

    // Entry URLs
    protected static String loginView = "login/login";
    protected static String forgotPasswordView = "login/forgotPassword";
    protected static String forgotUsernameView = "login/forgotUsername";
    protected static String resetPasswordView  = "login/resetPassword";
    protected static String changePasswordView  = "login/changePassword";
    protected static String loginRedirect = "login";
    protected static String resetPasswordRedirect = "resetPassword";

    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getLoginView();
    }

    public String loginSuccess(HttpServletRequest request, HttpServletResponse response, Model model) {
        List<AdminModule> modules = adminNavigationService.buildMenu(getPersistentAdminUser());
        if (!modules.isEmpty()) {
            AdminModule first = modules.get(0);
            if (!first.getSections().isEmpty()) {
                AdminSection adminSection = first.getSections().get(0);
                return "redirect:" + adminSection.getUrl();
            }
        }
        return null;
    }
   
    public String forgotPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getForgotPasswordView();
    }
    
    public String forgotUsername(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getForgotUsernameView();
    }
    
    public String processResetPassword(@RequestParam("username") String username, HttpServletRequest request, HttpServletResponse response, Model model) {
        GenericResponse errorResponse = adminSecurityService.sendResetPasswordNotification(username);
        if (errorResponse.getHasErrors()) {
            setErrors(errorResponse, request);
            return getForgotPasswordView();
        } else {
            request.getSession(true).setAttribute("forgot_password_username", username);
            return redirectToResetPasswordWithMessage("passwordTokenSent");
        }
    }
   
    public String processForgotUserName(@RequestParam("email") String email, HttpServletRequest request,Model model) {
        GenericResponse errorResponse = adminSecurityService.sendForgotUsernameNotification(email);
        if (errorResponse.getHasErrors()) {
            setErrors(errorResponse, request);
            return getForgotUsernameView();
        } else {
            return redirectToLoginWithMessage("usernameSent");
        }
    }

    public String resetPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getResetPasswordView();
    }

    public String resetPassword(ResetPasswordForm resetPasswordForm,
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
        StringBuffer url = new StringBuffer("redirect:").append(loginRedirect).append("?messageCode=").append(message);
        return url.toString();
    }

    protected String redirectToResetPasswordWithMessage(String message) {
        StringBuffer url = new StringBuffer("redirect:").append(resetPasswordRedirect).append("?messageCode=").append(message);
        return url.toString();
    }

    protected void setErrors(GenericResponse response, HttpServletRequest request) {
        String errorCode = response.getErrorCodesList().get(0);
        request.setAttribute("errorCode", errorCode);
    }

    public String changePassword(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getChangePasswordView();
    }

    public String processChangePassword(ResetPasswordForm resetPasswordForm,
            HttpServletRequest request) {
        GenericResponse errorResponse = adminSecurityService
                .changePassword(resetPasswordForm.getUsername(),
                        resetPasswordForm.getOldPassword(),
                        resetPasswordForm.getPassword(),
                        resetPasswordForm.getConfirmPassword());
        if (errorResponse.getHasErrors()) {
            setErrors(errorResponse, request);
            return getChangePasswordView();
        } else {
            return redirectToLoginWithMessage("passwordReset");
        }
    }

    public static String getLoginView() {
        return loginView;
    }

    public static void setLoginView(String loginView) {
        BroadleafAdminLoginController.loginView = loginView;
    }

    public static String getForgotPasswordView() {
        return forgotPasswordView;
    }

    public static void setForgotPasswordView(String forgotPasswordView) {
        BroadleafAdminLoginController.forgotPasswordView = forgotPasswordView;
    }

    public static String getForgotUsernameView() {
        return forgotUsernameView;
    }

    public static void setForgotUsernameView(String forgotUsernameView) {
        BroadleafAdminLoginController.forgotUsernameView = forgotUsernameView;
    }

    public static String getResetPasswordView() {
        return resetPasswordView;
    }

    public static void setResetPasswordView(String resetPasswordView) {
        BroadleafAdminLoginController.resetPasswordView = resetPasswordView;
    }

    public static String getChangePasswordView() {
        return changePasswordView;
    }

    public static void setChangePasswordView(String changePasswordView) {
        BroadleafAdminLoginController.changePasswordView = changePasswordView;
    }
    
    protected AdminUser getPersistentAdminUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx != null) {
            Authentication auth = ctx.getAuthentication();
            if (auth != null && !auth.getName().equals(ANONYMOUS_USER_NAME)) {
                UserDetails temp = (UserDetails) auth.getPrincipal();

                return adminSecurityService.readAdminUserByUserName(temp.getUsername());
            }
        }

        return null;
    }

    public AdminSecurityService getAdminSecurityService() {
        return adminSecurityService;
    }

    public void setAdminSecurityService(AdminSecurityService adminSecurityService) {
        this.adminSecurityService = adminSecurityService;
    }

    public static String getLoginRedirect() {
        return loginRedirect;
    }

    public static void setLoginRedirect(String loginRedirect) {
        BroadleafAdminLoginController.loginRedirect = loginRedirect;
    }

    public static String getResetPasswordRedirect() {
        return resetPasswordRedirect;
    }

    public static void setResetPasswordRedirect(String resetPasswordRedirect) {
        BroadleafAdminLoginController.resetPasswordRedirect = resetPasswordRedirect;
    }

}
