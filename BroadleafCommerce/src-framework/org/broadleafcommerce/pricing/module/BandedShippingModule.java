package org.broadleafcommerce.pricing.module;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;

public class BandedShippingModule implements ShippingModule {

    public static final String MODULENAME = "bandedShippingModule";

    protected String name = MODULENAME;

    @Override
    // this will need to calculate shipping on each fulfilmentGroup in an order
    public FulfillmentGroup calculateShippingForFulfillmentGroup(
            FulfillmentGroup fulfillmentGroup) {

        System.out.println("*** in BandedShippingModule.calculateShippingForFG()");

        return new FulfillmentGroupImpl();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
