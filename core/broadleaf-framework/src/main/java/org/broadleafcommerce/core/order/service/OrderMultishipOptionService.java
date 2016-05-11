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
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.call.OrderMultishipOptionDTO;

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
     * Finds all OrderMultishipOptions associated with the given Order Item 
     * based on the order item id
     * 
     * @param orderItemId the orderItem id to find OrderMultishipOptions for
     * @return the associated OrderMultishipOptions
     */
    public List<OrderMultishipOption> findOrderItemOrderMultishipOptions(Long orderItemId);

    /**
     * Creates a new instance of the OrderMultishipOption.
     * The default Broadleaf implementation will create an instance based on what is
     * configured in the EntityConfiguration.
     * 
     * @return the newly created OrderMultishipOption
     */
    public OrderMultishipOption create();
    
    /**
     * Removes all OrderMultishipOptions for this Order permanently.
     * 
     * @param order
     */
    public void deleteAllOrderMultishipOptions(Order order);

    /**
     * Removes all OrderMultishipOptions associated with the OrderItem
     * represented by this orderItemId permanently.
     * 
     * @param orderItemId
     */
    public void deleteOrderItemOrderMultishipOptions(Long orderItemId);
    
    /**
     * Removes up to numToDelete OrderMultishipOptions associated with 
     * the OrderItem represented by this orderItemId permanently.
     * 
     * @see #deleteOrderItemOrderMultishipOptions(Long)
     * 
     * @param orderItemId 
     * @param numToDelete the maximum number of occurrences of this orderItemId to delete
     */
    public void deleteOrderItemOrderMultishipOptions(Long orderItemId, int numToDelete);

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
    public List<OrderMultishipOption> generateOrderMultishipOptions(Order order);

    /**
     * If this order has associated OrderMultishipOptions, this method will return those
     * options. If there are items in the order that do not have associated options,
     * options for those items will be generated and attached to the existing options.
     * 
     * @see #findOrderMultishipOptions(Long)
     * @see #generateOrderMultishipOptions(Order)
     * 
     * @param order
     * @return the OrderMultishipOptions for this Order
     */
    public List<OrderMultishipOption> getOrGenerateOrderMultishipOptions(Order order);
    
    /**
     * Given the (potetially only partially filled out) OrderMultishipOptionDTO objects,
     * builds out the associated OrderMultishipOption objects. This is done by looking up
     * the non-null fields in the optionDtos for their associated entity.
     * 
     * Note that the only potentially null fields are address and location ids.
     * 
     * @param order
     * @param optionDtos
     * @return the associated OrderMultishipOptions
     */
    public List<OrderMultishipOption> getOrderMultishipOptionsFromDTOs(Order order, List<OrderMultishipOptionDTO> optionDtos);

    /**
     * Associates the appropriate objects based on the OrderMultishipOptionDTOs to
     * OrderMultishipOption instances, associates them with the given order,
     * and saves the set.
     * 
     * Note that this method will clear any previously saved OrderMultishipOptions for
     * the requested order before performing the save.
     * 
     * @param order
     * @param optionDTOs
     */
    public void saveOrderMultishipOptions(Order order, List<OrderMultishipOptionDTO> optionDTOs);



}
