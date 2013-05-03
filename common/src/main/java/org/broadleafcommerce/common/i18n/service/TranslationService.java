
package org.broadleafcommerce.common.i18n.service;

import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;

import java.util.Locale;

public interface TranslationService {

    /**
     * Persists the given translation
     * 
     * @param translation
     * @return the persisted translation
     */
    public Translation save(Translation translation);

    /**
     * Creates a new translation object for the requested parameters, saves it, and returns the saved instance
     * 
     * @param entityType
     * @param entityId
     * @param fieldName
     * @param localeCode
     * @param translatedValue
     * @return the persisted translation
     */
    public Translation save(TranslatedEntity entityType, String entityId, String fieldName, String localeCode, 
            String translatedValue);
    
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
     * it will return the default value of the property from the entity directly.
     * 
     * @param entity
     * @param property
     * @param locale
     * @return the translated value of the property for the given entity
     */
    public String getTranslatedValue(Object entity, String property, Locale locale);

}