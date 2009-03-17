package org.broadleafcommerce.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.CredentialsExpiredException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.TextUtils;

public class BCAuthenticationProcessingFilter extends AuthenticationProcessingFilter {

    public static final String BC_LOGIN_SUCCESS_URL_KEY = "broadleaf_commerce.login_success_url";

    private final List<PostLoginObserver> postLoginListeners = new ArrayList<PostLoginObserver>();

    private String passwordChangeUri = "/passwordChange.htm";

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
        }
        return auth;
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        super.onSuccessfulAuthentication(request, response, authResult);
        notifyListeners(request, response, authResult);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_PASSWORD_CHANGE_REQUIRED".equals(ga.getAuthority())) {
                return passwordChangeUri;
            }
        }
        String refererUrl = (String) request.getSession().getAttribute(BC_LOGIN_SUCCESS_URL_KEY);
        if (refererUrl != null) {
            removeLoginSuccessUrl(request);
            return refererUrl;
        }
        return super.determineTargetUrl(request);
    }

    public void addListener(PostLoginObserver postLoginObserver) {
        this.postLoginListeners.add(postLoginObserver);
    }

    public void removeListener(PostLoginObserver postLoginObserver) {
        if (this.postLoginListeners.contains(postLoginObserver)) {
            this.postLoginListeners.remove(postLoginObserver);
        }
    }

    public void notifyListeners(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        for (Iterator<PostLoginObserver> iter = postLoginListeners.iterator(); iter.hasNext();) {
            PostLoginObserver listener = iter.next();
            listener.process(request, response, authResult);
        }
    }

    public void setPasswordChangeUri(String passwordChangeUri) {
        this.passwordChangeUri = passwordChangeUri;
    }

    public void setLoginSuccessUrl(HttpServletRequest request, String refererUrl) {
        request.getSession().setAttribute(BC_LOGIN_SUCCESS_URL_KEY, refererUrl);
    }

    public void removeLoginSuccessUrl(HttpServletRequest request) {
        request.getSession().removeAttribute(BC_LOGIN_SUCCESS_URL_KEY);
    }

    public void setLoginSuccessUrlWithReferer(HttpServletRequest request) {
        setLoginSuccessUrl(request, request.getHeader("Referer"));
    }
}
