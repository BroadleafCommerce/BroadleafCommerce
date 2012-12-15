package org.broadleafcommerce.core.workflow.state.test;

import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

/**
 * Create an activity that simply throws an exception in order to engage the rollback behavior for the workflow
 *
 * @author Jeff Fischer
 */
public class TestRollbackActivity extends BaseActivity {

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        throw new IllegalArgumentException("TestRollbackActivity Accessed...");
    }

}
