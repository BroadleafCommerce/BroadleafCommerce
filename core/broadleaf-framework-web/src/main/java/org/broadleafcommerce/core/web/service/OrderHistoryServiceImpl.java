/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.service;

import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Jacob Mitash
 */
@Service("blOrderHistoryService")
public class OrderHistoryServiceImpl implements OrderHistoryService {

    @Value("${validate.customer.owned.data:true}")
    protected boolean validateCustomerOwnedData;

    protected static String orderHistoryPageParameter = "page";
    protected static String orderHistoryQueryParameter = "query";
    protected static String orderHistoryDateStartParameter = "dateStart";
    protected static String orderHistoryDateEndParameter = "dateEnd";
    protected static String orderHistoryDateFilterParameter = "dateFilter";
    protected static String orderHistoryResultParameter = "result";
    protected static DateFormat filterDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    protected static int itemsPerPage = 10;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Override
    public List<Order> getOrderHistory(Map<String, String[]> parameterMap, Map<String, Object> modelAttributes) {
        Map<String, String> parameters = singleValueParameterMap(parameterMap);

        int page = getCurrentPage(parameters.get(orderHistoryPageParameter));

        List<Order> orders;

        String dateFilter = parameters.get(orderHistoryDateFilterParameter);
        String query = parameters.get(orderHistoryQueryParameter);
        String dateStartParam = parameters.get(orderHistoryDateStartParameter);
        String dateEndParam = parameters.get(orderHistoryDateEndParameter);

        //Date filtering
        if (isDateFiltered(dateFilter, dateStartParam, dateEndParam)) {
            orders = filterDates(dateStartParam, dateEndParam);
        } else {
            orders = orderService.findOrdersForCustomer(CustomerState.getCustomer());
        }

        //Query filtering
        if (isSearch(query)) {
            orders = filterSearch(orders, query);
        }

        final int totalOrders = orders.size();
        if (!orders.isEmpty() && page > 0 && page <= orders.size() / itemsPerPage + 1) {
            orders = orders.subList((page - 1) * itemsPerPage, Math.min(orders.size(), page * itemsPerPage));
        }


        //Build search result so pagination works
        final List<Order> finalOrders = orders;
        SearchResult result = new SearchResult() {
            @Override
            public Integer getStartResult() {
                return itemsPerPage * (page - 1) + (finalOrders.isEmpty() ? 0 : 1);
            }

            @Override
            public Integer getTotalPages() {
                return totalOrders / itemsPerPage + 1;
            }
        };
        result.setPage(page);
        result.setPageSize(itemsPerPage);
        result.setTotalResults(totalOrders);

        //Keep the previously selected options and add result
        modelAttributes.put(orderHistoryResultParameter, result);
        modelAttributes.put(orderHistoryDateFilterParameter, dateFilter);
        modelAttributes.put(orderHistoryQueryParameter, query);
        modelAttributes.put(orderHistoryDateStartParameter, dateStartParam);
        modelAttributes.put(orderHistoryDateEndParameter, dateEndParam);
        modelAttributes.put(orderHistoryPageParameter, page);

        return orders;
    }

    @Override
    public List<Order> filterOrdersByStatus(List<Order> orders, List<OrderStatus> orderStatuses) {
        List<Order> filteredOrders = new ArrayList<>(orders);
        for(Iterator<Order> iterator = orders.iterator(); iterator.hasNext();) {
            Order order = iterator.next();
            validateCustomerOwnedData(order);
            if(!orderStatuses.contains(order.getStatus())) {
                iterator.remove();
            }
        }
        return filteredOrders;
    }

    @Override
    public void validateCustomerOwnedData(Order order) {
        if (validateCustomerOwnedData) {
            Customer activeCustomer = CustomerState.getCustomer();
            if (activeCustomer != null && !(activeCustomer.equals(order.getCustomer()))) {
                throw new SecurityException("The active customer does not own the object that they are trying to view, edit, or remove.");
            } else if (activeCustomer == null && order.getCustomer() != null) {
                throw new SecurityException("The active customer does not own the object that they are trying to view, edit, or remove.");
            }
        }
    }

    public void viewOrderDetails(HttpServletRequest request, Model model, String orderNumber) {
        Order order = orderService.findOrderByOrderNumber(orderNumber);
        if (order == null) {
            throw new IllegalArgumentException("The orderNumber provided is not valid");
        }

        validateCustomerOwnedData(order);

        model.addAttribute("order", order);
    }

    protected int getCurrentPage(String pageParameter) {
        return pageParameter == null ? 1 : Integer.parseInt(pageParameter);
    }

    protected boolean isDateFiltered(String filterParam, String dateStartParam, String dateEndParam) {
        if (filterParam != null) {
            if (dateStartParam == null || dateEndParam == null) {
                throw new InvalidParameterException("Start and end date parameters were null");
            }
            return true;
        } else {
            return false;
        }
    }

    protected boolean isSearch(String query) {
        return query != null && !query.trim().isEmpty();
    }

    protected List<Order> filterSearch(List<Order> originalOrders, String query) {
        List<Order> matchingOrders = new ArrayList<>();
        if (query.matches("[0-9]+")) {
            //SKU or order ID
            for (Order order : originalOrders) {
                boolean match = order.getOrderNumber().equals(query);
                if (!match) {
                    for (DiscreteOrderItem orderItem : order.getDiscreteOrderItems()) {
                        if (orderItem.getSku().getId().equals(Long.parseLong(query))) {
                            match = true;
                            break;
                        }
                    }
                }
                if (match) {
                    matchingOrders.add(order);
                }
            }
        } else {
            //Product name
            for (Order order : originalOrders) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    if (orderItem.getName().toLowerCase().contains(query.toLowerCase())) {
                        matchingOrders.add(order);
                    }
                }
            }
        }
        return matchingOrders;
    }

    protected List<Order> filterDates(String dateStartParam, String dateEndParam) {
        try {
            Date startDate = filterDateFormat.parse(dateStartParam + " 00:00:00"); //Start of day
            Date endDate = filterDateFormat.parse(dateEndParam + " 23:59:59"); //Last second of the day

            return orderService.findOrdersForCustomersInDateRange(Collections.singletonList(CustomerState.getCustomer().getId()), startDate, endDate);
        } catch (ParseException p) {
            throw new InvalidParameterException("The start/end date was specified in an invalid format");
        }
    }

    protected Map<String, String> singleValueParameterMap(Map<String, String[]> parameterMap) {
        Map<String, String> singleValueMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length > 0) {
                singleValueMap.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        return singleValueMap;
    }

}
