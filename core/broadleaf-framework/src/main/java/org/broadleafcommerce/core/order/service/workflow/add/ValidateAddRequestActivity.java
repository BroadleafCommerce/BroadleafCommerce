/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.service.workflow.add;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.ProductOptionValidationService;
import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.order.service.type.MessageType;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.ActivityMessages;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

public class ValidateAddRequestActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blProductOptionValidationService")
    protected ProductOptionValidationService productOptionValidationService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
        
        // Quantity was not specified or was equal to zero. We will not throw an exception,
        // but we will preven the workflow from continuing to execute
        if (orderItemRequestDTO.getQuantity() == null || orderItemRequestDTO.getQuantity() == 0) {
            context.stopProcess();
            return context;
        }

        // Throw an exception if the user tried to add a negative quantity of something
        if (orderItemRequestDTO.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        // Throw an exception if the user did not specify an order to add the item to
        if (request.getOrder() == null) {
            throw new IllegalArgumentException("Order is required when adding item to order");
        }

        // Validate that if the user specified a productId, it is a legitimate productId
        Product product = null;
        if (orderItemRequestDTO.getProductId() != null) {
            product = catalogService.findProductById(orderItemRequestDTO.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Product was specified but no matching product was found for productId " + orderItemRequestDTO.getProductId());
            }
        }
        
        Sku sku = determineSku(product, orderItemRequestDTO.getSkuId(), orderItemRequestDTO.getItemAttributes(), (ActivityMessages) context);
        
        // If we couldn't find a sku, then we're unable to add to cart.
        if (sku == null && !(orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO)) {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : orderItemRequestDTO.getItemAttributes().entrySet()) {
                sb.append(entry.toString());
            }
            throw new IllegalArgumentException("Could not find SKU for :" +
                    " productId: " + (product == null ? "null" : product.getId()) + 
                    " skuId: " + orderItemRequestDTO.getSkuId() + 
                    " attributes: " + sb.toString());
        } else if (sku == null) {
            NonDiscreteOrderItemRequestDTO ndr = (NonDiscreteOrderItemRequestDTO) orderItemRequestDTO;
            if (StringUtils.isBlank(ndr.getItemName())) {
                throw new IllegalArgumentException("Item name is required for non discrete order item add requests");
            }
            
            if (ndr.getOverrideRetailPrice() == null && ndr.getOverrideSalePrice() == null) {
                throw new IllegalArgumentException("At least one override price is required for non discrete order item add requests");
            }
        } else if (!sku.isActive()) {
            throw new IllegalArgumentException("The requested skuId of " + sku.getId() + " is no longer active");
        } else {
            // We know definitively which sku we're going to add, so we can set this
            // value with certainty
            request.getItemRequest().setSkuId(sku.getId());
        }
        
        if (!(orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO) &&
                request.getOrder().getCurrency() != null && 
                sku.getCurrency() != null && 
                !request.getOrder().getCurrency().equals(sku.getCurrency())) {
            throw new IllegalArgumentException("Cannot have items with differing currencies in one cart");
        }
        
        // If the user has specified a parent order item to attach this to, it must exist in this cart
        if (orderItemRequestDTO.getParentOrderItemId() != null) {
            OrderItem parent = orderItemService.readOrderItemById(orderItemRequestDTO.getParentOrderItemId());
            if (parent == null) {
                throw new IllegalArgumentException("Could not find parent order item by the given id");
            }
        }
        
        return context;
    }
    
    protected Sku determineSku(Product product, Long skuId, Map<String, String> attributeValues, ActivityMessages messages) {
        // Check whether the sku is correct given the product options.
        Sku sku = findMatchingSku(product, attributeValues, messages);

        if (sku == null && skuId != null) {
            sku = catalogService.findSkuById(skuId);
        }

        if (sku == null && product != null) {
            // Set to the default sku
            if (product.getAdditionalSkus() != null && product.getAdditionalSkus().size() > 0 && !product.getCanSellWithoutOptions()) {
                throw new RequiredAttributeNotProvidedException("Unable to find non-default sku matching given options and cannot sell default sku", null);
            }
            sku = product.getDefaultSku();
        }
        return sku;
    }
    
    protected Sku findMatchingSku(Product product, Map<String, String> attributeValues, ActivityMessages messages) {
        Map<String, String> attributeValuesForSku = new HashMap<String,String>();
        // Verify that required product-option values were set.

        if (product != null && product.getProductOptions() != null && product.getProductOptions().size() > 0) {
            for (ProductOption productOption : product.getProductOptions()) {
                if (productOption.getRequired() && (productOption.getProductOptionValidationStrategyType() == null ||
                        productOption.getProductOptionValidationStrategyType().getRank() <= ProductOptionValidationStrategyType.ADD_ITEM.getRank())) {
                    if (StringUtils.isEmpty(attributeValues.get(productOption.getAttributeName()))) {
                        throw new RequiredAttributeNotProvidedException("Unable to add to product ("+ product.getId() +") cart. Required attribute was not provided: " + productOption.getAttributeName());
                    } else if (productOption.getUseInSkuGeneration()) {
                        attributeValuesForSku.put(productOption.getAttributeName(), attributeValues.get(productOption.getAttributeName()));
                    }
                }
                if (!productOption.getRequired() && StringUtils.isEmpty(attributeValues.get(productOption.getAttributeName()))) {
                    //if the productoption is not required, and user has not set the optional value, then we dont need to validate
                } else if (productOption.getProductOptionValidationType() != null && (productOption.getProductOptionValidationStrategyType() == null || productOption.getProductOptionValidationStrategyType().getRank() <= ProductOptionValidationStrategyType.ADD_ITEM.getRank())) {
                        productOptionValidationService.validate(productOption, attributeValues.get(productOption.getAttributeName()));
                }
                if((productOption.getProductOptionValidationStrategyType() != null && productOption.getProductOptionValidationStrategyType().getRank() > ProductOptionValidationStrategyType.ADD_ITEM.getRank()))
                {
                    //we need to validate however, we will not error out since this message is 
                    try {
                        productOptionValidationService.validate(productOption, attributeValues.get(productOption.getAttributeName()));
                    } catch (ProductOptionValidationException e) {
                        ActivityMessageDTO msg = new ActivityMessageDTO(MessageType.PRODUCT_OPTION.getType(), 1, e.getMessage());
                        msg.setErrorCode(productOption.getErrorCode());
                        messages.getActivityMessages().add(msg);
                    }
                    
                }
            }
            

            if (product !=null && product.getSkus() != null) {
                for (Sku sku : product.getSkus()) {
                   if (checkSkuForMatch(sku, attributeValuesForSku)) {
                       return sku;
                   }
                }
            }
        }

        return null;
    }

    protected boolean checkSkuForMatch(Sku sku, Map<String,String> attributeValues) {
        if (attributeValues == null || attributeValues.size() == 0) {
            return false;
        }

        for (String attributeName : attributeValues.keySet()) {
            boolean optionValueMatchFound = false;
            for (ProductOptionValue productOptionValue : sku.getProductOptionValues()) {
                if (productOptionValue.getProductOption().getAttributeName().equals(attributeName)) {
                    if (productOptionValue.getAttributeValue().equals(attributeValues.get(attributeName))) {
                        optionValueMatchFound = true;
                        break;
                    } else {
                        return false;
                    }
                }
            }

            if (optionValueMatchFound) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }
}
