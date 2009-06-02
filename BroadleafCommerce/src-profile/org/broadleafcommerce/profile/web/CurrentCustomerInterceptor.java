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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CurrentCustomerInterceptor extends HandlerInterceptorAdapter {

    private final static String CUSTOMER_REQUEST_ATTR_NAME = "customer";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
        CustomerState customerState = (CustomerState) applicationContext.getBean("customerState");
        Customer requestCustomer = null;
        checkSession: {
            Customer sessionCustomer = customerState.getCustomer(request);
            if (sessionCustomer != null) {
                requestCustomer = sessionCustomer;
                break checkSession;
            }
            String cookieCustomerIdVal = CookieUtils.getCookieValue(request, CookieUtils.CUSTOMER_COOKIE_NAME);
            Long cookieCustomerId = null;
            if (cookieCustomerIdVal != null) {
                cookieCustomerId = new Long(cookieCustomerIdVal);
            }

            CustomerService customerService = (CustomerService) applicationContext.getBean("customerService");
            if (cookieCustomerId != null) {
                Customer cookieCustomer = customerService.createCustomerFromId(cookieCustomerId);
                customerState.setCustomer(cookieCustomer, request);
                requestCustomer = cookieCustomer;
                break checkSession;
            } else {
                // if no customer in session or cookie, create a new one
                Customer firstTimeCustomer = customerService.createCustomerFromId(null);
                CookieUtils.setCookieValue(response, CookieUtils.CUSTOMER_COOKIE_NAME, firstTimeCustomer.getId() + "", "/", 604800);
                customerState.setCustomer(firstTimeCustomer, request);
                requestCustomer = firstTimeCustomer;
                break checkSession;
            }
        }
        request.setAttribute(CUSTOMER_REQUEST_ATTR_NAME, requestCustomer);
        return true;
    }
}
