/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.test.common.context.autoconfig;

import org.broadleafcommerce.test.common.context.autoconfig.nested.ContainsNestedConfiguration;
import org.broadleafcommerce.test.common.context.autoconfig.nested.ContainsNestedConfiguration.NestedAfterAutoConfiguration;
import org.broadleafcommerce.test.common.context.autoconfig.scan.AfterAutoConfiguration;
import org.broadleafcommerce.test.common.context.autoconfig.scan.ComponentScanningConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PostAutoConfigurationTest {
    
    @Configuration
    @Import({ComponentScanningConfiguration.class, ContainsNestedConfiguration.class})
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
    public static class Config { } 

    @Autowired
    @Qualifier("overridingBean")
    String overridingBean;
    
    @Autowired
    @Qualifier("nonOverridingBean")
    String nonOverridingBean;
    
    @Autowired
    @Qualifier("nestedRunsFirst")
    String nestedRunsFirst;
    
    @Test
    public void testPostAutoConfigurationOverridesAutoConfig() {
        Assert.assertEquals(AfterAutoConfiguration.class.getName(), overridingBean);
    }
    
    @Test
    public void testPostAutoConfigurationRunsSecond() {
        Assert.assertEquals(BaseAutoConfiguration.class.getName(), nonOverridingBean);
    }
    
    @Test
    public void testNestedPostAutoConfigurationRunsFirst() {
        Assert.assertEquals(NestedAfterAutoConfiguration.class.getName(), nestedRunsFirst);
    }

}
