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
package org.broadleafcommerce.profile.web;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;

public class CurrentCustomerFilter implements Filter  {

    private final static String CUSTOMER_REQUEST_ATTR_NAME = "customer";

    @Resource(name="blCustomerState")
    protected CustomerState customerState;

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Customer requestCustomer = null;
        checkSession: {
            Customer sessionCustomer = customerState.getCustomer((HttpServletRequest) request);
            if (sessionCustomer != null) {
                requestCustomer = sessionCustomer;
                break checkSession;
            }
            String cookieCustomerIdVal = CookieUtils.getCookieValue((HttpServletRequest) request, CookieUtils.CUSTOMER_COOKIE_NAME);
            Long cookieCustomerId = null;
            if (cookieCustomerIdVal != null) {
                cookieCustomerId = new Long(cookieCustomerIdVal);
            }

            if (cookieCustomerId != null) {
                Customer cookieCustomer = customerService.createCustomerFromId(cookieCustomerId);
                customerState.setCustomer(cookieCustomer, (HttpServletRequest) request);
                requestCustomer = cookieCustomer;
                break checkSession;
            } else {
                // if no customer in session or cookie, create a new one
                Customer firstTimeCustomer = customerService.createCustomerFromId(null);
                CookieUtils.setCookieValue((HttpServletResponse) response, CookieUtils.CUSTOMER_COOKIE_NAME, firstTimeCustomer.getId() + "", "/", 604800);
                customerState.setCustomer(firstTimeCustomer, (HttpServletRequest) request);
                requestCustomer = firstTimeCustomer;
                break checkSession;
            }
        }
        request.setAttribute(CUSTOMER_REQUEST_ATTR_NAME, requestCustomer);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
