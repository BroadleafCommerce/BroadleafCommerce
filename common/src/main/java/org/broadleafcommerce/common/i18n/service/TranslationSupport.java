/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.i18n.service;

import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;

import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;

/**
 * {@link TranslationService} functionality, primarily in support of {@link TranslationOverrideStrategy} instances.
 *
 * @author Jeff Fischer
 */
public interface TranslationSupport {

    /**
     * Retrieve a cached translation from an individual property translation map retrieved from the cache.
     *
     * @param key
     * @param propertyTranslationMap
     * @param entityId
     * @return
     */
    StandardCacheItem lookupTranslationFromMap(String key, Map<String, Map<String, StandardCacheItem>> propertyTranslationMap, String entityId);

    /**
     * Retrieve the backing Ehcache Cache instance
     *
     * @return
     */
    Cache getCache();

    /**
     *
     * Returns a list of cacheKeys for a template site
     *
     * @param propertyName
     * @return
     */
    List<String> getCacheKeyListForTemplateSite(String propertyName);

    /**
     * Retrieve the threshold under which the full list of standard site translation overrides are cached. See
     * {@link TranslationServiceImpl#thresholdForFullCache} for more information on setting the value.
     *
     * @return
     */
    int getThresholdForFullCache();

    void setThresholdForFullCache(int thresholdForFullCache);

    /**
     * Retrieve the threshold under which the full list of template catalog translations are cached. See
     * {@link TranslationServiceImpl#templateThresholdForFullCache} for more information on setting the value.
     *
     * @return
     */
    int getTemplateThresholdForFullCache();

    void setTemplateThresholdForFullCache(int templateThresholdForFullCache);

    /**
     * Find the most appropriate translation in the map. The most specific qualified translation will win.
     *
     * @param specificPropertyKey
     * @param generalPropertyKey
     * @param propertyTranslationMap
     * @param entityId
     * @return
     */
    Translation findBestTemplateTranslation(String specificPropertyKey, String generalPropertyKey, Map<String, Map<String, Translation>> propertyTranslationMap, String entityId);

    /**
     * Build a cache key
     *
     * @param resultType
     * @param entityType
     * @return
     */
    String getCacheKey(ResultType resultType, TranslatedEntity entityType);

}
