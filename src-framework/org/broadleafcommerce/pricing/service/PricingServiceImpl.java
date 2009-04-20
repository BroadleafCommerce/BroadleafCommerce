package org.broadleafcommerce.pricing.service;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.exception.PricingException;
import org.broadleafcommerce.pricing.workflow.OfferContext;
import org.broadleafcommerce.workflow.SequenceProcessor;
import org.broadleafcommerce.workflow.WorkflowException;
import org.springframework.stereotype.Service;

@Service("pricingService")
public class PricingServiceImpl implements PricingService {

    @Resource(name="pricingWorkflow")
    private SequenceProcessor pricingWorkflow;

    public Order executePricing(Order order) throws PricingException {
        try {
            OfferContext context = (OfferContext) pricingWorkflow.doActivities(order);
            return context.getSeedData();
        } catch (WorkflowException e) {
            throw new PricingException("Unable to execute pricing for order -- id: " + order.getId(), e);
        }
    }

}
