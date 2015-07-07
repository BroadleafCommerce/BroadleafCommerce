/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.PromotableProduct;
import org.broadleafcommerce.core.catalog.domain.RelatedProductDTO;
import org.broadleafcommerce.core.catalog.domain.RelatedProductTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service("blRelatedProductsService")
/*
 * Service that provides method for finding a product's related products.   
 */
public class RelatedProductsServiceImpl implements RelatedProductsService {
    
    @Resource(name="blCategoryDao")
    protected CategoryDao categoryDao;

    @Resource(name="blProductDao")
    protected ProductDao productDao;
    
    @Resource(name="blCatalogService")
    protected CatalogService catalogService;

    @Override
    public List<? extends PromotableProduct> findRelatedProducts(RelatedProductDTO relatedProductDTO) {
        Product product = lookupProduct(relatedProductDTO);
        Category category = lookupCategory(relatedProductDTO);      
        
        if (RelatedProductTypeEnum.FEATURED.equals(relatedProductDTO.getType())) {
            return buildFeaturedProductsList(product, category, relatedProductDTO);
        } else if (RelatedProductTypeEnum.CROSS_SALE.equals(relatedProductDTO.getType())) {
            return buildCrossSaleProductsList(product, category, relatedProductDTO);
        } else if (RelatedProductTypeEnum.UP_SALE.equals(relatedProductDTO.getType())) {
            return buildUpSaleProductsList(product, category, relatedProductDTO);
        } else {
            throw new IllegalArgumentException("RelatedProductType " + relatedProductDTO.getType() + " not supported.");
        }       
    }
    
    /**
     * Returns the featured products for the past in product/category
     * @param product
     * @param category
     * @param relatedProductDTO
     * @return
     */
    protected List<? extends PromotableProduct> buildFeaturedProductsList(Product product, Category category, RelatedProductDTO relatedProductDTO) {
        List<FeaturedProduct> returnFeaturedProducts = null;
        
        if (product != null) {
            category = product.getDefaultCategory();
        }
        
        if (category != null) {
            if (relatedProductDTO.isCumulativeResults()) {
                returnFeaturedProducts = category.getCumulativeFeaturedProducts();
            } else {
                returnFeaturedProducts = category.getFeaturedProducts();
            }
        }
        
        removeCurrentProductFromReturnList(product, returnFeaturedProducts);
        returnFeaturedProducts = (List<FeaturedProduct>)removeDuplicatesFromList(returnFeaturedProducts);
        
        return resizeList(returnFeaturedProducts, relatedProductDTO.getQuantity());
    }
    
    private List<? extends PromotableProduct> removeDuplicatesFromList(List <? extends PromotableProduct> returnPromotableProducts) {
        Set<PromotableProduct> productSet = new LinkedHashSet<PromotableProduct>();
        Set<Product> relatedProductSet = new LinkedHashSet<Product>();

        if (returnPromotableProducts != null) {
            for(PromotableProduct p : returnPromotableProducts) {
                if (!relatedProductSet.contains(p.getRelatedProduct())){
                    productSet.add(p);
                    relatedProductSet.add(p.getRelatedProduct());
                }
            }
        } else {
            return null;
        }
        returnPromotableProducts.clear();
        returnPromotableProducts.addAll(new ArrayList(productSet));
        return returnPromotableProducts;

    }

     private void removeCurrentProductFromReturnList(Product product,
            List<? extends PromotableProduct> returnPromotableProducts) {
        if (product != null && returnPromotableProducts != null) {
            Iterator<? extends PromotableProduct> productIterator = returnPromotableProducts.iterator();
            while (productIterator.hasNext()) {
                PromotableProduct promotableProduct = productIterator.next();
                if (product.getId().equals(promotableProduct.getRelatedProduct().getId())) {
                    productIterator.remove();
                }               
            }           
        }
    }
    
    /**
     * Returns the upSale products for the past in product/category
     * @param product
     * @param category
     * @param relatedProductDTO
     * @return
     */
    protected List<? extends PromotableProduct> buildUpSaleProductsList(Product product, Category category, RelatedProductDTO relatedProductDTO) {
        List<? extends PromotableProduct> returnUpSaleProducts = null;
        
        if (product != null) {
            if (relatedProductDTO.isCumulativeResults()) {
                returnUpSaleProducts = product.getCumulativeUpSaleProducts();
            } else {
                returnUpSaleProducts = product.getUpSaleProducts();
            }
        } else if (category != null) {
            if (relatedProductDTO.isCumulativeResults()) {
                returnUpSaleProducts = category.getCumulativeUpSaleProducts();
            } else {
                returnUpSaleProducts = category.getUpSaleProducts();
            }
        }

        removeCurrentProductFromReturnList(product, returnUpSaleProducts);
        returnUpSaleProducts = removeDuplicatesFromList(returnUpSaleProducts);

        return resizeList(returnUpSaleProducts, relatedProductDTO.getQuantity());
    }
    
    /**
     * Returns the crossSale products for the past in product/category
     * @param product
     * @param category
     * @param relatedProductDTO
     * @return
     */
    protected List<? extends PromotableProduct> buildCrossSaleProductsList(Product product, Category category, RelatedProductDTO relatedProductDTO) {
        List<? extends PromotableProduct> crossSaleProducts = null;
        
        if (product != null) {
            if (relatedProductDTO.isCumulativeResults()) {
                crossSaleProducts = product.getCumulativeCrossSaleProducts();
            } else {
                crossSaleProducts = product.getCrossSaleProducts();
            }
        } else if (category != null) {
            if (relatedProductDTO.isCumulativeResults()) {
                crossSaleProducts = category.getCumulativeCrossSaleProducts();
            } else {
                crossSaleProducts = category.getCrossSaleProducts();
            }
        }

        removeCurrentProductFromReturnList(product, crossSaleProducts);
        crossSaleProducts = removeDuplicatesFromList(crossSaleProducts);
        
        return resizeList(crossSaleProducts, relatedProductDTO.getQuantity());
    }   
    
    /**
     * Resizes the list to match the passed in quantity.   If the quantity is greater than the size of the list or null,
     * the originalList is returned.
     * 
     * @param originalList
     * @param qty
     * @return
     */
    protected List<? extends PromotableProduct> resizeList(List<? extends PromotableProduct> originalList, Integer qty) {
        if (qty != null && originalList != null && originalList.size() > qty) {
            return originalList.subList(0, qty);
        } else {
            return originalList;
        }
    }
    
    protected Product lookupProduct(RelatedProductDTO relatedProductDTO) {
        if (relatedProductDTO.getProductId() != null) {
            return productDao.readProductById(relatedProductDTO.getProductId());
        } else {
            return null;
        }
    }
    
    protected Category lookupCategory(RelatedProductDTO relatedProductDTO) {
        if (relatedProductDTO.getCategoryId() != null) {
            return categoryDao.readCategoryById(relatedProductDTO.getCategoryId());
        } else {
            return null;
        }
    }
}
