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
package org.broadleafcommerce.order.web;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.Order;

/**
 * This class is used as a request-scope container for the current
 * orderid. As a result, items that need the order during the control
 * flow of a single request may retrieve the order from this object. OrderState
 * utilizes the DAO to retrieve the full order from its dehydrated state in the
 * Hibernate cache.
 * 
 * @author jfischer
 *
 */
public class OrderState {

    private Long orderId;

    @Resource(name="blOrderDao")
    private OrderDao orderDao;

    public Order getOrder() {
        if (orderId == null) {
            return null;
        }
        return orderDao.readOrderById(orderId);
    }

    public void setOrder(Order order) {
        if (order != null) {
            this.orderId = order.getId();
        }
    }

}
