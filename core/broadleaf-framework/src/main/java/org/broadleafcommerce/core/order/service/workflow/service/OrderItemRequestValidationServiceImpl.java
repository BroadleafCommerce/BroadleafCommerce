/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.service.workflow.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuMinOrderQuantity;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.service.ProductOptionValidationService;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.ActivityMessages;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

@Component("blOrderItemRequestValidationService")
public class OrderItemRequestValidationServiceImpl implements OrderItemRequestValidationService {

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blProductOptionValidationService")
    protected ProductOptionValidationService productOptionValidationService;

    @Autowired
    protected Environment env;

    @Override
    public boolean satisfiesMinQuantityCondition(OrderItemRequestDTO orderItemRequestDTO, ProcessContext<CartOperationRequest> context) {
        if (minOrderQuantityCheckIsEnabled()) {
            Sku sku = determineSku(orderItemRequestDTO, (ActivityMessages) context);

            if (sku instanceof SkuMinOrderQuantity) {
                boolean hasMinOrderQuantity = ((SkuMinOrderQuantity) sku).hasMinOrderQuantity();

                if (hasMinOrderQuantity) {
                    Integer requestedQuantity = orderItemRequestDTO.getQuantity();
                    Integer minOrderQuantity = ((SkuMinOrderQuantity) sku).getMinOrderQuantity();

                    return requestedQuantity >= minOrderQuantity;
                }
            }
        }

        return true;
    }

    @Override
    public Integer getMinQuantity(OrderItemRequestDTO orderItemRequestDTO, ProcessContext<CartOperationRequest> context) {
        if (minOrderQuantityCheckIsEnabled()) {
            Sku sku = determineSku(orderItemRequestDTO, (ActivityMessages) context);

            if (sku instanceof SkuMinOrderQuantity) {
                return ((SkuMinOrderQuantity) sku).getMinOrderQuantity();
            }
        }

        return 1;
    }

    @Override
    public Product determineProduct(OrderItemRequestDTO orderItemRequestDTO) {
        Product product = null;
        // Validate that if the user specified a productId, it is a legitimate productId
        if (orderItemRequestDTO.getProductId() != null) {
            product = catalogService.findProductById(orderItemRequestDTO.getProductId());

            if (product == null) {
                throw new IllegalArgumentException("Product was specified but no matching product was found with the productId ("
                                                   + orderItemRequestDTO.getProductId() + ")");
            }
        }

        return product;
    }

    @Override
    public Sku determineSku(OrderItemRequestDTO orderItemRequestDTO, ActivityMessages messages) throws RequiredAttributeNotProvidedException {
        Product product = determineProduct(orderItemRequestDTO);

        return determineSku(product, orderItemRequestDTO.getSkuId(), orderItemRequestDTO.getItemAttributes(), messages);
    }

    @Override
    public Sku determineSku(Product product, Long skuId, Map<String, String> attributeValues, ActivityMessages messages) throws RequiredAttributeNotProvidedException {
        Sku sku = null;

        //If sku browsing is enabled, product option data will not be available.
        if (!shouldUseSku()) {
            // Check whether the sku is correct given the product options.
            sku = findMatchingSku(product, attributeValues, messages);
        }

        if (sku == null && skuId != null) {
            sku = catalogService.findSkuById(skuId);
        }

        if (sku == null && product != null) {
            // Set to the default sku
            if (canSellDefaultSku(product)) {
                sku = product.getDefaultSku();
            } else {
                throw new RequiredAttributeNotProvidedException("Unable to find non-default sku matching given options and cannot sell default sku", null);
            }
        }

        return sku;
    }

    protected boolean canSellDefaultSku(Product product) {
        return CollectionUtils.isEmpty(product.getAdditionalSkus()) || product.getCanSellWithoutOptions();
    }

    protected Sku findMatchingSku(Product product, Map<String, String> attributeValues, ActivityMessages messages) throws RequiredAttributeNotProvidedException {
        Map<String, String> attributesRelevantToFindMatchingSku = new HashMap<>();

        // Verify that required product-option values were set.
        if (product != null) {
            for (ProductOptionXref productOptionXref : ListUtils.emptyIfNull(product.getProductOptionXrefs())) {
                ProductOption productOption = productOptionXref.getProductOption();
                String attributeName = productOption.getAttributeName();
                String attributeValue = attributeValues.get(attributeName);
                boolean isRequired = productOption.getRequired();
                boolean hasStrategy = productOptionValidationService.hasProductOptionValidationStrategy(productOption);
                boolean isAddOrNoneTypes = productOptionValidationService.isAddOrNoneType(productOption);

                if (shouldValidateWithException(isRequired, isAddOrNoneTypes, attributeValue, hasStrategy)) {
                    productOptionValidationService.validate(productOption, attributeValue);
                }

                if (hasStrategy && !isAddOrNoneTypes) {
                    // we need to validate; however, we will not error out
                    productOptionValidationService.validateWithoutException(productOption, attributeValue, messages);
                }

                if (productOption.getUseInSkuGeneration()) {
                    attributesRelevantToFindMatchingSku.put(attributeName, attributeValue);
                }
            }

            return findMatchingSku(product, attributesRelevantToFindMatchingSku);
        }

        return null;
    }

    protected boolean shouldValidateWithException(boolean isRequired, boolean isAddOrNoneType, String attributeValue, boolean hasStrategy) {
        return (!hasStrategy || isAddOrNoneType) && (isRequired || StringUtils.isNotEmpty(attributeValue));
    }

    protected Sku findMatchingSku(Product product, Map<String, String> attributeValuesForSku) {
        Sku matchingSku = null;
        List<Long> possibleSkuIds = new ArrayList<>();

        for (Entry<String, String> entry : MapUtils.emptyIfNull(attributeValuesForSku).entrySet()) {
            possibleSkuIds = productOptionValidationService.findSkuIdsForProductOptionValues(product.getId(), entry.getKey(), entry.getValue(), possibleSkuIds);

            if (CollectionUtils.isEmpty(possibleSkuIds)) {
                break;
            }
        }

        if (CollectionUtils.isNotEmpty(possibleSkuIds)) {
            matchingSku = catalogService.findSkuById(possibleSkuIds.iterator().next());
        }

        return matchingSku;
    }

    protected boolean minOrderQuantityCheckIsEnabled() {
        return env.getProperty("enable.sku.minOrderQuantity.field", boolean.class, false);
    }

    protected boolean shouldUseSku() {
        return env.getProperty("solr.index.use.sku", boolean.class, false);
    }

}
