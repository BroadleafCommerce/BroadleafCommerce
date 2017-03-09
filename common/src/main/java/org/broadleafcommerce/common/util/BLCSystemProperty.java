/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util;

import org.broadleafcommerce.common.config.service.SystemPropertiesPropertySourceRegistrar;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Convenience class to faciliate getting system properties
 * 
 * Note that this class is scanned as a bean to pick up the applicationContext, but the methods
 * this class provides should be invoked statically.
 * 
 * @author Andre Azzolini (apazzolini)
 * @deprecated this class should not be used to statically obtain referenes to properties. Instead, you should either inject
 * an instead of the {@link SystemPropertiesService} directly _or_ inject an {@link Environment} to get properties that way,
 * since {@link SystemPropertiesPropertySourceRegistrar} adds a {@link SystemPropertiesService} {@link PropertySource}
 */
@Deprecated
@Service("blBLCSystemProperty")
public class BLCSystemProperty implements ApplicationContextAware {

    protected static ApplicationContext applicationContext;
    
    /**
     * @see SystemPropertiesService#resolveSystemProperty(String)
     */
    public static String resolveSystemProperty(String name) {
        return getSystemPropertiesService().resolveSystemProperty(name);
    }
    
    public static String resolveSystemProperty(String name, String defaultValue) {
        return getSystemPropertiesService().resolveSystemProperty(name, defaultValue);
    }

    /**
     * @see SystemPropertiesService#resolveIntSystemProperty(String)
     */
    public static int resolveIntSystemProperty(String name) {
        return getSystemPropertiesService().resolveIntSystemProperty(name);
    }
    
    public static int resolveIntSystemProperty(String name, int defaultValue) {
        return getSystemPropertiesService().resolveIntSystemProperty(name, defaultValue);
    }

    /**
     * @see SystemPropertiesService#resolveBooleanSystemProperty(String)
     */
    public static boolean resolveBooleanSystemProperty(String name) {
        return getSystemPropertiesService().resolveBooleanSystemProperty(name);
    }
    
    public static boolean resolveBooleanSystemProperty(String name, boolean defaultValue) {
        return getSystemPropertiesService().resolveBooleanSystemProperty(name, defaultValue);
    }

    /**
     * @see SystemPropertiesService#resolveLongSystemProperty(String)
     */
    public static long resolveLongSystemProperty(String name) {
        return getSystemPropertiesService().resolveLongSystemProperty(name);
    }
    
    public static long resolveLongSystemProperty(String name, long defaultValue) {
        return getSystemPropertiesService().resolveLongSystemProperty(name, defaultValue);
    }
    
    /**
     * @return the "blSystemPropertiesService" bean from the application context
     */
    protected static SystemPropertiesService getSystemPropertiesService() {
        return (SystemPropertiesService) applicationContext.getBean("blSystemPropertiesService");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BLCSystemProperty.applicationContext = applicationContext;
    }

}
