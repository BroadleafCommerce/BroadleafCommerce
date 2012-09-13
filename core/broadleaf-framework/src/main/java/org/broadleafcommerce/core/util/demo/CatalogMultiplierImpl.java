/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.util.demo;

import org.broadleafcommerce.core.catalog.dao.CategoryXrefDao;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductAttribute;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Random;

/**
 * @author jfischer
 */
public class CatalogMultiplierImpl implements CatalogMultiplier {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blCatalogService")
    protected CatalogService catalogService;

    @Resource(name="blCategoryXrefDao")
    protected CategoryXrefDao categoryXrefDao;

    protected Random random = new Random();

    protected long numberOfProductsToAdd = 10000L;

    public void init() {
        long j = 0;
        List<Product> products = catalogService.findAllProducts();
        while (j < numberOfProductsToAdd) {
            for (Product product : products) {
                List<ProductAttribute> attributes = product.getProductAttributes();
                for (ProductAttribute attribute : attributes) {
                    em.detach(attribute);
                    attribute.setId(null);
                }
                Product derivedProduct = (Product) product;
                Sku sku = derivedProduct.getDefaultSku();
                em.detach(sku);
                sku.setId(null);
                String skuName = sku.getName();
                int pos = skuName.indexOf('_');
                if (pos >= 0) {
                    skuName = skuName.substring(0, pos);
                }
                sku.setName(skuName + '_' + j);
                sku = catalogService.saveSku(sku);
                derivedProduct.setDefaultSku(sku);
                product.getAllSkus().clear();
                product.getAllSkus().add(sku);
                product.getAllParentCategories().size();

                em.detach(product);
                product.setId(null);
                String productName = sku.getName();
                pos = productName.indexOf('_');
                if (pos >= 0) {
                    productName = productName.substring(0, pos);
                }
                product.setName(productName + '_' + j);
                String manuName = product.getManufacturer();
                pos = manuName.indexOf('_');
                if (pos >= 0) {
                    manuName = manuName.substring(0, pos);
                }
                product.setManufacturer(manuName + '_' + random.nextInt(100));
                product = catalogService.saveProduct(product);

                j++;
                System.out.println(j + ". Adding additional demo product: " + product.getName());
                if (j > numberOfProductsToAdd) {
                    break;
                }
            }
            em.flush();
            em.clear();
        }
    }

    public long getNumberOfProductsToAdd() {
        return numberOfProductsToAdd;
    }

    public void setNumberOfProductsToAdd(long numberOfProductsToAdd) {
        this.numberOfProductsToAdd = numberOfProductsToAdd;
    }
}
