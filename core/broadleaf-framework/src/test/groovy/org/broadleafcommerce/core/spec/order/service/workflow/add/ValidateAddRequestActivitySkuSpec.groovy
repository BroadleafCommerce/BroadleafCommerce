/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.spec.order.service.workflow.add

import org.broadleafcommerce.core.catalog.domain.ProductImpl
import org.broadleafcommerce.core.catalog.domain.ProductOption
import org.broadleafcommerce.core.catalog.domain.ProductOptionImpl
import org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl
import org.broadleafcommerce.core.catalog.domain.ProductOptionXrefImpl
import org.broadleafcommerce.core.catalog.domain.Sku
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.catalog.domain.SkuProductOptionValueXref
import org.broadleafcommerce.core.catalog.domain.SkuProductOptionValueXrefImpl
import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.order.service.ProductOptionValidationService
import org.broadleafcommerce.core.order.service.ProductOptionValidationServiceImpl
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException
import org.broadleafcommerce.core.order.service.workflow.add.ValidateAddRequestActivity
import org.broadleafcommerce.core.workflow.ActivityMessages

/**
 *  findMatchingSku:
 * <ol>
 * <li> product != null && product.getProductOptions().size() > 0
 *  a) for each productOption
 *      i) productOption.getRequired() && productOptionValidationStrategyType == null || rank <= ADD_ITEM.rank
 *          aa) attributeValues.get(productOption.getAttributeName()) == null
 *              -> throw new RequiredAttributeNotProvidedException
 *          bb) productOption.getUseInSkuGeneration()
 *              -> attributeValuesForSku.put(productOption.getAttributeName(),...)
 *          -> productOptionValidationService.validate(_) fails if
 *              i) productOption.getProductOptionValidationType() == ProductOptionValidationType.REGEX
 *               && (productOption.getValidationString() == null || Pattern.matches == false)
 *               -> throw ProductOptionValidationException
 *          -> passed so far then...
 *              i) product.getSkus() != null
 *                  -> checkSkuForMatch for each sku in product with attributeValuesForSku
 *                      found -> return sku
 *                      not found -> return null
 *      ii) !productOption.getRequired()
 *          -> validate is not called initially
 *          -> validationStrategy rank > ADD_ITEM rank
 *              -> validate called
 *                  if it throws exception -> (ActivityMessages)context .getActivityMessages().add(msg)
 *              sku found -> return sku
 *              sku not found -> return null
 *
 * <li> product == null -> returns null
 * </ol>
 * @author Nick Crum (ncrum)
 */
class ValidateAddRequestActivitySkuSpec extends BaseAddItemActivitySpec{

    OrderService mockOrderService = Mock()
    OrderItemService mockOrderItemService = Mock()
    CatalogService mockCatalogService = Mock()
    ProductOptionValidationService mockProductOptionValidationService = Spy(ProductOptionValidationServiceImpl) {
        findSkuIdsForProductOptionValues(*_) >> []
    }
    
    def setup() {
        activity = Spy(ValidateAddRequestActivity).with {
            orderService = mockOrderService
            orderItemService = mockOrderItemService
            catalogService = mockCatalogService
            productOptionValidationService = mockProductOptionValidationService
            it
        }
    }
    
    
    def "Test that when a null product is given, null is returned"(){
        
        when: "The activity is told to find a matching sku for a null product"
        Sku sku = activity.findMatchingSku(null,context.seedData.itemRequest.getItemAttributes(),(ActivityMessages)context)
        
        then: "No sku is found and it returns null"
        sku == null
    }
    
    def "If a product has a required productOption and no attribute, an exception is thrown"(){
        
        setup: "Create a product that has no attribute for a required product option"
        
        ProductImpl testProduct = new ProductImpl().with {
            productOptionXrefs = [new ProductOptionXrefImpl().with {
                productOption = new ProductOptionImpl().with {
                    required = true
                    productOptionValidationStrategyType = ProductOptionValidationStrategyType.ADD_ITEM
                    it
                }
                it
            }]
            it
        }
        
        when: "The activity tries to find a sku for the given product and attributes"
        Map<String, String> testAttributes = [:]
        Sku sku = activity.findMatchingSku(testProduct,testAttributes, (ActivityMessages)context)
        
        then: "A RequiredAttributeNotProvided Exception is thrown"
        RequiredAttributeNotProvidedException e = thrown()
        
    }
    
    def "If a productOption is used in sku generation, that value is considered when finding the right sku"(){
        setup: "Create a product that has a product option used in the sku generation"
        ProductOption testProductOption = new ProductOptionImpl().with {
            required = true
            attributeName = "name"
            useInSkuGeneration = true
            productOptionValidationStrategyType = ProductOptionValidationStrategyType.ADD_ITEM
            it
        }
        
        SkuProductOptionValueXref valueXref = new SkuProductOptionValueXrefImpl().with {
            productOptionValue = new ProductOptionValueImpl().with {
                productOption = testProductOption
                attributeValue = 'notempty'
                it
            }
            it
        }
        
        Sku testSku = new SkuImpl()
        testSku.id = 1l
        testSku.setProductOptionValueXrefs([valueXref] as Set)
        activity.productOptionValidationService = Spy(ProductOptionValidationServiceImpl) {
            findSkuIdsForProductOptionValues(*_) >> [1l]
        }
        activity.catalogService = Mock(CatalogService) {
            findSkuById(1l) >> testSku
        }
        
        ProductImpl testProduct = new ProductImpl().with {
            productOptionXrefs = [new ProductOptionXrefImpl().with {
                productOption = testProductOption
                it
            }]
            additionalSkus = [testSku]
            it
        }
        
       
        when: "The activity tries to find a sku for the given product and attributes"
        Map<String, String> testAttributes = [name : 'notempty']
        Sku resultSku = activity.findMatchingSku(testProduct,testAttributes, (ActivityMessages)context)
        
        then: "A valid sku is returned"
        resultSku == testSku
    }
    
    def "If a productOption has a validation type that is non-null and rank <= ADD_ITEM rank, then validate is called"(){
        setup: "Create a product option with a non-null validation type and strategy-rank less then or equal to the default ADD_ITEM strategy rank"
        ProductOption testProductOption = new ProductOptionImpl().with {
            required = true
            attributeName = "name"
            useInSkuGeneration = true
            productOptionValidationStrategyType = ProductOptionValidationStrategyType.ADD_ITEM
            productOptionValidationType = ProductOptionValidationType.REGEX
            it
        }
        
        ProductImpl testProduct = new ProductImpl().with {
            productOptionXrefs = [new ProductOptionXrefImpl().with {
                productOption = testProductOption
                it
            }]
            it
        }
        
        when: "The activity tries to find a sku for the given product and attributes"
        Map<String, String> testAttributes = [name: 'notempty']
        Sku resultSku = activity.findMatchingSku(testProduct,testAttributes, (ActivityMessages)context)
        
        then: "Then validate is called on the product option"
        1 * mockProductOptionValidationService.validate(*_)
    }
    
    def "If a productOption has strategy rank > ADD_ITEM rank, then validate is called"() {
        setup: "Create a product option with a strategy-rank greater than the default ADD_ITEM strategy-rank"
        ProductOption testProductOption = new ProductOptionImpl().with {
            required = true
            attributeName = "name"
            useInSkuGeneration = true
            productOptionValidationStrategyType = CustomValidationStrategyType.ADD_ITEM
            productOptionValidationType = ProductOptionValidationType.REGEX
            it
        }
        
        ProductImpl testProduct = new ProductImpl().with {
            productOptionXrefs = [new ProductOptionXrefImpl().with {
                productOption = testProductOption
                it
            }]
            it
        }
        
        when: "The activity tries to find a sku for the given product and attributes"
        Map<String, String> testAttributes = [name: 'notempty']
        Sku resultSku = activity.findMatchingSku(testProduct,testAttributes, (ActivityMessages)context)
        
        then: "Then validate is called on the product option"
        1 * mockProductOptionValidationService.validate(*_)
    }
    
    public static class CustomValidationStrategyType extends ProductOptionValidationStrategyType {
        public static final ProductOptionValidationStrategyType ADD_ITEM = new ProductOptionValidationStrategyType("ADD_ITEM", ProductOptionValidationStrategyType.ADD_ITEM.getRank()+1, "Validate On Add Item")
    }
}
