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

package org.broadleafcommerce.core.web.controller.checkout;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafOrderConfirmationController extends BroadleafAbstractController {

    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    protected static String orderConfirmationView = "checkout/confirmation";

    public String displayOrderConfirmationByOrderNumber(String orderNumber, Model model,
             HttpServletRequest request, HttpServletResponse response) {
        Customer customer = CustomerState.getCustomer();
        if (customer != null) {
            Order order = orderService.findOrderByOrderNumber(orderNumber);
            if (order != null && customer.equals(order.getCustomer())) {
                model.addAttribute("order", order);
                return getOrderConfirmationView();
            }
        }
        return null;
    }

    public String displayOrderConfirmationByOrderId(Long orderId, Model model,
             HttpServletRequest request, HttpServletResponse response) {

        Customer customer = CustomerState.getCustomer();
        if (customer != null) {
            Order order = orderService.findOrderById(orderId);
            if (order != null && customer.equals(order.getCustomer())) {
                model.addAttribute("order", order);
                return getOrderConfirmationView();
            }
        }
        return null;
    }

    public String getOrderConfirmationView() {
        return orderConfirmationView;
    }

}
