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

package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Created by bpolster.
 */
public interface Page extends Serializable {

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

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);
    
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

}
