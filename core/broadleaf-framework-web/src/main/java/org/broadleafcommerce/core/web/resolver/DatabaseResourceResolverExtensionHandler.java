/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.web.resolver;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import java.io.InputStream;


/**
 * Extension handler for resolving templates from the database.
 * 
 * @author Andre Azzolini (apazzolini), bpolster
 */
public interface DatabaseResourceResolverExtensionHandler extends ExtensionHandler {
    
    public static final String IS_KEY = "IS_KEY";
    
    /**
     * If this method returns any of the handled conditions in {@link ExtensionResultStatusType},
     * the value keyed by {@link DatabaseResourceResolverExtensionHandler.IS_KEY} in the 
     * {@link ExtensionResultHolder}'s context map will be an {@link InputStream} of the resolved resource's
     * contents.
     * 
     * @param erh
     * @param params
     * @param resourceName
     * @return whether or not a resource was resolved
     */
    public ExtensionResultStatusType resolveResource(ExtensionResultHolder erh, String resourceName);

}
