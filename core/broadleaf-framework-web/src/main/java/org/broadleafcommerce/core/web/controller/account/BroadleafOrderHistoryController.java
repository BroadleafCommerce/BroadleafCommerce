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
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BroadleafOrderHistoryController extends AbstractAccountController {

    @Value("${validate.customer.owned.data:true}")
    protected boolean validateCustomerOwnedData;

    protected static String orderHistoryView = "account/orderHistory";
    protected static String orderDetailsView = "account/partials/orderDetails";
    protected static String orderDetailsRedirectView = "account/partials/orderDetails";
    protected static String orderHistoryPageParameter = "page";
    protected static String orderHistoryDateStartParameter = "dateStart";
    protected static String orderHistoryDateEndParameter = "dateEnd";
    protected static String orderHistoryDateFilterParameter = "dateFilter";
    protected static DateFormat filterDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    protected static int itemsPerPage = 10;

    public String viewOrderHistory(HttpServletRequest request, Model model) {

        String pageNumber = request.getParameter(orderHistoryPageParameter);
        int page;
        if (pageNumber == null) {
            page = 1;
        } else {
            page = Integer.parseInt(pageNumber);
        }


        List<Order> orders;

        String filterParameter = request.getParameter(orderHistoryDateFilterParameter);
        if (filterParameter != null) {
            String dateStartParam = request.getParameter(orderHistoryDateStartParameter);
            String dateEndParam = request.getParameter(orderHistoryDateEndParameter);
            if(dateStartParam == null || dateEndParam == null) {
                throw new InvalidParameterException("Start and end date parameters were null");
            }
            if(CustomerState.getCustomer() == null || CustomerState.getCustomer().getId() == null) {
                throw new SecurityException("Unknown customer tried to access order history");
            }
            try {
                Date startDate = filterDateFormat.parse(dateStartParam + " 00:00:00"); //Start of day
                Date endDate = filterDateFormat.parse(dateEndParam + " 23:59:59"); //Last second of the day
                orders = orderService.findOrdersForCustomersInDateRange(Collections.singletonList(CustomerState.getCustomer().getId()), startDate, endDate);
            } catch (ParseException p) {
                throw new InvalidParameterException("The start/end date was specified in an invalid format");
            }
        } else {
            orders = orderService.findOrdersForCustomer(CustomerState.getCustomer(), OrderStatus.SUBMITTED);
        }

        if (!orders.isEmpty() && page > 0 && page <= orders.size() / itemsPerPage + 1) {
            orders = orders.subList((page - 1) * 10, Math.min(orders.size(), page * 10));
        }

        model.addAttribute("orders", orders);
        return getOrderHistoryView();
    }

    public String viewOrderDetails(HttpServletRequest request, Model model, String orderNumber) {
        Order order = orderService.findOrderByOrderNumber(orderNumber);
        if (order == null) {
            throw new IllegalArgumentException("The orderNumber provided is not valid");
        }

        validateCustomerOwnedData(order);

        model.addAttribute("order", order);
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

    protected void validateCustomerOwnedData(Order order) {
        if (validateCustomerOwnedData) {
            Customer activeCustomer = CustomerState.getCustomer();
            if (activeCustomer != null && !(activeCustomer.equals(order.getCustomer()))) {
                throw new SecurityException("The active customer does not own the object that they are trying to view, edit, or remove.");
            } else if (activeCustomer == null && order.getCustomer() != null) {
                throw new SecurityException("The active customer does not own the object that they are trying to view, edit, or remove.");
            }
        }
    }
}
