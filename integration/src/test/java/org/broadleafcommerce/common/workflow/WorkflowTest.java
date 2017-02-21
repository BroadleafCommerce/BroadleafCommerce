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
package org.broadleafcommerce.common.workflow;

import org.broadleafcommerce.core.pricing.service.workflow.TotalActivity;
import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ModuleActivity;
import org.broadleafcommerce.core.workflow.PassThroughActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.SequenceProcessor;
import org.broadleafcommerce.core.workflow.state.test.TestExampleModuleActivity;
import org.broadleafcommerce.core.workflow.state.test.TestRollbackActivity;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import javax.annotation.Resource;


/**
 * 
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@ContextHierarchy(@ContextConfiguration(name = "siteRoot"))
public class WorkflowTest extends TestNGSiteIntegrationSetup {
    
    
    @ImportResource("classpath:bl-applicationContext-test-module.xml")
    @Configuration
    public static class WorkflowTestConfig {}
    
    @Resource(name = "blCheckoutWorkflowActivities")
    protected List<Activity<ProcessContext<? extends Object>>> activities;
    
    @Resource(name = "blCheckoutWorkflow")
    protected SequenceProcessor checkoutWorkflow;
    
    @Resource(name = "blTotalActivity")
    protected TotalActivity totalActivity;
    
    
    @Test
    public void testMergedOrderedActivities() {
        Assert.assertEquals(activities.get(0).getClass(), PassThroughActivity.class);
        Assert.assertEquals(activities.get(0).getOrder(), 100);
        
        Assert.assertEquals(activities.get(5).getClass(), PassThroughActivity.class);
        Assert.assertEquals(activities.get(5).getOrder(), 3000);
    }
    
    @Test
    public void testFrameworkOrderingChanged() {
        Assert.assertEquals(totalActivity.getOrder(), 8080);
    }
    
    @Test
    public void testDetectedModuleActivity() {
        List<ModuleActivity> moduleActivities = checkoutWorkflow.getModuleActivities();
        Assert.assertEquals(moduleActivities.size(), 1);
        Assert.assertEquals(moduleActivities.get(0).getModuleName(), "integration");
    }
    
    @Test
    public void testNonExplicitOrdering() {
        Assert.assertEquals(activities.get(activities.size() - 1).getClass(), TestExampleModuleActivity.class);
        Assert.assertEquals(activities.get(activities.size() - 1).getOrder(), Ordered.LOWEST_PRECEDENCE);
    }
    
    /**
     * Tests that a merged activity can have the same order as a framework activity and come after it
     */
    @Test
    public void testSameOrderingConfiguredActivity() {
        Assert.assertEquals(activities.get(8).getClass(), TestRollbackActivity.class);
    }
    
    @Test
    public void testInBetweenActivity() {
        Assert.assertEquals(activities.get(5).getClass(), PassThroughActivity.class);
    }
    
}
