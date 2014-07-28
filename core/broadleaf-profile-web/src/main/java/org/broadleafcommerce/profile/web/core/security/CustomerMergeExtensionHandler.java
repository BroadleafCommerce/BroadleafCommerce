/*
 * #%L
 * BroadleafCommerce Profile Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.profile.web.core.security;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.web.context.request.WebRequest;

public interface CustomerMergeExtensionHandler extends ExtensionHandler {

    /**
     * This allows multiple extensions to modify or copy attributes from the anonymous customer, to the 
     * customer.  The customer is stored on the customerHolder.  If the customer is modified and / or saved to the DB, 
     * the new instance must be reset on the customerHolder.  Each implementation can expect that the result holder will 
     * have the latest version of the customer.  Implementors are not required to save the customer.  
     * It is the responsibility of each implementation to reset the reference.
     * Additionally and alternatively, this method allows for copying and / or modification of the request 
     * attributes from session or request.  Implementors should carefully consider security implications of 
     * copying customer data.  
     * 
     * The anonymous customer may be null.  If the request parameter, the customerHolder parameter, or the customer stored 
     * on the customerHolder is null, then an IllegalArgumentException should be thrown.
     * 
     * The return value, generally, should be ExtensionResultStatusType.HANDLED_CONTINUE.  If an implementation wishes 
     * to return an exception in the customerHolder, it should instantiate and set an exception on the customerHolder 
     * and return ExtensionResultStatusType.HANDLED_STOP.
     * 
     * @param customerHolder
     * @param request
     * @param anonymousCustomer
     * @return
     */
    public ExtensionResultStatusType merge(ExtensionResultHolder<Customer> customerHolder, WebRequest request, Customer anonymousCustomer);

}
