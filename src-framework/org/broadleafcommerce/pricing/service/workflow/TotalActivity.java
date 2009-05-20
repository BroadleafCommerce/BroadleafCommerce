package org.broadleafcommerce.pricing.service.workflow;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class TotalActivity extends BaseActivity {

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext) context).getSeedData();
        Money total = new Money(BigDecimal.ZERO);
        total = total.add(order.getSubTotal());
        total = total.add(order.getTotalTax());
        total = total.add(order.getTotalShipping());
        order.setTotal(total);

        for(FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            Money fgTotal = new Money(0D);
            fgTotal = fgTotal.add(fulfillmentGroup.getMerchandiseTotal());
            fgTotal = fgTotal.add(fulfillmentGroup.getShippingPrice());
            fgTotal = fgTotal.add(fulfillmentGroup.getTotalTax());
            fulfillmentGroup.setTotal(fgTotal);
        }

        context.setSeedData(order);
        return context;
    }

}
