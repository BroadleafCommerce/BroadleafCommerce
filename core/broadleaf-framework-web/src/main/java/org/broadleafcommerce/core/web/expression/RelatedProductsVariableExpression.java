/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.expression;

import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.PromotableProduct;
import org.broadleafcommerce.core.catalog.domain.RelatedProductDTO;
import org.broadleafcommerce.core.catalog.domain.RelatedProductTypeEnum;
import org.broadleafcommerce.core.catalog.service.RelatedProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nick Crum ncrum
 */
@Component("blRelatedProductsVariableExpression")
public class RelatedProductsVariableExpression implements BroadleafVariableExpression {

    private final RelatedProductsService relatedProductsService;

    @Autowired
    public RelatedProductsVariableExpression(RelatedProductsService relatedProductsService) {
        this.relatedProductsService = relatedProductsService;
    }

    @Override
    public String getName() {
        return "related_products";
    }

    public List<Product> findByProduct(Long productId) {
        return getRelatedProducts(productId, null, null, null);
    }

    public List<Product> findByProduct(Long productId, Integer quantity) {
        return getRelatedProducts(productId, null, quantity, null);
    }

    public List<Product> findByCategory(Long categoryId, Integer quantity) {
        return getRelatedProducts(null, categoryId, quantity, null);
    }

    public List<Product> findByProduct(Long productId, Integer quantity, String type) {
        return getRelatedProducts(productId, null, quantity, type);
    }

    public List<Product> findByCategory(Long categoryId, Integer quantity, String type) {
        return getRelatedProducts(null, categoryId, quantity, type);
    }

    public List<Product> getRelatedProducts(Long productId, Long categoryId, Integer quantity, String type) {
        RelatedProductDTO relatedProductDTO = new RelatedProductDTO();
        relatedProductDTO.setProductId(productId);
        relatedProductDTO.setCategoryId(categoryId);

        if (quantity != null) {
            relatedProductDTO.setQuantity(quantity);
        }

        if (type != null) {
            relatedProductDTO.setType(RelatedProductTypeEnum.getInstance(type));
        }
        List<? extends PromotableProduct> relatedProducts = relatedProductsService.findRelatedProducts(relatedProductDTO);
        return buildProductList(relatedProducts);
    }

    protected List<Product> buildProductList(List<? extends PromotableProduct> relatedProducts) {
        List<Product> productList = new ArrayList<>();
        if (relatedProducts != null) {
            for (PromotableProduct promProduct : relatedProducts) {
                productList.add(promProduct.getRelatedProduct());
            }
        }
        return productList;
    }
}
