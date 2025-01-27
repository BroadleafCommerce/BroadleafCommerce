/*-
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.admin.server.service.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionManagerOperation;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.springframework.stereotype.Service;

@Service("blOfferCustomServiceExtensionManager")
public class OfferCustomServiceExtensionManager extends ExtensionManager<OfferCustomServiceExtensionHandler>
        implements OfferCustomServiceExtensionHandler {

    public static final ExtensionManagerOperation clearHiddenQualifiers = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferCustomServiceExtensionHandler) handler).clearHiddenQualifiers((Entity) params[0]);
        }
    };

    public OfferCustomServiceExtensionManager() {
        super(OfferCustomServiceExtensionHandler.class);
    }

    @Override
    public ExtensionResultStatusType clearHiddenQualifiers(Entity entity) {
        return execute(clearHiddenQualifiers, entity);
    }

    @Override
    public boolean isEnabled() {
        //not used - fulfills interface contract
        return true;
    }

}
