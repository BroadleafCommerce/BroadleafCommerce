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

package org.broadleafcommerce.profile.web.core.security;

import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.core.Ordered;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This is a basic filter for finding the customer ID on the request and setting the customer object on the request.  This should come after any security filters.
 * This filter DOES NOT provide any security.  It simply looks for a "customerId" parameter on the request or in the request header.  If it finds 
 * this parameter it looks up the customer and makes it available as a request attribute.  This is generally for use in a filter chain for RESTful web services, 
 * allowing the client consuming services to specify the customerId on whos behalf they are invoking the service.  It is assumed that services are invoked either 
 * in a trusted, secured network where no additional security is required.  Or using OAuth or a similar trusted security model.  Whatever security model is used,
 * it should ensure that the caller has access to call the system, and that they have access to do so on behalf of the client whos ID is being determined by this class.
 *
 * For RESTful services, this should be used instead of CustomerStateFilter since it does not look at or touch cookies or session.
 * 
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/18/12
 */
public class RestApiCustomerStateFilter extends GenericFilterBean implements Ordered {

    @Resource(name="blCustomerService")
    private CustomerService customerService;
    
    private String customerIdAttributeName = "customerId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        
        String customerId = null;

        HttpServletRequest request = (HttpServletRequest)servletRequest;
        
        //If someone already set the customer on the request then we don't need to do anything.
        if (request.getAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName()) == null){
    
            //First check to see if someone already put the customerId on the request
            if (request.getAttribute(customerIdAttributeName) != null) {
                customerId = String.valueOf(request.getAttribute(customerIdAttributeName));
            }
            
            if (customerId == null) {
                //If it's not on the request attribute, try the parameter
                customerId = servletRequest.getParameter(customerIdAttributeName);
            }
            
            if (customerId == null) {
                //If it's not on the request parameter, look on the header
                customerId = request.getHeader(customerIdAttributeName);
            }
            
            if (customerId != null && customerId.trim().length() > 0) {

                //If we found it, look up the customer and put it on the request.
                Customer customer = customerService.readCustomerById(Long.valueOf(customerId));
                if (customer != null) {
                    servletRequest.setAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName(), customer);
                }
            }

        }

        filterChain.doFilter(request, servletResponse);

    }

    @Override
    public int getOrder() {
        return 2000;
    }

    public String getCustomerIdAttributeName() {
        return customerIdAttributeName;
    }

    public void setCustomerIdAttributeName(String customerIdAttributeName) {
        if (customerIdAttributeName == null || customerIdAttributeName.trim().length() < 1) {
            throw new IllegalArgumentException("customerIdAttributeName cannot be null");
        }
        this.customerIdAttributeName = customerIdAttributeName;
    }
}
