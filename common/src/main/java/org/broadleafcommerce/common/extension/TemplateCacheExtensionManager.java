/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.extension;

import org.springframework.stereotype.Service;

@Service("blTemplateCacheExtensionManager")
public class TemplateCacheExtensionManager extends ExtensionManager<TemplateCacheExtensionHandler> implements TemplateCacheExtensionHandler {

    public static final ExtensionManagerOperation getTemplateCacheKeyOperation = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((TemplateCacheExtensionHandler) handler).getTemplateCacheKey(
                    params[0], (String) params[1], (ExtensionResultHolder<Object>) params[2]
            );
        }
    };

    public static final ExtensionManagerOperation getTemplateNameOperation = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((TemplateCacheExtensionHandler) handler).getTemplateName(
                    params[0], (ExtensionResultHolder<Object>) params[1]
            );
        }
    };

    public TemplateCacheExtensionManager() {
        super(TemplateCacheExtensionHandler.class);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public ExtensionResultStatusType getTemplateCacheKey(
            Object key,
            String template,
            ExtensionResultHolder<Object> resultHolder
    ) {
        return execute(getTemplateCacheKeyOperation, key, template, resultHolder);
    }

    @Override
    public ExtensionResultStatusType getTemplateName(Object key, ExtensionResultHolder<Object> result) {
        return execute(getTemplateNameOperation, key, result);
    }

}
