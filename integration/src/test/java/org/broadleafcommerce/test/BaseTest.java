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
package org.broadleafcommerce.test;

import org.broadleafcommerce.common.extensibility.context.MergeClassPathXMLApplicationContext;
import org.broadleafcommerce.common.extensibility.context.StandardConfigLocations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Transactional(transactionManager = "blTransactionManager")
@Rollback
@TestExecutionListeners(inheritListeners = false, value = { MergeDependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    protected static MergeClassPathXMLApplicationContext mergeContext = null;

    protected static List<String> moduleContexts = new ArrayList<>();

    public static MergeClassPathXMLApplicationContext getContext() {
        try {
            if (mergeContext == null) {
                // Note that as of 2.2.0, this array will no longer include "bl-applicationContext-test", as we want that to
                // be the very last context loaded.
                String[] contexts = StandardConfigLocations.retrieveAll(StandardConfigLocations.TESTCONTEXTTYPE);
                List<String> allContexts = new ArrayList<>(Arrays.asList(contexts));

                // We need the content applicationContexts and admin applicationContexts
                allContexts.add("bl-open-admin-contentClient-applicationContext.xml");
                allContexts.add("bl-open-admin-contentCreator-applicationContext.xml");
                allContexts.add("bl-admin-applicationContext.xml");

                // After the framework applicationContexts are loaded, we want the module ones
                allContexts.addAll(moduleContexts);
                
                // Lastly, we want the test applicationContext
                allContexts.add("bl-applicationContext-test.xml");

                String[] strArray = new String[allContexts.size()];
                mergeContext = new MergeClassPathXMLApplicationContext(allContexts.toArray(strArray), new String[]{});
                
                //allow for request-scoped beans that can occur in web application contexts
                RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
                mergeContext.getBeanFactory().registerScope("request", new RequestScope());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return mergeContext;
    }

    protected static List<String> getModuleContexts() {
        return moduleContexts;
    }

}
