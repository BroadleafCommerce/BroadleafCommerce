/*
 * #%L
 * BroadleafCommerce Profile Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.web.site.security;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.encryption.EncryptionModule;
import org.broadleafcommerce.common.security.RandomGenerator;
import org.broadleafcommerce.common.security.util.CookieUtils;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter used to protected against session fixation attacks while still keeping the same session id on both
 * http and https protocols. Uses a secondary, https cookie that must be present on every https request for a
 * given session after the first request. If it's not present and equal to what we expect, we will redirect the
 * user to "/" and remove his session cookie.
 *
 * @author Andre Azzolini (apazzolini)
 *
 *
 * @deprecated use either {@link https://javadoc.io/doc/org.springframework.security/spring-security-web/latest/org/springframework/security/web/authentication/session/SessionFixationProtectionStrategy.html} instead
 */
@Deprecated
@Component("blSessionFixationProtectionFilter")
public class SessionFixationProtectionFilter extends GenericFilterBean {

    private static final Log LOG = LogFactory.getLog(SessionFixationProtectionFilter.class);

    protected static final String SESSION_ATTR = "SFP-ActiveID";

    @Autowired
    @Qualifier("blSessionFixationEncryptionModule")
    protected EncryptionModule encryptionModule;

    @Autowired
    @Qualifier("blCookieUtils")
    protected CookieUtils cookieUtils;

    @Override
    public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) sRequest;
        HttpServletResponse response = (HttpServletResponse) sResponse;
        HttpSession session = request.getSession(false);

        if (SecurityContextHolder.getContext() == null) {
            chain.doFilter(request, response);
        }

        String activeIdSessionValue = (session == null) ? null : (String) session.getAttribute(SESSION_ATTR);

        if (StringUtils.isNotBlank(activeIdSessionValue) && request.isSecure()) {
            // The request is secure and and we've set a session fixation protection cookie

            String activeIdCookieValue = cookieUtils.getCookieValue(request, SessionFixationProtectionCookie.COOKIE_NAME);
            String decryptedActiveIdValue = encryptionModule.decrypt(activeIdCookieValue);

            if (!activeIdSessionValue.equals(decryptedActiveIdValue)) {
                abortUser(request, response);
                LOG.info("Session has been terminated. ActiveID did not match expected value.");
                return;
            }
        } else if (request.isSecure() && session != null) {
            // If there is no session (session == null) then there isn't anything to worry about

            // The request is secure, but we haven't set a session fixation protection cookie yet
            String token;
            try {
                token = RandomGenerator.generateRandomId("SHA1PRNG", 32);
            } catch (NoSuchAlgorithmException e) {
                throw new ServletException(e);
            }

            String encryptedActiveIdValue = encryptionModule.encrypt(token);

            session.setAttribute(SESSION_ATTR, token);
            cookieUtils.setCookieValue(response, SessionFixationProtectionCookie.COOKIE_NAME, encryptedActiveIdValue, "/", -1, true);
        }

        chain.doFilter(request, response);
    }

    protected void abortUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecurityContextHolder.clearContext();
        cookieUtils.invalidateCookie(response, SessionFixationProtectionCookie.COOKIE_NAME);
        if (BLCRequestUtils.isOKtoUseSession(new ServletWebRequest(request))) {
            request.getSession().invalidate();
        }
        response.sendRedirect("/");
    }

}
