package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.TaxService;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;
import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;
import org.broadleafcommerce.core.workflow.state.RollbackHandler;

import java.util.Map;

import javax.annotation.Resource;

public class CommitTaxRollbackHandler implements RollbackHandler {

    @Resource(name = "blTaxService")
    protected TaxService taxService;

    @Override
    public void rollbackState(Activity<? extends ProcessContext> activity, ProcessContext processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
        CheckoutContext ctx = (CheckoutContext) processContext;
        Order order = ctx.getSeedData().getOrder();
        try {
            taxService.cancelTax(order);
        } catch (TaxException e) {
            throw new RollbackFailureException("An exception occured cancelling taxes for order id: " + order.getId(), e);
        }

    }

}
