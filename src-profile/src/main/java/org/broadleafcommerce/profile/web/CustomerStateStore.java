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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.security.PostLoginObserver;
import org.broadleafcommerce.profile.web.security.PreLogoutObserver;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("blCustomerStateStore")
public class CustomerStateStore implements PostLoginObserver, PreLogoutObserver {

    @Resource(name="blCustomerService")
    private CustomerService customerService;

    @Resource(name="blCustomerState")
    private CustomerState customerState;

    @Resource(name="blCookieUtils")
    private CookieUtils cookieUtils;

    public void processPostLogin(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        Customer customer = customerService.readCustomerByUsername((String) authResult.getPrincipal());
        cookieUtils.setCookieValue(response, CookieUtils.CUSTOMER_COOKIE_NAME, customer.getId() + "","/",604800);
        customerState.setCustomer(customer, request);
    }

    public void processPreLogout(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        cookieUtils.invalidateCookie(response, CookieUtils.CUSTOMER_COOKIE_NAME);
    }
}
