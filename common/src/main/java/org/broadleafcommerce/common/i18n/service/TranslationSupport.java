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

import net.sf.ehcache.Cache;

import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;

import java.util.Map;

/**
 * @author Jeff Fischer
 */
public interface TranslationSupport {

    StandardCacheItem lookupTranslationFromMap(String key, Map<String, Map<String, StandardCacheItem>> propertyTranslationMap, String entityId);

    Cache getCache();

    int getThresholdForFullCache();

    void setThresholdForFullCache(int thresholdForFullCache);

    int getTemplateThresholdForFullCache();

    void setTemplateThresholdForFullCache(int templateThresholdForFullCache);

    Translation findBestTemplateTranslation(String specificPropertyKey, String generalPropertyKey, Map<String, Map<String, Translation>> propertyTranslationMap, String entityId);

    String getCacheKey(ResultType resultType, TranslatedEntity entityType);

}
