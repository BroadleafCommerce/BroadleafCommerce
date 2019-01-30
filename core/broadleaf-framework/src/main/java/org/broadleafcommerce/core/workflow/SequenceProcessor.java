/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.workflow.state.ActivityStateManager;
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;
import org.broadleafcommerce.core.workflow.state.RollbackStateLocal;

import java.util.List;

public class SequenceProcessor<U, T> extends BaseProcessor<U, T> {

    private static final Log LOG = LogFactory.getLog(SequenceProcessor.class);

    private ProcessContextFactory<U, T> processContextFactory;

    @Override
    public boolean supports(Activity<? extends ProcessContext<U>> activity) {
        return true;
    }

    @Override
    public <P extends ProcessContext<U>> P doActivities() throws WorkflowException {
        return doActivities(null);
    }

    @Override
    public <P extends ProcessContext<U>> P doActivities(T seedData) throws WorkflowException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(getBeanName() + " processor is running..");
        }
        ActivityStateManager activityStateManager = getBeanFactory().getBean(ActivityStateManager.class, "blActivityStateManager");
        if (activityStateManager == null) {
            throw new IllegalStateException("Unable to find an instance of ActivityStateManager registered under bean id blActivityStateManager");
        }
        ProcessContext<U> context = null;
        
        RollbackStateLocal rollbackStateLocal = new RollbackStateLocal();
        rollbackStateLocal.setThreadId(String.valueOf(Thread.currentThread().getId()));
        rollbackStateLocal.setWorkflowId(getBeanName());
        RollbackStateLocal.setRollbackStateLocal(rollbackStateLocal);
        
        try {
            //retrieve injected by Spring
            List<Activity<ProcessContext<U>>> activities = getActivities();

            //retrieve a new instance of the Workflow ProcessContext
            context = createContext(seedData);

            for (Activity<ProcessContext<U>> activity : activities) {
                if (activity.shouldExecute(context)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("running activity:" + activity.getBeanName() + " using arguments:" + context);
                    }
    
                    try {
                        context = activity.execute(context);
                    } catch (Throwable activityException) {
                        RollbackFailureException rollbackFailure = null;
                        if (getAutoRollbackOnError()) {
                            LOG.info(String.format("Exception ocurred in %s, executing rollback handlers", rollbackStateLocal.getWorkflowId()));
                            
                            try {
                                ActivityStateManagerImpl.getStateManager().rollbackAllState();
                            } catch (Throwable rollbackException) {
                                LOG.fatal(String.format("There was an exception rolling back %s", rollbackStateLocal.getWorkflowId()), rollbackException);
                                
                                if (rollbackException instanceof RollbackFailureException) {
                                    rollbackFailure = (RollbackFailureException) rollbackException;
                                } else {
                                    rollbackFailure = new RollbackFailureException(rollbackException);
                                }
                                
                                LOG.error(String.format("The original cause of the rollback for %s was", rollbackStateLocal.getWorkflowId()), activityException);
                                rollbackFailure.setOriginalWorkflowException(activityException);
                                throw rollbackFailure;
                            }
                        }
                        
                        ErrorHandler errorHandler = activity.getErrorHandler();
                        if (errorHandler == null) {
                            getDefaultErrorHandler().handleError(context, activityException);
                            break;
                        } else {
                            errorHandler.handleError(context, activityException);
                        }
                    }
    
                    //ensure its ok to continue the process
                    if (processShouldStop(context, activity)) {
                        break;
                    }
    
                    //register the RollbackHandler
                    if (activity.getRollbackHandler() != null && activity.getAutomaticallyRegisterRollbackHandler()) {
                        ActivityStateManagerImpl.getStateManager().registerState(activity, context, activity.getRollbackRegion(), activity.getRollbackHandler(), activity.getStateConfiguration());
                    }
                } else {
                    LOG.debug("Not executing activity: " + activity.getBeanName() + " based on the context: " + context);
                }
            }
        } finally {
            rollbackStateLocal = RollbackStateLocal.getRollbackStateLocal();
            if (rollbackStateLocal != null && rollbackStateLocal.getWorkflowId().equals(getBeanName())) {
                activityStateManager.clearAllState();
            }
        }
        LOG.debug(getBeanName() + " processor is done.");

        return (P) context;
    }

    /**
     * Determine if the process should stop
     *
     * @param context
     *            the current process context
     * @param activity
     *            the current activity in the iteration
     */
    protected boolean processShouldStop(ProcessContext<U> context, Activity<ProcessContext<U>> activity) {
        if (context == null || context.isStopped()) {
            LOG.info("Interrupted workflow as requested by:" + activity.getBeanName());
            return true;
        }
        return false;
    }

    protected ProcessContext<U> createContext(T seedData) throws WorkflowException {
        return processContextFactory.createContext(seedData);
    }

    @Override
    public void setProcessContextFactory(ProcessContextFactory<U, T> processContextFactory) {
        this.processContextFactory = processContextFactory;
    }

}
