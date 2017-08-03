/*
 * #%L
 * BroadleafCommerce CMS Module
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
    URLHandler findURLHandlerByURI(String uri);

    /**
     * Be cautious when calling this.  If there are a large number of records, this can cause performance and
     * memory issues.
     *
     * @return
     */
    List<URLHandler> findAllURLHandlers();

    /**
     * Finds URLHandlers that have the destination as the given URI
     * @param uri the destination to search for
     * @return list of all URLHandlers with the given URI as a destination
     */
    List<URLHandler> findURLHandlersByDestination(String uri);

    /**
     * Persists the URLHandler to the DB.
     *
     * @param handler
     * @return
     */
    URLHandler saveURLHandler(URLHandler handler);

    /**
     * Persists only the URLs of a URLHandler by its ID. This is necessary because the entity may be replaced
     * with a DTO representing the URLHandler which cannot be persisted as an entity.
     * @param handler the handler to persist
     */
    void savePartialURLHandler(URLHandler handler);

    /**
     * Finds a URLHandler by its ID.
     *
     * @param id
     * @return
     */
    URLHandler findURLHandlerById(Long id);

    /**
     * This is assumed to be a relatively small list of regex URLHandlers (perhaps in the dozens or hundreds of
     * records at a maximum).  Having large number of records here (more 1000, for example)
     * is not likely necessary to accomplish the desired goal, and can cause performance problems.
     *
     * @return
     */
    List<URLHandler> findAllRegexURLHandlers();

    String buildURLHandlerCacheKey(Site site, String requestUri);

    Boolean removeURLHandlerFromCache(String mapKey);

    /**
     * Deletes a saved URLHandler
     * @param urlHandler The URLHandler to delete
     */
    void deleteURLHandler(URLHandler urlHandler);
}
