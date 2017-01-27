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

import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Phillip Verheyden (phillipuniverse)
 * @deprecated use either {@link TestNGAdminIntegrationSetup} or {@link TestNGSiteIntegrationSetup}
 */
@Rollback
@ContextHierarchy({
@ContextConfiguration(name = "commonRoot",
    locations = {"classpath*:/blc-config/admin/bl-*-applicationContext.xml",
            "classpath*:/blc-config/site/bl-*-applicationContext.xml",
            "classpath:bl-applicationContext-test-security.xml",
            "classpath:bl-applicationContext-test.xml"})
})
@WebAppConfiguration
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Deprecated
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    protected ApplicationContext getContext() {
        return applicationContext;
    }

}
