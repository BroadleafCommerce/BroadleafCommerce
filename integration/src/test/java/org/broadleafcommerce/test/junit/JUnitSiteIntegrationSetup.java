/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.test.junit;

import org.broadleafcommerce.test.config.BroadleafSiteIntegrationTest;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * <p>
 * Used as a customization point so that you can extend this class and add additional beans to the live Broadleaf ApplicationContext.
 * This also adds the additional default {@link TestExecutionListener}s that are not included in the Spring super class.
 * 
 * <p>
 * This class hierarchy <b>must</b> be used if you are making any customizations to the ApplicationContext. If you do not need
 * any addtiional customizations for your tests then you can just use {@literal @}BroadleafAdminIntegrationTest directly.
 * 
 * @see BroadleafSiteIntegrationTest
 * @see JUnitTransactionalSiteIntegrationSetup
 * @author Phillip Verheyden (phillipuniverse)
 */
@BroadleafSiteIntegrationTest
@TestExecutionListeners({TransactionalTestExecutionListener.class, SqlScriptsTestExecutionListener.class})
public class JUnitSiteIntegrationSetup extends AbstractJUnit4SpringContextTests {

}
