/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

package org.broadleafcommerce.core.web.processor;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.PromotableProduct;
import org.broadleafcommerce.core.catalog.domain.RelatedProductDTO;
import org.broadleafcommerce.core.catalog.domain.RelatedProductTypeEnum;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.RelatedProductsService;
import org.broadleafcommerce.presentation.condition.TemplatingExistCondition;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafVariableModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will find related products and skus.    A product or category id must be specified.    If both are specified, only the productId will be used.  
 *
 * Takes in the following parameters
 * <ul>
 *    <li>productId - productId to find related products.</li>
 *    <li>categoryId - categoryId to find related products.</li>
 *    <li>type - the type of relations to find (e.g. FEATURED (DEFAULT), UPSELL, CROSSSELL).   Implementations may have other specific types of related products.</li>  
 *    <li>cumulativeResults - true (DEFAULT) /false - indicates that the system should add results from the parent categories of the passed in item as well as the current item</li>
 *    <li>qty - if specified, determines the max-number of results that will be returned; otherwise, all results are returned.
 *    <li>productsResultVar - if specified, adds the products to the model keyed by this var.   Otherwise, uses "products" as the model identifier.
 *    <li>relatedProductsResultVar - if specified, adds the RelatedProduct(s) to the model keyed by this var.   Otherwise, uses "relatedProducts" as the model identifier.   
 *    <li>relatedSkusResultVar - if specified, adds the related skus to the model keyed by this var.   Otherwise, uses "relatedSkus" as the model identifier.   
 * </ul>
 * 
 * The output from this operation returns a list of PromotableProducts which represent the following. 
 *      relatedProduct.product 
 *      relatedProduct.promotionMessage.
 *      
 * @author bpolster
 */
@Component("blRelatedProductProcessor")
@Conditional(TemplatingExistCondition.class)
public class RelatedProductProcessor extends AbstractBroadleafVariableModifierProcessor {

    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    @Resource(name = "blRelatedProductsService")
    protected RelatedProductsService relatedProductsService;

    @Override
    public String getName() {
        return "related_products";
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }
    
    @Override
    public List<String> getCollectionModelVariableNamesToAddTo() {
        List<String> names = new ArrayList<>();
        names.add("blcAllProducts");
        return names;
    }

    /**
     * Controller method for the processor that readies the service call and adds the results to the model.
     */
    @Override
    public Map<String, Object> populateModelVariables(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        RelatedProductDTO relatedProductDTO = buildDTO(tagAttributes, tagName, context);
        List<? extends PromotableProduct> relatedProducts = relatedProductsService.findRelatedProducts(relatedProductDTO);
        
        Map<String, Object> newModelVars = new HashMap<>();
        newModelVars.put(getRelatedProductsResultVar(tagAttributes), relatedProducts);
        newModelVars.put(getProductsResultVar(tagAttributes), convertRelatedProductsToProducts(relatedProducts));
        newModelVars.put("blcAllProducts", buildProductList(relatedProducts));
        
        return newModelVars;
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

    protected List<Sku> getRelatedSkus(List<? extends PromotableProduct> relatedProducts, Integer maxQuantity) {
        List<Sku> relatedSkus = new ArrayList<>();
        if (relatedProducts != null) {
            Integer numSkus = 0;
            for (PromotableProduct promProduct : relatedProducts) {
                Product relatedProduct = promProduct.getRelatedProduct();
                List<Sku> additionalSkus = relatedProduct.getAdditionalSkus();
                if (CollectionUtils.isNotEmpty(additionalSkus)) {
                    for (Sku additionalSku : additionalSkus) {
                        if (numSkus == maxQuantity) {
                            break;
                        }
                        relatedSkus.add(additionalSku);
                        numSkus++;

                    }
                } else {
                    if (numSkus.equals(maxQuantity)) {
                        break;
                    }
                    relatedSkus.add(relatedProduct.getDefaultSku());
                    numSkus++;
                }
            }
        }
        return relatedSkus;
    }

    protected List<Product> convertRelatedProductsToProducts(List<? extends PromotableProduct> relatedProducts) {
        List<Product> products = new ArrayList<>();
        if (relatedProducts != null) {
            for (PromotableProduct product : relatedProducts) {
                products.add(product.getRelatedProduct());
            }
        }
        return products;
    }

    private String getRelatedProductsResultVar(Map<String, String> tagAttributes) {
        String resultVar = tagAttributes.get("relatedProductsResultVar");
        if (resultVar == null) {
            resultVar = "relatedProducts";
        }
        return resultVar;
    }

    private String getProductsResultVar(Map<String, String> tagAttributes) {
        String resultVar = tagAttributes.get("productsResultVar");
        if (resultVar == null) {
            resultVar = "products";
        }
        return resultVar;
    }

    private RelatedProductDTO buildDTO(Map<String, String> tagAttributes, String tagName, BroadleafTemplateContext context) {
        RelatedProductDTO relatedProductDTO = new RelatedProductDTO();
        String productIdStr = tagAttributes.get("productId");
        String categoryIdStr = tagAttributes.get("categoryId");
        String quantityStr = tagAttributes.get("quantity");
        String typeStr = tagAttributes.get("type");

        if (productIdStr != null) {
            Number productId = context.parseExpression(productIdStr);
            relatedProductDTO.setProductId(productId.longValue());
        }

        if (categoryIdStr != null) {
            Number categoryId = context.parseExpression(categoryIdStr);
            relatedProductDTO.setCategoryId(categoryId.longValue());
        }

        if (quantityStr != null) {
            Object quantityObj = context.parseExpression(quantityStr);
            Number quantity = 0;
            if (quantityObj instanceof Number) {
                quantity = (Number) quantityObj;
            } else if (quantityObj instanceof String) {
                quantity = new Integer((String) quantityObj);
            }
            relatedProductDTO.setQuantity(quantity.intValue());
        }

        if (typeStr != null) {
            Object typeExp = context.parseExpression(typeStr);
            if (typeExp instanceof String && RelatedProductTypeEnum.getInstance((String) typeExp) != null) {
                relatedProductDTO.setType(RelatedProductTypeEnum.getInstance((String) typeExp));
            }
        }

        if ("false".equalsIgnoreCase(tagAttributes.get("cumulativeResults"))) {
            relatedProductDTO.setCumulativeResults(false);
        }

        return relatedProductDTO;
    }

}
