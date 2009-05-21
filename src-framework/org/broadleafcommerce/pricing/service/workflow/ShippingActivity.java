package org.broadleafcommerce.pricing.service.workflow;

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

        /*
         * 1. Get FGs from Order
         * 2. take each FG and call shipping module with the shipping svc
         * 3. add FG back to order
         */

        Money totalShipping = new Money(0D);
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            fulfillmentGroup = shippingModule.calculateShippingForFulfillmentGroup(fulfillmentGroup);
            totalShipping = totalShipping.add(fulfillmentGroup.getShippingPrice());
        }
        order.setTotalShipping(totalShipping);
        context.setSeedData(order);
        return context;
    }

}
