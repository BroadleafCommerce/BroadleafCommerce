/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.test;

import org.broadleafcommerce.common.extensibility.context.MergeClassPathXMLApplicationContext;
import org.broadleafcommerce.common.extensibility.context.StandardConfigLocations;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.lang.management.ManagementFactory;

@TransactionConfiguration(transactionManager = "blTransactionManager", defaultRollback = true)
@TestExecutionListeners(inheritListeners = false, value = {MergeDependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, MergeTransactionalTestExecutionListener.class})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    private static MergeClassPathXMLApplicationContext mergeContext = null;
    
    public static MergeClassPathXMLApplicationContext getContext() {
            
        try {
            if (mergeContext == null) {
                String[] contexts = StandardConfigLocations.retrieveAll(StandardConfigLocations.TESTCONTEXTTYPE);
                
                String[] additionalContexts = (ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-Dlegacy=true")) 
                        ? new String[]{"bl-applicationContext-test-legacy.xml"} 
                        : new String[]{};
                
                mergeContext = new MergeClassPathXMLApplicationContext(contexts, additionalContexts);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mergeContext;
    }
    
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

}
