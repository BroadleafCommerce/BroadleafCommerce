/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.workflow;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SequenceProcessor extends BaseProcessor {

    private static final Log LOG = LogFactory.getLog(SequenceProcessor.class);

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
        if (LOG.isDebugEnabled()) {
            LOG.debug(getBeanName() + " processor is running..");
        }

        //retrieve injected by Spring
        List<Activity> activities = getActivities();

        //retrieve a new instance of the Workflow ProcessContext
        ProcessContext context = createContext(seedData);

        for (Iterator<Activity> it = activities.iterator(); it.hasNext();) {
            Activity activity = it.next();
            if (LOG.isDebugEnabled()) {
                LOG.debug("running activity:" + activity.getBeanName() + " using arguments:" + context);
            }

            try {
                context = activity.execute(context);
            } catch (Throwable th) {
                ErrorHandler errorHandler = activity.getErrorHandler();
                if (errorHandler == null) {
                    LOG.info("no error handler for this action, run default error" + "handler and abort processing ");
                    getDefaultErrorHandler().handleError(context, th);
                    break;
                } else {
                    LOG.info("run error handler and continue");
                    errorHandler.handleError(context, th);
                }
            }

            //ensure its ok to continue the process
            if (processShouldStop(context, activity)) {
                break;
            }
        }
        LOG.debug(getBeanName() + " processor is done.");

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
            LOG.info("Interrupted workflow as requested by:" + activity.getBeanName());
            return true;
        }
        return false;
    }

    private ProcessContext createContext(Object seedData) throws WorkflowException {
        return processContextFactory.createContext(seedData);
    }

    public void setProcessContextFactory(ProcessContextFactory processContextFactory) {
        this.processContextFactory = processContextFactory;
    }

}
