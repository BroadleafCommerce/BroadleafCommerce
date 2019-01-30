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
package org.broadleafcommerce.common.config;

import org.broadleafcommerce.common.cache.AbstractCacheMissAware;
import org.broadleafcommerce.common.config.dao.SystemPropertiesDao;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.config.domain.SystemPropertyImpl;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.common.config.service.SystemPropertiesServiceImpl;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.ehcache.Cache;

/**
 * Tests for the interactions between the system properties service as apart of the Spring Environment
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@ContextHierarchy(@ContextConfiguration(name="siteRoot"))
public class SystemPropertiesTest extends TestNGSiteIntegrationSetup {
    
    public static class SystemPropertiesTestConfig implements FrameworkCommonClasspathPropertySource {

        @Override
        public String getClasspathFolder() {
            return "config/bc/overrideprops";
        }
    }
    
    @Autowired
    protected Environment env;
    
    @Autowired
    protected SystemPropertiesService propsSvc;
    
    @Autowired
    protected SystemPropertiesDao propsDao;
    
    @Test
    @Transactional
    public void testEnvironmentResolvedFromDatabase() {
        SystemProperty prop = new SystemPropertyImpl();
        prop.setName("test.property.with.environment");
        prop.setValue("database");
        propsDao.saveSystemProperty(prop);
        
        String resolvedProperty = env.getProperty(prop.getName(), String.class);
        
        Assert.assertEquals(resolvedProperty, prop.getValue());
    }
    
    @Test
    @Transactional
    public void testOverridesPropertyFiles() {
        String propertyFileResolved = env.getProperty("property.file.override.test", String.class);
        Assert.assertEquals(propertyFileResolved, "propertyfile");
        
        clearSystemPropertiesCache();
        
        SystemProperty prop = new SystemPropertyImpl();
        prop.setName("property.file.override.test");
        prop.setValue("testngtest");
        prop = propsDao.saveSystemProperty(prop);
        String resolvedProperty = env.getProperty(prop.getName(), String.class);
        Assert.assertEquals(resolvedProperty, prop.getValue());
        
        propsDao.deleteSystemProperty(prop);
        clearSystemPropertiesCache();
        
        String fromPropertiesFile = propsSvc.resolveSystemProperty("property.file.override.test");
        Assert.assertEquals(fromPropertiesFile, "propertyfile");
    }
    
    /**
     * Clear out the cache since the SystemPropertiesService/Dao will have cached a not found
     */
    protected void clearSystemPropertiesCache() {
        try {
            Method m = AbstractCacheMissAware.class.getDeclaredMethod("getCache", new Class<?>[] {String.class});
            m.setAccessible(true);
            Cache cache = (Cache) m.invoke(propsDao, "blSystemPropertyNullCheckCache");
            cache.removeAll();
            m = SystemPropertiesServiceImpl.class.getDeclaredMethod("getSystemPropertyCache");
            m.setAccessible(true);
            cache = (Cache) m.invoke(propsSvc);
            cache.removeAll();
        } catch (NoSuchMethodException|SecurityException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
