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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * StructuredContent implementations provide a representation of a generic content
 * item with a set of predefined fields.    The fields associated with an instance
 * of StructuredContent are defined by its associated {@link StructuredContentType}.
 * <br>
 * StructuredContent items are typically maintained via the Broadleaf Commerce admin.
 * <br>
 * Display structured content items is typically done using the
 * {@link org.broadleafcommerce.cms.web.structure.DisplayContentTag} taglib.
 * <br>
 * An typical usage for <code>StructuredContent</code> is to display targeted ads.
 * Consider a <code>StructuredContentType</code> of "ad" with fields "ad-image" and
 * "target-url".    This "ad" might show on a websites home page.  By adding
 * <code>StructuredContentMatchRules</code> and setting the <code>priority</code>,
 * different ads could be shown to different users.
 *
 * It would not be typical in a Broadleaf implementation to extend this interface
 * or to use any implementation other than {@link StructuredContentImpl}.
 *
 * @see {@link StructuredContentType}
 * @see {@link StructuredContentImpl}
 * @see {@link org.broadleafcommerce.cms.web.structure.DisplayContentTag}
 * @author Brian Polster
 * @author Jeff Fischer
 *
 */
public interface StructuredContent extends Serializable,MultiTenantCloneable<StructuredContent> {

    /**
     * Gets the primary key.
     *
     * @return the primary key
     */
    @Nullable
    public Long getId();


    /**
     * Sets the primary key.
     *
     * @param id the new primary key
     */
    public void setId(@Nullable Long id);


    /**
     * Gets the name.
     *
     * @return the name
     */
    @Nonnull
    public String getContentName();

    /**
     * Sets the name.
     * @param contentName
     */
    public void setContentName(@Nonnull String contentName);

    /**
     * Gets the {@link Locale} associated with this content item.
     *
     * @return
     */
    @Nonnull
    public Locale getLocale();


    /**
     * Sets the locale associated with this content item.
     * @param locale
     */
    public void setLocale(@Nonnull Locale locale);

    /**
     * Gets the {@link StructuredContentType} associated with this content item.
     *
     * @return
     */
    @Nonnull
    public StructuredContentType getStructuredContentType();

    /**
     * Sets the {@link StructuredContentType} associated with this content item.
     *
     */
    public void setStructuredContentType(@Nonnull StructuredContentType structuredContentType);

    /**
     * @deprecated - use {@link #getStructuredContentFieldXrefs()}
     * @return
     */
    @Nullable
    @Deprecated
    public Map<String, StructuredContentField> getStructuredContentFields();

    /**
     * @deprecated - Use {@link #setStructuredContentFieldXrefs(Map)}
     *
     * @param structuredContentFields
     */
    @Deprecated
    public void setStructuredContentFields(@Nullable Map<String, StructuredContentField> structuredContentFields);

    /**
     * <b>NOTE: This method is typically only used when wanting to persist new {@link StructuredContentField}s.
     * Users trying to get a field to render should typically invoke {@link #getFieldValues()}.</b>
     * 
     * Gets a map with the custom fields associated with this content item.<br>
     * The map keys are based on the field types.   For example, consider a content
     * item with a <code>StructuredContentType</code> of ad which defined a field
     * named targetUrl.    The field could be accessed with
     * <code>structuredContentItem.getStructuredContentFields().get("targetUrl")</code>
     * @return
     */
    @Nullable
    public Map<String, StructuredContentFieldXref> getStructuredContentFieldXrefs();

    /**
     * Sets the structured content fields for this item.   Would not typically be called
     * outside of the ContentManagementSystem.
     *
     * @param structuredContentFields
     */
    public void setStructuredContentFieldXrefs(@Nullable Map<String, StructuredContentFieldXref> structuredContentFields);

    /**
     * Returns the offlineFlag.   Indicates that the item should no longer appear on the site.
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
     * Returns audit information for this content item.
     *
     * @return
     */
    @Nullable
    public AdminAuditable getAuditable();

    /**
     * Sets audit information for this content item.   Default implementations automatically
     * populate this data during persistence.
     *
     * @param auditable
     */
    public void setAuditable(@Nullable AdminAuditable auditable);

    /**
     * @deprecated - Not supported - simplifying interface
     * 
     * Functionality removed as of BLC 3.2
     * 
     * @return
     */
    @Nullable
    @Deprecated
    public Map<String, StructuredContentRule> getStructuredContentMatchRules();

    /**
     * @deprecated - Not supported - simplifying interface
     * 
     * Functionality removed as of BLC 3.2
     *
     * @param structuredContentMatchRules
     */
    @Deprecated
    public void setStructuredContentMatchRules(@Nullable Map<String, StructuredContentRule> structuredContentMatchRules);

    /**
     * @deprecated - no longer supported - simplifying interface
     * 
     * Functionality removed as of BLC 3.2
     *
     * @return
     */
    @Nullable
    @Deprecated
    public Set<StructuredContentItemCriteria> getQualifyingItemCriteria();

    /**
     * @deprecated - no longer supported - simplifying interface
     * 
     * Functionality removed as of BLC 3.2
     *
     * @param qualifyingItemCriteria
     */
    @Deprecated
    public void setQualifyingItemCriteria(@Nullable Set<StructuredContentItemCriteria> qualifyingItemCriteria);
    
    /**
     * Returns the value of the associated {@link StructuredContentField} if the given fieldName
     * exists in the map returned by {@link #getStructuredContentFieldXrefs()}, and null otherwise.
     * 
     * @param fieldName
     * @return the value of the given field
     */
    public String getFieldValue(String fieldName);

    /**
     * Sets the transient fieldValues map on this StructuredContent.
     * 
     * @param fieldValuesMap
     */
    public void setFieldValues(Map<String, String> fieldValuesMap);
    
    /**
     * @return a map of {@link StructuredContentField} field key names to their associated values
     */
    public Map<String, String> getFieldValues();

}
