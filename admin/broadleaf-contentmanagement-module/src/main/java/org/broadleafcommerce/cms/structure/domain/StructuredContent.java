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

package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

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
public interface StructuredContent extends Serializable {

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
     * Gets the Sandbox associated with the content item.  SandBoxes
     * allow for segmentation of data.    A result of null indicates
     * that this item is in "Production" in a single-site architecture.
     * <br>
     * The processing may differ depending on which type of SandBox is
     * returned.
     *
     * @return
     */
    @Nullable
    public SandBox getSandbox();

    /**
     * Sets the SandBox for this content item.   This method is typically
     * only called by the BLC Content Management System during content-item
     * lifecycle operations like New, Promote, Approve, Deploy.
     *
     * @param sandbox
     */
    public void setSandbox(@Nullable SandBox sandbox);

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
     * Gets a map with the custom fields associated with this content item.<br>
     * The map keys are based on the field types.   For example, consider a content
     * item with a <code>StructuredContentType</code> of ad which defined a field
     * named targetUrl.    The field could be accessed with
     * <code>structuredContentItem.getStructuredContentFields().get("targetUrl")</code>
     * @return
     */
    @Nullable
    public Map<String, StructuredContentField> getStructuredContentFields();

    /**
     * Sets the structured content fields for this item.   Would not typically called
     * outside of the ContentManagementSystem.
     *
     * @param structuredContentFields
     */
    public void setStructuredContentFields(@Nullable Map<String, StructuredContentField> structuredContentFields);

    /**
     * Gets the "deleted" indicator.    Deleted means that the item has been
     * marked for deletion.   If this method returns true, the item will not be returned
     * as part {@link org.broadleafcommerce.cms.structure.service.StructuredContentService#lookupStructuredContentItemsByType(org.broadleafcommerce.common.sandbox.domain.SandBox, StructuredContentType, org.broadleafcommerce.common.locale.domain.Locale, Integer, java.util.Map)}'s}.
     *
     * In a "production sandbox", an item that returns true for <code>getDeletedFlag</code>
     * should also return true for <code>getArchivedFlag</code>
     *
     * @return the deleted indicator or false if none found
     */
    @Nonnull
    public Boolean getDeletedFlag();

    /**
     * Sets the deleted flag for this item.    Would not typically be called
     * outside of the Content Administration system.
     *
     * @param deletedFlag
     */
    public void setDeletedFlag(@Nonnull Boolean deletedFlag);

    /**
     * Gets the archived indicator.   The archivedFlag indicates that the item
     * is no longer of importance to the system.   Items that have been
     * archived may be removed by a data cleanup process.
     *
     * @return true if this item has been archived.   Returns false if not set.
     */
    @Nonnull
    public Boolean getArchivedFlag();

    /**
     * Sets the archived flag for this item.   Would not typically be called
     * outside the Content Administration system.
     *
     * Content items with an archived flag of true will not be returned from
     * most APIs and can be deleted from the system.
     *
     * @param archivedFlag
     */
    public void setArchivedFlag(@Nonnull Boolean archivedFlag);

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
     * Gets the id of a related content item on which this item is based.   This value is
     * used internally by the content management system.    Generally, when an item is
     * promoted through a content workflow to production, the system will set mark the item
     * associated with the originalItemId as archived.
     *
     * @return the id of the originalItemId
     */
    @Nullable
    public Long getOriginalItemId();

    /**
     * The id of the item on which this content item is based.   This property gets set by the
     * content management system when an item is edited.
     *
     * @param originalItemId
     */
    public void setOriginalItemId(@Nullable Long originalItemId);

    /**
     * Builds a copy of this content item.   Used by the content management system when an
     * item is edited.
     *
     * @return a copy of this item
     */
    @Nonnull
    public StructuredContent cloneEntity();

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
     * Returns the locked flag.   If an item is locked, it is being edited by another user.
     *
     * @return true if this item is locked for editing.
     */
    @Nonnull
    public Boolean getLockedFlag();

    /**
     * Sets the lockedFlag for this item.
     *
     * @param lockedFlag
     */
    public void setLockedFlag(@Nullable Boolean lockedFlag);

    /**
     * Gets the <code>SandBox</code> which originally edited this item.   Used by the
     * Content Management System to determine where to return an item that is being
     * rejected.
     *
     * @return
     */
    @Nullable
    public SandBox getOriginalSandBox();

    /**
     * Sets the originalSandBox for this item.   The Content Management System will set this
     * value when an item is promoted from a user sandbox.
     *
     * @param originalSandBox
     */
    public void setOriginalSandBox(@Nullable SandBox originalSandBox);

    /**
     * Returns a map of the targeting rules associated with this content item.
     *
     * Targeting rules are defined in the content mangagement system and used to
     * enforce which items are returned to the client.
     *
     * @return
     */
    @Nullable
    public Map<String, StructuredContentRule> getStructuredContentMatchRules();

    /**
     * Sets the targeting rules for this content item.
     *
     * @param structuredContentMatchRules
     */
    public void setStructuredContentMatchRules(@Nullable Map<String, StructuredContentRule> structuredContentMatchRules);

    /**
     * Returns the item (or cart) based rules associated with this content item.
     *
     * @return
     */
    @Nullable
    public Set<StructuredContentItemCriteria> getQualifyingItemCriteria();

    /**
     * Sets the item (e.g. cart) based rules associated with this content item.
     *
     * @param qualifyingItemCriteria
     */
    public void setQualifyingItemCriteria(@Nullable Set<StructuredContentItemCriteria> qualifyingItemCriteria);

}
