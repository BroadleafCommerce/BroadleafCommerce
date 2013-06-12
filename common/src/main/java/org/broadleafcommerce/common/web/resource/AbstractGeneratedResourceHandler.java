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
     * Attempts to retrive the requested resource from cache. If not cached, generates the resource, caches it,
     * and then returns it
     * 
     * @param request
     * @return the generated resource
     */
    public Resource getResource() {
        String filename = getHandledFileName();
        
        Element e = getGeneratedResourceCache().get(filename);
        if (e == null || e.getObjectValue() == null) {
            String contents = getFileContents();
            GeneratedResource r = new GeneratedResource(contents.getBytes(), filename);
            e = new Element(filename,  r);
            getGeneratedResourceCache().put(e);
        }
        
        return (Resource) e.getObjectValue();
    }
    
    /**
     * For example, if the application is deployed under the "test" context and you want to handle the request 
     * for http://localhost/test/js/myFile.js, this method should return the String "/js/myFile.js".
     * 
     * @return the servlet-context-based file name to handle. 
     */
    public abstract String getHandledFileName();
    
    /**
     * @return the String representation of the contents of this generated file
     */
    public abstract String getFileContents();
    
    protected Cache getGeneratedResourceCache() {
        if (generatedResourceCache == null) {
            generatedResourceCache = CacheManager.getInstance().getCache("generatedResourceCache");
        }
        return generatedResourceCache;
    }
    
}
