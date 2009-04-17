package org.broadleafcommerce.pricing.workflow;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class CalculateTotal extends BaseActivity {

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((OfferContext)context).getSeedData();

        // TODO Add code to calculate total
        context.setSeedData(order);
        return context;
    }

}
