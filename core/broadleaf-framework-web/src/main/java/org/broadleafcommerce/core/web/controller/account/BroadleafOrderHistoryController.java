/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.web.service.OrderHistoryService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadleafOrderHistoryController extends AbstractAccountController {

    @Value("${validate.customer.owned.data:true}")
    protected boolean validateCustomerOwnedData;

    protected static String orderHistoryView = "account/orderHistory";
    protected static String orderDetailsView = "account/partials/orderDetails";
    protected static String orderDetailsRedirectView = "account/partials/orderDetails";

    @Resource(name = "blOrderHistoryService")
    protected OrderHistoryService orderHistoryService;

    public String viewOrderHistory(HttpServletRequest request, Model model) {
        if (CustomerState.getCustomer() == null) {
            throw new SecurityException("Null customer tried to access order history");
        }
        Map<String, Object> modelAttributes = new HashMap<>();
        List<Order> orders = orderHistoryService.getOrderHistory(request.getParameterMap(), modelAttributes);

        for (Order order : orders) {
            orderHistoryService.validateCustomerOwnedData(order);
        }

        model.addAllAttributes(modelAttributes);
        model.addAttribute("orders", orders);
        return getOrderHistoryView();
    }

    public String viewOrderDetails(HttpServletRequest request, Model model, String orderNumber) {
        orderHistoryService.viewOrderDetails(request, model, orderNumber);
        return isAjaxRequest(request) ? getOrderDetailsView() : getOrderDetailsRedirectView();
    }

    public String getOrderHistoryView() {
        return orderHistoryView;
    }

    public String getOrderDetailsView() {
        return orderDetailsView;
    }

    public String getOrderDetailsRedirectView() {
        return orderDetailsRedirectView;
    }
}
