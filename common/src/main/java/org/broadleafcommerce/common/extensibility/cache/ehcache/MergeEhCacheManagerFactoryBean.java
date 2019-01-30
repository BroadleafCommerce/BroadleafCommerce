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
package org.broadleafcommerce.common.extensibility.cache.ehcache;

import org.broadleafcommerce.common.extensibility.context.merge.MergeXmlConfigResource;
import org.broadleafcommerce.common.extensibility.context.merge.ResourceInputStream;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.ehcache.CacheManager;

public class MergeEhCacheManagerFactoryBean extends EhCacheManagerFactoryBean implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @javax.annotation.Resource(name="blMergedCacheConfigLocations")
    protected Set<String> mergedCacheConfigLocations;

    protected List<Resource> configLocations;

    @Override
    public void destroy() {
        super.destroy();
        try {
            CacheManager cacheManager = getObject();
            Field cacheManagerTimer = CacheManager.class.getDeclaredField("cacheManagerTimer");
            cacheManagerTimer.setAccessible(true);
            Object failSafeTimer = cacheManagerTimer.get(cacheManager);
            Field timer = failSafeTimer.getClass().getDeclaredField("timer");
            timer.setAccessible(true);
            Object time = timer.get(failSafeTimer);
            Field thread = time.getClass().getDeclaredField("thread");
            thread.setAccessible(true);
            Thread item = (Thread) thread.get(time);
            item.setContextClassLoader(Thread.currentThread().getContextClassLoader().getParent());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        List<Resource> temp = new ArrayList<>();
        if (mergedCacheConfigLocations != null && !mergedCacheConfigLocations.isEmpty()) {
            for (String location : mergedCacheConfigLocations) {
                temp.add(applicationContext.getResource(location));
            }
        }
        if (configLocations != null && !configLocations.isEmpty()) {
            for (Resource resource : configLocations) {
                temp.add(resource);
            }
        }
        try {
            MergeXmlConfigResource merge = new MergeXmlConfigResource();
            ResourceInputStream[] sources = new ResourceInputStream[temp.size()];
            int j=0;
            for (Resource resource : temp) {
                sources[j] = new ResourceInputStream(resource.getInputStream(), resource.getURL().toString());
                j++;
            }
            setConfigLocation(merge.getMergedConfigResource(sources));
        } catch (Exception e) {
            throw new FatalBeanException("Unable to merge cache locations", e);
        }
        super.afterPropertiesSet();
    }

    public void setConfigLocations(List<Resource> configLocations) throws BeansException {
        this.configLocations = configLocations;
    }
}
