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

package org.broadleafcommerce.profile.web.core;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.security.CustomerStateRequestProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Convenient class to get the active customer from the current request. This state is kept up-to-date in regards to the database
 * throughout the lifetime of the request via the {@link CustomerStateRefresher}.
 *
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blCustomerState")
public class CustomerState {

    public static Customer getCustomer(HttpServletRequest request) {
        return getCustomer(new ServletWebRequest(request));
    }
    
    public static Customer getCustomer(WebRequest request) {
        return (Customer) request.getAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName(), WebRequest.SCOPE_REQUEST);
    }
    
    /**
     * Utilizes the current BroadleafRequestContext to lookup a customer from the request.
     * @return
     */
    public static Customer getCustomer() {
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        return (Customer) request.getAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName(), WebRequest.SCOPE_REQUEST);
    }
    
    public static void setCustomer(Customer customer) {
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        request.setAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName(), customer, WebRequest.SCOPE_REQUEST);
    }

}