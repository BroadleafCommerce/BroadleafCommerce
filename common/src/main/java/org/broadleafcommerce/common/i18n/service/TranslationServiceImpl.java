package org.broadleafcommerce.common.i18n.service;

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

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;


@Service("blTranslationService")
public class TranslationServiceImpl implements TranslationService {
    protected static final Log LOG = LogFactory.getLog(TranslationServiceImpl.class);
    
    @Resource(name = "blTranslationDao")
    protected TranslationDao dao;
    
    @Override
    public Translation save(Translation translation) {
        return dao.save(translation);
    }
    
    @Override
    public Translation save(TranslatedEntity entityType, String entityId, String fieldName, String localeCode, 
            String translatedValue) {
        Translation translation = dao.create();
        translation.setEntityType(entityType);
        translation.setEntityId(entityId);
        translation.setFieldName(fieldName);
        translation.setLocaleCode(localeCode);
        translation.setTranslatedValue(translatedValue);
        return dao.save(translation);
    }
    
    @Override
    public Translation getTranslation(TranslatedEntity entity, String entityId, String fieldName, String localeCode) {
        return dao.readTranslation(entity, entityId, fieldName, localeCode);
    }
    
    @Override
    public String getTranslatedValue(Object entity, String property, Locale locale) {
        // Get the value of this property straight from the entity. We will fall back on this if we do not
        // find a translation for the property
        String entityPropertyValue = null;
        try {
            Object rawPropertyValue = PropertyUtils.getSimpleProperty(entity, property);
            if (rawPropertyValue != null) {
                entityPropertyValue = String.valueOf(rawPropertyValue);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn(String.format("Unable to find a value for property %s in object %s, %s", property, entity, msg));
        }
        
        // Attempt to get a translated value for this property to override the default value
        TranslatedEntity entityType = getEntityType(entity);
        String entityId = getEntityId(entity, entityType);
        
        String localeCode = locale.getLanguage();
        String localeCountryCode = localeCode;
        if (StringUtils.isNotBlank(locale.getCountry())) {
            localeCountryCode += "_" + locale.getCountry();
        }
        
        // First, we'll try to look up a country language combo (en_GB)
        Translation translation = getTranslation(entityType, entityId, property, localeCountryCode);
        
        // If we don't find one, let's try just the language (en)
        if (translation == null) {
            translation = getTranslation(entityType, entityId, property, localeCode);
        }
        
        // If we have a match on a translation, use that instead of what we found on the entity
        if (translation != null && StringUtils.isNotBlank(translation.getTranslatedValue())) {
            entityPropertyValue = translation.getTranslatedValue();
        }
        
        return entityPropertyValue;
    }
    
    protected TranslatedEntity getEntityType(Object entity) {
        for (Entry<String, TranslatedEntity> entry : TranslatedEntity.getTypes().entrySet()) {
            try {
                Class<?> clazz = Class.forName(entry.getKey());
                if (clazz.isAssignableFrom(entity.getClass())) {
                    return entry.getValue();
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("TranslatedEntity type was not set to a known class", e);
            }
        }
        throw new IllegalArgumentException(entity.getClass().getName() + " is not a known translatable class");
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

}