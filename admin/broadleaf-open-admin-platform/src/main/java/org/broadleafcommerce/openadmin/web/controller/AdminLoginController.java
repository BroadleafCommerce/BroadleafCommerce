/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.common.web.JsonResponse;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.openadmin.server.security.domain.AdminMenu;
import org.broadleafcommerce.openadmin.server.security.domain.AdminModule;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.security.service.AdminUserDetails;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.broadleafcommerce.openadmin.web.form.ResetPasswordForm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@Controller("blAdminLoginController")
public class AdminLoginController extends BroadleafAbstractController {

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
    protected static String changePasswordView  = "login/changePasswordPopup";
    protected static String loginRedirect = "login";
    protected static String resetPasswordRedirect = "resetPassword";
    protected static String noAccessView = "noAccess";

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String baseLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getLoginView();
    }

    @RequestMapping(value = {"/", "/loginSuccess"}, method = RequestMethod.GET)
    public String loginSuccess(HttpServletRequest request, HttpServletResponse response, Model model) {
        AdminMenu adminMenu = adminNavigationService.buildMenu(getPersistentAdminUser());
        if (!adminMenu.getAdminModules().isEmpty()) {
            AdminModule first = adminMenu.getAdminModules().get(0);
            List<AdminSection> sections = first.getSections();
            if (!sections.isEmpty()) {
                AdminSection adminSection = sections.get(0);
                return "redirect:" + adminSection.getUrl();
            }
        }
        return "noAccess";
    }
   
    @RequestMapping(value="/forgotPassword", method=RequestMethod.GET)
    public String forgotPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getForgotPasswordView();
    }
    
    @RequestMapping(value="/forgotUsername", method=RequestMethod.GET)
    public String forgotUsername(HttpServletRequest request, HttpServletResponse response,Model model) {
        return getForgotUsernameView();
    }
    
    @RequestMapping(value = "/sendResetPassword", method = RequestMethod.POST)
    public String processSendResetPasswordEmail(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("username") String username) {

        GenericResponse errorResponse = adminSecurityService.sendResetPasswordNotification(username);
        if (errorResponse.getHasErrors()) {
            setErrors(errorResponse, request);
            return getForgotPasswordView();
        } else {
            request.getSession(true).setAttribute("forgot_password_username", username);
            return redirectToResetPasswordWithMessage("passwordTokenSent");
        }
    }

    @RequestMapping(value="/resetPassword", method=RequestMethod.POST)
    public String processResetPassword(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm) {
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
   
    @RequestMapping(value="/forgotUsername", method=RequestMethod.POST)
    public String processForgotUserName(HttpServletRequest request,
            @RequestParam("emailAddress") String email) {
        GenericResponse errorResponse = adminSecurityService.sendForgotUsernameNotification(email);
        if (errorResponse.getHasErrors()) {
            setErrors(errorResponse, request);
            return getForgotUsernameView();
        } else {
            return redirectToLoginWithMessage("usernameSent");
        }
    }

    @RequestMapping(value="/resetPassword", method=RequestMethod.GET)
    public String resetPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getResetPasswordView();
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

    @RequestMapping(value="/changePassword", method=RequestMethod.GET)
    public String changePassword(HttpServletRequest request, HttpServletResponse response, Model model) {
        SecurityContext c = SecurityContextHolder.getContext();
        model.addAttribute("username", ((AdminUserDetails) c.getAuthentication().getPrincipal()).getUsername());
        return "login/changePasswordPopup";
    }

    @RequestMapping(value="/changePassword", method=RequestMethod.POST)
    public String processchangePassword(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm) {
        GenericResponse errorResponse = adminSecurityService.changePassword(resetPasswordForm.getUsername(),
                resetPasswordForm.getOldPassword(),
                resetPasswordForm.getPassword(),
                resetPasswordForm.getConfirmPassword());
        
        if (errorResponse.getHasErrors()) {
            String errorCode = errorResponse.getErrorCodesList().get(0);
            return new JsonResponse(response)
                .with("status", "error")
                .with("errorText", BLCMessageUtils.getMessage("password." + errorCode))
                .done();
        } else {
            return new JsonResponse(response)
                .with("data.status", "ok")
                .with("successMessage", BLCMessageUtils.getMessage("PasswordChange_success"))
                .done();
        }
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

    public static String getLoginView() {
        return loginView;
    }

    public static void setLoginView(String loginView) {
        AdminLoginController.loginView = loginView;
    }

    public static String getForgotPasswordView() {
        return forgotPasswordView;
    }

    public static void setForgotPasswordView(String forgotPasswordView) {
        AdminLoginController.forgotPasswordView = forgotPasswordView;
    }

    public static String getForgotUsernameView() {
        return forgotUsernameView;
    }

    public static void setForgotUsernameView(String forgotUsernameView) {
        AdminLoginController.forgotUsernameView = forgotUsernameView;
    }

    public static String getResetPasswordView() {
        return resetPasswordView;
    }

    public static void setResetPasswordView(String resetPasswordView) {
        AdminLoginController.resetPasswordView = resetPasswordView;
    }

    public static String getChangePasswordView() {
        return changePasswordView;
    }

    public static void setChangePasswordView(String changePasswordView) {
        AdminLoginController.changePasswordView = changePasswordView;
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
        AdminLoginController.loginRedirect = loginRedirect;
    }

    public static String getResetPasswordRedirect() {
        return resetPasswordRedirect;
    }

    public static void setResetPasswordRedirect(String resetPasswordRedirect) {
        AdminLoginController.resetPasswordRedirect = resetPasswordRedirect;
    }

}
