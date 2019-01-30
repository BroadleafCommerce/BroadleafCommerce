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
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionManagerOperation;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import org.springframework.stereotype.Service;

/**
 * Manage the interaction between a duplication operation and other modules that wish to contribute to that lifecycle
 *
 * @author Jeff Fischer
 */
@Service("blEntityDuplicatorExtensionManager")
public class EntityDuplicatorExtensionManager extends ExtensionManager<EntityDuplicatorExtensionHandler> implements EntityDuplicatorExtensionHandler {

    public static final ExtensionManagerOperation validateDuplicate = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityDuplicatorExtensionHandler) handler).validateDuplicate(params[0], (ExtensionResultHolder<Boolean>) params[1]);
        }
    };

    public static final ExtensionManagerOperation setupDuplicate = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityDuplicatorExtensionHandler) handler).setupDuplicate(params[0], (ExtensionResultHolder<MultiTenantCopyContext>) params[1]);
        }
    };

    public static final ExtensionManagerOperation tearDownDuplicate = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityDuplicatorExtensionHandler) handler).tearDownDuplicate();
        }
    };

    public EntityDuplicatorExtensionManager() {
        super(EntityDuplicatorExtensionHandler.class);
    }

    @Override
    public ExtensionResultStatusType validateDuplicate(Object entity, ExtensionResultHolder<Boolean> resultHolder) {
        return execute(validateDuplicate, entity, resultHolder);
    }

    @Override
    public ExtensionResultStatusType setupDuplicate(Object entity, ExtensionResultHolder<MultiTenantCopyContext> resultHolder) {
        return execute(setupDuplicate, entity, resultHolder);
    }

    @Override
    public ExtensionResultStatusType tearDownDuplicate() {
        return execute(tearDownDuplicate);
    }

    @Override
    public boolean isEnabled() {
        //not used - fulfills interface contract
        return true;
    }
}
