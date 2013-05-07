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

/**
 * Container for ThreadLocal attributes that relate to Translation.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class TranslationConsiderationContext {
    
    private static final ThreadLocal<TranslationService> translationService = new ThreadLocal<TranslationService>();
    private static final ThreadLocal<Boolean> translationConsiderationContext = new ThreadLocal<Boolean>();
    
    public static boolean hasTranslation() {
        return getTranslationConsiderationContext() && getTranslationService() != null;
    }
    
    public static Boolean getTranslationConsiderationContext() {
        return TranslationConsiderationContext.translationConsiderationContext.get();
    }
    
    public static void setTranslationConsiderationContext(Boolean isEnabled) {
        TranslationConsiderationContext.translationConsiderationContext.set(isEnabled);
    }
    
    public static TranslationService getTranslationService() {
        return TranslationConsiderationContext.translationService.get();
    }
    
    public static void setTranslationService(TranslationService translationService) {
        TranslationConsiderationContext.translationService.set(translationService);
    }
    
}
