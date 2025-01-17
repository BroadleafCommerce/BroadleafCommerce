/*-
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.DefaultPostLoaderDao;
import org.broadleafcommerce.common.util.ApplicationContextHolder;
import org.broadleafcommerce.test.config.BroadleafSiteIntegrationTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Base TestNG support class used for Broadleaf Site tests. This is slightly different than the normal {@link AbstractTestNGSpringContextTests}
 * in that this also includes the other default {@link TestExecutionListeners} in order to use {@literal @}Transactional in test methods,
 * while not marking the entire test as {@literal @}Transactional (like in {@link TestNGTransactionalSiteIntegrationSetup}.
 * 
 * @see BroadleafSiteIntegrationTest
 * @see TestNGTransactionalSiteIntegrationSetup
 * @author Phillip Verheyden (phillipuniverse)
 */
@BroadleafSiteIntegrationTest
//Seems spring should add DependencyInjectionTestExecutionListener by default, but in spring boot 3/spring 6 at lest now
//because of defined listeners explicitly it doesn't have it, and so test classes are missing injected beans
// and also spring context is not started early enough, so some other code triggers loading of entities before we register our transformers
@TestExecutionListeners({TransactionalTestExecutionListener.class, SqlScriptsTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, ServletTestExecutionListener.class})
public abstract class TestNGSiteIntegrationSetup extends AbstractTestNGSpringContextTests {

    protected final Log LOG = LogFactory.getLog(getClass());

    /**
     * This was added as a result of update to 7.0 with new spring, hdsqldb, testng, surefire and maybe something else
     * So now spring test framework caches application context and so when some test is starting it will check
     * if needs to create a new context or can use cached. Seems this basically depends on @ContextHierarchy
     * and @ContextConfiguration and maybe their combination. Now we do have some ApplicationContextAware implementations
     * that provide access to a context via static method, so they store it in static variable. Lets assume situation
     * TestA is running, as it is the first application context(lest name it contextA) will be created for it. We have
     * DefaultPostLoaderDao that not only caches context in its static variable but also acts as a singleton and also
     * caches its instance in static variable. If it happens that during TestA run there will be invocation of
     * PostLoaderDao it will have spring context pointing to contextA and PostLoaderDao instance to the one that was get
     * from the contextA. Now TestB is running and let's imaging that new context is created for it. Now PostLoaderDao
     * has application context instance pointing to contextB - beacuse of AppicationContextAware interface, but
     * PostLoaderDao instance is pointing to the one the was created from contextA. The problem here that PostLoader has
     * a references injected and one of them will have a reference to em, and em one way or another will be bound to
     * db connection that in case of in-memory/in-process hsqldb will hold a reference to hsqldb session, that has
     * a reference to database itself.
     * Now test2 does something in transaction, let's say read/query for example. Now inside transaction
     * loader dao is invoked(like DiscreteOrderItem.getSku). In case of transaction is not marked as a
     * readonly and hibernate session flush mode is not set to manual, hibernate will flush and hsqldb session/db
     * will mark tables involved in read as "writing". Now Postloader does findById or whatever query that reference
     * one of the tables that were read in the beginning of the transaction. HSQLDB session/database will check if it
     * has "writing" tables - and it does, it will get associated to corresponding table(sku in this case) hsqldb session
     * And check if the current session and associated are the same, if not it will pause current thread till number
     * of holding "writing lock" tables will not decrement. And because it all happens in one transaction/thread it
     * will hang forever. So this is a defensive mechanism to make sure that application code is always using the
     * lates version of the application context and doesn't have a stale static references. This happens in tests only
     * as in real application there is only 1 context that is created on a startup.
     * <p/>
     * This method is marked as @BeforeMethod because for some reason jenkins(can't reproduce locally) runs it in some
     * sort of parallel way that causing issue with PostLoaderDao - seems it can run method from class A, then method
     * from class B, and then continue to run some other method from class A, so context is bouncing between runs and
     * this can cause that some queries(that are run through PostLoaderDao) return unexpected results - like something not found etc.
     */
    @BeforeMethod(alwaysRun = true)
    public void reSetApplicationContext() {
        DefaultPostLoaderDao.resetApplicationContext(this.applicationContext);
        ApplicationContextHolder.resetApplicationContext(applicationContext);
    }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextPrepareTestInstance")
    public void logStart(){
        LOG.info("Staring Test Class:"+getClass());
    }

    @AfterClass(alwaysRun = true)
    public void logWhenDone(){
        LOG.info("Ending Test Class:"+getClass());
    }
    
}
