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

package org.broadleafcommerce.core.order.domain;

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
public interface OrderItemAttribute extends ValueAssignable<String> {

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
