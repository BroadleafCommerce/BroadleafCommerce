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

import java.util.List;
import java.util.Map;

import org.broadleafcommerce.core.order.domain.Order;

/**
 * Service for retrieving filtered (by search query and date) order history
 * @author Jacob Mitash
 */
public interface OrderHistoryService {

    /**
     * Gets the orders that should be displayed after pagination, date filtering, and search filtering
     * @param parameterMap the parameters from the web request
     * @param modelAttributes a map that holds attributes that should be added to the model
     * @param startingOrders the starting orders to filter from
     * @return the filtered ordes from <code>startingOrders</code>
     */
    List<Order> getOrderHistory(Map<String, String[]> parameterMap, Map<String, Object> modelAttributes, List<Order> startingOrders);

    /**
     * Throws an exception if the customer tries to access an order they don't have access to
     * @param order the order to check ownership of
     */
    void validateCustomerOwnedData(Order order);

    /**
     * Loads a single order
     * @param orderNumber the order number of the order to retrieve
     */
    Order getOrderDetails(String orderNumber);

    /**
     * Gets the number of items per page
     * @return number of items per page
     */
    int getItemsPerPage();
}
