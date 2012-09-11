/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.admin.web.controller;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafCustomerCareController extends BroadleafAbstractController {

    protected static String customerView = "/blcadmin/customerCare/customer";
    protected static String orderView = "/blcadmin/customerCare/order";

    public String customer(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getCustomerView();
    }

    public String order(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getOrderView();
    }

    public static String getCustomerView() {
        return customerView;
    }

    public static void setCustomerView(String customerView) {
        BroadleafCustomerCareController.customerView = customerView;
    }

    public static String getOrderView() {
        return orderView;
    }

    public static void setOrderView(String orderView) {
        BroadleafCustomerCareController.orderView = orderView;
    }
}
