/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.PromotableProduct;
import org.broadleafcommerce.core.catalog.domain.RelatedProductDTO;
import org.broadleafcommerce.core.catalog.domain.RelatedProductTypeEnum;
import org.broadleafcommerce.core.catalog.service.RelatedProductsService;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


/**
 * A Thymeleaf processor that will find related products.    A product or category id must be specified.    If both are specified, only the productId will be used.  
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
 * </ul>
 * 
 * The output from this operation returns a list of PromotableProducts which represent the following. 
 *      relatedProduct.product 
 *      relatedProduct.promotionMessage.
 *      
 * @author bpolster
 */
@Component("blRelatedProductProcessor")
public class RelatedProductProcessor extends AbstractModelVariableModifierProcessor {
    
    @Resource(name = "blRelatedProductsService")
    protected RelatedProductsService relatedProductsService;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public RelatedProductProcessor() {
        super(TemplateMode.HTML, "blc", "related_products", true, null, false, 10000);
    }

    @Override
    protected Map<String, Object> populateModelVariables(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        Map<String,Object> result = new HashMap<>();
        Map<String, String> attributeMap = tag.getAttributeMap();
        List<? extends PromotableProduct> relatedProducts = relatedProductsService.findRelatedProducts(buildDTO(attributeMap, context));
        result.put(getRelatedProductsResultVar(attributeMap), relatedProducts);
        result.put(getProductsResultVar(attributeMap), convertRelatedProductsToProducts(relatedProducts));
        return result;
    }

    protected List<Product> convertRelatedProductsToProducts(List<? extends PromotableProduct> relatedProducts) {
        List<Product> products = new ArrayList<Product>();
        if (relatedProducts != null) {
            for (PromotableProduct product : relatedProducts) {
                products.add(product.getRelatedProduct());
            }
        }
        return products;        
    }
    
    private String getRelatedProductsResultVar(Map<String, String> attributeMap) {
        String resultVar = attributeMap.get("relatedProductsResultVar");
        if (resultVar == null) {
            resultVar = "relatedProducts";
        }
        return resultVar;
    }
    
    private String getProductsResultVar(Map<String, String> attributeMap) {
        String resultVar = attributeMap.get("productsResultVar");
        if (resultVar == null) {
            resultVar = "products";
        }
        return resultVar;
    }

    private RelatedProductDTO buildDTO(Map<String, String> attributeMap, ITemplateContext context) {
        RelatedProductDTO relatedProductDTO = new RelatedProductDTO();
        String productIdStr = attributeMap.get("productId");
        String categoryIdStr = attributeMap.get("categoryId");
        String quantityStr = attributeMap.get("quantity");
        String typeStr = attributeMap.get("type");
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        if (productIdStr != null) {
            Object productId = expressionParser.parseExpression(context, productIdStr).execute(context);
            if (productId instanceof BigDecimal) {
                productId = new Long(((BigDecimal) productId).toPlainString());
            }
            relatedProductDTO.setProductId((Long) productId);
        }
        
        if (categoryIdStr != null) {
            Object categoryId = expressionParser.parseExpression(context, categoryIdStr).execute(context);
            if (categoryId instanceof BigDecimal) {
                categoryId = new Long(((BigDecimal) categoryId).toPlainString());
            }
            relatedProductDTO.setCategoryId((Long) categoryId);         
        }
        
        if (quantityStr != null) {
            relatedProductDTO.setQuantity(((BigDecimal) expressionParser.parseExpression(context, quantityStr).execute(context)).intValue());
        }       
                
        if (typeStr != null && RelatedProductTypeEnum.getInstance(typeStr) != null) {
            relatedProductDTO.setType(RelatedProductTypeEnum.getInstance(typeStr));         
        }
        
        if ("false".equalsIgnoreCase(attributeMap.get("cumulativeResults"))) {
            relatedProductDTO.setCumulativeResults(false);          
        }
                    
        return relatedProductDTO;
    }
}
