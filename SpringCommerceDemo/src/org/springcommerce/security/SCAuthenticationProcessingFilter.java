package org.springcommerce.security;

import javax.servlet.http.HttpServletRequest;

import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.UserService;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

public class SCAuthenticationProcessingFilter extends AuthenticationProcessingFilter {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByUsername(auth.getName());
        if (user.isPasswordChangeRequired()) {
            return "/passwordChange.jsp";
        }
        return super.determineTargetUrl(request);
    }
}
