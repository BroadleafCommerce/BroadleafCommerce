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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Created by bpolster.
 */
public interface Page extends Serializable,MultiTenantCloneable<Page> {

    public Long getId();

    public void setId(Long id);

    public String getFullUrl();

    public void setFullUrl(String fullUrl);

    public String getDescription();

    public void setDescription(String description);

    public PageTemplate getPageTemplate();

    public void setPageTemplate(PageTemplate pageTemplate);

    public Map<String, PageField> getPageFields();

    public void setPageFields(Map<String, PageField> pageFields);

    /**
     * Returns the offlineFlag.   True indicates that the page should no longer appear on the site.
     * The item will still appear within the content administration program but no longer
     * be returned as part of the client facing APIs.
     *
     * @return true if this item is offline
     */
    @Nullable
    public Boolean getOfflineFlag();

    /**
     * Sets the offline flag.
     *
     * @param offlineFlag
     */
    public void setOfflineFlag(@Nullable Boolean offlineFlag);
    
    
    /**
     * Gets the integer priority of this content item.   Items with a lower priority should
     * be displayed before items with a higher priority.
     *
     * @return the priority as a numeric value
     */
    @Nullable
    public Integer getPriority();

    /**
     * Sets the display priority of this item.   Lower priorities should be displayed first.
     *
     * @param priority
     */
    public void setPriority(@Nullable Integer priority);
    
    /**
     * Returns a map of the targeting rules associated with this page.
     *
     * Targeting rules are defined in the content mangagement system and used to
     * enforce which page is returned to the client.
     *
     * @return
     */
    @Nullable
    public Map<String, PageRule> getPageMatchRules();

    /**
     * Sets the targeting rules for this content item.
     *
     * @param pageRules
     */
    public void setPageMatchRules(@Nullable Map<String, PageRule> pageRules);
    
    /**
     * Returns the item (or cart) based rules associated with this content item.
     *
     * @return
     */
    @Nullable
    public Set<PageItemCriteria> getQualifyingItemCriteria();

    /**
     * Sets the item (e.g. cart) based rules associated with this content item.
     *
     * @param qualifyingItemCriteria
     */
    public void setQualifyingItemCriteria(@Nullable Set<PageItemCriteria> qualifyingItemCriteria);

    /**
     * Returns the excludeFromSiteMap flag.  True indicates that the page should be excluded from the site map.
     *
     * @return true if this page is excluded from the site map
     */
    @Nullable
    public boolean getExcludeFromSiteMap();

    /**
     * Sets the excludeFromSiteMap flag.
     * 
     * @param excludeFromSiteMap
     */
    public void setExcludeFromSiteMap(boolean excludeFromSiteMap);

    public Map<String, PageAttribute> getAdditionalAttributes();

    public void setAdditionalAttributes(Map<String, PageAttribute> additionalAttributes);
    
    public Date getActiveStartDate();
    
    public void setActiveStartDate(Date activeStartDate);
    
    public Date getActiveEndDate();
    
    public void setActiveEndDate(Date activeEndDate);
    
    public String getMetaTitle();
    
    public void setMetaTitle(String metaTitle);
    
    public String getMetaDescription();
    
    public void setMetaDescription(String metaDescription);

}
