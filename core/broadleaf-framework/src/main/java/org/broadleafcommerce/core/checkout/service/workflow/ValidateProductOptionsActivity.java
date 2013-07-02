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

package org.broadleafcommerce.core.checkout.service.workflow;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.ProductOptionValidationService;
import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.order.service.type.MessageType;
import org.broadleafcommerce.core.workflow.ActivityMessages;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.util.Map;

import javax.annotation.Resource;

/**
 * This is an required activity to valiate if required product options are in the order.
 * 
 * @author Priyesh Patel
 *
 */
public class ValidateProductOptionsActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blProductOptionValidationService")
    protected ProductOptionValidationService productOptionValidationService;

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        Order order = context.getSeedData().getOrder();
        for (OrderItem i : order.getOrderItems()) {
            Map<String, OrderItemAttribute> attributeValues = i.getOrderItemAttributes();
            Product product = ((DiscreteOrderItem) i).getProduct();
            if (product != null && product.getProductOptions() != null && product.getProductOptions().size() > 0) {
                for (ProductOption productOption : product.getProductOptions()) {
                    if (productOption.getRequired() && (productOption.getProductOptionValidationStrategyType() == null ||
                            productOption.getProductOptionValidationStrategyType().getRank() <= ProductOptionValidationStrategyType.SUBMIT_ORDER.getRank())) {
                        if (attributeValues.get(productOption.getAttributeName()) == null || StringUtils.isEmpty(attributeValues.get(productOption.getAttributeName()).getValue())) {
                            throw new RequiredAttributeNotProvidedException("Unable to submit cart, product  (" + product.getId() + ") required attribute was not provided: " + productOption.getAttributeName());
                        }
                    }
                    if (productOption.getProductOptionValidationType() != null && (productOption.getProductOptionValidationStrategyType() == null || productOption.getProductOptionValidationStrategyType().getRank() <= ProductOptionValidationStrategyType.SUBMIT_ORDER.getRank())) {
                        productOptionValidationService.validate(productOption, attributeValues.get(productOption.getAttributeName()).getValue());
                    }
                    if ((productOption.getProductOptionValidationStrategyType() != null && productOption.getProductOptionValidationStrategyType().getRank() > ProductOptionValidationStrategyType.SUBMIT_ORDER.getRank()))
                    {
                        //we need to validate however, we will not error out since this message is 
                        try {
                            productOptionValidationService.validate(productOption, (attributeValues.get(productOption.getAttributeName()) != null) ? attributeValues.get(productOption.getAttributeName()).getValue() : null);
                        } catch (ProductOptionValidationException e) {
                            ActivityMessageDTO msg = new ActivityMessageDTO(MessageType.PRODUCT_OPTION, 1, e.getMessage());

                            ((ActivityMessages) context).getActivityMessages().add(msg);
                        }

                    }
                }

            }
        }
        return context;
    }

}
