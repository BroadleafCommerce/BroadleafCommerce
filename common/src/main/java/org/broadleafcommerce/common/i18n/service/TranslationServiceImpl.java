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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    @Transactional("blTransactionManager")
    public Translation save(Translation translation) {
        return dao.save(translation);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public Translation save(String entityType, String entityId, String fieldName, String localeCode, 
            String translatedValue) {
        TranslatedEntity te = TranslatedEntity.getInstance(entityType);
        
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
        TranslatedEntity entityType = TranslatedEntity.getInstance(ceilingEntityClassname);
        return dao.readTranslations(entityType, entityId, property);
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