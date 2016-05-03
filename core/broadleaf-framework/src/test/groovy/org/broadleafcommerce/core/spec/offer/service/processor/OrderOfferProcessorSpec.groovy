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
package org.broadleafcommerce.core.spec.offer.service.processor

import org.apache.commons.collections.map.MultiValueMap
import org.broadleafcommerce.core.catalog.domain.CategoryImpl
import org.broadleafcommerce.core.catalog.domain.ProductAttribute
import org.broadleafcommerce.core.catalog.domain.ProductAttributeImpl
import org.broadleafcommerce.core.catalog.domain.ProductImpl
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessor
import org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessorImpl
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl
import spock.lang.Specification

/**
 * Intended to test the validity of various MVEL expressions using
 * {@link org.broadleafcommerce.core.offer.service.processor.AbstractBaseProcessor#executeExpression(java.lang.String, java.util.Map)}
 * @author Elbert Bautista (elbertbautista)
 */
class OrderOfferProcessorSpec extends Specification {

    def discreteOrderItem = new DiscreteOrderItemImpl();
    def mvDiscreteOrderItem = new DiscreteOrderItemImpl();

    def setup() {
        def category = new CategoryImpl()
        category.id = 1
        category.name = "Test Category"

        def sku = new SkuImpl()
        sku.id = 101
        sku.name = "Test SKU"

        def product = new ProductImpl()
        product.id = 100
        product.defaultSku = sku

        def productAttribute = new ProductAttributeImpl()
        productAttribute.id = 100
        productAttribute.product = product;
        productAttribute.name="myProductAttribute"
        productAttribute.value="myProductAttributeValue"

        def otherProductAttribute = new ProductAttributeImpl()
        otherProductAttribute.id = 200
        otherProductAttribute.product = product;
        otherProductAttribute.name="myOtherProductAttribute"
        otherProductAttribute.value="myOtherProductAttributeValue"
        Map<String, ProductAttribute> attributeMap = new HashMap<>();

        attributeMap.put("myProductAttribute", productAttribute)
        attributeMap.put("myOtherProductAttribute", otherProductAttribute)

        product.setProductAttributes(attributeMap)

        def mvCategory = new CategoryImpl()
        mvCategory.id = 1
        mvCategory.name = "Test Category"

        def mvSku = new SkuImpl()
        mvSku.id = 101
        mvSku.name = "Test SKU"

        def mvProduct = new ProductImpl()
        mvProduct.id = 100
        mvProduct.defaultSku = mvSku

        def mvProductAttribute = new ProductAttributeImpl()
        mvProductAttribute.id = 100
        mvProductAttribute.product = mvProduct;
        mvProductAttribute.name="myProductAttribute"
        mvProductAttribute.value="myProductAttributeValue"

        def mvOtherProductAttribute = new ProductAttributeImpl()
        mvOtherProductAttribute.id = 200
        mvOtherProductAttribute.product = mvProduct;
        mvOtherProductAttribute.name="myProductAttribute"
        mvOtherProductAttribute.value="myOtherProductAttributeValue"
        Map<String, ProductAttribute> mvAttributeMap = new MultiValueMap();

        mvAttributeMap.put("myProductAttribute", mvProductAttribute)
        mvAttributeMap.put("myProductAttribute", mvOtherProductAttribute)

        mvProduct.setProductAttributes(mvAttributeMap)

        discreteOrderItem.category = category
        discreteOrderItem.product = product
        discreteOrderItem.sku = sku
        discreteOrderItem.quantity = 5

        mvDiscreteOrderItem.category = mvCategory
        mvDiscreteOrderItem.product = mvProduct
        mvDiscreteOrderItem.sku = mvSku
        mvDiscreteOrderItem.quantity = 5
    }

    def "Test AbstractBaseProcessor MVEL executeExpression with different variables"() {
        setup: "Initialize the OrderOfferProcessor and test data"

        OrderOfferProcessor orderOfferProcessor = new OrderOfferProcessorImpl()
        Map<String, Object> ruleVars = new HashMap<>();
        ruleVars.put("orderItem", discreteOrderItem);
        ruleVars.put("discreteOrderItem", discreteOrderItem);

        Map<String, Object> mvRuleVars = new HashMap<>();
        mvRuleVars.put("orderItem", mvDiscreteOrderItem);
        mvRuleVars.put("discreteOrderItem", mvDiscreteOrderItem);

        when: "I run AbstractBaseProcessor.executeExpression"
        boolean catNameCorrect = orderOfferProcessor.executeExpression("MvelHelper.toUpperCase(orderItem.?category.?name)==MvelHelper.toUpperCase(\"test category\")", ruleVars);
        boolean catNameIncorrect = orderOfferProcessor.executeExpression("MvelHelper.toUpperCase(orderItem.?category.?name)==MvelHelper.toUpperCase(\"wrong\")", ruleVars);
        boolean containsCorrect = orderOfferProcessor.executeExpression("[\"-100\", \"100\", \"500\"].contains(discreteOrderItem.?product.?id.toString()) && orderItem.?category.?name == \"Test Category\"", ruleVars);
        boolean mapCorrect = orderOfferProcessor.executeExpression("orderItem.?product.?getProductAttributes().?get(\"myProductAttribute\").?getValue()==\"myProductAttributeValue\"", ruleVars);
        boolean mapIncorrect = orderOfferProcessor.executeExpression("orderItem.?product.?getProductAttributes().?get(\"myProductAttribute2\").?getValue()==\"myProductAttributeValue\"", ruleVars);
        boolean legacyMapCorrect = orderOfferProcessor.executeExpression("orderItem.?product.?getProductAttributes()[\"myProductAttribute\"]==\"myProductAttributeValue\"", ruleVars);
        boolean legacyMapIncorrect = orderOfferProcessor.executeExpression("orderItem.?product.?getProductAttributes()[\"myProductAttribute3\"]==\"myProductAttributeValue\"", ruleVars);
        boolean mvMapCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(orderItem.?product.?getMultiValueProductAttributes()[\"myProductAttribute\"],[\"myProductAttributeValue\",\"myOtherProductAttributeValue\"]).size()>0", mvRuleVars);
        boolean mvMapIncorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(orderItem.?product.?getMultiValueProductAttributes()[\"myProductAttribute4\"],[\"myProductAttributeValue\",\"myOtherProductAttributeValue\"]).size()>0", mvRuleVars);

        then: "The result of executing the expression should be as expected"
        catNameCorrect
        !catNameIncorrect
        containsCorrect
        mapCorrect
        !mapIncorrect
        legacyMapCorrect
        !legacyMapIncorrect
        mvMapCorrect
        !mvMapIncorrect
    }

    def "Test AbstractBaseProcessor MVEL executeExpression with different variables and custom MVEL imports"() {
        setup: "Initialize the OrderOfferProcessor and test data"

        OrderOfferProcessor orderOfferProcessor = new OrderOfferProcessorImpl()
        Map<String, Object> ruleVars = new HashMap<>();
        ArrayList<String> groupIds = new ArrayList<>();
        groupIds.add("100")
        groupIds.add("200")
        groupIds.add("300")
        groupIds.add("400")
        ruleVars.put("groupIds", groupIds);
        ruleVars.put("orderItem", discreteOrderItem);
        ruleVars.put("discreteOrderItem", discreteOrderItem);

        when: "I run AbstractBaseProcessor.executeExpression with mvel imports"
        boolean inExpressionCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(groupIds,[\"100\",\"300\"]).size()>0", ruleVars);
        boolean inALLExpressionCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(groupIds,[\"100\",\"300\"]).size()==2", ruleVars);
        boolean notInExpressionCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(groupIds,[\"-100\",\"-300\"]).size()==0", ruleVars);
        boolean containsUsingIntersectionCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection([discreteOrderItem.?product.?id.toString()], [\"-100\", \"100\", \"500\"]).size()>0 ", ruleVars);
        boolean legacyMapIntersectionCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(orderItem.?product.?getProductAttributes()[\"myProductAttribute\"], [\"myProductAttributeValue\", \"myOtherProductAttributeValue\"]).size()>0 ", ruleVars);
        boolean legacyMapIntersectionIncorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(orderItem.?product.?getProductAttributes()[\"myProductAttribute\"], [\"yourProductAttributeValue\", \"yourOtherProductAttributeValue\"]).size()>0 ", ruleVars);

        then: "The result of executing the expression should be as expected"
        inExpressionCorrect
        inALLExpressionCorrect
        notInExpressionCorrect
        containsUsingIntersectionCorrect
        legacyMapIntersectionCorrect
        !legacyMapIntersectionIncorrect


    }
}
