/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.common.i18n.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.i18n.dao.TranslationDao;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;


@Service("blTranslationService")
public class TranslationServiceImpl implements TranslationService {
    protected static final Log LOG = LogFactory.getLog(TranslationServiceImpl.class);
    
    @Resource(name = "blTranslationDao")
    protected TranslationDao dao;
    
    protected Cache cache;
    
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
    public String getTranslatedValue(Object entity, String property, Locale locale) {
        // Attempt to get a translated value for this property to override the default value
        TranslatedEntity entityType = getEntityType(entity);
        String entityId = getEntityId(entity, entityType);
        
        String localeCode = locale.getLanguage();
        String localeCountryCode = localeCode;
        if (StringUtils.isNotBlank(locale.getCountry())) {
            localeCountryCode += "_" + locale.getCountry();
        }

        Translation translation;
        
        // First, we'll try to look up a country language combo (en_GB), utilizing the cache
        String countryCacheKey = getCacheKey(entityType, entityId, property, localeCountryCode);
        Element countryValue = getCache().get(countryCacheKey);
        if (countryValue != null) {
            translation = (Translation) countryValue.getObjectValue();
        } else {
            translation = getTranslation(entityType, entityId, property, localeCountryCode);
            if (translation != null) {
                getCache().put(new Element(countryCacheKey, translation));
            }
        }
        
        // If we don't find one, let's try just the language (en), again utilizing the cache
        if (translation == null) {
            String nonCountryCacheKey = getCacheKey(entityType, entityId, property, localeCode);
            Element nonCountryValue = getCache().get(nonCountryCacheKey);
            if (nonCountryValue != null) {
                translation = (Translation) nonCountryValue.getObjectValue();
            } else {
                translation = getTranslation(entityType, entityId, property, localeCode);
                if (translation != null) {
                    getCache().put(new Element(nonCountryCacheKey, translation));
                }
            }
        }
        
        // If we have a match on a translation, use that instead of what we found on the entity.
        if (translation != null && StringUtils.isNotBlank(translation.getTranslatedValue())) {
            return translation.getTranslatedValue();
        }
        
        return null;
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
    
    protected String getEntityId(Object entity, TranslatedEntity entityType) {
        Map<String, Object> idMetadata = dao.getIdPropertyMetadata(entityType);
        String idProperty = (String) idMetadata.get("name");
        Type idType = (Type) idMetadata.get("type");
        
        if (!(idType instanceof LongType || idType instanceof StringType)) {
            throw new UnsupportedOperationException("Only ID types of String and Long are currently supported");
        }
        
        Object idValue = null;
        try {
            idValue = PropertyUtils.getProperty(entity, idProperty);
        } catch (Exception e) {
            throw new RuntimeException("Error reading id property", e);
        }
        
        if (idType instanceof StringType) {
            return (String) idValue;
        } else if (idType instanceof LongType) {
            return String.valueOf(idValue);
        }
        
        throw new IllegalArgumentException(String.format("Could not retrieve value for id property. Object: [%s], " +
        		"ID Property: [%s], ID Type: [%s]", entity, idProperty, idType));
    }
    
    protected String getCacheKey(TranslatedEntity entityType, String entityId, String property, String localeCode) {
        return StringUtils.join(new String[] { entityType.getFriendlyType(), entityId, property, localeCode }, "|");
    }
    
    protected Cache getCache() {
        if (cache == null) {
            cache = CacheManager.getInstance().getCache("blTranslationElements");
        }
        return cache;
    }

}