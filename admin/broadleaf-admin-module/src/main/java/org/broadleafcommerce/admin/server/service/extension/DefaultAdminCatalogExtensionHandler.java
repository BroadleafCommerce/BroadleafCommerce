/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.admin.server.service.extension;

import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Default implementation used by the core framework.
 *
 * @see org.broadleafcommerce.admin.server.service.extension.AdminCatalogServiceExtensionHandler
 * @author Jeff Fischer
 */
@Component("blDefaultAdminCatalogExtensionHandler")
public class DefaultAdminCatalogExtensionHandler extends AbstractExtensionHandler implements AdminCatalogServiceExtensionHandler {

    @Resource(name = "blAdminCatalogServiceExtensionManager")
    protected AdminCatalogServiceExtensionManager extensionManager;

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    /**
     * Simply iterates through the permutations based on the product options and persists them
     * as new {@link org.broadleafcommerce.core.catalog.domain.Sku} instances in the {@link org.broadleafcommerce.core.catalog.domain.Product}
     *
     * @param product
     * @param permutationsToGenerate
     * @param erh
     * @return
     */
    @Override
    public ExtensionResultStatusType persistSkuPermutation(Product product, List<List<ProductOptionValue>>
            permutationsToGenerate, ExtensionResultHolder<Integer> erh) {
        int numPermutationsCreated = 0;
        //For each permutation, I need them to map to a specific Sku
        for (List<ProductOptionValue> permutation : permutationsToGenerate) {
            if (permutation.isEmpty()) continue;
            Sku permutatedSku = catalogService.createSku();
            permutatedSku.setProduct(product);
            permutatedSku.setProductOptionValues(permutation);
            permutatedSku = catalogService.saveSku(permutatedSku);
            product.getAdditionalSkus().add(permutatedSku);
            numPermutationsCreated++;
        }
        if (numPermutationsCreated != 0) {
            catalogService.saveProduct(product);
        }
        erh.setResult(numPermutationsCreated);
        return ExtensionResultStatusType.HANDLED;
    }

    @Override
    public int getPriority() {
        return DEFAULT_PRIORITY;
    }
}
