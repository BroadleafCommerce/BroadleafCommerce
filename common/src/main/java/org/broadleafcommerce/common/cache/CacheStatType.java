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
package org.broadleafcommerce.common.cache;

/**
 * @author Jeff Fischer
 */
public enum CacheStatType {
    PAGE_CACHE_HIT_RATE, STRUCTURED_CONTENT_CACHE_HIT_RATE, URL_HANDLER_CACHE_HIT_RATE,
    PRODUCT_URL_MISSING_CACHE_HIT_RATE, CATEGORY_URL_MISSING_CACHE_HIT_RATE, TRANSLATION_CACHE_HIT_RATE,
    RESOURCE_BUNDLING_CACHE_HIT_RATE,GENERATED_RESOURCE_CACHE_HIT_RATE
}
