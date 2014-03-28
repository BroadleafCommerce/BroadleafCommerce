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
package org.broadleafcommerce.core.web.service;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

/**
 * Used to build a cacheKey for caching templates.
 * @author Brian Polster (bpolster)
 */
public interface TemplateCacheKeyResolverService {

    /**
     * Takes in the Thymeleaf arguments, element, and templateName.    Returns the cacheKey by which
     * this template can be cached.      
     * 
     * @see SimpleCacheKeyResolver
     * 
     * @param arguments
     * @param element
     * @param templateName
     * @param cacheKeyAttrValue 
     * @return
     */
    public String resolveCacheKey(Arguments arguments, Element element, String templateName, String cacheKeyAttrValue);
}
