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

package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

public class BroadleafOrderHistoryController extends BroadleafAbstractController {
	
    @Resource(name="blCartService")
    protected CartService cartService;

    public String viewOrderHistory(Model model, HttpServletRequest request) {
        List<Order> orders = cartService.findOrdersForCustomer(CustomerState.getCustomer(request), OrderStatus.SUBMITTED);
        model.addAttribute("orderList", orders);
        return "orderHistory";
    }

    public String viewOrderDetails(Model model, HttpServletRequest request, 
    		String orderNumber) {
        Order order = cartService.findOrderByOrderNumber(orderNumber);
        if (order == null) {
        	throw new IllegalArgumentException("The orderNumber provided is not valid");
        }

        model.addAttribute("order", order);
        return "orderDetails";
    }
    
}
