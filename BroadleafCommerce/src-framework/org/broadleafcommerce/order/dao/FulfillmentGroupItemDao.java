package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;

public interface FulfillmentGroupItemDao {

    public FulfillmentGroupItem readFulfillmentGroupItemById(Long fulfillmentGroupItemId);

    public FulfillmentGroupItem maintainFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem);

    public List<FulfillmentGroupItem> readFulfillmentGroupItemsForFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    public void deleteFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem);

    public FulfillmentGroupItem create();
}
