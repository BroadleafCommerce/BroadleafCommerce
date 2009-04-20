package org.broadleafcommerce.pricing.workflow;

import java.util.Iterator;
import java.util.List;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.module.ShippingModule;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class CalculateShipping extends BaseActivity {

    private ShippingModule shippingModule;

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((OfferContext)context).getSeedData();

        System.out.println("*** in CalculateShipping.execute()");

        /*
         * 1. Get FGs from Order
         * 2. take each FG and call shipping module with the shipping svc
         * 3. add FG back to order
         */

        List<FulfillmentGroup> fulfillmentGroups = order.getFulfillmentGroups();
        Iterator<FulfillmentGroup> itr = fulfillmentGroups.iterator();

        while ( itr.hasNext() ) {
            FulfillmentGroup fulfillmentGroup = itr.next();
            fulfillmentGroup =
                shippingModule.calculateShippingForFulfillmentGroup(fulfillmentGroup);
            order.setFulfillmentGroups(fulfillmentGroups);
        }

        // TODO Add code to calculate shipping
        context.setSeedData(order);
        return context;
    }

    public ShippingModule getShippingModule() {
        return shippingModule;
    }

    public void setShippingModule(ShippingModule shippingModule) {
        this.shippingModule = shippingModule;
    }

}
