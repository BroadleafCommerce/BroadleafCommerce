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
package org.broadleafcommerce.common.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * @deprecated Instead of using anything around the -Druntime-environment values, you should be using Spring profiles
 * and properties activated with that via {@link BroadleafEnvironmentConfiguringApplicationListener}.
 */
@Deprecated
public class RuntimeEnvironmentPropertiesManager implements BeanFactoryAware {

    private static final Log LOG = LogFactory.getLog(RuntimeEnvironmentPropertiesManager.class);

    protected ConfigurableBeanFactory beanFactory;

    protected String prefix;

    public String getPrefix() {
        return prefix;
    }

    public String setPrefix(String prefix) {
        return this.prefix = prefix;
    }

    public String getProperty(String key, String suffix) {
        if(key==null) {
            return null;
        }
        String name = prefix + "." + key + "." + suffix;
        if (prefix == null) {
            name = key + "." + suffix;
        }
        String rv = beanFactory.resolveEmbeddedValue("${" + name + "}");
       
        if (rv == null ||rv.equals("${" + name + "}")) {
            LOG.warn("property ${" + name + "} not found, Reverting to property without suffix"+suffix);
            rv = getProperty(key);
        }
        return rv;

    }

    public String getProperty(String key) {
        if(key==null) {
            return null;
        }
        String name = prefix + "." + key;
        if (prefix == null) {
            name = key;
        }
        String rv = beanFactory.resolveEmbeddedValue("${" + name + "}");
        if(rv.equals("${" + name + "}")) {
            return null;
        }
        return rv;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

}
