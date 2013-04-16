package org.broadleafcommerce.core.workflow.state.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;
import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.broadleafcommerce.core.workflow.state.RollbackStateLocal;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Demonstrate a call to a RollbackHandler
 *
 * @author Jeff Fischer
 */
public class TestRollbackHandler implements RollbackHandler {

    private static final Log LOG = LogFactory.getLog(TestRollbackHandler.class);

    @Override
    @Transactional("blTransactionManager")
    public void rollbackState(Activity<? extends ProcessContext> activity,
            ProcessContext processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
        LOG.warn("******************* TestRollbackHandler Engaged *********************");
        LOG.warn("******************* Activity: " + activity.getBeanName() + " *********************");
        RollbackStateLocal rollbackStateLocal = RollbackStateLocal.getRollbackStateLocal();
        LOG.warn("******************* Workflow: " + rollbackStateLocal.getWorkflowId() + " *********************");
        LOG.warn("******************* Thread: " + rollbackStateLocal.getThreadId() + " *********************");
    }
}
