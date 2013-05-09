/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.web.core.service;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafWebRequestProcessor;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;


@Service("blLoginService")
public class LoginServiceImpl implements LoginService {

    @Resource(name="blAuthenticationManager")
    private AuthenticationManager authenticationManager;
    
    @Resource(name="blUserDetailsService")
    private UserDetailsService userDetailsService;

    @Resource(name = "blCartStateRequestProcessor")
    protected BroadleafWebRequestProcessor cartStateRequestProcessor;

    @Resource(name = "blCustomerStateRequestProcessor")
    private BroadleafWebRequestProcessor customerStateRequestProcessor;

    @Override
    public Authentication loginCustomer(Customer customer) {
        return loginCustomer(customer.getUsername(), customer.getUnencodedPassword());
    }
    
    @Override
    public Authentication loginCustomer(String username, String clearTextPassword) {
        UserDetails principal = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, clearTextPassword, principal.getAuthorities());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        customerStateRequestProcessor.process(getWebRequest());
        cartStateRequestProcessor.process(getWebRequest());
        return authentication;
    }

    @Override
    public void logoutCustomer() {
        SecurityContextHolder.getContext().setAuthentication(null);
        customerStateRequestProcessor.process(getWebRequest());
        cartStateRequestProcessor.process(getWebRequest());
    }

    protected WebRequest getWebRequest() {
        return BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
    }

}
