/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.core.security;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.encryption.EncryptionModule;
import org.broadleafcommerce.common.security.RandomGenerator;
import org.broadleafcommerce.common.security.util.CookieUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;
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
 */
@Component("blSessionFixationProtectionFilter")
public class SessionFixationProtectionFilter extends GenericFilterBean {

    private static final Log LOG = LogFactory.getLog(SessionFixationProtectionFilter.class);

    protected static final String SESSION_ATTR = "SFP-ActiveID";
    
    @Resource(name = "blSessionFixationEncryptionModule")
    protected EncryptionModule encryptionModule;

    @Resource(name = "blCookieUtils")
    protected CookieUtils cookieUtils;

    @Override
    public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) sRequest;
        HttpServletResponse response = (HttpServletResponse) sResponse;
        HttpSession session = request.getSession(false);
        
        if (SecurityContextHolder.getContext() == null) {
            chain.doFilter(request, response);
        }
        

        String activeIdSessionValue = session == null ? null : (String) session.getAttribute(SESSION_ATTR);
        
        if (StringUtils.isNotBlank(activeIdSessionValue) && request.isSecure()) {
            // The request is secure and and we've set a session fixation protection cookie

            String activeIdCookieValue = cookieUtils.getCookieValue(request, SessionFixationProtectionCookie.COOKIE_NAME);
            String decryptedActiveIdValue = encryptionModule.decrypt(activeIdCookieValue);
            
            if (!activeIdSessionValue.equals(decryptedActiveIdValue)) {
                abortUser(request, response);
                LOG.info("Session has been terminated. ActiveID did not match expected value.");
                return;
            }
        } else if (request.isSecure()) {
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
        request.getSession().invalidate();
        response.sendRedirect("/"); 
    }

}
