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

import org.broadleafcommerce.test.config.BroadleafAdminIntegrationTest;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All-in-one annotation that you can use for JUnit tests. This annotation encapsulates everything that you need
 * to initialize a Spring ApplicationContext in a JUnit test. Example usage:
 * 
 * <pre>
 * {@literal @}BroadleafAdminIntegrationTest
 * public class ExampleBroadleafTest {
 *     
 *     {@literal @}Autowired
 *     private DynamicEntityDao dynamicEntityDao;
 *     
 *     {@literal @}Test
 *     public void catalogServiceInjected() {
 *         Assert.assertNotEquals(dynamicEntityDao, null);
 *     }
 * }
 * </pre>
 * 
 * This is used to instantiate all of the Broadleaf Admin beans. For site tests, see {@link JUnitBroadleafSiteIntegrationTest}.
 * 
 * @see BroadleafAdminIntegrationTest
 * @author Phillip Verheyden (phillipuniverse)
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RunWith(SpringRunner.class)
@BroadleafAdminIntegrationTest
public @interface JUnitBroadleafAdminIntegrationTest {

}
