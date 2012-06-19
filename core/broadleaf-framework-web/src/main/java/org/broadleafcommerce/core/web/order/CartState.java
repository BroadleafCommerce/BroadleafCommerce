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

package org.broadleafcommerce.core.web.order;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.web.order.security.CartStateFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component("blCartState")
public class CartState {

    public static Order getCart(HttpServletRequest request) {
        return (Order) request.getAttribute(CartStateFilter.getCartRequestAttributeName());
    }
    
    public static void setCart(HttpServletRequest request, Order cart) {
        request.setAttribute(CartStateFilter.getCartRequestAttributeName(), cart);
    }

}