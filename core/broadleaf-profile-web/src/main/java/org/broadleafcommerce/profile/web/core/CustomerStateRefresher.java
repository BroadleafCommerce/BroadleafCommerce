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
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPersistedEvent;
import org.broadleafcommerce.profile.web.core.security.CustomerStateRequestProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;


/**
 * {@link ApplicationListener} responsible for updating {@link CustomerState} as well as invalidating the session-based
 * customer, if one existed previously. For instance, when originally browsing the catalog a Customer is created but only
 * stored in session and retrieved from session on subsequent requests. However, once the Customer has been persisted
 * (like when they add something to the cart) then this component is responsible for updating {@link CustomerState} as well
 * as invalidating the session-based customer.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blCustomerStateRefresher")
public class CustomerStateRefresher implements ApplicationListener<CustomerPersistedEvent> {

    /**
     * Removes the complete {@link Customer} stored in session and adds a new session variable for just the customer ID. This
     * should occur once the session-based {@link Customer} (all anonymous Customers start out this way) has been persisted.
     * 
     * <p>Also updates {@link CustomerState} with the persisted {@link Customer} so that it will always represent the most
     * up-to-date version that is in the database</p>
     * 
     * @param request
     * @param databaseCustomer
     */
    @Override
    public void onApplicationEvent(final CustomerPersistedEvent event) {
        Customer dbCustomer = event.getCustomer();

        //if there is an active request, remove the session-based customer if it exists and update CustomerState
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        if (request != null) {
            String customerAttribute = CustomerStateRequestProcessor.getAnonymousCustomerSessionAttributeName();
            String customerIdAttribute = CustomerStateRequestProcessor.getAnonymousCustomerIdSessionAttributeName();
            Customer sessionCustomer = (Customer) request.getAttribute(customerAttribute, WebRequest.SCOPE_GLOBAL_SESSION);
            //invalidate the session-based customer if it's there and the ID is the same as the Customer that has been
            //persisted
            if (sessionCustomer != null && sessionCustomer.getId().equals(dbCustomer.getId())) {
                request.removeAttribute(customerAttribute, WebRequest.SCOPE_GLOBAL_SESSION);
                request.setAttribute(customerIdAttribute, dbCustomer.getId(), WebRequest.SCOPE_GLOBAL_SESSION);
            }
            
            //Update CustomerState if the persisted Customer ID is the same
            if (CustomerState.getCustomer() != null && CustomerState.getCustomer().getId().equals(dbCustomer.getId())) {
                CustomerState.setCustomer(event.getCustomer());
            }
        }
    }
    
}
