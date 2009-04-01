package org.broadleafcommerce.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.ui.logout.LogoutFilter;
import org.springframework.security.ui.logout.LogoutHandler;

public class BCLogoutFilter extends LogoutFilter {

    public static final String BC_LOGOUT_SUCCESS_URL_KEY = "broadleaf_commerce.logout_success_url";

    public BCLogoutFilter(String logoutSuccessUrl, LogoutHandler[] handlers) {
        super(logoutSuccessUrl, handlers);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String sessionUrl = (String) request.getSession().getAttribute(BC_LOGOUT_SUCCESS_URL_KEY);
        if (sessionUrl != null) {
            removeLogoutSuccessUrl(request);
            return sessionUrl;
        }
        return super.determineTargetUrl(request, response);
    }

    public void setLogoutSuccessUrl(HttpServletRequest request, String refererUrl) {
        request.getSession().setAttribute(BC_LOGOUT_SUCCESS_URL_KEY, refererUrl);
    }

    public void removeLogoutSuccessUrl(HttpServletRequest request) {
        request.getSession().removeAttribute(BC_LOGOUT_SUCCESS_URL_KEY);
    }

    public void setLogoutSuccessUrlWithReferer(HttpServletRequest request) {
        setLogoutSuccessUrl(request, request.getHeader("Referer"));
    }
}
