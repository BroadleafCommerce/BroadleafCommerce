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
import org.broadleafcommerce.url.handler.ehcache.EhCacheUrlStreamHandlerFactory;
import org.broadleafcommerce.url.handler.ehcache.Handler;
import org.ehcache.config.Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.ehcache.xml.ConfigurationParser;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.cache.jcache.JCacheManagerFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class MergeEhCacheManagerFactoryBean extends JCacheManagerFactoryBean implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @javax.annotation.Resource(name="blMergedCacheConfigLocations")
    protected Set<String> mergedCacheConfigLocations;

    protected List<Resource> configLocations;

    @Nullable
    private CacheManager cacheManager;

    @Override
    public void afterPropertiesSet() {
        List<Resource> resources = new ArrayList<>();
        if (mergedCacheConfigLocations != null && !mergedCacheConfigLocations.isEmpty()) {
            for (String location : mergedCacheConfigLocations) {
                resources.add(applicationContext.getResource(location));
            }
        }
        if (configLocations != null && !configLocations.isEmpty()) {
            resources.addAll(configLocations);
        }
        try {
            MergeXmlConfigResource merge = new MergeXmlConfigResource();
            ResourceInputStream[] sources = new ResourceInputStream[resources.size()];
            int j=0;
            for (Resource resource : resources) {
                sources[j] = new ResourceInputStream(resource.getInputStream(), resource.getURL().toString());
                j++;
            }

            // TODO this does not work because it's a ByteArrayResource and has no URI
            Caching.setDefaultClassLoader(getClass().getClassLoader());
            CachingProvider provider = Caching.getCachingProvider();
            if (EhcacheCachingProvider.class.isAssignableFrom(provider.getClass())) {
                Resource mergeResource = merge.getMergedConfigResource(sources);
                Handler.inputStream = mergeResource.getInputStream();
//                URL.setURLStreamHandlerFactory(new EhCacheUrlStreamHandlerFactory(mergeResource.getInputStream()));
                EhcacheCachingProvider ehcacheProvider = (EhcacheCachingProvider) provider;

/*                MergeConfigurationParser configurationParser = new MergeConfigurationParser();
                Configuration config = configurationParser.parseConfiguration(mergeResource.getInputStream(),
                                                            provider.getDefaultClassLoader(),
                                                            Collections.emptyMap())
                        .getConfiguration();*/

                this.cacheManager = ehcacheProvider.getCacheManager(new URI("ehcache:fakeuri"), getClass().getClassLoader());
            } else {
                log.warn("Caching Provider does not support merged cache locations. Falling back to default");
            }
        } catch (Exception e) {
            throw new FatalBeanException("Unable to merge cache locations", e);
        }
        super.afterPropertiesSet();
    }

    @Override
    @Nullable
    public CacheManager getObject() {
        return this.cacheManager;
    }

    @Override
    public Class<?> getObjectType() {
        return (this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    @Override
    public void destroy() {
        if (this.cacheManager != null) {
            this.cacheManager.close();
        }
    }

    public void setConfigLocations(List<Resource> configLocations) throws BeansException {
        this.configLocations = configLocations;
    }
}
