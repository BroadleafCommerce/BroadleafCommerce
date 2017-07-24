/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
