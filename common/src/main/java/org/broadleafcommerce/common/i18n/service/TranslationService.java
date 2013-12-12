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

import java.util.List;
import java.util.Locale;

import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;

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
    Cache getCache();

}
