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

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.service.ProductOptionValidationService;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.workflow.ActivityMessages;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
@Component("blValidateProductOptionsActivity")
public class ValidateProductOptionsActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    public static final int ORDER = 2000;
    
    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    @Resource(name = "blProductOptionValidationService")
    protected ProductOptionValidationService productOptionValidationService;

    public ValidateProductOptionsActivity() {
        setOrder(ORDER);
    }
    
    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        if(!useSku) {
            Order order = context.getSeedData().getOrder();
            List<DiscreteOrderItem> orderItems = getOrderItems(order);

            for (DiscreteOrderItem discreteOI : orderItems) {
                Map<String, OrderItemAttribute> attributeValues = discreteOI.getOrderItemAttributes();
                Product product = discreteOI.getProduct();

                if (product != null) {
                    for (ProductOptionXref productOptionXref : ListUtils.emptyIfNull(product.getProductOptionXrefs())) {
                        ProductOption productOption = productOptionXref.getProductOption();
                        String attributeName = productOption.getAttributeName();
                        OrderItemAttribute attribute = attributeValues.get(attributeName);
                        String attributeValue = (attribute != null) ? attribute.getValue() : null;
                        boolean isRequired = productOption.getRequired();
                        boolean hasStrategy = productOptionValidationService.hasProductOptionValidationStrategy(productOption);
                        boolean isAddOrNoneType = productOptionValidationService.isAddOrNoneType(productOption);
                        boolean isSubmitType = productOptionValidationService.isSubmitType(productOption);

                        if (isMissingRequiredAttribute(isRequired, hasStrategy, isAddOrNoneType, isSubmitType, attributeValue)) {
                            String message = "Unable to validate cart, product  (" + product.getId() + ") required"
                                             + " attribute was not provided: " + attributeName;
                            throw new RequiredAttributeNotProvidedException(message, attributeName, String.valueOf(product.getId()));
                        }

                        boolean hasValidationType = productOption.getProductOptionValidationType() != null;

                        if (shouldValidateWithException(hasValidationType, hasStrategy, isAddOrNoneType, isSubmitType)) {
                            productOptionValidationService.validate(productOption, attributeValue);
                        }

                        if (hasStrategy && !(isAddOrNoneType || isSubmitType)) {
                            //we need to validate however, we will not error out
                            ActivityMessages messages = (ActivityMessages) context;
                            productOptionValidationService.validateWithoutException(productOption, attributeValue, messages);
                        }
                    }
                }
            }
        }

        return context;
    }

    protected List<DiscreteOrderItem> getOrderItems(Order order) {
        List<DiscreteOrderItem> orderItems = new ArrayList<>();

        for (OrderItem oi : order.getOrderItems()) {
            if (oi instanceof DiscreteOrderItem) {
                orderItems.add((DiscreteOrderItem) oi);
            } else if (oi instanceof BundleOrderItem) {
                orderItems.addAll(((BundleOrderItem) oi).getDiscreteOrderItems());
            }
        }

        return orderItems;
    }

    protected boolean shouldValidateWithException(boolean hasValidationType, boolean hasStrategy, boolean isAddOrNoneType, boolean isSubmitType) {
        boolean passesStrategyValidation = !hasStrategy || (isAddOrNoneType || isSubmitType);
        return hasValidationType && (passesStrategyValidation);
    }

    protected boolean isMissingRequiredAttribute(boolean isRequired, boolean hasStrategy, boolean isAddOrNoneType, boolean isSubmitType, String attributeValue) {
        boolean passesStrategyValidation = !hasStrategy || (hasStrategy && (isAddOrNoneType || isSubmitType));
        return isRequired && passesStrategyValidation && StringUtils.isEmpty(attributeValue);
    }

    public ProductOptionValidationStrategyType getProductOptionValidationStrategyType() {
        return ProductOptionValidationStrategyType.SUBMIT_ORDER;
    }
}
