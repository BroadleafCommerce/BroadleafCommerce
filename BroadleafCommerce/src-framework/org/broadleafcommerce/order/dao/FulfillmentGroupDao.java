package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.Order;

public interface FulfillmentGroupDao {

    public FulfillmentGroup readFulfillmentGroupById(Long fulfillmentGroupId);

    public FulfillmentGroup maintainFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

//    public List<FulfillmentGroup> readFulfillmentGroupsForOrder(Order order);
//
    public FulfillmentGroupImpl maintainDefaultFulfillmentGroup(FulfillmentGroupImpl defaultFulfillmentGroup);

    public FulfillmentGroupImpl readDefaultFulfillmentGroupById(Long fulfillmentGroupId);

    public FulfillmentGroupImpl readDefaultFulfillmentGroupForOrder(Order order);

    public void removeFulfillmentGroupForOrder(Order order, FulfillmentGroup fulfillmentGroup);

    public FulfillmentGroupImpl createDefault();

    public FulfillmentGroup create();
}
