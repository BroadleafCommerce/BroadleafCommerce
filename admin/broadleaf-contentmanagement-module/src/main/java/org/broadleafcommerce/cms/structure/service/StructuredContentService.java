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
package org.broadleafcommerce.cms.structure.service;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.structure.dto.StructuredContentDTO;
import org.hibernate.Criteria;

import java.util.List;
import java.util.Map;

import javax.cache.Cache;

/**
 * Provides services to manage <code>StructuredContent</code> items.
 *
 * @author bpolster
 */
public interface StructuredContentService {


    /**
     * Returns the StructuredContent item associated with the passed in id.
     *
     * @param contentId - The id of the content item.
     * @return The associated structured content item.
     */
    StructuredContent findStructuredContentById(Long contentId);


    /**
     * Returns the <code>StructuredContentType</code> associated with the passed in id.
     *
     * @param id - The id of the content type.
     * @return The associated <code>StructuredContentType</code>.
     */
    StructuredContentType findStructuredContentTypeById(Long id);


    /**
     * Returns the <code>StructuredContentType</code> associated with the passed in
     * String value.
     *
     * @param name - The name of the content type.
     * @return The associated <code>StructuredContentType</code>.
     */
    StructuredContentType findStructuredContentTypeByName(String name);

    /**
     *
     * @return a list of all <code>StructuredContentType</code>s
     */
    List<StructuredContentType> retrieveAllStructuredContentTypes();

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
    List<StructuredContent> findContentItems(Criteria criteria);
    
    /**
     * Finds all content items regardless of the {@link Sandbox} they are a member of
     * @return
     */
    List<StructuredContent> findAllContentItems();
    
    /**
     * Follows the same rules as {@link #findContentItems(org.broadleafcommerce.common.sandbox.domain.SandBox, org.hibernate.Criteria) findContentItems}.
     *
     * @return the count of items in this sandbox that match the passed in Criteria
     */
    Long countContentItems(Criteria c);

    /**
     * Saves the given <b>type</b> and returns the merged instance
     */
    StructuredContentType saveStructuredContentType(StructuredContentType type);

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
    List<StructuredContentDTO> lookupStructuredContentItemsByType(StructuredContentType contentType, Locale locale, Integer count, Map<String,Object> ruleDTOs, boolean secure);

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
    List<StructuredContentDTO> lookupStructuredContentItemsByName(String contentName, Locale locale, Integer count, Map<String,Object> ruleDTOs, boolean secure);



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
    List<StructuredContentDTO> lookupStructuredContentItemsByName(StructuredContentType contentType, String contentName, Locale locale, Integer count, Map<String,Object> ruleDTOs, boolean secure);

    Locale findLanguageOnlyLocale(Locale locale);

    List<StructuredContentDTO> buildStructuredContentDTOList(List<StructuredContent> structuredContentList, boolean secure);

    List<StructuredContentDTO> evaluateAndPriortizeContent(List<StructuredContentDTO> structuredContentList, int count, Map<String, Object> ruleDTOs);

    Cache getStructuredContentCache();

    /**
     * Converts a StructuredContent into a StructuredContentDTO.   If the item contains fields with
     * broadleaf cms urls, the urls are converted to utilize the domain.
     * 
     * The StructuredContentDTO is built via the {@link EntityConfiguration}. To override the actual type that is returned,
     * include an override in an applicationContext like any other entity override.
     * 
     * @param sc
     * @param secure
     * @return
     */
    StructuredContentDTO buildStructuredContentDTO(StructuredContent sc, boolean secure);


    public void addStructuredContentListToCache(String key, List<StructuredContentDTO> scDTOList);

    /**
     * Builds the cache key for DTOLists based on the SC Type.
     *
     * @param currentSandbox
     * @param site
     * @param locale
     * @param contentType
     * @return cache key for DTOList
     *
     * @deprecated use {@link #buildTypeKeyWithSecure(SandBox, Long, Locale, String, Boolean)}
     */
    @Deprecated
    public String buildTypeKey(SandBox currentSandbox, Long site, Locale locale, String contentType);

    /**
     * Builds the cache key for DTOLists based on the SC Type.
     *
     * @param currentSandbox
     * @param site
     * @param locale
     * @param contentType
     * @param secure
     * @return cache key for DTOList
     */
    public String buildTypeKeyWithSecure(SandBox currentSandbox, Long site, Locale locale, String contentType, Boolean secure);


    public List<StructuredContentDTO> getStructuredContentListFromCache(String key);

    /**
     * Call to evict an item from the cache.
     *
     * @param sandBox
     * @param sc
     */
    void removeStructuredContentFromCache(SandBox sandBox, StructuredContent sc);

    /**
     * Call to evict both secure and non-secure SC items matching
     * the passed in keys.
     *
     * @param nameKey
     * @param typeKey
     * 
     * @deprecated use {@link #removeItemFromCacheByKey(String)}
     */
    @Deprecated
    public void removeItemFromCache(String nameKey, String typeKey);

    /**
     * Call to evict both secure and non-secure SC items matching
     * the passed in key.
     *
     * @param key
     * @return
     */
    public boolean removeItemFromCacheByKey(String key);

    /**
     * Converts a list of StructuredContent objects into their corresponding {@link StructuredContentDTO}s. This method 
     * will utilize a cache in production mode, and it will additionally hydrate the returned {@link StructuredContentDTO}
     * objects via the {@link #hydrateForeignLookups(List)} method.
     * 
     * @param scs
     * @return the list of {@link StructuredContentDTO}s
     */
    public List<StructuredContentDTO> convertToDtos(List<StructuredContent> scs, boolean isSecure);


    /**
     * First attempts to retrieve {@link StructuredContentDTO} from cache before making calls to database
     * @param contentName (Name of ContentItem
     * @param locale
     * @param isSecure
     * @return
     */
    List<StructuredContentDTO> getStructuredContentItemsByContentName(String contentName, Locale locale,  boolean isSecure);

}
