/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.i18n.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.cache.CacheStatType;
import org.broadleafcommerce.common.cache.StatisticsService;
import org.broadleafcommerce.common.dao.GenericEntityDao;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ItemStatus;
import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.broadleafcommerce.common.i18n.dao.TranslationDao;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.i18n.domain.TranslationImpl;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.locale.util.LocaleUtil;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;


@Service("blTranslationService")
public class TranslationServiceImpl implements TranslationService {

    protected static final Log LOG = LogFactory.getLog(TranslationServiceImpl.class);
    private static final Translation DELETED_TRANSLATION = new TranslationImpl();
    
    @Resource(name = "blTranslationDao")
    protected TranslationDao dao;

    @Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;
    
    protected Cache cache;

    @Resource(name="blTranslationServiceExtensionManager")
    protected TranslationServiceExtensionManager extensionManager;

    @Value("${translation.thresholdForFullCache:1000}")
    protected int thresholdForFullCache;

    @Value("${returnBlankTranslationForNotDefaultLocale:false}")
    protected boolean returnBlankTranslationForNotDefaultLocale;

    @Resource(name = "blTranslationExceptionProperties")
    protected List<String> translationExceptionProperties = new ArrayList<String>();

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @Resource(name="blGenericEntityDao")
    protected GenericEntityDao genericEntityDao;
    
    @Override
    @Transactional("blTransactionManager")
    public Translation save(Translation translation) {
        return dao.save(translation);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public Translation save(String entityType, String entityId, String fieldName, String localeCode, 
            String translatedValue) {
        TranslatedEntity te = getEntityType(entityType);
        
        Translation translation = getTranslation(te, entityId, fieldName, localeCode);
        
        if (translation == null) {
            translation = dao.create();
            translation.setEntityType(te);
            translation.setEntityId(entityId);
            translation.setFieldName(fieldName);
            translation.setLocaleCode(localeCode);
        }
        
        translation.setTranslatedValue(translatedValue);
        return save(translation);
    }

    @Override
    public Translation findTranslationById(Long id) {
        return dao.readTranslationById(id);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public Translation update(Long translationId, String localeCode, String translatedValue) {
        Translation t = dao.readTranslationById(translationId);
        
        // Check to see if there is another translation that matches this updated one. We'll remove it if it exists
        Translation t2 = dao.readTranslation(t.getEntityType(), t.getEntityId(), t.getFieldName(), localeCode);
        if (t2 != null && t != t2) {
            dao.delete(t2);
        }
        
        t.setLocaleCode(localeCode);
        t.setTranslatedValue(translatedValue);
        return save(t);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public void deleteTranslationById(Long translationId) {
        Translation t = dao.readTranslationById(translationId);
        dao.delete(t);
    }
    
    @Override
    public Translation getTranslation(TranslatedEntity entity, String entityId, String fieldName, String localeCode) {
        return dao.readTranslation(entity, entityId, fieldName, localeCode);
    }
    
    @Override
    public List<Translation> getTranslations(String ceilingEntityClassname, String entityId, String property) {
        TranslatedEntity entityType = getEntityType(ceilingEntityClassname);
        return dao.readTranslations(entityType, entityId, property);
    }

    @Override
    public Cache getCache() {
        if (cache == null) {
            cache = CacheManager.getInstance().getCache("blTranslationElements");
        }
        return cache;
    }

    @Override
    public String getTranslatedValue(Object entity, String property, Locale locale) {
        TranslatedEntity entityType = getEntityType(entity);
        String entityId = dao.getEntityId(entityType, entity);

        String localeCode = locale.getLanguage();
        String localeCountryCode = localeCode;
        if (StringUtils.isNotBlank(locale.getCountry())) {
            localeCountryCode += "_" + locale.getCountry();
        }
        boolean isValidForCache = false;
        if (extensionManager != null) {
            ExtensionResultHolder<Boolean> response = new ExtensionResultHolder<Boolean>();
            response.setResult(false);
            extensionManager.getProxy().isValidState(response);
            isValidForCache = response.getResult();
        }
        if (!BroadleafRequestContext.getBroadleafRequestContext().isProductionSandBox() || !isValidForCache) {
            Translation translation = dao.readTranslation(entityType, entityId, property, localeCode, localeCountryCode,
                    ResultType.IGNORE);
            if (translation != null) {
                return translation.getTranslatedValue();
            } else {
                return null;
            }
        }

        return getOverrideTranslatedValue(property, entityType, entityId, localeCode, localeCountryCode);
    }

    @Override
    public void removeTranslationFromCache(Translation translation) {
        if (BroadleafRequestContext.getBroadleafRequestContext().isProductionSandBox()) {
            ResultType resultType = ResultType.STANDARD;
            if (extensionManager != null) {
                ExtensionResultHolder<ResultType> response = new ExtensionResultHolder<ResultType>();
                extensionManager.getProxy().getResultType(translation, response);
                resultType = response.getResult();
            }
            String key = getCacheKey(resultType, translation.getEntityType());
            getCache().remove(key);
        }
    }

    protected String getOverrideTranslatedValue(String property, TranslatedEntity entityType,
                                                String entityId, String localeCode, String localeCountryCode) {
        String specificPropertyKey = property + "_" + localeCountryCode;
        String generalPropertyKey = property + "_" + localeCode;
        String cacheKey = getCacheKey(ResultType.STANDARD, entityType);
        Element cacheResult = getCache().get(cacheKey);
        Element result = null;
        String response = null;
        if (cacheResult == null) {
            statisticsService.addCacheStat(CacheStatType.TRANSLATION_CACHE_HIT_RATE.toString(), false);
            if (dao.countTranslationEntries(entityType, ResultType.STANDARD_CACHE) < getThresholdForFullCache()) {
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
                Element newElement = new Element(cacheKey, propertyTranslationMap);
                getCache().put(newElement);
                result = newElement;
            } else {
                Translation translation = dao.readTranslation(entityType, entityId, property, localeCode, localeCountryCode, ResultType.IGNORE);
                if (translation != null) {
                    response = translation.getTranslatedValue();
                }
            }
        } else {
            result = cacheResult;
            statisticsService.addCacheStat(CacheStatType.TRANSLATION_CACHE_HIT_RATE.toString(), true);
        }

        if (result != null) {
            Map<String, Map<String, StandardCacheItem>> propertyTranslationMap =
                    (Map<String, Map<String, StandardCacheItem>>) result.getObjectValue();

            boolean specificTranslationDeleted = false;
            boolean generalTranslationDeleted = false;

            // Check For a Specific Standard Site Match (language and country)
            StandardCacheItem specificTranslation = lookupTranslationFromMap(specificPropertyKey, propertyTranslationMap,
                    entityId);            
            if (specificTranslation != null) {
                if (ItemStatus.DELETED.equals(specificTranslation.getItemStatus())) {
                    specificTranslationDeleted = true;
                } else {
                    response = ((Translation) specificTranslation.getCacheItem()).getTranslatedValue();
                    return replaceEmptyWithNullResponse(response);
                }
            }
                
            // Check For a General Match (language and country)
            StandardCacheItem generalTranslation = lookupTranslationFromMap(generalPropertyKey, propertyTranslationMap,
                    entityId);
            if (generalTranslation != null) {
                if (ItemStatus.DELETED.equals(generalTranslation.getItemStatus())) {
                    generalTranslationDeleted = true;
                    if (specificTranslationDeleted) {
                        return null;
                    }
                }
            
                if (specificTranslationDeleted) {
                    response = ((Translation) generalTranslation.getCacheItem()).getTranslatedValue();
                    return replaceEmptyWithNullResponse(response);
                }
            }
            
            // Check for a Template Match
            if (specificTranslationDeleted) {
                // only check general properties since we explicitly deleted specific properties at standard (site) level            
                specificPropertyKey = generalPropertyKey;
            } else if (generalTranslationDeleted) {
                // only check specific properties since we explicitly deleted general properties at standard (site) level            
                generalPropertyKey = specificPropertyKey;                
            }
            
            response = getTemplateTranslatedValue(cacheKey, property, entityType, entityId, localeCode,
                        localeCountryCode, specificPropertyKey, generalPropertyKey);            
        }

        return replaceEmptyWithNullResponse(response);
    }

    protected String replaceEmptyWithNullResponse(String response) {
        if (!StringUtils.isEmpty(response)) {
            return response;
        }
        return null;
    }

    protected String getTemplateTranslatedValue(String standardCacheKey, String property, TranslatedEntity entityType,
                        String entityId, String localeCode, String localeCountryCode, String specificPropertyKey, String generalPropertyKey) {
        String cacheKey = getCacheKey(ResultType.TEMPLATE, entityType);
        if (standardCacheKey.equals(cacheKey)) {
            return null;
        }
        Element cacheResult = getCache().get(cacheKey);
        if (cacheResult == null) {
            statisticsService.addCacheStat(CacheStatType.TRANSLATION_CACHE_HIT_RATE.toString(), false);
            if (dao.countTranslationEntries(entityType, ResultType.TEMPLATE_CACHE) < getThresholdForFullCache()) {
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
                getCache().put(new Element(cacheKey, propertyTranslationMap));
                Translation translation = findBestTemplateTranslation(specificPropertyKey, generalPropertyKey, propertyTranslationMap, entityId);
                if (translation != null) {
                    return translation.getTranslatedValue();
                } else {
                    return null;
                }
            } else {
                Translation translation = dao.readTranslation(entityType, entityId, property, localeCode, localeCountryCode, ResultType.TEMPLATE);
                if (translation != null) {
                    return translation.getTranslatedValue();
                } else {
                    return null;
                }
            }
        } else {
            statisticsService.addCacheStat(CacheStatType.TRANSLATION_CACHE_HIT_RATE.toString(), true);
            Map<String, Map<String, Translation>> propertyTranslationMap = (Map<String, Map<String, Translation>>) cacheResult.getObjectValue();
            Translation bestTranslation = findBestTemplateTranslation(specificPropertyKey, generalPropertyKey, propertyTranslationMap, entityId);
            if (bestTranslation != null) {
                return bestTranslation.getTranslatedValue();
            } else {
                return null;
            }
        }
    }

    protected StandardCacheItem lookupTranslationFromMap(String key,
            Map<String, Map<String, StandardCacheItem>> propertyTranslationMap, String entityId) {

        StandardCacheItem cacheItem = null;
        if (propertyTranslationMap.containsKey(key)) {
            Map<String, StandardCacheItem> byEntity = propertyTranslationMap.get(key);
            cacheItem = byEntity.get(entityId);
        }
        return cacheItem;
    }

    protected Translation findBestTemplateTranslation(String specificPropertyKey, String generalPropertyKey, Map<String, Map<String, Translation>> propertyTranslationMap, String entityId) {
        Translation translation = null;
        if (propertyTranslationMap.containsKey(specificPropertyKey)) {
            Map<String, Translation> byEntity = propertyTranslationMap.get(specificPropertyKey);
            translation = byEntity.get(entityId);
        }
        if (translation == null && propertyTranslationMap.containsKey(generalPropertyKey)) {
            Map<String, Translation> byEntity = propertyTranslationMap.get(generalPropertyKey);
            translation = byEntity.get(entityId);
        }
        return translation;
    }
    
    protected TranslatedEntity getEntityType(Class<?> entityClass) {
        for (Entry<String, TranslatedEntity> entry : TranslatedEntity.getTypes().entrySet()) {
            try {
                Class<?> clazz = Class.forName(entry.getKey());
                if (clazz.isAssignableFrom(entityClass)) {
                    return entry.getValue();
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("TranslatedEntity type was not set to a known class", e);
            }
        }
        throw new IllegalArgumentException(entityClass.getName() + " is not a known translatable class");
    }
    
    protected TranslatedEntity getEntityType(Object entity) {
        return getEntityType(entity.getClass());
    }
    
    protected TranslatedEntity getEntityType(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return getEntityType(clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(className + " is not a known translatable class");
        }
    }

    protected String getCacheKey(ResultType resultType, TranslatedEntity entityType) {
        String cacheKey = StringUtils.join(new String[] { entityType.getFriendlyType()}, "|");
        if (extensionManager != null) {
            ExtensionResultHolder<String> result = new ExtensionResultHolder<String>();
            extensionManager.getProxy().getCacheKey(cacheKey, resultType, result);
            if (result.getResult() != null) {
                cacheKey = result.getResult();
            }
        }
        return cacheKey;
    }

    protected int getThresholdForFullCache() {
        if (BroadleafRequestContext.getBroadleafRequestContext().isProductionSandBox()) {
            return thresholdForFullCache;
        } else {
            // don't cache when not in a SandBox
            return -1;
        }
    }

    public String getDefaultTranslationValue(Object entity, String property, Locale locale,
            String requestedDefaultValue) {

        if (returnBlankTranslationForNotDefaultLocale) {
            if (localeMatchesDefaultLocale(locale)) {
            } else if (!propertyInDefaultLocaleExceptionList(entity, property)) {
                return "";
            }
        }

        return requestedDefaultValue;
    }

    /**
     * Returns true if the passed in entity / property combination is in the defaultLocaleExceptionList
     * 
     * The default implementation checks the "translationExceptionProperties" list to see if the
     * property matches one of the regularExpressions in that list.
     * 
     * Implementors are expected to override this method for implementation specific needs. 
     * 
     * @param entity
     * @param property
     * @return
     */
    protected boolean propertyInDefaultLocaleExceptionList(Object entity, String property) {
        TranslatedEntity entityType = getEntityType(entity);
        if (entityType != null && entityType.getFriendlyType() != null) {
            for (String exceptionProperty : translationExceptionProperties) {
                if (property.matches(exceptionProperty)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the passed in locale's language matches the Broadleaf default locale.
     * @param locale
     * @return
     */
    protected boolean localeMatchesDefaultLocale(Locale locale) {
        String defaultLanguage = LocaleUtil.findLanguageCode(localeService.findDefaultLocale());

        if (defaultLanguage != null && locale != null) {
            return defaultLanguage.equals(locale.getLanguage());
        }
        return false;
    }

}
