package org.broadleafcommerce.pricing.service.workflow;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.workflow.ProcessContext;
import org.broadleafcommerce.workflow.ProcessContextFactory;
import org.broadleafcommerce.workflow.WorkflowException;

public class PricingProcessContextFactory implements ProcessContextFactory {

    @Override
    public ProcessContext createContext(Object seedData) throws WorkflowException {
        if(!(seedData instanceof Order)){
            throw new WorkflowException("Seed data instance is incorrect. " +
                    "Required class is "+Order.class.getName()+" " +
                    "but found class: "+seedData.getClass().getName());
        }
        PricingContext context = new PricingContext();
        context.setSeedData(seedData);

        return context;
    }

}
