/*
 * #%L
 * BroadleafCommerce Integration
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
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
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
public class RollbackTest extends TestNGSiteIntegrationSetup {
    
    @Resource(name = "testRollbackWorkflow")
    protected SequenceProcessor testRollbackWorkflow;

    @Test
    public void testRollbackOrder() {
        List<String> results = new ArrayList<>();
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
    
    public static class SimpleRollbackHandler implements RollbackHandler<ProcessContext<List<String>>> {

        @Override
        public void rollbackState(Activity<ProcessContext<List<String>>> activity, ProcessContext<List<String>> processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
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
