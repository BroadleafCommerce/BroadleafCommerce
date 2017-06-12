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
package org.broadleafcommerce.cms.page.dao;

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.common.locale.domain.Locale;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by bpolster.
 */
public interface PageDao {

    Page readPageById(Long id);
    
    List<PageField> readPageFieldsByPageId(Long pageId);

    PageTemplate readPageTemplateById(Long id);
    
    /**
     * Saves the given {@link PageTemplate}
     * 
     * @param template the {@link PageTemplate} to save
     * @return the database-saved {@link PageTemplate}
     */
    PageTemplate savePageTemplate(PageTemplate template);

    Page updatePage(Page page);

    void delete(Page page);

    Page addPage(Page clonedPage);

    /**
     * Returns all pages, regardless of any sandbox they are apart of
     * @return all Pages configured in the system
     */
    List<Page> readAllPages();
    
    /**
     * Retrieve a subset of all online and site map included Pages
     *
     * @param limit the maximum number of results
     * @param offset the starting point in the record set
     * @param sortBy the column to sort by
     * @return
     */
    @Nonnull
    List<Page> readOnlineAndIncludedPages(@Nonnull int limit, @Nonnull int offset, @Nonnull String sortBy);

    /**
     * Returns all page templates, regardless of any sandbox they are apart of
     * @return all {@link PageTemplate}s configured in the system
     */
    List<PageTemplate> readAllPageTemplates();

    List<Page> findPageByURI(String uri);
    
    List<Page> findPageByURI(Locale fullLocale, Locale languageOnlyLocale, String uri);

    List<Page> findPageByURI(Locale locale, String uri);

    /**
     * Returns pages that match the given URI and are within 1 day of the active date (potentially cached).
     * This will only cache for 1 day.
     * 
     * @param uri
     * @param activeDate
     * @return
     */
    List<Page> findPageByURIAndActiveDate(String uri, Date activeDate);

    void detachPage(Page page);

    Long getCurrentDateResolution();

    void setCurrentDateResolution(Long currentDateResolution);
}
