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
package org.broadleafcommerce.core.order.service.manipulation;

import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderItemSplitContainer {
    
    protected OrderItem key;
    protected List<PromotableOrderItem> splitItems = new ArrayList<PromotableOrderItem>();
    
    public OrderItem getKey() {
        return key;
    }
    
    public void setKey(OrderItem key) {
        this.key = key;
    }
    
    public List<PromotableOrderItem> getSplitItems() {
        return splitItems;
    }
    
    public void setSplitItems(List<PromotableOrderItem> splitItems) {
        this.splitItems = splitItems;
    }

}
