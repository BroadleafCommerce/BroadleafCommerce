/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.server.service.persistence.module.provider;

import org.broadleafcommerce.admin.server.service.persistence.module.provider.extension.ProductUrlFieldPersistenceProviderExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProviderAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;

@Component("blProductUrlFieldPersistenceProvider")
@Scope("prototype")
public class ProductUrlFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    @Resource(name = "blProductUrlFieldPersistenceProviderExtensionManager")
    private ProductUrlFieldPersistenceProviderExtensionManager extensionManager;

    @Override
    public MetadataProviderResponse populateValue(PopulateValueRequest request, Serializable instance) {
        String propName = request.getProperty().getName();
        String val = request.getRequestedValue();

        if ("url".equals(propName) && ProductImpl.class.isAssignableFrom(instance.getClass())) {
            Product product = (Product) instance;

            ExtensionResultHolder<String> holder = new ExtensionResultHolder<>();
            ExtensionResultStatusType result = extensionManager.getProxy().modifyUrl(val, product, holder);

            if (ExtensionResultStatusType.HANDLED == result) {
                product.setUrl(holder.getResult());
                return MetadataProviderResponse.HANDLED;
            }

        }
        return super.populateValue(request, instance);
    }
}
