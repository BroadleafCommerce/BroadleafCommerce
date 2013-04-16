/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.order.service.workflow.add;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.order.service.workflow.CartOperationContext;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.BaseActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

public class ValidateAddRequestActivity extends BaseActivity<CartOperationContext> {
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Override
    public CartOperationContext execute(CartOperationContext context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
        
        // Quantity was not specified or was equal to zero. We will not throw an exception,
        // but we will preven the workflow from continuing to execute
        if (orderItemRequestDTO.getQuantity() == null || orderItemRequestDTO.getQuantity() == 0) {
            context.stopProcess();
            return null;
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
        
        Sku sku = determineSku(product, orderItemRequestDTO.getSkuId(), orderItemRequestDTO.getItemAttributes());
        
        // If we couldn't find a sku, then we're unable to add to cart.
        if (sku == null) {

            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : orderItemRequestDTO.getItemAttributes().entrySet()) {
                sb.append(entry.toString());
            }
            throw new IllegalArgumentException("Could not find SKU for :" +
                    " productId: " + (product == null ? "null" : product.getId()) + 
                    " skuId: " + orderItemRequestDTO.getSkuId() + 
                    " attributes: " + sb.toString());
        } else if (!sku.isActive()) {
            throw new IllegalArgumentException("The requested skuId of " + sku.getId() + " is no longer active");
        } else {
            // We know definitively which sku we're going to add, so we can set this
            // value with certainty
            request.getItemRequest().setSkuId(sku.getId());
        }
        
        return context;
    }
    
    protected Sku determineSku(Product product, Long skuId, Map<String,String> attributeValues) {
        // Check whether the sku is correct given the product options.
        Sku sku = findMatchingSku(product, attributeValues);

        if (sku == null && skuId != null) {
            sku = catalogService.findSkuById(skuId);
        }

        if (sku == null && product != null) {
            // Set to the default sku
            if (product.getAdditionalSkus() != null && product.getAdditionalSkus().size() > 0 && !product.getCanSellWithoutOptions()) {
                throw new RequiredAttributeNotProvidedException("Unable to find non-default sku matching given options and cannot sell default sku");
            }
            sku = product.getDefaultSku();
        }
        return sku;
    }
    
    protected Sku findMatchingSku(Product product, Map<String,String> attributeValues) {
        Map<String, String> attributeValuesForSku = new HashMap<String,String>();
        // Verify that required product-option values were set.

        if (product != null && product.getProductOptions() != null && product.getProductOptions().size() > 0) {
            for (ProductOption productOption : product.getProductOptions()) {
                if (productOption.getRequired()) {
                    if (attributeValues.get(productOption.getAttributeName()) == null) {
                        throw new RequiredAttributeNotProvidedException("Unable to add to product ("+ product.getId() +") cart. Required attribute was not provided: " + productOption.getAttributeName());
                    } else {
                        attributeValuesForSku.put(productOption.getAttributeName(), attributeValues.get(productOption.getAttributeName()));
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
