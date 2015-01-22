/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.event;

import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

import java.util.Collections;
import java.util.Map;

public class ItemsReturnedEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;

    protected final Map<Long, Integer> itemsAndQuantitiesReturned;

    public ItemsReturnedEvent(Long orderId, Map<Long, Integer> returnedItems,
            boolean asynchronous, ErrorHandler errorHandler) {
        super(orderId, asynchronous, errorHandler);
        Assert.notNull(orderId);
        Assert.notEmpty(returnedItems);
        this.itemsAndQuantitiesReturned = Collections.unmodifiableMap(returnedItems);
    }

    public ItemsReturnedEvent(Long orderId, Map<Long, Integer> returnedItems,
            boolean asynchronous) {
        super(orderId, asynchronous);
        Assert.notNull(orderId);
        Assert.notEmpty(returnedItems);
        this.itemsAndQuantitiesReturned = Collections.unmodifiableMap(returnedItems);
    }

    public ItemsReturnedEvent(Long orderId, ErrorHandler errorHandler,
            Map<Long, Integer> returnedItems) {
        super(orderId, errorHandler);
        Assert.notNull(orderId);
        Assert.notEmpty(returnedItems);
        this.itemsAndQuantitiesReturned = Collections.unmodifiableMap(returnedItems);
    }

    public ItemsReturnedEvent(Long orderId, Map<Long, Integer> returnedItems) {
        super(orderId);
        Assert.notNull(orderId);
        Assert.notEmpty(returnedItems);
        this.itemsAndQuantitiesReturned = Collections.unmodifiableMap(returnedItems);
    }

    public Long getOrderId() {
        return (Long) super.getSource();
    }

    public Map<Long, Integer> getItemsAndQuantitiesReturned() {
        return itemsAndQuantitiesReturned;
    }

}
