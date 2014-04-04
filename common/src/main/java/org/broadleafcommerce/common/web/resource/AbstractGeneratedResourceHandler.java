/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.web.resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.cache.CacheStatType;
import org.broadleafcommerce.common.cache.StatisticsService;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;


/**
 * An abstract GeneratedResourceHandler that is capable of responding to a single specified filename and generate
 * contents for that filename. This abstract parent will handle caching of the generated resource.
 * 
 * @author Andre Azzolini (apazzolini)
 *
 */
public abstract class AbstractGeneratedResourceHandler implements Ordered {
    
    public static final int DEFAULT_ORDER = 10000;

    protected static final Log LOG = LogFactory.getLog(AbstractGeneratedResourceHandler.class);

    @javax.annotation.Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;

    @javax.annotation.Resource(name="blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;

    @javax.annotation.Resource(name = "blResourceRequestExtensionManager")
    protected ResourceRequestExtensionManager extensionManager;

    protected Cache generatedResourceCache;
    
    /**
     * @param path
     * @return booelean determining whether or not this handler is able to handle the given request
     */
    public abstract boolean canHandle(String path);
    
    /**
     * @param path
     * @param locations 
     * @return the Resource representing this file
     */
    public abstract Resource getFileContents(String path, List<Resource> locations);
    
    /**
     * @param cachedResource
     * @param path
     * @param locations
     * @return whether or not the given cachedResource needs to be regenerated
     */
    public abstract boolean isCachedResourceExpired(GeneratedResource cachedResource, String path, List<Resource> locations);

    /**
     * Attempts to retrive the requested resource from cache. If not cached, generates the resource, caches it,
     * and then returns it
     * 
     * @param request
     * @param location
     * @return the generated resource
     */
    public Resource getResource(final String path, final List<Resource> locations) {
        Element e = getGeneratedResourceCache().get(path);
        Resource r = null;
        if (e == null) {
            statisticsService.addCacheStat(CacheStatType.GENERATED_RESOURCE_CACHE_HIT_RATE.toString(), false);
        } else {
            statisticsService.addCacheStat(CacheStatType.GENERATED_RESOURCE_CACHE_HIT_RATE.toString(), true);
        }
        boolean shouldGenerate = false;
        if (e == null || e.getObjectValue() == null) {
            shouldGenerate = true;
        } else if (e.getObjectValue() instanceof GeneratedResource
                && isCachedResourceExpired((GeneratedResource) e.getObjectValue(), path, locations)) {
            shouldGenerate = true;
        } else {
            r = (Resource) e.getObjectValue();
        }

        if (shouldGenerate) {
            r = getFileContents(path, locations);
            e = new Element(path,  r);
            getGeneratedResourceCache().put(e);
        }
        return r;
    }
    
    /**
     * This method can be used to read in a resource given a path and at least one resource location
     * 
     * @param path
     * @param locations
     * @return the resource from the file system, classpath, etc, if it exists
     */
    protected Resource getRawResource(String path, List<Resource> locations) {
        ExtensionResultHolder erh = new ExtensionResultHolder();
        extensionManager.getProxy().getOverrideResource(path, erh);
        if (erh.getContextMap().get(ResourceRequestExtensionHandler.RESOURCE_ATTR) != null) {
            return (Resource) erh.getContextMap().get(ResourceRequestExtensionHandler.RESOURCE_ATTR);
        }

		for (Resource location : locations) {
			try {
				Resource resource = location.createRelative(path);
				if (resource.exists() && resource.isReadable()) {
				    return resource;
				}
			}
			catch (IOException ex) {
				LOG.debug("Failed to create relative resource - trying next resource location", ex);
			}
		}
		return null;
    }
    
	/**
	 * @param resource
	 * @return the UTF-8 String represetation of the contents of the resource
	 */
	protected String getResourceContents(Resource resource) throws IOException {
    	StringWriter writer = null;
	    try {
	        writer = new StringWriter();
    	    IOUtils.copy(resource.getInputStream(), writer, "UTF-8");
    	    return writer.toString();
	    } finally {
	        if (writer != null) {
    	        writer.flush();
    	        writer.close();
	        }
	    }
	}
    
    protected Cache getGeneratedResourceCache() {
        if (generatedResourceCache == null) {
            generatedResourceCache = CacheManager.getInstance().getCache("generatedResourceCache");
        }
        return generatedResourceCache;
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
    
}
