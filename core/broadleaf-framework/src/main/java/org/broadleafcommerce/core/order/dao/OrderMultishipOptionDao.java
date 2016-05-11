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
package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.core.order.domain.OrderMultishipOption;

import java.util.List;

/**
 * Provides support for reading OrderMultishipOptions.
 * The default Broadleaf implementation uses Hibernate to perform the reading.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface OrderMultishipOptionDao {

    /**
     * Saves a given OrderMultishipOption. Note that the method will return the new
     * saved instance from Hibernate
     * 
     * @param orderMultishipOption the OrderMultishipOption to save
     * @return the saved instance from Hibernate
     */
    public OrderMultishipOption save(final OrderMultishipOption orderMultishipOption);

    /**
     * Returns all associated OrderMultishipOptions to the given order 
     * 
     * @param orderId the order's id to find OrderMultishipOptions for
     * @return the associated OrderMultishipOptions
     */
    public List<OrderMultishipOption> readOrderMultishipOptions(Long orderId);
    
    /**
     * Returns all associated OrderMultishipOptions to the given OrderItem
     * 
     * @param orderItemId the order item's id to find OrderMultishipOptions for
     * @return the associated OrderMultishipOptions
     */
    public List<OrderMultishipOption> readOrderItemOrderMultishipOptions(Long orderItemId);

    /**
     * Creates a new OrderMultishipOption instance.
     * 
     * The default Broadleaf implemntation uses the EntityConfiguration to create
     * the appropriate implementation class based on the current configuration
     * 
     * @return the OrderMultishipOption that was just created
     */
    public OrderMultishipOption create();

    /**
     * Removes all of the OrderMultishipOptions in the list permanently
     * 
     * @param options the options to delete
     */
    public void deleteAll(List<OrderMultishipOption> options);



}
