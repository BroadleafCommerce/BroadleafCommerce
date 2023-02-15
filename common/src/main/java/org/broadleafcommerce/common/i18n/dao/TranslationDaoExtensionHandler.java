/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.common.i18n.dao;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import javax.persistence.EntityManager;


/**
 * @author Andre Azzolini (apazzolini)
 */
public interface TranslationDaoExtensionHandler extends ExtensionHandler {
    
    /**
     * If there is a different id that should be used for a translation lookup instead of the given entityId,
     * the handler should place the result in the {@link ExtensionResultHolder} argument.
     * 
     * @param erh
     * @param em
     * @param clazz
     * @param entityId
     * @return the status of the call to the given extension handler
     */
    public ExtensionResultStatusType overrideRequestedId(ExtensionResultHolder erh, EntityManager em, 
            Class<?> clazz, Long entityId);

}
