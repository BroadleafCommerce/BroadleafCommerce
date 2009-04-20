package org.broadleafcommerce.workflow;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SequenceProcessor extends BaseProcessor {

    private Log logger = LogFactory.getLog(SequenceProcessor.class);
    private ProcessContextFactory processContextFactory;

    /*
     * (non-Javadoc)
     *
     * @see org.iocworkflow.BaseProcessor#supports(java.lang.Class)
     */
    public boolean supports(Activity activity) {
        return (activity instanceof BaseActivity);
    }

    public ProcessContext doActivities() throws WorkflowException {
        return doActivities(null);
    }

    public ProcessContext doActivities(Object seedData) throws WorkflowException {
        if (logger.isDebugEnabled()) {
            logger.debug(getBeanName() + " processor is running..");
        }

        //retrieve injected by Spring
        List<Activity> activities = getActivities();

        //retrieve a new instance of the Workflow ProcessContext
        ProcessContext context = createContext(seedData);

        for (Iterator<Activity> it = activities.iterator(); it.hasNext();) {
            Activity activity = it.next();
            if (logger.isDebugEnabled()) {
                logger.debug("running activity:" + activity.getBeanName() + " using arguments:" + context);
            }

            try {
                context = activity.execute(context);
            } catch (Throwable th) {
                th.printStackTrace();
                ErrorHandler errorHandler = activity.getErrorHandler();
                if (errorHandler == null) {
                    logger.info("no error handler for this action, run default error" + "handler and abort processing ");
                    getDefaultErrorHandler().handleError(context, th);
                    break;
                } else {
                    logger.info("run error handler and continue");
                    errorHandler.handleError(context, th);
                }
            }

            //ensure its ok to continue the process
            if (processShouldStop(context, activity)) {
                break;
            }
        }
        logger.debug(getBeanName() + " processor is done.");

        return context;
    }

    /**
     * Determine if the process should stop
     *
     * @param context
     *            the current process context
     * @param activity
     *            the current activity in the iteration
     */
    private boolean processShouldStop(ProcessContext context, Activity activity) {
        if (context != null && context.isStopped()) {
            logger.info("Interrupted workflow as requested by:" + activity.getBeanName());
            return true;
        }
        return false;
    }

    private ProcessContext createContext(Object seedData) throws WorkflowException {
        return processContextFactory.createContext(seedData);
    }

    @Override
    public void setProcessContextFactory(ProcessContextFactory processContextFactory) {
        this.processContextFactory = processContextFactory;
    }

}
