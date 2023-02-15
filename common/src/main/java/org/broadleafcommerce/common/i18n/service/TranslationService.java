/*-
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.i18n.service;

import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;

import java.util.List;
import java.util.Locale;

import javax.cache.Cache;

public interface TranslationService {

    /**
     * Persists the given translation
     * 
     * @param translation
     * @return the persisted translation
     */
    public Translation save(Translation translation);

    /**
     * Creates a new translation object for the requested parameters, saves it, and returns the saved instance.
     * 
     * <b>Note: This method will overwrite a previously existing translation if it matches on entityType, entityId, 
     * fieldName, and localeCode.</b>
     * 
     * @param entityType
     * @param entityId
     * @param fieldName
     * @param localeCode
     * @param translatedValue
     * @return the persisted translation
     */
    public Translation save(String entityType, String entityId, String fieldName, String localeCode, 
            String translatedValue);
    
    /**
     * Updates the given translation id with the new locale code and translated value
     * 
     * @param translationId
     * @param localeCode
     * @param translatedValue
     * @return the persisted translation
     */
    public Translation update(Long translationId, String localeCode, String translatedValue);
    
    /**
     * Deletes the given translations
     * 
     * @param translationId
     */
    public void deleteTranslationById(Long translationId);
    
    /**
     * Finds all current translations for the specified field
     * 
     * @param ceilingEntityClassname
     * @param entityId
     * @param property
     * @return the list of translations
     */
    public List<Translation> getTranslations(String ceilingEntityClassname, String entityId, String property);
    
    /**
     * Attempts to find the translation object for the given parameters
     * 
     * @param entity
     * @param entityId
     * @param fieldName
     * @param localeCode
     * @return the persisted translation
     */
    public Translation getTranslation(TranslatedEntity entity, String entityId, String fieldName, String localeCode);

    /**
     * Returns the translated value of the property for the given entity. For example, if entity is an instance of 
     * Product and property is equal to name, this method might return "Hoppin' Hot Sauce" if we are in an English 
     * locale and "Salsa de la Muerte Saltante" if we are in a Spanish locale.
     * 
     * If a country is set on the locale (locale code en_GB for example), we will first look for a translation that matches
     * en_GB, and then look for a translation for en. If a translated value for the given locale is not available, 
     * it will return null.
     * 
     * @param entity
     * @param property
     * @param locale
     * @return the translated value of the property for the given entity
     */
    public String getTranslatedValue(Object entity, String property, Locale locale);

    /**
     * Gets the TranslatedEntity based on the passed className.  The TranslatedEntity may be an assignable.
     * 
     * @param className
     * @return
     */
    public TranslatedEntity getAssignableEntityType(String className);

    
    /**
     * Remove a translation instance from the translation specific cache (different than level-2 hibernate cache)
     *
     * @param translation The translation instance to remove
     */
    void removeTranslationFromCache(Translation translation);

    /**
     * Find a translation instance by its primary key value.
     *
     * @param id the primary key value
     * @return
     */
    Translation findTranslationById(Long id);

    /**
     * Get the translation specific cache (different than the level-2 hibernate cache)
     *
     * @return the translation specific cache
     */
    Cache<String, Object> getCache();

    /**
     * Intended for use with the {@link DynamicTranslationProvider} to determine the default value when a 
     * translation was not provided.
     * 
     * The default implementation of this method relies on a system property "returnBlankTranslationForNotDefaultLocale". 
     * If this is true, the system will return blank if the language of the defaultLocale does not match the language
     * of the passed in locale.
     * 
     * For example, consider the "longDescription" property and the system default locale is "en".   If this method is 
     * called for a locale of "en_CA", the requestedDefaultValue will be returned.   If the method is called with a value
     * of "fr_CA", blank will be returned.
     *  
     * @param entity
     * @param property
     * @param locale
     * @param requestedDefaultValue
     * @return
     */
    String getDefaultTranslationValue(Object entity, String property, Locale locale, String requestedDefaultValue);

    /**
     * Find all the available translations for the given params.
     *
     * @param entityType
     * @param stage param drives whether to look for entries at a template level or standard site level (multitenant concepts). Can be IGNORE. Any multitenant behavior is ignored in the absence of the multitenant module.
     * @param entityIds the {@link Translation#getEntityId()} to restrict the results by
     * @return
     */
    List<Translation> findAllTranslationEntries(TranslatedEntity translatedEntity, ResultType standard, List<String> entityIds);

}
