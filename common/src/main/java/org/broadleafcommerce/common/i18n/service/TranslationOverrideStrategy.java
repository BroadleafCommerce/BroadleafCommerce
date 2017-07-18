/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.springframework.core.Ordered;

/**
 * @author Jeff Fischer
 */
public interface TranslationOverrideStrategy extends Ordered {

    LocalePair getLocaleBasedOverride(String property, TranslatedEntity entityType, String entityId,
                                      String localeCode, String localeCountryCode, String basicCacheKey);

    LocalePair getLocaleBasedTemplateValue(String templateCacheKey, String property, TranslatedEntity entityType,
                                           String entityId, String localeCode, String localeCountryCode, String specificPropertyKey,
                                           String generalPropertyKey);

    boolean validateTemplateKey(String standardCacheKey, String templateCacheKey);
}
