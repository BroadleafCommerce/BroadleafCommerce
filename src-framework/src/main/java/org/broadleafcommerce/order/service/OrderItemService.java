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
package org.broadleafcommerce.order.service;

import org.broadleafcommerce.order.domain.BundleOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PersonalMessage;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.order.service.call.OrderItemRequest;

public interface OrderItemService {

    public OrderItem createOrderItem(OrderItemRequest itemRequest);

    public DiscreteOrderItem createDiscreteOrderItem(DiscreteOrderItemRequest itemRequest);

    public GiftWrapOrderItem createGiftWrapOrderItem(GiftWrapOrderItemRequest itemRequest);

    public BundleOrderItem createBundleOrderItem(BundleOrderItemRequest itemRequest);

    public OrderItem readOrderItemById(Long orderItemId);

    public void delete(OrderItem item);

    public OrderItem saveOrderItem(OrderItem orderItem);

    public PersonalMessage createPersonalMessage();
}
