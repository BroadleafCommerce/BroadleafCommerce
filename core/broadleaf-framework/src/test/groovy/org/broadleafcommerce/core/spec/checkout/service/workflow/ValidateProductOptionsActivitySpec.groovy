/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.core.catalog.domain.Product
import org.broadleafcommerce.core.catalog.domain.ProductImpl
import org.broadleafcommerce.core.catalog.domain.ProductOption
import org.broadleafcommerce.core.catalog.domain.ProductOptionImpl
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref
import org.broadleafcommerce.core.catalog.domain.ProductOptionXrefImpl
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType
import org.broadleafcommerce.core.checkout.service.workflow.ValidateProductOptionsActivity
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl
import org.broadleafcommerce.core.order.domain.OrderItem
import org.broadleafcommerce.core.order.domain.OrderItemAttributeImpl
import org.broadleafcommerce.core.order.service.ProductOptionValidationService
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException
import org.broadleafcommerce.core.workflow.ActivityMessages


/**
 * 
 * 
 * @author Austin Rooke
 *
 */

class ValidateProductOptionsActivitySpec extends BaseCheckoutActivitySpec {

    ProductOptionValidationService mockProductOptionValidationService
    DiscreteOrderItem orderItem
    ProductOption productOption
    ProductOptionXref productOptionXref
    Product product
    List<ProductOptionXref> productOptions
    List<OrderItem> orderItems

    def setup() {
        orderItem = new DiscreteOrderItemImpl()
        productOption = new ProductOptionImpl()
        productOptionXref = new ProductOptionXrefImpl()
        product = new ProductImpl()
        productOptions = new ArrayList()
        orderItems = new ArrayList()

        mockProductOptionValidationService = Mock()
    }

    def "Test that validation is skipped when useSku is set"() {
        setup:
        activity = new ValidateProductOptionsActivity().with {
            useSku = true
            productOptionValidationService = mockProductOptionValidationService
            it
        }

        when: "I execute the ValidateProductOptionsActivity"
        context = activity.execute(context)

        then: "No validation steps should be taken, and the ProductOptionValidationService should never be used"
        0 * mockProductOptionValidationService._
    }

    def "Test that exception is thrown when attributeValues for a DiscreteOrder Item when ProductOptions are required are not provided"() {
        setup:



        productOption.setRequired(true)
        productOption.setProductOptionValidationStrategyType(null)
        productOptionXref.setProductOption(productOption)
        productOptions << productOptionXref


        product.setProductOptionXrefs(productOptions)
        orderItem.setProduct(product)
        orderItem.setOrderItemAttributes(new HashMap())
        orderItems << orderItem
        context.seedData.order.setOrderItems(orderItems)




        activity = new ValidateProductOptionsActivity().with {
            useSku = false
            productOptionValidationService = mockProductOptionValidationService
            it
        }

        when: "I execute the ValidateProductOptionsActivity"
        context = activity.execute(context)

        then: "RequiredAttributeNotProvidedException is thrown"
        thrown(RequiredAttributeNotProvidedException)
    }

    def "Test that ProductOptionValidationService.validate is called when ProductOptionValidationType is provided but the ProductOptionValidationStrategy is either not provided or lower than SUBMIT_ORDER in rank"() {
        setup:

        productOption.setRequired(false)
        productOption.setAttributeName("blah")
        productOption.setProductOptionValidationStrategyType(null)
        productOption.setProductOptionValidationType(new ProductOptionValidationType("1","1"))
        productOptionXref.setProductOption(productOption)
        productOptions << productOptionXref

        product.setProductOptionXrefs(productOptions)
        orderItem.setProduct(product)
        Map orderItemAttributes = new HashMap()
        orderItemAttributes.put("blah",new OrderItemAttributeImpl())
        orderItem.setOrderItemAttributes(orderItemAttributes)
        orderItems << orderItem

        mockProductOptionValidationService.validate(_, _) >> true

        context.seedData.order.setOrderItems(orderItems)

        activity = new ValidateProductOptionsActivity().with {
            useSku = false
            productOptionValidationService = mockProductOptionValidationService
            it
        }

        when: "I execute the ValidateProductOptionsActivity"
        context = activity.execute(context)

        then: "ProductOptionValidationService.validate() method is called once"
        1 * mockProductOptionValidationService.validate(*_)
    }

    def "Test that an ActivityMessageDTO is added to the context when a ProductOption has been provided a ProductOptionValidationType and a ProductOptionValidationStrategyType rank greater than SUBMIT_ORDER"() {
        setup:

        productOption.setRequired(false)
        productOption.setProductOptionValidationStrategyType(new ProductOptionValidationStrategyType("1", 3000, "1"))
        productOption.setProductOptionValidationType(null)
        productOptionXref.setProductOption(productOption)
        productOptions << productOptionXref

        product.setProductOptionXrefs(productOptions)
        orderItem.setProduct(product)
        orderItem.setOrderItemAttributes(new HashMap())
        orderItems << orderItem

        context.seedData.order.setOrderItems(orderItems)

        mockProductOptionValidationService.validate(*_) >> { throw new ProductOptionValidationException() }


        activity = new ValidateProductOptionsActivity().with {
            useSku = false
            productOptionValidationService = mockProductOptionValidationService
            it
        }

        when: "I execute the ValidationProductOptionsActivity"
        context = activity.execute(context)

        then: "Context will have a new message in its ActivityMessages"
        ((ActivityMessages) context).getActivityMessages().size() == 1
    }
}
