/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.io.IOUtils;
import org.broadleafcommerce.common.extensibility.cache.DefaultJCacheUriProvider;
import org.broadleafcommerce.common.extensibility.context.merge.MergeXmlConfigResource;
import org.broadleafcommerce.common.extensibility.context.merge.ResourceInputStream;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component("blJCacheUriProvider")
@ConditionalOnEhCache
public class DefaultEhCacheUriProvider extends DefaultJCacheUriProvider implements ApplicationContextAware, InitializingBean {

    @javax.annotation.Resource(name = "blMergedCacheConfigLocations")
    protected Set<String> mergedCacheConfigLocations;

    protected URI cacheManagerUri = new File(System.getProperty("java.io.tmpdir"), "broadleaf-merged-jcache.xml").toURI();

    protected List<Resource> configLocations;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        mergeCacheLocations();
    }

    @Override
    public URI getJCacheUri() {
        return cacheManagerUri;
    }

    protected void mergeCacheLocations() throws IOException {
        List<Resource> resources = new ArrayList<>();
        if (mergedCacheConfigLocations != null && !mergedCacheConfigLocations.isEmpty()) {
            for (String location : mergedCacheConfigLocations) {
                resources.add(applicationContext.getResource(location));
            }
        }
        if (configLocations != null && !configLocations.isEmpty()) {
            resources.addAll(configLocations);
        }
        MergeXmlConfigResource merge = new MergeXmlConfigResource();
        ResourceInputStream[] sources = new ResourceInputStream[resources.size()];
        int j = 0;
        for (Resource resource : resources) {
            sources[j] = new ResourceInputStream(resource.getInputStream(), resource.getURL().toString());
            j++;
        }

        Resource mergeResource = merge.getMergedConfigResource(sources);
        createTemporaryMergeXml(mergeResource);
    }
    
    protected File createTemporaryMergeXml(Resource mergedJcacheResource) throws FileNotFoundException, IOException {
        File file = new File(getJCacheUri());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(mergedJcacheResource.getInputStream(), outputStream);
        }
        return file;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setConfigLocations(List<Resource> configLocations) throws BeansException {
        this.configLocations = configLocations;
    }

}
