package org.broadleafcommerce.pricing.workflow;

import org.broadleafcommerce.checkout.workflow.CheckoutSeed;
import org.broadleafcommerce.workflow.ProcessContext;
import org.broadleafcommerce.workflow.ProcessContextFactory;
import org.broadleafcommerce.workflow.WorkflowException;

public class PricingProcessContextFactory implements ProcessContextFactory {

    @Override
    public ProcessContext createContext(Object seedData) throws WorkflowException {
        if(!(seedData instanceof CheckoutSeed)){
            throw new WorkflowException("Seed data instance is incorrect. " +
                    "Required class is "+CheckoutSeed.class.getName()+" " +
                    "but found class: "+seedData.getClass().getName());
        }
        OfferContext context = new OfferContext();
        context.setSeedData(((CheckoutSeed) seedData).getOrder());

        return context;
    }

}
