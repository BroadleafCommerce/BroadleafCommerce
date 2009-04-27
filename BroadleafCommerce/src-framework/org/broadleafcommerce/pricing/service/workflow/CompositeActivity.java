package org.broadleafcommerce.pricing.service.workflow;

import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;
import org.broadleafcommerce.workflow.SequenceProcessor;

public class CompositeActivity extends BaseActivity {

    private SequenceProcessor workflow;

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        ProcessContext subContext = workflow.doActivities(((PricingContext) context).getSeedData());
        if (subContext.isStopped()) {
            context.stopProcess();
        }

        return context;
    }

    public SequenceProcessor getWorkflow() {
        return workflow;
    }

    public void setWorkflow(SequenceProcessor workflow) {
        this.workflow = workflow;
    }

}
