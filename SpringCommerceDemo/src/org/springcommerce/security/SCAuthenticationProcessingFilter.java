package org.springcommerce.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springcommerce.profile.service.UserService;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.CredentialsExpiredException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.TextUtils;

public class SCAuthenticationProcessingFilter extends AuthenticationProcessingFilter {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        Authentication auth = null;
        try {
            auth = super.attemptAuthentication(request);
        } catch (CredentialsExpiredException e) {
            String username = obtainUsername(request);
            String password = obtainPassword(request);

            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }

            username = username.trim();

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password, new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_PASSWORD_CHANGE_REQUIRED") });

            // Place the last username attempted into HttpSession for views
            HttpSession session = request.getSession(false);

            if (session != null || getAllowSessionCreation()) {
                request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextUtils.escapeEntities(username));
            }

            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);
            auth = authRequest;
            logger.info("###jb username = "+this.obtainUsername(request));
        }
        return auth;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_PASSWORD_CHANGE_REQUIRED".equals(ga.getAuthority())) {
                return "/passwordChange.htm";
            }
        }
        return super.determineTargetUrl(request);
    }
}
