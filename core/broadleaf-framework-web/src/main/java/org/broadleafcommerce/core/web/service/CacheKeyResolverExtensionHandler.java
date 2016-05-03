/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;


/**
 * @author bpolster
 */
public interface CacheKeyResolverExtensionHandler extends ExtensionHandler {

    /**
     * The passed in StringBuilder represents the current state of the cache key prior
     * to running any extension handlers.
     * 
     * Any implementations of this processor can read modify the passed in stringBuilder as
     * needed.    
     * 
     * @param stringBuilder
     * @param hasProducts
     * @return
     */
    public ExtensionResultStatusType updateCacheKey(StringBuilder stringBuilder, boolean hasProducts);
}
