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
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.ConfigurableOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.ProductBundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.type.OrderStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface OrderItemService {

    OrderItem readOrderItemById(Long orderItemId);

    OrderItem saveOrderItem(OrderItem orderItem);

    void delete(OrderItem item);

    PersonalMessage createPersonalMessage();

    DiscreteOrderItem createDiscreteOrderItem(DiscreteOrderItemRequest itemRequest);

    DiscreteOrderItem createDynamicPriceDiscreteOrderItem(final DiscreteOrderItemRequest itemRequest, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations);

    GiftWrapOrderItem createGiftWrapOrderItem(GiftWrapOrderItemRequest itemRequest);

    /**
     * Used to create "manual" product bundles.   Manual product bundles are primarily designed
     * for grouping items in the cart display.    Typically ProductBundle will be used to
     * achieve non programmer related bundles.
     *
     * @param itemRequest
     * @return
     */
    BundleOrderItem createBundleOrderItem(BundleOrderItemRequest itemRequest);

    BundleOrderItem createBundleOrderItem(ProductBundleOrderItemRequest itemRequest);

    BundleOrderItem createBundleOrderItem(ProductBundleOrderItemRequest itemRequest, boolean saveItem);

    /**
     * Creates an OrderItemRequestDTO object that most closely resembles the given OrderItem.
     * That is, it will copy the SKU and quantity and attempt to copy the product and category
     * if they exist.
     *
     * @param item the item to copy
     * @return the OrderItemRequestDTO that mirrors the item
     */
    OrderItemRequestDTO buildOrderItemRequestDTOFromOrderItem(OrderItem item);

    OrderItem updateDiscreteOrderItem(OrderItem orderItem, DiscreteOrderItemRequest itemRequest);

    OrderItem createOrderItem(OrderItemRequest itemRequest);

    OrderItem buildOrderItemFromDTO(Order order, OrderItemRequestDTO orderItemRequestDTO);

    void priceOrderItem(OrderItem item);

    Set<Product> findAllProductsInRequest(ConfigurableOrderItemRequest itemRequest);

    void applyAdditionalOrderItemProperties(OrderItem orderItem);

    ConfigurableOrderItemRequest createConfigurableOrderItemRequestFromProduct(Product product);

    void modifyOrderItemRequest(ConfigurableOrderItemRequest itemRequest);

    void mergeOrderItemRequest(ConfigurableOrderItemRequest itemRequest, OrderItem orderItem);

    List<OrderItem> findOrderItemsForCustomersInDateRange(List<Long> customerIds, Date startDate, Date endDate);

    List<OrderItem> readBatchOrderItems(int start, int count, List<OrderStatus> orderStatusList);

    Long readNumberOfOrderItems();

}
