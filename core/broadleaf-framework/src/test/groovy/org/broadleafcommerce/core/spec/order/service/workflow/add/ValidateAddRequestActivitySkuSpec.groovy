package org.broadleafcommerce.core.spec.order.service.workflow.add

import org.broadleafcommerce.core.catalog.domain.ProductImpl
import org.broadleafcommerce.core.catalog.domain.ProductOption
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref
import org.broadleafcommerce.core.catalog.domain.Sku
import org.broadleafcommerce.core.catalog.service.CatalogService
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.order.service.ProductOptionValidationService
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException
import org.broadleafcommerce.core.order.service.workflow.add.ValidateAddRequestActivity
import org.broadleafcommerce.core.workflow.ActivityMessages


/* findMatchingSku:
 * 1) product != null && product.getProductOptions().size() > 0
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
 * 2) product == null -> returns null
 *
 */
class ValidateAddRequestActivitySkuSpec extends BaseAddItemActivitySpec{

    OrderService mockOrderService = Mock()
    OrderItemService mockOrderItemService = Mock()
    CatalogService mockCatalogService = Mock()
    ProductOptionValidationService mockProductOptionValidationService = Mock()
    
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
        ProductOptionValidationStrategyType testStrategyType = Mock(ProductOptionValidationStrategyType)
        testStrategyType.getRank() >> ProductOptionValidationStrategyType.ADD_ITEM.getRank()
        
        ProductOption testProductOption = Mock(ProductOption)
        testProductOption.getRequired() >> true
        testProductOption.getProductOptionValidationStrategyType() >> testStrategyType
        
        ArrayList<ProductOptionXref> testProductOptions = new ArrayList<ProductOptionXref>()
        testProductOptions.add(testProductOption)
        
        Map<String, String> testAttributes = Mock(Map)
        testAttributes.get(_) >> ""
        
        ProductImpl testProduct = Spy(ProductImpl)
        testProduct.getProductOptions() >> testProductOptions
        
        when: "The activity tries to find a sku for the given product and attributes"
        Sku sku = activity.findMatchingSku(testProduct,testAttributes, (ActivityMessages)context)
        
        then: "A RequiredAttributeNotProvided Exception is thrown"
        RequiredAttributeNotProvidedException e = thrown()
        
    }
    
    def "If a productOption is used in sku generation, that value is considered when finding the right sku"(){
        setup: "Create a product that has a product option used in the sku generation"
        ProductOption testProductOption = Mock(ProductOption)
        testProductOption.getRequired() >> true
        testProductOption.getAttributeName() >> "name"
        testProductOption.getUseInSkuGeneration() >> true
        
        ProductOptionValidationStrategyType testStrategyType = Mock(ProductOptionValidationStrategyType)
        testStrategyType.getRank() >> ProductOptionValidationStrategyType.ADD_ITEM.getRank()
        testProductOption.getProductOptionValidationStrategyType() >> testStrategyType
        
        ArrayList<ProductOptionXref> testProductOptions = new ArrayList<ProductOptionXref>()
        testProductOptions.add(testProductOption)
        
        Map<String, String> testAttributes = Mock()
        testAttributes.get(_) >> "notempty"
        
        
        ProductOptionValue testProductOptionValue= Mock(ProductOptionValue)
        testProductOptionValue.getProductOption() >> testProductOption
        testProductOptionValue.getAttributeValue() >> "notempty"
        
        ArrayList<ProductOptionValue> testProductOptionValues = new ArrayList<ProductOptionValue>();
        testProductOptionValues.add(testProductOptionValue)
        
        Sku testSku = Mock(Sku)
        testSku.getProductOptionValues() >> testProductOptionValues
        
        ArrayList<Sku> testSkus = new ArrayList<Sku>()
        testSkus.add(testSku)
        
        ProductImpl testProduct = Spy(ProductImpl)
        testProduct.getProductOptions() >> testProductOptions
        testProduct.getSkus() >> testSkus
       
        when: "The activity tries to find a sku for the given product and attributes"
        Sku resultSku = activity.findMatchingSku(testProduct,testAttributes, (ActivityMessages)context)
        
        then: "A valid sku is returned"
        resultSku == testSku
    }
    
    def "If a productOption has a validation type that is non-null and rank <= ADD_ITEM rank, then validate is called"(){
        setup: "Create a product option with a non-null validation type and strategy-rank less then or equal to the default ADD_ITEM strategy rank"
        ProductOption testProductOption = Mock(ProductOption)
        testProductOption.getRequired() >> true
        testProductOption.getAttributeName() >> "name"
        testProductOption.getUseInSkuGeneration() >> true
        
        ProductOptionValidationStrategyType testStrategyType = Mock(ProductOptionValidationStrategyType)
        testStrategyType.getRank() >> ProductOptionValidationStrategyType.ADD_ITEM.getRank()
        testProductOption.getProductOptionValidationStrategyType() >> testStrategyType
        
        ArrayList<ProductOptionXref> testProductOptions = new ArrayList<ProductOptionXref>()
        testProductOptions.add(testProductOption)
        
        Map<String, String> testAttributes = Mock()
        testAttributes.get(_) >> "notempty"
        
        testProductOption.getProductOptionValidationType() >> ProductOptionValidationType.REGEX
       
        ProductImpl testProduct = Spy(ProductImpl)
        testProduct.getProductOptions() >> testProductOptions
        
        when: "The activity tries to find a sku for the given product and attributes"
        Sku resultSku = activity.findMatchingSku(testProduct,testAttributes, (ActivityMessages)context)
        
        then: "Then validate is called on the product option"
        1 * mockProductOptionValidationService.validate(*_)
    }
    
    def "If a productOption has strategy rank > ADD_ITEM rank, then validate is called"() {
        setup: "Create a product option with a strategy-rank greater than the default ADD_ITEM strategy-rank"
        ProductOption testProductOption = Mock(ProductOption)
        testProductOption.getRequired() >> true
        testProductOption.getAttributeName() >> "name"
        testProductOption.getUseInSkuGeneration() >> true
        
        ProductOptionValidationStrategyType testStrategyType = Mock(ProductOptionValidationStrategyType)
        testStrategyType.getRank() >> ProductOptionValidationStrategyType.ADD_ITEM.getRank()+1
        testProductOption.getProductOptionValidationStrategyType() >> testStrategyType
        
        ArrayList<ProductOptionXref> testProductOptions = new ArrayList<ProductOptionXref>()
        testProductOptions.add(testProductOption)
        
        Map<String, String> testAttributes = Mock()
        testAttributes.get(_) >> "notempty"
        
        ProductImpl testProduct = Spy(ProductImpl)
        testProduct.getProductOptions() >> testProductOptions
        
        when: "The activity tries to find a sku for the given product and attributes"
        Sku resultSku = activity.findMatchingSku(testProduct,testAttributes, (ActivityMessages)context)
        
        then: "Then validate is called on the product option"
        1 * mockProductOptionValidationService.validate(*_)
    }
}
