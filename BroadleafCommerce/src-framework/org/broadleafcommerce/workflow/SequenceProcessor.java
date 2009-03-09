package org.broadleafcommerce.workflow;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;

public class SequenceProcessor extends BaseProcessor {

	   //private ProcessContextLocator processContextLocator;
    private Log logger = LogFactory.getLog(SequenceProcessor.class);
    private Class<ProcessContext> processContextClass;

    /*
     * (non-Javadoc)
     * 
     * @see org.iocworkflow.BaseProcessor#supports(java.lang.Class)
     */
    public boolean supports(Activity activity) {

        return (activity instanceof BaseActivity);
    }

    public void doActivities() {
        doActivities(null);

    }

    public void doActivities(Object seedData) {

        if (logger.isDebugEnabled())
            logger.debug(getBeanName() + " processor is running..");

        //retrieve injected by Spring
        List<Activity> activities = getActivities();

        //retrieve a new instance of the Workflow ProcessContext
        ProcessContext context = createContext();

        if (seedData != null)
            context.setSeedData(seedData);

        for (Iterator<Activity> it = activities.iterator(); it.hasNext();) {

            Activity activity = (Activity) it.next();

            if (logger.isDebugEnabled())
                logger.debug("running activity:" + activity.getBeanName() + " using arguments:" + context);

            try {
                context = activity.execute(context);

            } catch (Throwable th) {
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
            if (processShouldStop(context, activity))
                break;
        }
        logger.debug(getBeanName() + " processor is done.");
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
        if (context != null && context.stopProcess()) {
            logger.info("Interrupted workflow as requested by:" + activity.getBeanName());
            return true;
        }
        return false;
    }

    private ProcessContext createContext() {
        // return processContextLocator.getService();
        return (ProcessContext) BeanFactoryUtils.beanOfTypeIncludingAncestors((ListableBeanFactory) getBeanFactory(),
                                                                              processContextClass);
    }

    //    public void setProcessContextLocator(ProcessContextLocator processContextLocator) {
    //        this.processContextLocator = processContextLocator;
    //    }
    public void setProcessContextClass(Class<ProcessContext> processContextClass) {
        this.processContextClass = processContextClass;
    }

}
