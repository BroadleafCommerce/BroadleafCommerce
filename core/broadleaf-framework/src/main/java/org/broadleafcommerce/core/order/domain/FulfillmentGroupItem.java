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

package org.broadleafcommerce.core.order.domain;

import java.io.Serializable;

import org.broadleafcommerce.money.Money;

public interface FulfillmentGroupItem extends Serializable {

    Long getId();

    void setId(Long id);

    FulfillmentGroup getFulfillmentGroup();

    void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    OrderItem getOrderItem();

    void setOrderItem(OrderItem orderItem);

    int getQuantity();

    void setQuantity(int quantity);

    Money getRetailPrice();

    Money getSalePrice();

    Money getPrice();

    String getStatus();

    void setStatus(String status);
    
    public void removeAssociations();
    
}
