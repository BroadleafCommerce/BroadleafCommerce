/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.spec.offer.service.processor

import org.apache.commons.collections4.CollectionUtils
import org.broadleafcommerce.core.catalog.domain.CategoryImpl
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

        discreteOrderItem.category = category
        discreteOrderItem.product = product
        discreteOrderItem.sku = sku
        discreteOrderItem.quantity = 5
    }

    def "Test AbstractBaseProcessor MVEL executeExpression with different variables"() {
        setup: "Initialize the OrderOfferProcessor and test data"

        OrderOfferProcessor orderOfferProcessor = new OrderOfferProcessorImpl()
        Map<String, Object> ruleVars = new HashMap<>();
        ruleVars.put("orderItem", discreteOrderItem);
        ruleVars.put("discreteOrderItem", discreteOrderItem);

        when: "I run AbstractBaseProcessor.executeExpression"
        boolean catNameCorrect = orderOfferProcessor.executeExpression("MvelHelper.toUpperCase(orderItem.?category.?name)==MvelHelper.toUpperCase(\"test category\")", ruleVars);
        boolean catNameIncorrect = orderOfferProcessor.executeExpression("MvelHelper.toUpperCase(orderItem.?category.?name)==MvelHelper.toUpperCase(\"wrong\")", ruleVars);
        boolean containsCorrect = orderOfferProcessor.executeExpression("[\"-100\", \"100\", \"500\"].contains(discreteOrderItem.?product.?id.toString()) && orderItem.?category.?name == \"Test Category\"", ruleVars);

        then: "The result of executing the expression should be as expected"
        catNameCorrect
        !catNameIncorrect
        containsCorrect
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

        Map<String, Class> mvelImports = new HashMap<>()
        mvelImports.put("CollectionUtils", CollectionUtils.class);

        when: "I run AbstractBaseProcessor.executeExpression with mvel imports"
        boolean inExpressionCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(groupIds,[\"100\",\"300\"]).size()>0", ruleVars, mvelImports);
        boolean inALLExpressionCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(groupIds,[\"100\",\"300\"]).size()==2", ruleVars, mvelImports);
        boolean notInExpressionCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection(groupIds,[\"-100\",\"-300\"]).size()==0", ruleVars, mvelImports);
        boolean containsUsingIntersectionCorrect = orderOfferProcessor.executeExpression("CollectionUtils.intersection([discreteOrderItem.?product.?id.toString()], [\"-100\", \"100\", \"500\"]).size()>0 ", ruleVars, mvelImports);

        then: "The result of executing the expression should be as expected"
        inExpressionCorrect
        inALLExpressionCorrect
        notInExpressionCorrect
        containsUsingIntersectionCorrect


    }
}