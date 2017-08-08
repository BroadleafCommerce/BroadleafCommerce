/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.i18n.service;

import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.cache.CacheStatType;
import org.broadleafcommerce.common.cache.StatisticsService;
import org.broadleafcommerce.common.extension.ItemStatus;
import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.broadleafcommerce.common.i18n.dao.TranslationDao;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Default {@link TranslationOverrideStrategy}. Should run last and will always return a result. Primarily supports multitenant
 * scenarios with the following characteristics:
  * <ul>
  *     <li>Small to medium template translation catalog</li>
  *     <li>Small to medium number of standard site overrides</li>
  *     <li>Small or large quantity of individual standard sites</li>
  * </ul>
 * This strategy tries to efficiently cache all standard site overries and/or all template catalog translations. This is the
 * highest efficiency settings, as all translations are cached on the app tier. However, if the override or template catalog
 * member count exceeds the threshold, the strategy falls back to on demand queries that are cached as they occur. See
 * {@link TranslationSupport#getThresholdForFullCache()} and {@link TranslationSupport#getTemplateThresholdForFullCache()}
 * for more information on these properties and how to set their values.
 *
 * @see SparseTranslationOverrideStrategy
 * @author Jeff Fischer
 */
@Component("blThresholdCacheTranslationOverrideStrategy")
@Lazy
public class ThresholdCacheTranslationOverrideStrategy implements TranslationOverrideStrategy {

    @Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;

    @Resource(name = "blTranslationDao")
    protected TranslationDao dao;

    @Autowired
    protected TranslationSupport translationSupport;

    @Override
    public LocalePair getLocaleBasedOverride(String property, TranslatedEntity entityType, String entityId,
                                             String localeCode, String localeCountryCode, String basicCacheKey) {
        String specificPropertyKey = property + "_" + localeCountryCode;
        String generalPropertyKey = property + "_" + localeCode;
        Element cacheResult = translationSupport.getCache().get(basicCacheKey);
        Element result;
        LocalePair response = new LocalePair();
        if (cacheResult == null) {
            statisticsService.addCacheStat(CacheStatType.TRANSLATION_CACHE_HIT_RATE.toString(), false);
            if (dao.countTranslationEntries(entityType, ResultType.STANDARD_CACHE) < translationSupport.getThresholdForFullCache()) {
                Map<String, Map<String, StandardCacheItem>> propertyTranslationMap = new HashMap<String, Map<String, StandardCacheItem>>();
                List<StandardCacheItem> convertedList = dao.readConvertedTranslationEntries(entityType, ResultType.STANDARD_CACHE);
                if (!CollectionUtils.isEmpty(convertedList)) {
                    for (StandardCacheItem standardCache : convertedList) {
                        Translation translation = (Translation) standardCache.getCacheItem();
                        String key = translation.getFieldName() + "_" + translation.getLocaleCode();
                        if (!propertyTranslationMap.containsKey(key)) {
                            propertyTranslationMap.put(key, new HashMap<String, StandardCacheItem>());
                        }
                        propertyTranslationMap.get(key).put(translation.getEntityId(), standardCache);
                    }
                }
                Element newElement = new Element(basicCacheKey, propertyTranslationMap);
                translationSupport.getCache().put(newElement);
                result = newElement;
            } else {
                //Translation is dual discriminated by site and catalog, which can make it impossible to find results under normal
                //circumstances because the two discriminators can cancel eachother out. We use the CATALOG_ONLY ResultType
                //to force the system to only honor the catalog discrimination during this call.
                Translation translation = dao.readTranslation(entityType, entityId, property, localeCode, localeCountryCode, ResultType.CATALOG_ONLY);
                buildSingleItemResponse(response, translation);
                return response;
            }
        } else {
            result = cacheResult;
            statisticsService.addCacheStat(CacheStatType.TRANSLATION_CACHE_HIT_RATE.toString(), true);
        }
        Map<String, Map<String, StandardCacheItem>> propertyTranslationMap = (Map<String, Map<String, StandardCacheItem>>) result.getObjectValue();
        // Check For a Specific Standard Site Match (language and country)
        StandardCacheItem specificTranslation = translationSupport.lookupTranslationFromMap(specificPropertyKey, propertyTranslationMap, entityId);
        // Check For a General Match (language and country)
        StandardCacheItem generalTranslation = translationSupport.lookupTranslationFromMap(generalPropertyKey, propertyTranslationMap, entityId);
        response.setSpecificItem(specificTranslation);
        response.setGeneralItem(generalTranslation);

        return response;
    }

    @Override
    public LocalePair getLocaleBasedTemplateValue(String templateCacheKey, String property, TranslatedEntity entityType,
                                                  String entityId, String localeCode, String localeCountryCode, String specificPropertyKey, String generalPropertyKey) {
        Element cacheResult = translationSupport.getCache().get(templateCacheKey);
        LocalePair response = new LocalePair();
        if (cacheResult == null) {
            statisticsService.addCacheStat(CacheStatType.TRANSLATION_CACHE_HIT_RATE.toString(), false);
            if (dao.countTranslationEntries(entityType, ResultType.TEMPLATE_CACHE) < translationSupport.getTemplateThresholdForFullCache()) {
                Map<String, Map<String, Translation>> propertyTranslationMap = new HashMap<String, Map<String, Translation>>();
                List<Translation> translationList = dao.readAllTranslationEntries(entityType, ResultType.TEMPLATE_CACHE);
                if (!CollectionUtils.isEmpty(translationList)) {
                    for (Translation translation : translationList) {
                        String key = translation.getFieldName() + "_" + translation.getLocaleCode();
                        if (!propertyTranslationMap.containsKey(key)) {
                            propertyTranslationMap.put(key, new HashMap<String, Translation>());
                        }
                        propertyTranslationMap.get(key).put(translation.getEntityId(), translation);
                    }
                }
                translationSupport.getCache().put(new Element(templateCacheKey, propertyTranslationMap));
                Translation translation = translationSupport.findBestTemplateTranslation(specificPropertyKey, generalPropertyKey, propertyTranslationMap, entityId);
                if (translation != null) {
                    buildSingleItemResponse(response, translation);
                }
            } else {
                Translation translation = dao.readTranslation(entityType, entityId, property, localeCode, localeCountryCode, ResultType.TEMPLATE);
                if (translation != null) {
                    buildSingleItemResponse(response, translation);
                }
            }
        } else {
            statisticsService.addCacheStat(CacheStatType.TRANSLATION_CACHE_HIT_RATE.toString(), true);
            Map<String, Map<String, Translation>> propertyTranslationMap = (Map<String, Map<String, Translation>>) cacheResult.getObjectValue();
            Translation bestTranslation = translationSupport.findBestTemplateTranslation(specificPropertyKey, generalPropertyKey, propertyTranslationMap, entityId);
            if (bestTranslation != null) {
                buildSingleItemResponse(response, bestTranslation);
            }
        }
        return response;
    }

    @Override
    public boolean validateTemplateProcessing(String standardCacheKey, String templateCacheKey) {
        return !standardCacheKey.equals(templateCacheKey);
    }

    @Override
    public int getOrder() {
            return 0;
        }

    protected void buildSingleItemResponse(LocalePair response, Translation translation) {
        StandardCacheItem cacheItem = new StandardCacheItem();
        cacheItem.setItemStatus(ItemStatus.NORMAL);
        cacheItem.setCacheItem(translation==null?"":translation);
        response.setSpecificItem(cacheItem);
    }

}
