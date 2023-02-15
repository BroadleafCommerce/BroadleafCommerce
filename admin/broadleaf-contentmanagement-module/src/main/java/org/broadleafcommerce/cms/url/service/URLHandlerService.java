/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.cms.url.service;

import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.broadleafcommerce.common.site.domain.Site;

import java.util.List;

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
     *
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

    public String buildURLHandlerCacheKey(Site site, String requestUri);

    public Boolean removeURLHandlerFromCache(String mapKey);

}
