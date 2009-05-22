package org.broadleafcommerce.pricing.service;

import org.broadleafcommerce.order.domain.FulfillmentGroup;

public interface ShippingService {
    public FulfillmentGroup calculateShippingForFulfillmentGroup(FulfillmentGroup fulfillmentGroup);
}
