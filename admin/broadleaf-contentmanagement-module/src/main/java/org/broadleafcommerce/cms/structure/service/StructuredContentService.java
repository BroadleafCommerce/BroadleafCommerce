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

package org.broadleafcommerce.cms.structure.service;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentField;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.cms.structure.dto.StructuredContentDTO;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemListener;
import org.hibernate.Criteria;

import java.util.List;
import java.util.Map;

/**
 * Provides services to manage <code>StructuredContent</code> items.
 *
 * @author bpolster
 */
public interface StructuredContentService extends SandBoxItemListener {


    /**
     * Returns the StructuredContent item associated with the passed in id.
     *
     * @param contentId - The id of the content item.
     * @return The associated structured content item.
     */
    public StructuredContent findStructuredContentById(Long contentId);


    /**
     * Returns the <code>StructuredContentType</code> associated with the passed in id.
     *
     * @param id - The id of the content type.
     * @return The associated <code>StructuredContentType</code>.
     */
    public StructuredContentType findStructuredContentTypeById(Long id);


    /**
     * Returns the <code>StructuredContentType</code> associated with the passed in
     * String value.
     *
     * @param name - The name of the content type.
     * @return The associated <code>StructuredContentType</code>.
     */
    public StructuredContentType findStructuredContentTypeByName(String name);

    /**
     *
     * @return a list of all <code>StructuredContentType</code>s
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
     * This method is intended to be called solely from the CMS admin.    Similar methods
     * exist that are intended for other clients (e.g. lookupStructuredContentItemsBy....
     * <br>
     * Returns content items for the passed in sandbox that match the passed in criteria.
     * The criteria acts as a where clause to be used in the search for content items.
     * Implementations should automatically add criteria such that no archived items
     * are returned from this method.
     * <br>
     * The SandBox parameter impacts the results as follows.  If a <code>SandBoxType</code> of
     * production is passed in, only those items in that SandBox are returned.
     * <br>
     * If a non-production SandBox is passed in, then the method will return the items associatd
     * with the related production SandBox and then merge in the results of the passed in SandBox.
     *
     * @param sandbox - the sandbox to find structured content items (null indicates items that are in production for
     *                  sites that are single tenant.
     * @param criteria - the criteria used to search for content
     * @return
     */
    public List<StructuredContent> findContentItems(SandBox sandbox, Criteria criteria);
    
    /**
     * Finds all content items regardless of the {@link Sandbox} they are a member of
     * @return
     */
    public List<StructuredContent> findAllContentItems();
    
    /**
     * Follows the same rules as {@link #findContentItems(org.broadleafcommerce.common.sandbox.domain.SandBox, org.hibernate.Criteria) findContentItems}.
     *
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
     * Saves the given <b>type</b> and returns the merged instance
     */
    public StructuredContentType saveStructuredContentType(StructuredContentType type);

    /**
     * If deleting and item where content.originalItemId != null
     * then the item is deleted from the database.
     *
     * If the originalItemId is null, then this method marks
     * the items as deleted within the passed in sandbox.
     *
     * @param content
     * @param destinationSandbox
     * @return
     */
    public void deleteStructuredContent(StructuredContent content, SandBox destinationSandbox);

    /**
     * This method returns content
     * <br>
     * Returns active content items for the passed in sandbox that match the passed in type.
     * <br>
     * The SandBox parameter impacts the results as follows.  If a <code>SandBoxType</code> of
     * production is passed in, only those items in that SandBox are returned.
     * <br>
     * If a non-production SandBox is passed in, then the method will return the items associatd
     * with the related production SandBox and then merge in the results of the passed in SandBox.
     * <br>
     * The secure item is used in cases where the structured content item contains an image path that needs
     * to be rewritten to use https.
     *
     * @param sandBox - the sandbox to find structured content items (null indicates items that are in production for
     *                  sites that are single tenant.
     * @param contentType - the type of content to return
     * @param count - the max number of content items to return
     * @param ruleDTOs - a Map of objects that will be used in MVEL processing.
     * @param secure - set to true if the request is being served over https
     * @return - The matching items
     * @see org.broadleafcommerce.cms.web.structure.DisplayContentTag
     */
    public List<StructuredContentDTO> lookupStructuredContentItemsByType(SandBox sandBox, StructuredContentType contentType, Locale locale, Integer count, Map<String,Object> ruleDTOs, boolean secure);

    /**
     * This method returns content by name only.
     * <br>
     * Returns active content items for the passed in sandbox that match the passed in type.
     * <br>
     * The SandBox parameter impacts the results as follows.  If a <code>SandBoxType</code> of
     * production is passed in, only those items in that SandBox are returned.
     * <br>
     * If a non-production SandBox is passed in, then the method will return the items associatd
     * with the related production SandBox and then merge in the results of the passed in SandBox.
     *
     * @param sandBox - the sandbox to find structured content items (null indicates items that are in production for
     *                  sites that are single tenant.
     * @param contentName - the name of content to return
     * @param count - the max number of content items to return
     * @param ruleDTOs - a Map of objects that will be used in MVEL processing.
     * @param secure - set to true if the request is being served over https
     * @return - The matching items
     * @see org.broadleafcommerce.cms.web.structure.DisplayContentTag
     */
    public List<StructuredContentDTO> lookupStructuredContentItemsByName(SandBox sandBox, String contentName, Locale locale, Integer count, Map<String,Object> ruleDTOs, boolean secure);



    /**
     * This method returns content by name and type.
     * <br>
     * Returns active content items for the passed in sandbox that match the passed in type.
     * <br>
     * The SandBox parameter impacts the results as follows.  If a <code>SandBoxType</code> of
     * production is passed in, only those items in that SandBox are returned.
     * <br>
     * If a non-production SandBox is passed in, then the method will return the items associatd
     * with the related production SandBox and then merge in the results of the passed in SandBox.
     *
     * @param sandBox - the sandbox to find structured content items (null indicates items that are in production for
     *                  sites that are single tenant.
     * @param contentType - the type of content to return
     * @param contentName - the name of content to return
     * @param count - the max number of content items to return
     * @param ruleDTOs - a Map of objects that will be used in MVEL processing.
     * @param secure - set to true if the request is being served over https
     * @return - The matching items
     * @see org.broadleafcommerce.cms.web.structure.DisplayContentTag
     */
    public List<StructuredContentDTO> lookupStructuredContentItemsByName(SandBox sandBox, StructuredContentType contentType, String contentName, Locale locale, Integer count, Map<String,Object> ruleDTOs, boolean secure);


    /**
     * Removes the items from cache that match the passed in name and page keys.
     * @param nameKey - key for a specific content item
     * @param typeKey - key for a type of content item
     */
    public void removeItemFromCache(String nameKey, String typeKey);

    public boolean isAutomaticallyApproveAndPromoteStructuredContent();

    public void setAutomaticallyApproveAndPromoteStructuredContent(boolean automaticallyApproveAndPromoteStructuredContent);

    public Locale findLanguageOnlyLocale(Locale locale);

    public String buildTypeKey(SandBox currentSandbox, Locale locale, String contentType);

    public SandBox getProductionSandBox(SandBox currentSandBox);

    public boolean isProductionSandBox(SandBox dest);

    public void addStructuredContentListToCache(String key, List<StructuredContentDTO> scDTOList);

    public List<StructuredContentDTO> getStructuredContentListFromCache(String key);

    public List<StructuredContentDTO> buildStructuredContentDTOList(List<StructuredContent> structuredContentList, boolean secure);

    public List<StructuredContentDTO> mergeContent(List<StructuredContentDTO> productionList, List<StructuredContent> sandboxList, boolean secure);

    public List<StructuredContentDTO> evaluateAndPriortizeContent(List<StructuredContentDTO> structuredContentList, int count, Map<String, Object> ruleDTOs);
}
