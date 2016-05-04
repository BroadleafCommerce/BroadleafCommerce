/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.workflow;

import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.DefaultProcessContextImpl;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.ProcessContextFactory;
import org.broadleafcommerce.core.workflow.SequenceProcessor;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;
import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.broadleafcommerce.test.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Ensures that activities are rolled back in the correct order
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class RollbackTest extends BaseTest {
    
    @Resource(name = "testRollbackWorkflow")
    protected SequenceProcessor testRollbackWorkflow;

    @Test
    public void testRollbackOrder() {
        List<String> results = new ArrayList<String>();
        boolean exceptionThrown = false;
        try {
            testRollbackWorkflow.doActivities(results);
        } catch (WorkflowException e) {
            exceptionThrown = true;
        }
        
        List<String> expected = Arrays.asList("Activity1",
            "Activity2",
            "ActivityA",
            "RollbackActivityA",
            "NestedActivityException",
            "RollbackActivity2",
            "RollbackActivity1");
        Assert.assertTrue(exceptionThrown);
        Assert.assertEquals(results, expected, "Rollback occurred out of order");
    }
    
    public static class SimpleActivity extends BaseActivity<ProcessContext<List<String>>> {

        protected String name;

        public SimpleActivity(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public ProcessContext<List<String>> execute(ProcessContext<List<String>> context) throws Exception {
            context.getSeedData().add(name);
            return context;
        }
        
        @Override
        public boolean getAutomaticallyRegisterRollbackHandler() {
            return true;
        }
        
    }
    
    public static class SimpleRollbackHandler implements RollbackHandler<List<String>> {

        @Override
        public void rollbackState(Activity<? extends ProcessContext<List<String>>> activity, ProcessContext<List<String>> processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
            processContext.getSeedData().add("Rollback" + ((SimpleActivity) activity).getName());
        }
        
    }
    
    public static class NestedActivity extends BaseActivity<ProcessContext<List<String>>> {

        protected SequenceProcessor workflow;
        
        public NestedActivity(SequenceProcessor workflow) {
            this.workflow = workflow;
        }

        @Override
        public ProcessContext<List<String>> execute(ProcessContext<List<String>> context) throws Exception {
            try {
                workflow.doActivities(context.getSeedData());
            } catch (WorkflowException e) {
                context.getSeedData().add("NestedActivityException");
                throw e;
            }
            return context;
        }
        
    }
    
    public static class ExceptionActivity extends BaseActivity<ProcessContext<List<String>>> {

        @Override
        public ProcessContext<List<String>> execute(ProcessContext<List<String>> context) throws Exception {
            throw new RuntimeException();
        }
        
    }

    public static class DummyProcessContextFactory implements ProcessContextFactory<Object, Object> {

        @Override
        public ProcessContext<Object> createContext(Object preSeedData) throws WorkflowException {
            ProcessContext<Object> context = new DefaultProcessContextImpl<>();
            context.setSeedData(preSeedData);
            return context;
        }
        
    }
}
