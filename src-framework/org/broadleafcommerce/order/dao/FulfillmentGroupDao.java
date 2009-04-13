package org.broadleafcommerce.order.dao;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;

public interface FulfillmentGroupDao {

    public FulfillmentGroup readFulfillmentGroupById(Long fulfillmentGroupId);

    public FulfillmentGroup maintainFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    public FulfillmentGroup maintainDefaultFulfillmentGroup(FulfillmentGroup defaultFulfillmentGroup);

    public FulfillmentGroup readDefaultFulfillmentGroupById(Long fulfillmentGroupId);

    public FulfillmentGroup readDefaultFulfillmentGroupForOrder(Order order);

    public void removeFulfillmentGroupForOrder(Order order, FulfillmentGroup fulfillmentGroup);

    public FulfillmentGroup createDefault();

    public FulfillmentGroup create();
}
