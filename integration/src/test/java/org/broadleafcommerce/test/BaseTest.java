/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.test;

import org.broadleafcommerce.common.extensibility.context.MergeClassPathXMLApplicationContext;
import org.broadleafcommerce.common.extensibility.context.StandardConfigLocations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@TransactionConfiguration(transactionManager = "blTransactionManager", defaultRollback = true)
@TestExecutionListeners(inheritListeners = false, value = { MergeDependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, MergeTransactionalTestExecutionListener.class })
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    protected static MergeClassPathXMLApplicationContext mergeContext = null;

    protected static List<String> moduleContexts = new ArrayList<String>();

    public static MergeClassPathXMLApplicationContext getContext() {
        try {
            if (mergeContext == null) {
                // Note that as of 2.2.0, this array will no longer include "bl-applicationContext-test", as we want that to
                // be the very last context loaded.
                String[] contexts = StandardConfigLocations.retrieveAll(StandardConfigLocations.TESTCONTEXTTYPE);
                List<String> allContexts = new ArrayList<String>(Arrays.asList(contexts));

                // We need the content applicationContexts and admin applicationContexts
                allContexts.add("bl-open-admin-contentClient-applicationContext.xml");
                allContexts.add("bl-open-admin-contentCreator-applicationContext.xml");
                allContexts.add("bl-admin-applicationContext.xml");

                // After the framework applicationContexts are loaded, we want the module ones
                allContexts.addAll(moduleContexts);
                
                // Lastly, we want the test applicationContext
                allContexts.add("bl-applicationContext-test.xml");

                // If we're running in legacy test mode, we need that one too
                if (ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-Dlegacy=true")) {
                    allContexts.add("bl-applicationContext-test-legacy.xml");
                }
                
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
