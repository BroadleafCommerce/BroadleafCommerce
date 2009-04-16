package org.broadleafcommerce.payment.order.workflow;

import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PayWithCreditCard extends BaseActivity {

    /* (non-Javadoc)
     * @see org.broadleafcommerce.workflow.Activity#execute(org.broadleafcommerce.workflow.ProcessContext)
     */
    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        /*Order order = ((OfferContext)context).getSeedData();

        TaxModule module = taxService.getTaxModuleByName(taxModuleName);
        order = module.calculateTaxForOrder(order);

        context.setSeedData(order);
        return context;*/
        return null;
    }

}
