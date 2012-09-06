/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.common.security.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Andre Azzolini (apazzolini)
 */
public class CsrfFilter extends GenericFilterBean {
    protected static final Log LOG = LogFactory.getLog(CsrfFilter.class);
	
    @Resource(name="blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

	@Override
	public void doFilter(ServletRequest baseRequest, ServletResponse baseResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) baseRequest;
		HttpServletResponse response = (HttpServletResponse) baseResponse;
		
		// We only validate CSRF tokens on POST
        if (request.getMethod().equals("POST")) {
			String requestToken = request.getParameter(exploitProtectionService.getCsrfTokenParameter());
			try {
				exploitProtectionService.compareToken(requestToken);
			} catch (ServiceException e) {
				throw new ServletException(e);
			}
		}
        
        chain.doFilter(request, response);
	}
}
