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

import org.broadleafcommerce.test.config.BroadleafAdminIntegrationTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * Base TestNG support class used for Broadleaf Admin tests. This is slightly different than the normal {@link AbstractTestNGSpringContextTests}
 * in that this also includes the other default {@link TestExecutionListeners} in order to use {@literal @}Transactional in test methods,
 * while not marking the entire test as {@literal @}Transactional (like in {@link TestNGTransactionalAdminIntegrationSetup}.
 * 
 * @see BroadleafAdminIntegrationTest
 * @see TestNGTransactionalAdminIntegrationSetup
 * @author Phillip Verheyden (phillipuniverse)
 */
@BroadleafAdminIntegrationTest
@TestExecutionListeners({TransactionalTestExecutionListener.class, SqlScriptsTestExecutionListener.class})
public abstract class TestNGAdminIntegrationSetup extends AbstractTestNGSpringContextTests {
    
}
