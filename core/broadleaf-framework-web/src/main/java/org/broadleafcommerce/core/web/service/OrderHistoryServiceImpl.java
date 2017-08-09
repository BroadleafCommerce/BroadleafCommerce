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

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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

    protected final Environment environment;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Autowired
    public OrderHistoryServiceImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public List<Order> getOrderHistory(Map<String, String[]> parameterMap, Map<String, Object> modelAttributes, List<Order> startingOrders) {
        Map<String, String> parameters = singleValueParameterMap(parameterMap);

        int page = getCurrentPage(parameters.get(orderHistoryPageParameter));

        List<Order> orders;

        String dateFilter = parameters.get(orderHistoryDateFilterParameter);
        String query = parameters.get(orderHistoryQueryParameter);
        String dateStartParam = parameters.get(orderHistoryDateStartParameter);
        String dateEndParam = parameters.get(orderHistoryDateEndParameter);

        //Date filtering
        if(isDateFiltered(dateFilter, dateStartParam, dateEndParam)) {
            orders = filterDates(startingOrders, dateStartParam, dateEndParam);
        } else {
            orders = startingOrders;
        }

        //Query filtering
        if (isSearch(query)) {
            orders = filterSearch(orders, query);
        }

        final int totalOrders = orders.size();
        if (!orders.isEmpty() && page > 0 && page <= orders.size() / getItemsPerPage() + 1) {
            orders = orders.subList((page - 1) * getItemsPerPage(), Math.min(orders.size(), page * getItemsPerPage()));
        }

        //Build search result so pagination works
        final List<Order> finalOrders = orders;
        SearchResult result = new SearchResult() {
            @Override
            public Integer getStartResult() {
                return getItemsPerPage() * (page - 1) + (finalOrders.isEmpty() ? 0 : 1);
            }

            @Override
            public Integer getTotalPages() {
                return totalOrders / getItemsPerPage() + 1;
            }
        };
        result.setPage(page);
        result.setPageSize(getItemsPerPage());
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

    @Override
    public Order getOrderDetails(String orderNumber) {
        Order order = orderService.findOrderByOrderNumber(orderNumber);
        if (order == null) {
            throw new IllegalArgumentException("The orderNumber provided is not valid");
        }

        validateCustomerOwnedData(order);

        return order;
    }

    @Override
    public int getItemsPerPage() {
        String itemsPerPage = environment.getProperty("web.defaultPageSize", "15");
        try {
            return Integer.parseInt(itemsPerPage);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("web.defaultPageSize could not be parsed as an integer '" + itemsPerPage + "'");
        }
    }

    /**
     * Gets the current page number
     * @param pageParameter the "page" parameter from the request
     * @return the current page
     */
    protected int getCurrentPage(String pageParameter) {
        return pageParameter == null ? 1 : Integer.parseInt(pageParameter);
    }

    /**
     * Tells whether the results should be filtered by date
     * @param filterParam the string from the "filter date" checkbox
     * @param dateStartParam the starting date
     * @param dateEndParam the ending date
     * @return
     */
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

    /**
     * Tells if the request is a search
     * @param query The query string
     * @return true if the query is a search, false otherwise
     */
    protected boolean isSearch(String query) {
        return query != null && !query.trim().isEmpty();
    }

    /**
     * Filters the <code>originalOrders</code> to match the <code>query</code>
     * TODO: Solr order searching
     * @param originalOrders the orders to filter
     * @param query the query to filter by- either a SKU, Order number, or partial product name
     * @return a subset of the orders
     */
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
                        break;
                    }
                }
            }
        }
        return matchingOrders;
    }

    /**
     * Filters the orders between the given start date and end date
     * TODO: Do filtering directly from database query/Solr
     * @param orders the orders to filter
     * @param dateStartParam the first date to allow orders from
     * @param dateEndParam the last date to allow orders to
     * @return a subset of orders from <code>orders</code> between the start and end dates
     */
    protected List<Order> filterDates(List<Order> orders, String dateStartParam, String dateEndParam) {
        Date startDate = getDateFromString(dateStartParam, true);
        Date endDate = getDateFromString(dateEndParam, false);
        List<Order> filteredOrders = new ArrayList<>();
        for(Order order : orders) {
            if(order.getSubmitDate().after(startDate) && order.getSubmitDate().before(endDate)) {
                filteredOrders.add(order);
            }
        }
        return filteredOrders;
    }

    /**
     * Converts a MM/DD/YYYY date to a {@link java.util.Date} at either the start or end of day depending on
     * <code>startOfDay</code>
     * @param stringDate the date as a MM/DD/YYYY string
     * @param startOfDay true if the date should represent the first second of the day, false if it should represent
     *                   the last second of the day
     * @return a date representing <code>stringDate</code> at the start or end of the day
     */
    protected Date getDateFromString(String stringDate, boolean startOfDay) {
        Date date;
        try {
            date = filterDateFormat.parse(stringDate + (startOfDay ? " 00:00:00" : "23:59:59"));
        } catch (ParseException e) {
            throw new InvalidParameterException("The start/end date '" + stringDate + "' was specified in an invalid format");
        }
        return date;
    }

    /**
     * Converts a parameter map which can have multiple values per key into a new map that has only the first value
     * @param parameterMap the original parameter map with potentially multiple values per key
     * @return a new map with only the first value per key of the original map
     */
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
