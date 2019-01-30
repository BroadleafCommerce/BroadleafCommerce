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
package org.broadleafcommerce.core.workflow.state;

import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.annotation.PostConstruct;

/**
 * @author Jeff Fischer
 */
@Service("blActivityStateManager")
public class ActivityStateManagerImpl<T extends ProcessContext<?>> implements ActivityStateManager<T> {

    private static ActivityStateManager ACTIVITY_STATE_MANAGER;

    public static ActivityStateManager getStateManager() {
        return ACTIVITY_STATE_MANAGER;
    }

    protected Map<String, Stack<StateContainer>> stateMap = Collections.synchronizedMap(new HashMap<String, Stack<StateContainer>>());

    @PostConstruct
    public void init() {
        ACTIVITY_STATE_MANAGER = this;
    }

    @Override
    public void clearAllState() {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        stateMap.remove(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
        RollbackStateLocal.clearRollbackStateLocal();
    }

    @Override
    public void clearRegionState(String region) {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        Stack<StateContainer> containers = stateMap.get(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
        if (containers != null) {
            while (!containers.empty()) {
                String myRegion = containers.pop().getRegion();
                if ((region == null && myRegion == null) || (region != null && region.equals(myRegion))) {
                    break;
                }
            }
        }
    }

    @Override
    public void registerState(RollbackHandler<T> rollbackHandler, Map<String, Object> stateItems) {
        registerState(null, null, null, rollbackHandler, stateItems);
    }

    @Override
    public void registerState(Activity<T> activity, T processContext, RollbackHandler<T> rollbackHandler, Map<String, Object> stateItems) {
        registerState(activity, processContext, null, rollbackHandler, stateItems);
    }

    @Override
    public void registerState(Activity<T> activity, T processContext, String region, RollbackHandler<T> rollbackHandler, Map<String, Object> stateItems) {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        Stack<StateContainer> containers = stateMap.get(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
        if (containers == null) {
            containers = new Stack<>();
            stateMap.put(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId(), containers);
        }

        StateContainer stateContainer = new StateContainer();
        stateContainer.setRollbackHandler(rollbackHandler);
        stateContainer.setStateItems(stateItems);
        stateContainer.setRegion(region);
        stateContainer.setActivity(activity);
        stateContainer.setProcessContext(processContext);

        containers.push(stateContainer);
    }

    @Override
    public void rollbackAllState() throws RollbackFailureException {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        Stack<StateContainer> containers = stateMap.get(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
        if (containers != null) {
            while (!containers.empty()) {
                StateContainer stateContainer = containers.pop();
                stateContainer.getRollbackHandler().rollbackState(stateContainer.getActivity(), stateContainer.getProcessContext(), stateContainer.getStateItems());
            }
        }
    }

    @Override
    public void rollbackRegionState(String region) throws RollbackFailureException {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        Stack<StateContainer> containers = stateMap.get(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
        if (containers != null) {
            while (!containers.empty()) {
                StateContainer stateContainer = containers.pop();
                if ((region == null && stateContainer.getRegion() == null) || (region != null && region.equals(stateContainer.getRegion()))) {
                    stateContainer.getRollbackHandler().rollbackState(stateContainer.getActivity(), stateContainer.getProcessContext(), stateContainer.getStateItems());
                }
            }
        }
    }

    protected RollbackStateLocal getRollbackStateLocal() {
        RollbackStateLocal rollbackStateLocal = RollbackStateLocal.getRollbackStateLocal();
        if (rollbackStateLocal == null) {
            throw new IllegalThreadStateException("Unable to perform ActivityStateManager operation, as the RollbackStateLocal instance is not set on the current thread! ActivityStateManager methods may not be called outside the scope of workflow execution.");
        }
        return rollbackStateLocal;
    }

    private class StateContainer {

        private String region;
        private RollbackHandler<T> rollbackHandler;
        private Map<String, Object> stateItems;
        private Activity<T> activity;
        private T processContext;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public RollbackHandler<T> getRollbackHandler() {
            return rollbackHandler;
        }

        public void setRollbackHandler(RollbackHandler<T> rollbackHandler) {
            this.rollbackHandler = rollbackHandler;
        }

        public Map<String, Object> getStateItems() {
            return stateItems;
        }

        public void setStateItems(Map<String, Object> stateItems) {
            this.stateItems = stateItems;
        }

        public Activity<T> getActivity() {
            return activity;
        }

        public void setActivity(Activity<T> activity) {
            this.activity = activity;
        }

        public T getProcessContext() {
            return processContext;
        }

        public void setProcessContext(T processContext) {
            this.processContext = processContext;
        }
    }
}
