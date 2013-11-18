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
package org.broadleafcommerce.core.web.order;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.web.order.security.CartStateRequestProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component("blCartState")
public class CartState {

    /**
     * Gets the current cart based on the current request
     * 
     * @return the current customer's cart
     */
    public static Order getCart() {
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        return (Order) request.getAttribute(CartStateRequestProcessor.getCartRequestAttributeName(), WebRequest.SCOPE_REQUEST);
    }
    
    /**
     * Sets the current cart on the current request
     * 
     * @param cart the new cart to set
     */
    public static void setCart(Order cart) {
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        request.setAttribute(CartStateRequestProcessor.getCartRequestAttributeName(), cart, WebRequest.SCOPE_REQUEST);
    }

}