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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.cache.StatisticsService;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

@Service("blTranslationService")
public class TranslationServiceImpl implements TranslationService, TranslationSupport {

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

    /**
     * The default is 1000. Use the 'translation.thresholdForFullCache' to change the value.
     */
    @Value("${translation.thresholdForFullCache:1000}")
    protected int thresholdForFullCache;

    /**
     * The default is 1000. This property also uses 'translation.thresholdForFullCache' for
     * backwards compatibility. If you wish to change this value, you'll need to extend
     * TranslationServiceImpl and return a custom value for {@link TranslationSupport#getTemplateThresholdForFullCache()}
     */
    @Value("${translation.thresholdForFullCache:1000}")
    protected int templateThresholdForFullCache;

    @Value("${returnBlankTranslationForNotDefaultLocale:false}")
    protected boolean returnBlankTranslationForNotDefaultLocale;

    @Resource(name = "blTranslationExceptionProperties")
    protected List<String> translationExceptionProperties = new ArrayList<String>();

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @Resource
    protected List<TranslationOverrideStrategy> strategies;
    
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
        
        if (TranslationBatchReadCache.hasCache()) {
            Translation translation = TranslationBatchReadCache.getFromCache(entityType, entityId, property, localeCountryCode);
            if (translation != null) {
                return translation.getTranslatedValue();
            } else {
                // There is no translation for this entity if it is not in the cache
                return null;
            }
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
                    ResultType.CATALOG_ONLY);
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
                if (ResultType.STANDARD == resultType) {
                    String key = getCacheKey(resultType, translation.getEntityType());
                    LOG.debug("Removing key [" + key + "] for STANDARD site");
                    getCache().remove(key);
                } else {
                    List<String> cacheKeysList =
                            getCacheKeyListForTemplateSite(translation.getEntityType().getFriendlyType());
                    for (String key: cacheKeysList) {
                        LOG.debug("Removing key [" + key + "] for TEMPLATE site");
                        getCache().remove(key);
                    }
                }
            }
        }
    }

    protected String getOverrideTranslatedValue(String property, TranslatedEntity entityType,
                                                String entityId, String localeCode, String localeCountryCode) {
        boolean specificTranslationDeleted = false;
        boolean generalTranslationDeleted = false;
        StandardCacheItem specificTranslation = null;
        StandardCacheItem generalTranslation = null;
        String specificPropertyKey = property + "_" + localeCountryCode;
        String generalPropertyKey = property + "_" + localeCode;
        String response = null;
        String cacheKey = getCacheKey(ResultType.STANDARD, entityType);
        LocalePair override = null;
        for (TranslationOverrideStrategy strategy : strategies) {
            override = strategy.getLocaleBasedOverride(property, entityType, entityId, localeCode, localeCountryCode, cacheKey);
            if(override != null) {
                specificTranslation = override.getSpecificItem();
                generalTranslation = override.getGeneralItem();
                break;
            }
        }
        if (override == null) {
            throw new IllegalStateException("Expected at least one TranslationOverrideStrategy to return a valid value");
        }

        if (specificTranslation != null) {
            if (ItemStatus.DELETED.equals(specificTranslation.getItemStatus())) {
                specificTranslationDeleted = true;
            } else {
                if (specificTranslation.getCacheItem() instanceof Translation) {
                    response = ((Translation) specificTranslation.getCacheItem()).getTranslatedValue();
                } else {
                    response = (String) specificTranslation.getCacheItem();
                }
                return replaceEmptyWithNullResponse(response);
            }
        }

        if (generalTranslation != null) {
            if (ItemStatus.DELETED.equals(generalTranslation.getItemStatus())) {
                generalTranslationDeleted = true;
                //If the specific translation is override deleted as well, we're done
                //If the general translation is override deleted and we've only got a general local code - we're done
                if (specificTranslationDeleted || !localeCountryCode.contains("_")) {
                    return null;
                }
            } else {
                if (generalTranslation.getCacheItem() instanceof Translation) {
                    response = ((Translation) generalTranslation.getCacheItem()).getTranslatedValue();
                } else {
                    response = (String) generalTranslation.getCacheItem();
                }
                //If the specific translation is override deleted as well, we're done
                //If the general translation is override and we've only got a general local code - we're done
                if (specificTranslationDeleted || !localeCountryCode.contains("_")) {
                    return replaceEmptyWithNullResponse(response);
                }
                //We have a valid general override - don't check for general template value
                generalTranslationDeleted = true;
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

        String templateResponse = getTemplateTranslatedValue(cacheKey, property, entityType, entityId, localeCode,
                    localeCountryCode, specificPropertyKey, generalPropertyKey);
        if (templateResponse != null) {
            response = templateResponse;
        }
        return response;
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
        StandardCacheItem translation = null;
        LocalePair override = null;
        for (TranslationOverrideStrategy strategy : strategies) {
            override = strategy.getLocaleBasedTemplateValue(cacheKey, property, entityType, entityId, localeCode, localeCountryCode, specificPropertyKey, generalPropertyKey);
            if(override != null) {
                translation = override.getSpecificItem();
                if (!strategy.validateTemplateProcessing(standardCacheKey, cacheKey)) {
                    return null;
                }
                break;
            }
        }
        if (override == null) {
            throw new IllegalStateException("Expected at least one TranslationOverrideStrategy to return a valid value");
        }
        return translation==null?null:replaceEmptyWithNullResponse(((Translation) translation.getCacheItem()).getTranslatedValue());
    }

    @Override
    public StandardCacheItem lookupTranslationFromMap(String key,
            Map<String, Map<String, StandardCacheItem>> propertyTranslationMap, String entityId) {

        StandardCacheItem cacheItem = null;
        if (propertyTranslationMap.containsKey(key)) {
            Map<String, StandardCacheItem> byEntity = propertyTranslationMap.get(key);
            cacheItem = byEntity.get(entityId);
        }
        return cacheItem;
    }

    @Override
    public Translation findBestTemplateTranslation(String specificPropertyKey, String generalPropertyKey, Map<String, Map<String, Translation>> propertyTranslationMap, String entityId) {
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

    @Override
    public String getCacheKey(ResultType resultType, TranslatedEntity entityType) {
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

    @Override
    public List<String> getCacheKeyListForTemplateSite(String propertyName) {
        List<String> cacheKeyList = new ArrayList<>();
        String cacheKey = StringUtils.join(new String[] {propertyName}, "|");
        if (extensionManager != null) {
            ExtensionResultHolder<List<String>> result = new ExtensionResultHolder<List<String>>();
            extensionManager.getProxy().getCacheKeyListForTemplateSite(cacheKey, result);
            if (result.getResult() != null) {
                cacheKeyList = result.getResult();
            }
        }
        return cacheKeyList;
    }

    @Override
    public int getThresholdForFullCache() {
        if (BroadleafRequestContext.getBroadleafRequestContext().isProductionSandBox()) {
            return thresholdForFullCache;
        } else {
            // don't cache when not in a SandBox
            return -1;
        }
    }

    @Override
    public void setThresholdForFullCache(int thresholdForFullCache) {
        this.thresholdForFullCache = thresholdForFullCache;
    }

    @Override
    public int getTemplateThresholdForFullCache() {
        if (BroadleafRequestContext.getBroadleafRequestContext().isProductionSandBox()) {
            return templateThresholdForFullCache;
        } else {
            // don't cache when not in a SandBox
            return -1;
        }
    }

    @Override
    public void setTemplateThresholdForFullCache(int templateThresholdForFullCache) {
        this.templateThresholdForFullCache = templateThresholdForFullCache;
    }

    @Override
    public String getDefaultTranslationValue(Object entity, String property, Locale locale,
            String requestedDefaultValue) {

        if (returnBlankTranslationForNotDefaultLocale && !localeMatchesDefaultLocale(locale) && !propertyInDefaultLocaleExceptionList(entity, property)) {
            return "";
        }

        return requestedDefaultValue;
    }

    @Override
    public List<Translation> findAllTranslationEntries(TranslatedEntity translatedEntity, ResultType standard, List<String> entityIds) {
        return dao.readAllTranslationEntries(translatedEntity, standard, entityIds);
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
