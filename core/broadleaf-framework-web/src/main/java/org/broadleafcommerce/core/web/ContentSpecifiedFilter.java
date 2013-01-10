/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author btaylor
 *
 */
public class ContentSpecifiedFilter implements Filter {
    private static String SESSION_SANDBOX_VAR = "BLC_CONTENT_SANDBOX";
    private static String SESSION_DATE_VAR = "BLC_CONTENT_DATE_TIME";
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {

    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        String sandbox = (String)request.getParameter("blcSandbox");
        String displayDate = (String)request.getParameter("blcCurrentTime");        
        HttpSession session = req.getSession(true);
        
        if(sandbox != null && sandbox != ""){
            session.setAttribute(SESSION_SANDBOX_VAR, sandbox);         
        }
        
        if(displayDate != null && displayDate != ""){
            session.setAttribute(SESSION_DATE_VAR, displayDate);            
        }
        
//      String currentSessionSandbox = (session != null)?(String)session.getAttribute(SESSION_SANDBOX_VAR):"bullocks";
//      String currentSessionDate = (session != null)?(String)session.getAttribute(SESSION_DATE_VAR):"bullocks";
//      if((currentSessionDate == null || currentSessionDate == "") &&
//         (currentSessionSandbox == null || currentSessionSandbox == "")){
//          req.getSession().removeAttribute("BLC_CONTENT_SANDBOX");
//          req.getSession().removeAttribute("BLC_CONTENT_DATE_TIME");
//      }
        filterChain.doFilter(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig arg0) throws ServletException {

    }

}
