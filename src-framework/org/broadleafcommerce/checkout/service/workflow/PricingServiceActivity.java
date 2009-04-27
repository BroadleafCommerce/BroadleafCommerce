package org.broadleafcommerce.checkout.service.workflow;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.PricingService;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PricingServiceActivity extends BaseActivity {

    @Resource
    private PricingService pricingService;

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        CheckoutSeed seed = ((CheckoutContext) context).getSeedData();
        Order order = pricingService.executePricing(seed.getOrder());
        CheckoutSeed newSeed = new CheckoutSeed(order, seed.getInfos());
        context.setSeedData(newSeed);

        return context;
    }

}
