/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.core.order.service.legacy;

import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.legacy.LegacyBundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.legacy.LegacyDiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.legacy.LegacyGiftWrapOrderItemRequest;

import java.util.HashMap;

public interface LegacyOrderItemService extends OrderItemService {

    public DiscreteOrderItem createDiscreteOrderItem(LegacyDiscreteOrderItemRequest itemRequest);

    public GiftWrapOrderItem createGiftWrapOrderItem(LegacyGiftWrapOrderItemRequest itemRequest);

    /**
     * Used to create "manual" product bundles.   Manual product bundles are primarily designed
     * for grouping items in the cart display.    Typically ProductBundle will be used to
     * achieve non programmer related bundles.
     *
     *
     * @param itemRequest
     * @return
     */
    public BundleOrderItem createBundleOrderItem(LegacyBundleOrderItemRequest itemRequest);

    public DiscreteOrderItem createDynamicPriceDiscreteOrderItem(final LegacyDiscreteOrderItemRequest itemRequest, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations);

    public OrderItem readOrderItemById(Long orderItemId);

    public void delete(OrderItem item);

    public OrderItem saveOrderItem(OrderItem orderItem);
    
    public PersonalMessage createPersonalMessage();

}
