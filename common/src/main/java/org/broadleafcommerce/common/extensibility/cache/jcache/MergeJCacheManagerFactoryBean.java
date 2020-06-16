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
package org.broadleafcommerce.common.extensibility.cache.jcache;

import org.apache.commons.io.IOUtils;
import org.broadleafcommerce.common.extensibility.cache.ehcache.DefaultEhCacheUtil;
import org.broadleafcommerce.common.extensibility.cache.ehcache.DummyCacheManager;
import org.broadleafcommerce.common.extensibility.context.merge.MergeXmlConfigResource;
import org.broadleafcommerce.common.extensibility.context.merge.ResourceInputStream;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

/**
 * Generic Spring Bean Factory to merge various XML files together and pass them to the JCache {@link CachingProvider} to create a {@link CacheManager}.
 * 
 * The defaults assume you are using EhCache, but this could be used for other JCache implementations.
 * 
 * @author Kelly Tisdell
 *
 */
public class MergeJCacheManagerFactoryBean implements FactoryBean<CacheManager>, BeanClassLoaderAware, InitializingBean, DisposableBean, ApplicationContextAware {
    
    @Nullable
    private Properties cacheManagerProperties;

    @Nullable
    private ClassLoader beanClassLoader;
    
    @Nullable
    private CacheManager cacheManager;

    private ApplicationContext applicationContext;
    
    @javax.annotation.Resource(name="blMergedCacheConfigLocations")
    protected Set<String> mergedCacheConfigLocations;
    
    protected List<Resource> configLocations;

    @Value("${use.dummy.cache:false}")
    protected boolean useDummyCache;
    
    //We use EhCache as the default.  Provide the URI that referrs to a merged JCache (typically EhCache) XML file that will be created
    protected URI cacheManagerUri = DefaultEhCacheUtil.JCACHE_MERGED_XML_RESOUCE_URI;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        if(useDummyCache){
            this.cacheManager = new DummyCacheManager();
            return;
        }

        if (getObject() != null && !getObject().isClosed() && getObject().getURI().equals(cacheManagerUri)) {
            return;
        }
        
        Caching.setDefaultClassLoader(getDefaultClassLoaderForProvider());
        CachingProvider provider = Caching.getCachingProvider();
        
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
            int j = 0;
            for (Resource resource : resources) {
                sources[j] = new ResourceInputStream(resource.getInputStream(), resource.getURL().toString());
                j++;
            }

            Resource mergeResource = merge.getMergedConfigResource(sources);
            createTemporaryMergeXml(mergeResource);
            
            //The ClassLoader needs to be the same as what Hibernate expects (see org.hibernate.cache.jcache.internal.JCacheRegionFactory).
            this.cacheManager = provider.getCacheManager(cacheManagerUri,  
                    provider.getDefaultClassLoader(), cacheManagerProperties);

        } catch (Exception e) {
            throw new FatalBeanException("Unable to merge cache locations", e);
        }
    }

    public void setConfigLocations(List<Resource> configLocations) throws BeansException {
        this.configLocations = configLocations;
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
    
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    public void setCacheManagerProperties(@Nullable Properties cacheManagerProperties) {
        this.cacheManagerProperties = cacheManagerProperties;
    }
    
    public void setCacheManagerUri(@NonNull URI cacheManagerUri) {
        Assert.notNull(cacheManagerUri, "The CacheManager URI cannot be null.");
        this.cacheManagerUri = cacheManagerUri;
    }
    
    protected ClassLoader getDefaultClassLoaderForProvider() {
        if (beanClassLoader != null) {
            return beanClassLoader;
        }
        return getClass().getClassLoader();
    }
    
    protected File createTemporaryMergeXml(Resource mergedJcacheResource) throws FileNotFoundException, IOException {
        File file = new File(cacheManagerUri);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(mergedJcacheResource.getInputStream(), outputStream);
        }
        return file;
    }

}
