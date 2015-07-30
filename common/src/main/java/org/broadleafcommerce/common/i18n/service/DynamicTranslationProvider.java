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
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.util.Locale;

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
     * @param obj
     * @param field
     * @param defaultValue
     * @return the translated value
     */
    public static String getValue(Object obj, String field, final String defaultValue) {
        String valueToReturn = defaultValue;
        
        if (TranslationConsiderationContext.hasTranslation()) {
            TranslationService translationService = TranslationConsiderationContext.getTranslationService();
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getJavaLocale();
            String translatedValue = translationService.getTranslatedValue(obj, field, locale);
            
            if (StringUtils.isNotBlank(translatedValue)) {
                valueToReturn = translatedValue;
            } else {
                valueToReturn = translationService.getDefaultTranslationValue(obj, field, locale, defaultValue);
            }
        }
            
        return valueToReturn;
    }

}
