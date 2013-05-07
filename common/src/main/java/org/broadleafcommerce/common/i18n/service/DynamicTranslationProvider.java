/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.i18n.service;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.util.Locale;
import java.util.Map;

/**
 * Convenience class to provide dynamic field translations.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class DynamicTranslationProvider {
    
    /**
     * If translations are enabled, this method will look for a translation for the specified field. If translations are
     * disabled or if this particular field did not have a translation, it will return back the defaultValue.
     * 
     * If the cache parameter is not null, we will first attempt to resolve the translation from the map before asking
     * the translation service for a value. If it is not null and the property did not have a value in the map, we will
     * cache the result.
     * 
     * @param obj
     * @param field
     * @param defaultValue
     * @param cache - an optional map that will cache field name to translated value
     * @return the translated value
     */
    public static String getValue(Object obj, String field, final String defaultValue, Map<String, String> cache) {
        // If this field is in the cache, that means we have already calculated the appropriate translation
        // for this request.
        if (cache != null && cache.containsKey(field)) {
            return cache.get(field);
        }
        
        String valueToReturn = defaultValue;
        
        if (TranslationConsiderationContext.hasTranslation()) {
            TranslationService translationService = TranslationConsiderationContext.getTranslationService();
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getJavaLocale();
            String translatedValue = translationService.getTranslatedValue(obj, field, locale);
            
            if (StringUtils.isNotBlank(translatedValue)) {
                valueToReturn = translatedValue;
            }
        }
        
        if (cache != null) {
            cache.put(field, valueToReturn);
        }
            
        return valueToReturn;
    }

}
