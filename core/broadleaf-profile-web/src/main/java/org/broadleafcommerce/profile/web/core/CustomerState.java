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

package org.broadleafcommerce.profile.web.core;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.security.CustomerStateFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component("blCustomerState")
public class CustomerState {

    public static Customer getCustomer(HttpServletRequest request) {
        return (Customer) request.getAttribute(CustomerStateFilter.getCustomerRequestAttributeName());
    }
    
    /**
     * Utilizes the current BroadleafRequestContext to lookup a customer from the request.
     * @return
     */
    public static Customer getCustomer() {
    	HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        return (Customer) request.getAttribute(CustomerStateFilter.getCustomerRequestAttributeName());
    }

}