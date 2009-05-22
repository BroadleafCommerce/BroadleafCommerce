package org.broadleafcommerce.pricing.service;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.pricing.service.module.ShippingModule;

public class ShippingServiceImpl implements ShippingService {

    private ShippingModule shippingModule;

    public FulfillmentGroup calculateShippingForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        return shippingModule.calculateShippingForFulfillmentGroup(fulfillmentGroup);
    }

    public ShippingModule getShippingModule() {
        return shippingModule;
    }

    public void setShippingModule(ShippingModule shippingModule) {
        this.shippingModule = shippingModule;
    }



}
