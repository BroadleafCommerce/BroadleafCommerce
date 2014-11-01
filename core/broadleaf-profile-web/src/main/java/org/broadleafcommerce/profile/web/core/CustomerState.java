/*
 * #%L
 * BroadleafCommerce Profile Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.profile.web.core;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafRequestCustomerResolverImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.stereotype.Component;
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
        return (Customer) BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().getCustomer(request);
    }
    
    public static Customer getCustomer(WebRequest request) {
        return (Customer) BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().getCustomer(request);
    }
    
    public static Customer getCustomer() {
        if (BroadleafRequestContext.getBroadleafRequestContext() == null
                || BroadleafRequestContext.getBroadleafRequestContext().getWebRequest() == null) {
            return null;
        }
        return (Customer) BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().getCustomer();
    }
    
    public static void setCustomer(Customer customer) {
        BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().setCustomer(customer);
    }

}
