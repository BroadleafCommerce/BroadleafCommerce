package org.broadleafcommerce.core.workflow.state.test;

import org.broadleafcommerce.core.workflow.ErrorHandler;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.WorkflowException;

/**
 * Add an ErrorHandler that does nothing and does not stop the workflow
 *
 * @author Jeff Fischer
 */
public class TestPassThroughRollbackErrorHandler implements ErrorHandler {

    @Override
    public void handleError(ProcessContext context, Throwable th) throws WorkflowException {
        //do nothing
        //could get programmatic access to the ActivityStateManager for explicit rollbacks here
    }

    @Override
    public void setBeanName(String name) {
        //do nothing
    }
}
