package org.broadleafcommerce.core.workflow.state;

/**
 * Handles the identification of the outermost workflow and the current thread so that the StateManager can
 * operate on the appropriate RollbackHandlers.
 *
 * @author Jeff Fischer
 */
public class RollbackStateLocal {

    private static final ThreadLocal<RollbackStateLocal> THREAD_LOCAL = new ThreadLocal<RollbackStateLocal>();

    public static RollbackStateLocal getRollbackStateLocal() {
        return THREAD_LOCAL.get();
    }

    public static void setRollbackStateLocal(RollbackStateLocal rollbackStateLocal) {
        THREAD_LOCAL.set(rollbackStateLocal);
    }

    private String threadId;
    private String workflowId;

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
