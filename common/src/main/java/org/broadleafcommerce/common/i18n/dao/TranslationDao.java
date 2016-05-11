/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

package org.broadleafcommerce.common.i18n.dao;

import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;

import java.util.List;
import java.util.Map;

/**
 * Provides data access for the {@link Translation} entity.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface TranslationDao {

    /**
     * Persists the given translation
     * @param translation
     * @return the saved translation
     */
    public Translation save(Translation translation);
    
    /**
     * Creates an empty translation instance that is not persisted to the database
     * 
     * @return the unsaved, empty translation
     */
    public Translation create();

    /**
     * Deletes the given translation
     * 
     * @param translation
     */
    public void delete(Translation translation);

    /**
     * Returns a map that holds the following data for the given entity:
     *  "name" --> idProperty (the name of the id property, always a String)
     *  "type" --> idProperty's type (usually either Long or String)
     *  
     * @param entity
     * @return the id property's metadata
     */
    public Map<String, Object> getIdPropertyMetadata(TranslatedEntity entity);

    /**
     * Returns the entity implementation class based on the TranslatedEntity
     *
     * @param entity
     * @return the entity implementation class
     */
    Class<?> getEntityImpl(TranslatedEntity entity);
    
    /**
     * Reads a translation by its own primary key
     * 
     * @param translationId
     * @return the translation
     */
    public Translation readTranslationById(Long translationId);
    
    /**
     * Reads all translations for a given field
     * 
     * @param entity
     * @param entityId
     * @param fieldName
     * @return the list of translations
     */
    public List<Translation> readTranslations(TranslatedEntity entity, String entityId, String fieldName);

    /**
     * Reads a translation for the requested parameters. Returns null if there is no translation found
     * 
     * @param entity
     * @param entityId
     * @param fieldName
     * @param localeCode
     * @return the translation
     */
    public Translation readTranslation(TranslatedEntity entity, String entityId, String fieldName, String localeCode);

    /**
     * Get the id for the object. Can take into account hierarchical multitenancy to retrieve the original id.
     *
     * @param entityType
     * @param entity
     * @return
     */
    String getEntityId(TranslatedEntity entityType, Object entity);

    /**
     * Count the number of translations for the given params.
     *
     * @param entityType
     * @param stage param drives whether to look for entries at a template level or standard site level (multitenant concepts). Can be IGNORE. Any multitenant behavior is ignored in the absence of the multitenant module.
     * @return
     */
    Long countTranslationEntries(TranslatedEntity entityType, ResultType stage);

    /**
     * Read all the available translations for the given params.
     *
     * @param entityType
     * @param stage param drives whether to look for entries at a template level or standard site level (multitenant concepts). Can be IGNORE. Any multitenant behavior is ignored in the absence of the multitenant module.
     * @return
     */
    List<Translation> readAllTranslationEntries(TranslatedEntity entityType, ResultType stage);
    
    /**
     * Read all the available translations for the given params.
     *
     * @param entityType
     * @param stage param drives whether to look for entries at a template level or standard site level (multitenant concepts). Can be IGNORE. Any multitenant behavior is ignored in the absence of the multitenant module.
     * @param entityIds the {@link Translation#getEntityId()} to restrict the results by
     * @return
     */
    List<Translation> readAllTranslationEntries(TranslatedEntity entityType, ResultType stage, List<String> entityIds);

    /**
     * Read all translation entries (see {@link #readAllTranslationEntries(org.broadleafcommerce.common.i18n.domain.TranslatedEntity, org.broadleafcommerce.common.extension.ResultType)}),
     * and convert those results into a list of {@link org.broadleafcommerce.common.extension.StandardCacheItem} instances.
     *
     * @param entityType
     * @param stage param drives whether to look for entries at a template level or standard site level (multitenant concepts). Can be IGNORE. Any multitenant behavior is ignored in the absence of the multitenant module.
     * @return
     */
    List<StandardCacheItem> readConvertedTranslationEntries(TranslatedEntity entityType, ResultType stage);

    /**
     * Read a specific translation for the given params.
     *
     * @param entityType
     * @param entityId
     * @param fieldName
     * @param localeCode
     * @param localeCountryCode
     * @param stage param drives whether to look for entries at a template level or standard site level (multitenant concepts). Can be IGNORE. Any multitenant behavior is ignored in the absence of the multitenant module.
     * @return
     */
    Translation readTranslation(TranslatedEntity entityType, String entityId, String fieldName, String localeCode, String localeCountryCode, ResultType stage);
}
