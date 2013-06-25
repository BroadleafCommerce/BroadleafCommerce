/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web.resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.broadleafcommerce.common.resource.GeneratedResource;
import org.springframework.core.io.Resource;

import java.util.List;


/**
 * An abstract GeneratedResourceHandler that is capable of responding to a single specified filename and generate
 * contents for that filename. This abstract parent will handle caching of the generated resource.
 * 
 * @author Andre Azzolini (apazzolini)
 *
 */
public abstract class AbstractGeneratedResourceHandler {
    
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
     * Attempts to retrive the requested resource from cache. If not cached, generates the resource, caches it,
     * and then returns it
     * 
     * @param request
     * @param location
     * @return the generated resource
     */
    public Resource getResource(String path, List<Resource> locations) {
        Element e = getGeneratedResourceCache().get(path);
        Resource r = null;
        
        if (e == null || e.getObjectValue() == null) {
            r = getFileContents(path, locations);
            if (!(r instanceof GeneratedResource) || ((GeneratedResource) r).isCacheable()) {
                e = new Element(path,  r);
                getGeneratedResourceCache().put(e);
            }
        } else {
            r = (Resource) e.getObjectValue();
        }
        
        return r;
    }
    
    protected GeneratedResource createGeneratedResource(String contents, String path, boolean cacheable) {
        return new GeneratedResource(contents.getBytes(), path, cacheable);
    }
    
    protected Cache getGeneratedResourceCache() {
        if (generatedResourceCache == null) {
            generatedResourceCache = CacheManager.getInstance().getCache("generatedResourceCache");
        }
        return generatedResourceCache;
    }
    
}
