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
package org.broadleafcommerce.order.dao;

import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PersonalMessage;
import org.broadleafcommerce.order.service.type.OrderItemType;

public interface OrderItemDao {

    public OrderItem readOrderItemById(Long orderItemId);

    public OrderItem save(OrderItem orderItem);

    public void delete(OrderItem orderItem);

    public OrderItem create(OrderItemType orderItemType);

    public OrderItem saveOrderItem(OrderItem orderItem);

    public PersonalMessage createPersonalMessage();

}
