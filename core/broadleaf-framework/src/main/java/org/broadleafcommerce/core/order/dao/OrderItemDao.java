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

package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.type.OrderItemType;

public interface OrderItemDao {

    OrderItem readOrderItemById(Long orderItemId);

    OrderItem save(OrderItem orderItem);

    void delete(OrderItem orderItem);

    OrderItem create(OrderItemType orderItemType);

    OrderItem saveOrderItem(OrderItem orderItem);
    
    PersonalMessage createPersonalMessage();

    OrderItemPriceDetail createOrderItemPriceDetail();

    OrderItemQualifier createOrderItemQualifier();

    /**
     * Sets the initial orderItemPriceDetail for the item.
     */
    OrderItemPriceDetail initializeOrderItemPriceDetails(OrderItem item);

}
