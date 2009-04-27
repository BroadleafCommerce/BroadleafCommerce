package org.broadleafcommerce.pricing.service.workflow;

import java.util.Iterator;
import java.util.List;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.module.ShippingModule;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class ShippingActivity extends BaseActivity {

    private ShippingModule shippingModule;

    public void setShippingModule(ShippingModule shippingModule) {
        this.shippingModule = shippingModule;
    }

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext)context).getSeedData();

        System.out.println("*** in ShippingActivity.execute()");
        System.out.println("*** order: " + order);

        /*
         * 1. Get FGs from Order
         * 2. take each FG and call shipping module with the shipping svc
         * 3. add FG back to order
         */

        List<FulfillmentGroup> fulfillmentGroups = order.getFulfillmentGroups();
        Iterator<FulfillmentGroup> itr = fulfillmentGroups.iterator();

        Money totalShipping = new Money(0D);
        while ( itr.hasNext() ) {
            FulfillmentGroup fulfillmentGroup = itr.next();
            System.out.println("**** FG b4 change: " + fulfillmentGroup.getMethod());
            //fulfillmentGroup.getMethod();
            fulfillmentGroup =
                shippingModule.calculateShippingForFulfillmentGroup(fulfillmentGroup);

            System.out.println("**** FG after change: " + fulfillmentGroup.getMethod());
            order.setFulfillmentGroups(fulfillmentGroups);
            System.out.println("**** order after change: " + order);
            totalShipping = totalShipping.add(fulfillmentGroup.getPrice());
        }
        order.setTotalShipping(totalShipping);
        // TODO Add code to calculate shipping
        context.setSeedData(order);
        return context;
    }

}
