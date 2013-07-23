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

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;

/**
 * Container for ThreadLocal attributes that relate to Translation.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class TranslationConsiderationContext {

    private static final ThreadLocal<TranslationConsiderationContext> translationConsiderationContext = ThreadLocalManager.createThreadLocal(TranslationConsiderationContext.class);
    
    public static boolean hasTranslation() {
        return getTranslationConsiderationContext() != null && getTranslationConsiderationContext() && getTranslationService() != null;
    }
    
    public static Boolean getTranslationConsiderationContext() {
        Boolean val = TranslationConsiderationContext.translationConsiderationContext.get().enabled;
        return val == null ? false : val;
    }
    
    public static void setTranslationConsiderationContext(Boolean isEnabled) {
        TranslationConsiderationContext.translationConsiderationContext.get().enabled = isEnabled;
    }
    
    public static TranslationService getTranslationService() {
        return TranslationConsiderationContext.translationConsiderationContext.get().service;
    }
    
    public static void setTranslationService(TranslationService translationService) {
        TranslationConsiderationContext.translationConsiderationContext.get().service = translationService;
    }

    protected Boolean enabled = false;
    protected TranslationService service;
}
