/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.value.ValueAssignable;

/**
 * The Interface OrderItemAttribute.   Allows for arbitrary data to be
 * persisted with the orderItem.  This can be used to store additional
 * items that are required during order entry.
 *
 * Examples:
 *   Engravement Message for a jewelry item
 *   TestDate for someone purchasing an online exam
 *   Number of minutes for someone purchasing a rate plan.
 *
 */
public interface OrderItemAttribute extends ValueAssignable<String>, MultiTenantCloneable<OrderItemAttribute> {

    /**
     * Gets the id.
     * 
     * @return the id
     */
    Long getId();

    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    void setId(Long id);

    /**
     * Gets the parent orderItem
     * 
     * @return the orderItem
     */
    OrderItem getOrderItem();

    /**
     * Sets the orderItem.
     * 
     * @param orderItem the associated orderItem
     */
    void setOrderItem(OrderItem orderItem);

    /**
     * Provide support for a deep copy of an order item.
     * @return
     */
    public OrderItemAttribute clone();
}
