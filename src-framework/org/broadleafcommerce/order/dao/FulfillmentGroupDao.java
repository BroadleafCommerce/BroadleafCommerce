package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.DefaultFulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;

public interface FulfillmentGroupDao {

    public FulfillmentGroup readFulfillmentGroupById(Long fulfillmentGroupId);

    public FulfillmentGroup maintainFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    public List<FulfillmentGroup> readFulfillmentGroupsForOrder(Order order);

    public DefaultFulfillmentGroup maintainDefaultFulfillmentGroup(DefaultFulfillmentGroup defaultFulfillmentGroup);

    public DefaultFulfillmentGroup readDefaultFulfillmentGroupById(Long fulfillmentGroupId);

    public DefaultFulfillmentGroup readDefaultFulfillmentGroupForOrder(Order order);

    public void removeFulfillmentGroupForOrder(Order order, FulfillmentGroup fulfillmentGroup);

    public DefaultFulfillmentGroup createDefault();

    public FulfillmentGroup create();
}
