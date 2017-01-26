/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Base Integration Test Setup java file for Admin based integration tests. This base class has all the
 * applicationContext's shared by Integration tests for Admin based testing. Extend from this class on a
 * per project basis with another setup file that contains only an @ContextHierarchy(@ContextConfiguration)
 * that references this "adminContexts" ContextConfiguration and add only the contexts, in the locations
 * parameter, that you need to run your tests at that level. Then extend off of that setup file with your
 * actual integration tests. IntegrationSetup files should not have any code in their body's.
 *
 */
@TransactionConfiguration(transactionManager = "blTransactionManager", defaultRollback = true)
@ContextHierarchy({
@ContextConfiguration(name = "adminRoot",
    locations = {"classpath:/bl-open-admin-contentClient-applicationContext.xml",
            "classpath:/bl-open-admin-contentCreator-applicationContext.xml",
            "classpath:/bl-admin-applicationContext.xml",
            "classpath:/bl-cms-contentClient-applicationContext.xml",
            "classpath:/bl-cms-contentCreator-applicationContext.xml"},
    loader = BroadleafGenericGroovyXmlWebContextLoader.class)
})
@WebAppConfiguration
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class JUnitAdminIntegrationSetup extends AbstractJUnit4SpringContextTests {
    /*
     * Intentionally left blank. Subclasses should be inheriting from
     * the configuration annotations defined at the class level
     */
}
