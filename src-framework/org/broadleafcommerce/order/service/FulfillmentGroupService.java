package org.broadleafcommerce.order.service;

import org.broadleafcommerce.order.domain.FulfillmentGroup;

public interface FulfillmentGroupService {

    public FulfillmentGroup save(FulfillmentGroup fulfillmentGroup);

    public FulfillmentGroup createEmptyFulfillmentGroup();

}
