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

import org.broadleafcommerce.common.extension.StandardCacheItem;

/**
 * Represents a cached translation pair.
 *
 * @author Jeff Fischer
 */
public class LocalePair {

    StandardCacheItem specificItem = null;
    StandardCacheItem generalItem = null;

    /**
     * Retrieve the language and country specific translation.
     *
     * @return
     */
    public StandardCacheItem getSpecificItem() {
        return specificItem;
    }

    public void setSpecificItem(StandardCacheItem specificItem) {
        this.specificItem = specificItem;
    }

    /**
     * Retrieve the language only translation.
     *
     * @return
     */
    public StandardCacheItem getGeneralItem() {
        return generalItem;
    }

    public void setGeneralItem(StandardCacheItem generalItem) {
        this.generalItem = generalItem;
    }

}
