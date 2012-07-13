/*
 * Copyright 2012 the original author or authors.
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

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;

import java.util.List;

/**
 * Service to interact with OrderMultishipOptions
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface OrderMultishipOptionService {

	/**
	 * Saves the given OrderMultishipOption and returns the saved entity
	 * 
	 * @param orderMultishipOption
	 * @return the saved entity
	 */
	public OrderMultishipOption save(OrderMultishipOption orderMultishipOption);

	/**
	 * Finds all OrderMultishipOptions associated with the given Order based 
	 * on the orderId
	 * 
	 * @param orderId the order id to find OrderMultishipOptions for
	 * @return the associated OrderMultishipOptions
	 */
	public List<OrderMultishipOption> findOrderMultishipOptions(Long orderId);

	/**
	 * Creates a new instance of the OrderMultishipOption.
	 * The default Broadleaf implementation will create an instance based on what is
	 * configured in the EntityConfiguration.
	 * 
	 * @return the newly created OrderMultishipOption
	 */
	public OrderMultishipOption create();

	/**
	 * Generates the blank set of OrderMultishipOptions for a given order.
	 * Note that the default Broadleaf implementation splits up all DiscreteOrderItems
	 * in the given order into instances of OrderMultishipOption such that each instance
	 * assumes its quantity is one. Also note that this will not set the Address or the
	 * FulfillmentOption for any of the generated options.
	 * 
	 * @param order the order to generate OrderMultishipOptions for
	 * @return the OrderMultishipOptions generated for the Order.
	 */
	public List<OrderMultishipOption> generateMultishipOptions(Order order);

}