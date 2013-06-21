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
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


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

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public RelatedProductProcessor() {
        super("related_products");
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    /**
     * Controller method for the processor that readies the service call and adds the results to the model.
     */
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        RelatedProductsService relatedProductsService = ProcessorUtils.getRelatedProductsService(arguments);
        List<? extends PromotableProduct> relatedProducts = relatedProductsService.findRelatedProducts(buildDTO(arguments, element));
        addToModel(arguments, getRelatedProductsResultVar(element), relatedProducts);
        addToModel(arguments, getProductsResultVar(element), convertRelatedProductsToProducts(relatedProducts));
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
    
    private String getRelatedProductsResultVar(Element element) {
        String resultVar = element.getAttributeValue("relatedProductsResultVar");       
        if (resultVar == null) {
            resultVar = "relatedProducts";
        }
        return resultVar;
    }
    
    private String getProductsResultVar(Element element) {
        String resultVar = element.getAttributeValue("productsResultVar");      
        if (resultVar == null) {
            resultVar = "products";
        }
        return resultVar;
    }

    private RelatedProductDTO buildDTO(Arguments args, Element element) {
        RelatedProductDTO relatedProductDTO = new RelatedProductDTO();
        String productIdStr = element.getAttributeValue("productId"); 
        String categoryIdStr = element.getAttributeValue("categoryId"); 
        String quantityStr = element.getAttributeValue("quantity"); 
        String typeStr = element.getAttributeValue("type"); 
        
        if (productIdStr != null) {
            Object productId = StandardExpressionProcessor.processExpression(args, productIdStr);
            if (productId instanceof BigDecimal) {
                productId = new Long(((BigDecimal) productId).toPlainString());
            }
            relatedProductDTO.setProductId((Long) productId);
        }
        
        if (categoryIdStr != null) {
            Object categoryId = StandardExpressionProcessor.processExpression(args, categoryIdStr);
            if (categoryId instanceof BigDecimal) {
                categoryId = new Long(((BigDecimal) categoryId).toPlainString());
            }
            relatedProductDTO.setCategoryId((Long) categoryId);         
        }
        
        if (quantityStr != null) {
            relatedProductDTO.setQuantity(((BigDecimal) StandardExpressionProcessor.processExpression(args, quantityStr)).intValue());          
        }       
                
        if (typeStr != null && RelatedProductTypeEnum.getInstance(typeStr) != null) {
            relatedProductDTO.setType(RelatedProductTypeEnum.getInstance(typeStr));         
        }
        
        if ("false".equalsIgnoreCase(element.getAttributeValue("cumulativeResults"))) {
            relatedProductDTO.setCumulativeResults(false);          
        }
                    
        return relatedProductDTO;
    }
}
