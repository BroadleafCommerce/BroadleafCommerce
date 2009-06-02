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
package org.broadleafcommerce.profile.web.security;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.CustomerCookie;
import org.springframework.security.Authentication;
import org.springframework.security.ui.rememberme.AbstractRememberMeServices;
import org.springframework.security.ui.rememberme.RememberMeAuthenticationException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

public class BroadleafRememberMeServices extends AbstractRememberMeServices {

    @Resource
    private CustomerService customerService;

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        Customer customer = customerService.readCustomerByUsername((String) successfulAuthentication.getPrincipal());
        CustomerCookie.getInstance().write(response, customer.getId());
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {
        if (!CustomerCookie.getInstance().isValid(request)) {
            throw new RememberMeAuthenticationException("Invalid Authentication");
        }

        Long customerId = CustomerCookie.getInstance().getCustomerIdFromCookie(request);
        Customer customer = customerService.readCustomerById(customerId);
        return this.getUserDetailsService().loadUserByUsername(customer.getUsername());
    }

}
