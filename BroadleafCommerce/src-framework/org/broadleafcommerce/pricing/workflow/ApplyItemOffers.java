package org.broadleafcommerce.pricing.workflow;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class ApplyItemOffers extends BaseActivity {

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext) context).getSeedData();

        // TODO Add code to apply item offers
        context.setSeedData(order);
        return context;
    }

}
