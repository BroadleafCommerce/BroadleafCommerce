/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.i18n.service;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.TypedClosure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thread-local cache structure that contains all of the {@link Translation}s for a batch of processing. This is mainly
 * used when executing a search re-index operation. Rather than go to the database for each item being indexed, it makes
 * more sense to go to the database once, cache all of the results here, and then let the {@link TranslationService}
 * use this instead.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class TranslationBatchReadCache {
    
    protected static final ThreadLocal<Map> TRANSLATION_CACHE = ThreadLocalManager.createThreadLocal(Map.class, false);

    public static void setCache(Map<String, Translation> cache) {
        TRANSLATION_CACHE.set(cache);
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Translation> getCache() {
        return TRANSLATION_CACHE.get();
    }
    
    public static void clearCache() {
        TRANSLATION_CACHE.remove();
    }
    
    public static void addToCache(List<Translation> translations) {
        Map<String, Translation> translationMap = BLCMapUtils.keyedMap(translations, new TypedClosure<String, Translation>() {

            @Override
            public String getKey(Translation value) {
                return buildCacheKey(value);
            }
        });
        if (getCache() == null) {
            setCache(new HashMap<String, Translation>());
        }
        getCache().putAll(translationMap);
    }
    
    public static Translation getFromCache(TranslatedEntity entityType, String id, String propertyName, String localeCode) {
        Translation translation = getCache().get(buildCacheKey(entityType, id, propertyName, localeCode));
        if (translation == null && StringUtils.contains(localeCode, '_')) {
            String languageWithoutCountryCode = localeCode.substring(localeCode.indexOf('_') + 1);
            translation = getCache().get(buildCacheKey(entityType, id, propertyName, languageWithoutCountryCode));
        }
        
        return (translation == null) ? null : translation;
    }
    
    public static String buildCacheKey(Translation translation) {
        return buildCacheKey(translation.getEntityType(),
            translation.getEntityId(),
            translation.getFieldName(),
            translation.getLocaleCode());
    }
    
    public static String buildCacheKey(TranslatedEntity entityType, String id, String propertyName, String localeCode) {
        return StringUtils.join(new String[]{entityType.getType(), id, propertyName, localeCode}, "-");
    }
}
