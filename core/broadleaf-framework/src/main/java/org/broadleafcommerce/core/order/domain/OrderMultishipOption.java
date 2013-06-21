/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.profile.core.domain.Address;

/**
 * Represents a given set of options for an OrderItem in an Order in the 
 * multiship context. This class is used to store current multiship settings
 * for an Order without having to generate the necessary FulfillmentGroups and
 * FulfillmentGroupItems. It also can be used to re-create the multiship set
 * should the Order change
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface OrderMultishipOption {

    /**
     * Returns the internal id of this OrderMultishipOption
     * 
     * @return the internal id
     */
    public Long getId();

    /**
     * Sets the internal id of this OrderMultishipOption
     * 
     * @param id the internal id
     */
    public void setId(Long id);

    /**
     * Returns the Order associated with this OrderMultishipOption
     * 
     * @return the associated Order
     */
    public Order getOrder();

    /**
     * Sets the associated Order with this OrderMultishipOption
     * 
     * @param order the associated order
     */
    public void setOrder(Order order);

    /**
     * Gets the OrderItem associated with this OrderMultishipOption.
     * Note that the default Broadleaf implementation will produce
     * an equal number of instances of OrderMultishipOption to the
     * quantity of the OrderItem
     * 
     * @return the associated OrderItem
     */
    public OrderItem getOrderItem();

    /**
     * Sets the associated OrderItem with this OrderMultishipOption
     * 
     * @see OrderMultishipOption#getOrderItem()
     * 
     * @param orderItem the associated OrderItem
     */
    public void setOrderItem(OrderItem orderItem);

    /**
     * Gets the associated Address with this OrderMultishipOption
     * 
     * @return the associated Address
     */
    public Address getAddress();

    /**
     * Sets the associated Address with this OrderMultishipOption
     * 
     * @param address the associated Address
     */
    public void setAddress(Address address);

    /**
     * Gets the associated FulfillmentOption with this OrderMultishipOption
     * 
     * @return the associated FulfillmentOption
     */
    public FulfillmentOption getFulfillmentOption();

    /**
     * Sets the associated FulfillmentOption with this OrderMultishipOption
     * 
     * @param fulfillmentOption the associated FulfillmentOption
     */
    public void setFulfillmentOption(FulfillmentOption fulfillmentOption);

}