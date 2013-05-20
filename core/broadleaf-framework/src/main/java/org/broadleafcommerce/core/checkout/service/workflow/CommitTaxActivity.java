package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.TaxService;
import org.broadleafcommerce.core.workflow.BaseActivity;

import javax.annotation.Resource;

/**
 * This is an optional activity to allow a committal of taxes to a tax sub system. Many tax 
 * providers store tax details for reference, debugging, reporting, and reconciliation.
 * 
 * @author Kelly Tisdell
 *
 */
public class CommitTaxActivity extends BaseActivity<CheckoutContext> {
    
    @Resource(name = "blTaxService")
    protected TaxService taxService;

    @Override
    public CheckoutContext execute(CheckoutContext context) throws Exception {
        Order order = context.getSeedData().getOrder();
        order = taxService.commitTaxForOrder(order);
        context.getSeedData().setOrder(order);
        return context;
    }

}
