/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Sets the Mapped Diagnostic Context (MDC) for Log4J.  By default the following attributes will be in the context:
 * 
 * userName - the spring security context username, "anonymousUser", or "none" if there is no authentication principal
 * threadName - the name of the currently executing thread
 * sessionId - the id from the current {@link javax.servlet.http.HttpSession}
 * serverName - the server name as identified in the {@link javax.servlet.ServletRequest}
 * ipAddress - the remote address as identified in the {@link javax.servlet.ServletRequest}
 * 
 * These attributes can then be used in the Log4J output pattern using X{attribute_name}
 * For example:
 *
 *{@code
 *        <layout class="org.apache.log4j.PatternLayout">
 *           <param name="ConversionPattern" value="[%5p] %d{HH:mm:ss} %c{1} [X{serverName}] - %m%n" />
 *       </layout>
 *}
 *   
 * 
 * @author Daniel Colgrove (dcolgrove)
 */
@Component("blLog4jMappedDiagnosticContextFilter")
public class Log4jMappedDiagnosticContextFilter extends GenericFilterBean {
  
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            MDC.put("userName", authentication.getName());
        } else {
            MDC.put("userName", "none");
        }
        HttpSession session = httpRequest.getSession(false);
        String sessionId = "none";
        if (session != null) {
            sessionId = session.getId();
        }
        MDC.put("sessionId", sessionId);
        MDC.put("threadName", Thread.currentThread().getName());
        MDC.put("serverName", httpRequest.getServerName());
        MDC.put("ipAddress", httpRequest.getRemoteAddr());        
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (authentication != null) {
                MDC.remove("userName");
                MDC.remove("sessionId");
                MDC.remove("threadName");
                MDC.remove("serverName");
                MDC.remove("ipAddress");
            }
        }
    }
}
