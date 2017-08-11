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

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;

/**
 * Service for gathering previously completed orders
 *
 * @author Jacob Mitash
 */
public interface OrderHistoryService {

    /**
     * Gathers an {@link Order} based on the given {@param orderNumber} value
     *
     * If `validate.customer.owned.data` is true, then we must throw an exception to avoid giving the currently logged in
     *  {@link Customer} access to an order that they do not own.
     */
    Order getOrderDetails(String orderNumber);

    /**
     * If is validation is enabled via the `validate.customer.owned.data` property, a {@link SecurityException} should be thrown if the current customer
     *  ({@link CustomerState#getCustomer()}) is not associated to the given {@param order}.
     */
    void validateCustomerOwnedData(Order order) throws SecurityException;
}
