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
package org.broadleafcommerce.core.checkout.service.workflow;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.service.ProductOptionValidationService;
import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.order.service.type.MessageType;
import org.broadleafcommerce.core.workflow.ActivityMessages;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * This is an required activity to valiate if required product options are in the order.
 * 
 * If sku browsing is enabled, product option data will not be available.
 * In this case, the following validation is skipped.
 * 
 * @author Priyesh Patel
 *
 */
public class ValidateProductOptionsActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    @Resource(name = "blProductOptionValidationService")
    protected ProductOptionValidationService productOptionValidationService;


    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        if(useSku) {
            return context;
        }
        Order order = context.getSeedData().getOrder();
        List<DiscreteOrderItem> orderItems = new ArrayList<DiscreteOrderItem>();
        for (OrderItem i : order.getOrderItems()) {
            if (i instanceof DiscreteOrderItem) {
                orderItems.add((DiscreteOrderItem) i);
            } else if (i instanceof BundleOrderItem) {
                orderItems.addAll(((BundleOrderItem) i).getDiscreteOrderItems());
            } else
                continue;
        }
        for (DiscreteOrderItem i : orderItems) {
            Map<String, OrderItemAttribute> attributeValues = i.getOrderItemAttributes();
            Product product = i.getProduct();

            if (product != null && product.getProductOptions() != null && product.getProductOptions().size() > 0) {
                for (ProductOption productOption : product.getProductOptions()) {
                    if (productOption.getRequired() && (productOption.getProductOptionValidationStrategyType() == null ||
                            productOption.getProductOptionValidationStrategyType().getRank() <= getProductOptionValidationStrategyType().getRank())) {
                        if (attributeValues.get(productOption.getAttributeName()) == null || StringUtils.isEmpty(attributeValues.get(productOption.getAttributeName()).getValue())) {
                            throw new RequiredAttributeNotProvidedException("Unable to validate cart, product  (" + product.getId() + ") required attribute was not provided: " + productOption.getAttributeName(), productOption.getAttributeName());
                        }
                    }
                    if (productOption.getProductOptionValidationType() != null && (productOption.getProductOptionValidationStrategyType() == null || productOption.getProductOptionValidationStrategyType().getRank() <= getProductOptionValidationStrategyType().getRank())) {
                        productOptionValidationService.validate(productOption, attributeValues.get(productOption.getAttributeName()).getValue());
                    }
                    if ((productOption.getProductOptionValidationStrategyType() != null && productOption.getProductOptionValidationStrategyType().getRank() > getProductOptionValidationStrategyType().getRank()))
                    {
                        //we need to validate however, we will not error out since this message is 
                        try {
                            productOptionValidationService.validate(productOption, (attributeValues.get(productOption.getAttributeName()) != null) ? attributeValues.get(productOption.getAttributeName()).getValue() : null);
                        } catch (ProductOptionValidationException e) {
                            ActivityMessageDTO msg = new ActivityMessageDTO(MessageType.PRODUCT_OPTION.getType(), 1, e.getMessage());
                            msg.setErrorCode(productOption.getErrorCode());
                            ((ActivityMessages) context).getActivityMessages().add(msg);
                        }

                    }
                }

            }
        }
        return context;
    }

    public ProductOptionValidationStrategyType getProductOptionValidationStrategyType() {
        return ProductOptionValidationStrategyType.SUBMIT_ORDER;
    }


}
