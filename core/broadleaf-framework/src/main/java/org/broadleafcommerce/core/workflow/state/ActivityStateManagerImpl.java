/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.workflow.state;

import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * @author Jeff Fischer
 */
@Service("blActivityStateManager")
public class ActivityStateManagerImpl implements ActivityStateManager {

    private static ActivityStateManager ACTIVITY_STATE_MANAGER;

    public static ActivityStateManager getStateManager() {
        return ACTIVITY_STATE_MANAGER;
    }

    protected Map<String, List<StateContainer>> stateMap = Collections.synchronizedMap(new HashMap<String, List<StateContainer>>());

    @PostConstruct
    public void init() {
        ACTIVITY_STATE_MANAGER = this;
    }

    @Override
    public void clearAllState() {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        stateMap.remove(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
    }

    @Override
    public void clearRegionState(String region) {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        List<StateContainer> containers = stateMap.get(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
        if (containers != null) {
            Iterator<StateContainer> itr = containers.iterator();
            while(itr.hasNext()) {
                String myRegion = itr.next().getRegion();
                if ((region == null && myRegion == null) || (region != null && region.equals(myRegion))) {
                    itr.remove();
                    break;
                }
            }
        }
    }

    @Override
    public void registerState(RollbackHandler rollbackHandler, Map<String, Object> stateItems) {
        registerState(null, null, null, rollbackHandler, stateItems);
    }

    @Override
    public void registerState(Activity<? extends ProcessContext> activity, ProcessContext processContext, RollbackHandler rollbackHandler, Map<String, Object> stateItems) {
        registerState(activity, processContext, null, rollbackHandler, stateItems);
    }

    @Override
    public void registerState(Activity<? extends ProcessContext> activity, ProcessContext processContext, String region, RollbackHandler rollbackHandler, Map<String, Object> stateItems) {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        List<StateContainer> containers = stateMap.get(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
        if (containers == null) {
            containers = new ArrayList<StateContainer>();
            stateMap.put(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId(), containers);
        }

        StateContainer stateContainer = new StateContainer();
        stateContainer.setRollbackHandler(rollbackHandler);
        stateContainer.setStateItems(stateItems);
        stateContainer.setRegion(region);
        stateContainer.setActivity(activity);
        stateContainer.setProcessContext(processContext);

        containers.add(stateContainer);
    }

    @Override
    public void rollbackAllState() throws RollbackFailureException {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        List<StateContainer> containers = stateMap.get(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
        if (containers != null) {
            Iterator<StateContainer> itr = containers.iterator();
            while (itr.hasNext()) {
                StateContainer stateContainer = itr.next();
                stateContainer.getRollbackHandler().rollbackState(stateContainer.getActivity(), stateContainer.getProcessContext(), stateContainer.getStateItems());
                itr.remove();
            }
        }
    }

    @Override
    public void rollbackRegionState(String region) throws RollbackFailureException {
        RollbackStateLocal rollbackStateLocal = getRollbackStateLocal();
        List<StateContainer> containers = stateMap.get(rollbackStateLocal.getThreadId() + "_" + rollbackStateLocal.getWorkflowId());
        if (containers != null) {
            Iterator<StateContainer> itr = containers.iterator();
            while (itr.hasNext()) {
                StateContainer stateContainer = itr.next();
                if ((region == null && stateContainer.getRegion() == null) || (region != null && region.equals(stateContainer.getRegion()))) {
                    stateContainer.getRollbackHandler().rollbackState(stateContainer.getActivity(), stateContainer.getProcessContext(), stateContainer.getStateItems());
                    itr.remove();
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
        private RollbackHandler rollbackHandler;
        private Map<String, Object> stateItems;
        private Activity<? extends ProcessContext> activity;
        private ProcessContext processContext;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public RollbackHandler getRollbackHandler() {
            return rollbackHandler;
        }

        public void setRollbackHandler(RollbackHandler rollbackHandler) {
            this.rollbackHandler = rollbackHandler;
        }

        public Map<String, Object> getStateItems() {
            return stateItems;
        }

        public void setStateItems(Map<String, Object> stateItems) {
            this.stateItems = stateItems;
        }

        public Activity<? extends ProcessContext> getActivity() {
            return activity;
        }

        public void setActivity(Activity<? extends ProcessContext> activity) {
            this.activity = activity;
        }

        public ProcessContext getProcessContext() {
            return processContext;
        }

        public void setProcessContext(ProcessContext processContext) {
            this.processContext = processContext;
        }
    }
}
