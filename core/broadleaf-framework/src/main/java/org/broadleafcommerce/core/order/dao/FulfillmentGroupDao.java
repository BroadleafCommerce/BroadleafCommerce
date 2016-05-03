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

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType;

import java.util.List;

public interface FulfillmentGroupDao {

    public FulfillmentGroup readFulfillmentGroupById(Long fulfillmentGroupId);

    public FulfillmentGroup save(FulfillmentGroup fulfillmentGroup);

    public FulfillmentGroup readDefaultFulfillmentGroupForOrder(Order order);

    public void delete(FulfillmentGroup fulfillmentGroup);

    public FulfillmentGroup createDefault();

    public FulfillmentGroup create();

    public FulfillmentGroupFee createFulfillmentGroupFee();
    
    /**
     * Reads FulfillmentGroups whose status is not FULFILLED or DELIVERED.
     * @param start
     * @param maxResults
     * @return
     */
    public List<FulfillmentGroup> readUnfulfilledFulfillmentGroups(int start, int maxResults);
    
    /**
     * Reads FulfillmentGroups whose status is PARTIALLY_FULFILLED or PARTIALLY_DELIVERED.
     * 
     * @param start
     * @param maxResults
     * @return
     */
    public List<FulfillmentGroup> readPartiallyFulfilledFulfillmentGroups(int start, int maxResults);
    
    /**
     * Returns FulfillmentGroups whose status is null, or where no processing has yet occured. 
     * Default returns in ascending order according to date that the order was created.
     * @param start
     * @param maxResults
     * @return
     */
    public List<FulfillmentGroup> readUnprocessedFulfillmentGroups(int start, int maxResults);
    
    /**
     * Reads FulfillmentGroups by status, either ascending or descending according to the date that 
     * the order was created.
     * @param status
     * @param start
     * @param maxResults
     * @param ascending
     * @return
     */
    public List<FulfillmentGroup> readFulfillmentGroupsByStatus(FulfillmentGroupStatusType status, int start, int maxResults, boolean ascending);
    
    /**
     * Reads FulfillmentGroups by status, ascending according to the date that 
     * the order was created.
     * @param status
     * @param start
     * @param maxResults
     * @return
     */
    public List<FulfillmentGroup> readFulfillmentGroupsByStatus(FulfillmentGroupStatusType status, int start, int maxResults);

    /**
     * Reads the max sequnce of fulfillment groups for a particular order and increments by 1.
     * @param order
     * @return
     */
    public Integer readNextFulfillmentGroupSequnceForOrder(Order order);
}
