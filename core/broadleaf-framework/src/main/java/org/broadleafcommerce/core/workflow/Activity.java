/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.workflow;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.Ordered;

import java.util.Map;

/**
 * <p>
 * Interface to be used for workflows in Broadleaf. Usually implementations will subclass {@link BaseActivity}.
 * </p>
 * 
 * Important note: if you are writing a 3rd-party integration module or adding a module outside of the Broadleaf core, your
 * activity should implement the {@link ModuleActivity} interface as well. This ensures that there is proper logging
 * for users that are using your module so that they know exactly what their final workflow configuration looks like.
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @param <T>
 * @see {@link BaseActivity}
 * @see {@link ModuleActivity}
 * @see {@link BaseProcessor}
 * @see {@link SequenceProcessor}
 */
public interface Activity<T extends ProcessContext<? extends Object>> extends BeanNameAware, Ordered {

    /**
     * Called by the encompassing processor to activate
     * the execution of the Activity
     * 
     * @param context - process context for this workflow
     * @return resulting process context
     * @throws Exception
     */
    public T execute(T context) throws Exception;

    /**
     * Determines if an activity should execute based on the current values in the {@link ProcessContext}. For example, a
     * context might have both an {@link Order} as well as a String 'status' of what the order should be changed to. It is
     * possible that an activity in a workflow could only deal with a particular status change, and thus could return false
     * from this method.
     * 
     * @param context
     * @return
     */
    public boolean shouldExecute(T context);

    /**
     * Get the fine-grained error handler wired up for this Activity
     * @return
     */
    public ErrorHandler getErrorHandler();

    public void setErrorHandler(final ErrorHandler errorHandler);
    
    public String getBeanName();

    /**
     * Retrieve the RollbackHandler instance that should be called by the ActivityStateManager in the
     * event of a workflow execution problem. This RollbackHandler will presumably perform some
     * compensating operation to revert state for the activity.
     *
     * @return the handler responsible for reverting state for the activity
     */
    public RollbackHandler<T> getRollbackHandler();

    /**
     * Set the RollbackHandler instance that should be called by the ActivityStateManager in the
     * event of a workflow execution problem. This RollbackHandler will presumably perform some
     * compensating operation to revert state for the activity.
     *
     * @param rollbackHandler the handler responsible for reverting state for the activity
     */
    public void setRollbackHandler(RollbackHandler<T> rollbackHandler);

    /**
     * Retrieve the optional region label for the RollbackHandler. Setting a region allows
     * partitioning of groups of RollbackHandlers for more fine grained control of rollback behavior.
     * Explicit calls to the ActivityStateManager API in an ErrorHandler instance allows explicit rollback
     * of specific rollback handler regions. Note, to disable automatic rollback behavior and enable explicit
     * rollbacks via the API, the workflow.auto.rollback.on.error property should be set to false in your implementation's
     * runtime property configuration.
     *
     * @return the rollback region label for the RollbackHandler instance
     */
    public String getRollbackRegion();

    /**
     * Set the optional region label for the RollbackHandler. Setting a region allows
     * partitioning of groups of RollbackHandlers for more fine grained control of rollback behavior.
     * Explicit calls to the ActivityStateManager API in an ErrorHandler instance allows explicit rollback
     * of specific rollback handler regions. Note, to disable automatic rollback behavior and enable explicit
     * rollbacks via the API, the workflow.auto.rollback.on.error property should be set to false in your implementation's
     * runtime property configuration.
     *
     * @param rollbackRegion the rollback region label for the RollbackHandler instance
     */
    public void setRollbackRegion(String rollbackRegion);

    /**
     * Retrieve any user-defined state that should accompany the RollbackHandler. This configuration will be passed to
     * the RollbackHandler implementation at runtime.
     *
     * @return any user-defined state configuratio necessary for the execution of the RollbackHandler
     */
    public Map<String, Object> getStateConfiguration();

    /**
     * Set any user-defined state that should accompany the RollbackHandler. This configuration will be passed to
     * the RollbackHandler implementation at runtime.
     *
     * @param stateConfiguration any user-defined state configuration necessary for the execution of the RollbackHandler
     */
    public void setStateConfiguration(Map<String, Object> stateConfiguration);

    /**
     * Whether or not this activity should automatically register a configured RollbackHandler with the ActivityStateManager.
     * It is useful to adjust this value if you plan on using the ActivityStateManager API to register RollbackHandlers
     * explicitly in your code. The default value is true.
     *
     * @return Whether or not to automatically register a RollbackHandler with the ActivityStateManager
     */
    public boolean getAutomaticallyRegisterRollbackHandler();

    /**
     * Whether or not this activity should automatically register a configured RollbackHandler with the ActivityStateManager.
     * It is useful to adjust this value if you plan on using the ActivityStateManager API to register RollbackHandlers
     * explicitly in your code. The default value is true.
     *
     * @param automaticallyRegisterRollbackHandler Whether or not to automatically register a RollbackHandler with the ActivityStateManager
     */
    public void setAutomaticallyRegisterRollbackHandler(boolean automaticallyRegisterRollbackHandler);
}
