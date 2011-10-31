/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.cms.structure.service;

import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentField;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemListener;
import org.hibernate.Criteria;

import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StructuredContentService extends SandBoxItemListener {


    /**
     * Returns the StructuredContent item associated with the passed in id.
     *
     * @param contentId - The id of the content item.
     * @return The associated structured content item.
     */
    public StructuredContent findStructuredContentById(Long contentId);

    public StructuredContentType findStructuredContentTypeById(Long id);

    public StructuredContentType findStructuredContentTypeByName(String name);

    /**
     * Returns the list of structured content types.
     */
    public List<StructuredContentType> retrieveAllStructuredContentTypes();

    /**
     * Returns the fields associated with the passed in contentId.
     * This is preferred over the direct access from the ContentItem so that the
     * two items can be cached distinctly
     *
     * @param contentId - The id of the content.
     * @return Map of fields for this content id
     */
    public Map<String,StructuredContentField> findFieldsByContentId(Long contentId);

    /**
     * Retuns content items for the passed in sandbox that match the passed in criteria.
     *
     * Merges the sandbox content with the production content.
     * @param sandbox - the sandbox to find structured content items (null indicates items that are in production for
     *                  sites that are single tenant.
     * @return
     */
    public List<StructuredContent> findContentItems(SandBox sandbox, Criteria c);

    /**
     * @return the count of items in this sandbox that match the passed in Criteria
     */
    public Long countContentItems(SandBox sandBox, Criteria c);

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     *
     * Adds the passed in contentItem to the DB.
     *
     * Creates a sandbox/site if one doesn't already exist.
     */
    public StructuredContent addStructuredContent(StructuredContent content, SandBox destinationSandbox);

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     *
     * Updates the structuredContent according to the following rules:
     *
     * 1.  If sandbox has changed from null to a value
     * This means that the user is editing an item in production and
     * the edit is taking place in a sandbox.
     *
     * Clone the item and add it to the new sandbox and set the cloned
     * item's originalItemId to the id of the item being updated.
     *
     * 2.  If the sandbox has changed from one value to another
     * This means that the user is moving the item from one sandbox
     * to another.
     *
     * Update the siteId for the item to the one associated with the
     * new sandbox
     *
     * 3.  If the sandbox has changed from a value to null
     * This means that the item is moving from the sandbox to production.
     *
     * If the item has an originalItemId, then update that item by
     * setting it's archived flag to true.
     *
     * Then, update the siteId of the item being updated to be the
     * siteId of the original item.
     *
     * 4.  If the sandbox is the same then just update the item.
     */
    public StructuredContent updateStructuredContent(StructuredContent content, SandBox sandbox);


    /**
     * If deleting and item where content.originalItemId != null
     * then the item is deleted from the database.
     *
     * If the originalItemId is null, then this method marks
     * the items as deleted within the passed in sandbox.
     *
     * @param page
     * @param destinationSandbox
     * @return
     */
    public void deleteStructuredContent(StructuredContent content, SandBox destinationSandbox);

    public List<StructuredContent> lookupStructuredContentItemsByType(SandBox sandBox, StructuredContentType contentType, Locale locale, Integer count, Map<String,Object> ruleDTOs);

    public List<StructuredContent> lookupStructuredContentItemsByName(SandBox sandBox, StructuredContentType contentType, String contentName, Locale locale, Integer count, Map<String,Object> ruleDTOs);

}
