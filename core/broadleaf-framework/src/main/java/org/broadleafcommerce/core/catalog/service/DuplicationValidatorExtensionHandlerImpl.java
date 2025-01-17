/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.common.admin.condition.ConditionalOnAdmin;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.persistence.EntityDuplicatorExtensionHandler;
import org.broadleafcommerce.common.persistence.EntityDuplicatorExtensionManager;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@ConditionalOnAdmin
@Service("blDuplicationValidatorExtensionHandlerImpl")
public class DuplicationValidatorExtensionHandlerImpl implements EntityDuplicatorExtensionHandler {

    @Resource(name = "blEntityDuplicatorExtensionManager")
    protected EntityDuplicatorExtensionManager extensionManager;

    public DuplicationValidatorExtensionHandlerImpl() {
    }

    @PostConstruct
    public void init() {
        if (this.isEnabled()) {
            this.extensionManager.getHandlers().add(this);
        }
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public ExtensionResultStatusType validateDuplicate(Object entity, ExtensionResultHolder<Boolean> resultHolder) {
        resultHolder.setResult(true);
        return resultHolder.getResult() ? ExtensionResultStatusType.HANDLED_CONTINUE : ExtensionResultStatusType.HANDLED_STOP;
    }

    @Override
    public ExtensionResultStatusType setupDuplicate(Object entity, ExtensionResultHolder<MultiTenantCopyContext> resultHolder) {
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    @Override
    public ExtensionResultStatusType addToSandbox(Object entity) {
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    @Override
    public ExtensionResultStatusType tearDownDuplicate() {
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    @Override
    public ExtensionResultStatusType getCatalogsForPropagation(MultiTenantCopyContext context, ExtensionResultHolder<List<MultiTenantCopyContext>> resultHolder) {
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    @Override
    public ExtensionResultStatusType getClonesByCatalogs(String tableName, Long id, MultiTenantCopyContext multiTenantCopyContext, ExtensionResultHolder<Map<Long, Map<Long, Long>>> resultHolder) {
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }
}
