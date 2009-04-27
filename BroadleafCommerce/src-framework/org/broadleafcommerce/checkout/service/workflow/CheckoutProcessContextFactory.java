package org.broadleafcommerce.checkout.service.workflow;

import org.broadleafcommerce.workflow.ProcessContext;
import org.broadleafcommerce.workflow.ProcessContextFactory;
import org.broadleafcommerce.workflow.WorkflowException;

public class CheckoutProcessContextFactory implements ProcessContextFactory {

    @Override
    public ProcessContext createContext(Object seedData) throws WorkflowException {
        if(!(seedData instanceof CheckoutSeed)){
            throw new WorkflowException("Seed data instance is incorrect. " +
                    "Required class is "+CheckoutSeed.class.getName()+" " +
                    "but found class: "+seedData.getClass().getName());
        }
        CheckoutContext context = new CheckoutContext();
        context.setSeedData(seedData);

        return context;
    }

}
