/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.order.security;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.web.context.request.WebRequest;


/**
 * @author bpolster
 */
public interface CartStateRequestProcessorExtensionHandler extends ExtensionHandler {
    
    /**
     * Throws an exception if cart is invalid.
     * 
     * @param cart
     * @param customer (the current customer)
     * @param resultHolder
     * @return
     */
    public ExtensionResultStatusType lookupOrCreateCart(WebRequest request, Customer customer, ExtensionResultHolder<Order> resultHolder);

}
