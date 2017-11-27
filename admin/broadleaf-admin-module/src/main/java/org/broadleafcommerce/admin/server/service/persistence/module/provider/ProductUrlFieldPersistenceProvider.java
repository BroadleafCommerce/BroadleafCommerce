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
