/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;

public interface FulfillmentGroupService {

    public FulfillmentGroup save(FulfillmentGroup fulfillmentGroup);

    public FulfillmentGroup createEmptyFulfillmentGroup();

    public FulfillmentGroup findFulfillmentGroupById(Long fulfillmentGroupId);

    public void delete(FulfillmentGroup fulfillmentGroup);
    
    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest, boolean priceOrder) throws PricingException;
    
    public FulfillmentGroup addItemToFulfillmentGroup(FulfillmentGroupItemRequest fulfillmentGroupItemRequest, boolean priceOrder) throws PricingException;
    
    public Order removeAllFulfillmentGroupsFromOrder(Order order, boolean priceOrder) throws PricingException;

    /**
     * Removes every fulfillment group item in every fulfillment group in the order
     * that is associated with the given orderItem. Note that it does not save the changes
     * made - instead, the caller is responsible for saving the order further down.
     * 
     * @param order
     * @param orderItem
     */
	public void removeOrderItemFromFullfillmentGroups(Order order, OrderItem orderItem);
	
	public FulfillmentGroupFee createFulfillmentGroupFee();

	/**
	 * Associates FulfillmentGroupItems in the given Order such that they match the structure
	 * of the OrderMultishipOptions associated with the given Order. 
	 * 
	 * @see OrderMultishipOption
	 * 
	 * @param order
	 * @return the saved order
	 * @throws PricingException 
	 */
	public Order matchFulfillmentGroupsToMultishipOptions(Order order, boolean priceOrder) throws PricingException;


}
