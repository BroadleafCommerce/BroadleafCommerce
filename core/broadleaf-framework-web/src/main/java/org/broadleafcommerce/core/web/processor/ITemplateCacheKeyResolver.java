/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.processor;


/**
 * Used to build a cacheKey for caching templates.
 * @author Brian Polster (bpolster)
 */
public interface ITemplateCacheKeyResolver {
    
    /**
     * Takes in the cacheKey param and parses into an actual cacheKey.    A simple implementation simply returns the
     * templateName + cacheKey.    
     * 
     * More complex implementations could allow additional meaning with the cacheKey.    For example, an implementation
     * could honor a cacheKey like this  <pre>cacheKey="includeQueryParams=true,key=value"</pre> and convert it into 
     * something meaningful for the application.
     * 
     * @param templateName - Name of the template that is subject to being cached. 
     * @param cacheKey - Value of the param passed in from the template
     * 
     * @see SimpleCacheKeyResolver
     * @return
     */
    public String resolveCacheKey(String templateName, String cacheKey);
}
