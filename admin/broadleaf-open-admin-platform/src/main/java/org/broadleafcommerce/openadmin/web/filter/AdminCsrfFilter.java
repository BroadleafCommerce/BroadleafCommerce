package org.broadleafcommerce.openadmin.web.filter;


import org.broadleafcommerce.common.security.handler.CsrfFilter;
import org.broadleafcommerce.openadmin.security.BroadleafAdminAuthenticationFailureHandler;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author trevorleffert
 *
 */

public class AdminCsrfFilter extends CsrfFilter {
    
    @Resource(name = "blAdminAuthenticationFailureHandler")
    BroadleafAdminAuthenticationFailureHandler failureHandler;
    
    public void doFilter(ServletRequest baseRequest, ServletResponse baseResponse, FilterChain chain) throws IOException, ServletException {
        try {
            super.doFilter(baseRequest, baseResponse, chain);
        } catch (ServletException e) {
            SecurityContextRepository repo = new HttpSessionSecurityContextRepository();
            HttpServletRequest baseHttpRequest = (HttpServletRequest) baseRequest;
            HttpRequestResponseHolder holder = new HttpRequestResponseHolder(baseHttpRequest, (HttpServletResponse) baseResponse);
            SecurityContext securityContext = repo.loadContext(holder);
            //if authentication is null and CSRF token is invalid, must be session time out
            if (securityContext.getAuthentication() == null) {
                baseHttpRequest.setAttribute("sessionTimeout", true);
                failureHandler.onAuthenticationFailure((HttpServletRequest) baseRequest, (HttpServletResponse) baseResponse, new AuthenticationException("Session Time Out") {
                    private static final long serialVersionUID = 1L;
                });
            } else {
                //If session is determined to not be a timeout, redirect to users previous location
                String previousLocation = baseHttpRequest.getHeader("referer");
                HttpServletResponse response = (HttpServletResponse) baseResponse;
                response.sendRedirect(previousLocation);
            }
        }
    }

}
