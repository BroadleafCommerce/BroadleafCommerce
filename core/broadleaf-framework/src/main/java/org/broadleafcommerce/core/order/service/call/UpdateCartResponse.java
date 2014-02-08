/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.order.service.call;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateCartResponse implements Serializable {
    protected static final long serialVersionUID = 1L;

    protected Order order;

    protected List<OrderItem> removedItems = new ArrayList<OrderItem>();;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getRemovedItems() {
        return removedItems;
    }

    public void setRemovedItems(List<OrderItem> removedItems) {
        this.removedItems = removedItems;
    }

}
