/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.admin.client.service.AdminCatalogService;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Phillip Verheyden
 *
 */
@Service("blAdminCatalogRemoteService")
public class AdminCatalogRemoteService implements AdminCatalogService {
    
    private static final Log LOG = LogFactory.getLog(AdminCatalogRemoteService.class);

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Override
    public Boolean generateSkusFromProduct(Long productId) {
        Product product = catalogService.findProductById(productId);
        
        if (product.getProductOptions() == null || product.getProductOptions().size() == 0) {
            return false;
        }
        
        List<List<ProductOptionValue>> allPermutations = generatePermutations(0, new ArrayList<ProductOptionValue>(), product.getProductOptions());
        LOG.info("Total number of permutations: " + allPermutations.size());
        LOG.info(allPermutations);
        
        //For each permutation, I need them to map to a specific Sku which is derived from the Product's defaultSku
        for (List<ProductOptionValue> permutation : allPermutations) {
            Sku permutatedSku = catalogService.createSku();
            permutatedSku.setDefaultProduct(product);
            product.getSkus().add(permutatedSku);
            permutatedSku.setProductOptionValues(permutation);
            
            catalogService.saveSku(permutatedSku);
        }
        
        return true;
    }
    
    /**
     * Generates all the possible permutations for the combinations of given ProductOptions
     * @param currentTypeIndex
     * @param currentPermutation
     * @param options
     * @return a list containing all of the possible combinations of ProductOptionValues based on grouping by the ProductOptionValue
     */
    public List<List<ProductOptionValue>> generatePermutations(int currentTypeIndex, List<ProductOptionValue> currentPermutation, List<ProductOption> options) {
        List<List<ProductOptionValue>> result = new ArrayList<List<ProductOptionValue>>();
        if (currentTypeIndex == options.size()) {
            result.add(currentPermutation);
            return result;
        }
        
        ProductOption currentOption = options.get(currentTypeIndex);
        for(ProductOptionValue option : currentOption.getAllowedValues()) {
            List<ProductOptionValue> permutation = new ArrayList<ProductOptionValue>();
            permutation.addAll(currentPermutation);
            permutation.add(option);
            result.addAll(generatePermutations(currentTypeIndex + 1, permutation, options));
        }
        
        return result;
    }

}
