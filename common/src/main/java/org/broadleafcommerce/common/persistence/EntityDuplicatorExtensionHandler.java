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
package org.broadleafcommerce.common.persistence;

import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

/**
 * Allow other modules to contribute to the duplication lifecycle
 *
 * @author Jeff Fischer
 */
public interface EntityDuplicatorExtensionHandler extends ExtensionHandler {

    /**
     * Confirm whether or not duplication operation is allowed
     *
     * @param entity
     * @param resultHolder
     * @return
     */
    ExtensionResultStatusType validateDuplicate(Object entity, ExtensionResultHolder<Boolean> resultHolder);

    /**
     * Perform any required context and state setup before commencing with the duplication
     *
     * @param entity
     * @param resultHolder
     * @return
     */
    ExtensionResultStatusType setupDuplicate(Object entity, ExtensionResultHolder<MultiTenantCopyContext> resultHolder);

    /**
     * Tear down any expired context and state used during the duplication
     *
     * @return
     */
    ExtensionResultStatusType tearDownDuplicate();
}
