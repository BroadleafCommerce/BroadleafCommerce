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
import org.broadleafcommerce.core.catalog.dao.SkuDao;
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
    
    @Resource(name = "blSkuDao")
    protected SkuDao skuDao;
    
    @Override
    public Integer generateSkusFromProduct(Long productId) {
        Product product = catalogService.findProductById(productId);
        
        if (product.getProductOptions() == null || product.getProductOptions().size() == 0) {
            return -1;
        }
        
        /** Cascading deletes for a many-to-many doesn't actually delete the entities
         * themselves so do it manually. By removing the actual entities themselves,
         * it will cascade down to the xref and remove it from there as well. However,
         * since I'm going to actually be dealing with the allSkus list further on,
         * the transient list needs to get cleared also after deleting the individual Skus
         */
        if (product.getAllSkus().size() > 0) {
            for (Sku sku : product.getAllSkus()) {
                skuDao.delete(sku);
            }
            product.getAllSkus().clear();
        }
        
        List<List<ProductOptionValue>> allPermutations = generatePermutations(0, new ArrayList<ProductOptionValue>(), product.getProductOptions());
        LOG.info("Total number of permutations: " + allPermutations.size());
        LOG.info(allPermutations);
        
        //For each permutation, I need them to map to a specific Sku
        for (List<ProductOptionValue> permutation : allPermutations) {
            Sku permutatedSku = catalogService.createSku();
            permutatedSku.setDefaultProduct(product);
            permutatedSku.setProductOptionValues(permutation);
            Sku savedSku = catalogService.saveSku(permutatedSku);
            
            //Throw the newly generated Sku into the allSkus list to ensure the xref table is
            //updated properly
            product.getAllSkus().add(savedSku);
        }
        catalogService.saveProduct(product);
        
        return allPermutations.size();
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
        for (ProductOptionValue option : currentOption.getAllowedValues()) {
            List<ProductOptionValue> permutation = new ArrayList<ProductOptionValue>();
            permutation.addAll(currentPermutation);
            permutation.add(option);
            result.addAll(generatePermutations(currentTypeIndex + 1, permutation, options));
        }
        
        return result;
    }

}
