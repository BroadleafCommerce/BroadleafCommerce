/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.url.service;

import java.util.List;

import org.broadleafcommerce.cms.url.domain.URLHandler;

/**
 * Created by bpolster.
 */
public interface URLHandlerService {

    /**
     * Checks the passed in URL to determine if there is a matching URLHandler.
     * Returns null if no handler was found.
     * 
     * @param uri
     * @return
     */
    public URLHandler findURLHandlerByURI(String uri);
    
    /**
     * Be cautious when calling this.  If there are a large number of records, this can cause performance and 
     * memory issues.
     * 
     * @return
     */
    public List<URLHandler> findAllURLHandlers();
    
    /**
     * Persists the URLHandler to the DB.
     * @param handler
     * @return
     */
    public URLHandler saveURLHandler(URLHandler handler);

    /**
     * Finds a URLHandler by its ID.
     * 
     * @param id
     * @return
     */
    public URLHandler findURLHandlerById(Long id);
    
    /**
     * This is assumed to be a relatively small list of regex URLHandlers (perhaps in the dozens or hundreds of 
     * records at a maximum).  Having large number of records here (more 1000, for example) 
     * is not likely necessary to accomplish the desired goal, and can cause performance problems.
     * 
     * @return
     */
    public List<URLHandler> findAllRegexURLHandlers();

}
