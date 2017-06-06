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
package org.broadleafcommerce.cms.page.service;

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.page.dto.PageDTO;

import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;

/**
 * Created by bpolster.
 */
public interface PageService {


    /**
     * Returns the page with the passed in id.
     *
     * @param pageId - The id of the page.
     * @return The associated page.
     */
    Page findPageById(Long pageId);

    /**
     * Returns the page-fields associated with a page.
     * @param pageId
     * @return
     */
    Map<String, PageField> findPageFieldMapByPageId(Long pageId);

    /**
     * Returns the page template with the passed in id.
     *
     * @param id - the id of the page template
     * @return The associated page template.
     */
    PageTemplate findPageTemplateById(Long id);
    
    /**
     * Saves the given {@link PageTemplate}
     * 
     * @param template the {@link PageTemplate} to save
     * @return the database-saved {@link PageTemplate}
     */
    PageTemplate savePageTemplate(PageTemplate template);

    /**
     * Looks up the page from the backend datastore.   Processes the page's fields to
     * fix the URL if the site has overridden the URL for images.   If secure is true
     * and images are being overridden, the system will use https.
     *
     * @param locale - current locale
     * @param uri - the URI to return a page for
     * @param ruleDTOs - ruleDTOs that are used as the data to process page rules
     * @param secure - set to true if current request is over HTTPS
     * @return
     */
    PageDTO findPageByURI(Locale locale, String uri, Map<String,Object> ruleDTOs, boolean secure);
    
    /**
     * Returns all pages, regardless of any sandbox they are apart of
     * @return all {@link Page}s configured in the system
     */
    List<Page> readAllPages();
    
    /**
     * Returns all page templates, regardless of any sandbox they are apart of
     * @return all {@link PageTemplate}s configured in the system
     */
    List<PageTemplate> readAllPageTemplates();

    /**
     * Call to evict all known PageDTOs that are associated with the given page from cache
     * 
     * @param key
     * @return whether successful
     */
    Boolean removePageFromCache(String key);

    Cache getPageCache();

    Cache getPageMapCache();
    
    Cache getUriCachedDateCache();

    /**
     * Builds a list of {@link PageDTO} objects from the given list of {@link Page} objects and caches the list
     * 
     * @param pageList
     * @param secure
     * @param identifier
     * @param locale
     * @return copy of DTOList
     */
    List<PageDTO> buildPageDTOList(List<Page> pageList, boolean secure, String identifier, Locale locale);

    String getPageMapCacheKey(String uri, Long site);
}
