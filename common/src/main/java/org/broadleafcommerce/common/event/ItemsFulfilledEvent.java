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

/**
 * Event that may be raised to indicate that items have been fulfilled.
 * 
 * @author Kelly Tisdell
 *
 */
public class ItemsFulfilledEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;

    protected final Map<Long, Integer> itemsAndQuantitiesFulfilled;

    public ItemsFulfilledEvent(Long fulfillmentGroupId, Map<Long, Integer> fulfilled,
            boolean asynchronous, ErrorHandler errorHandler) {
        super(fulfillmentGroupId, asynchronous, errorHandler);
        Assert.notNull(fulfillmentGroupId);
        Assert.notEmpty(fulfilled);
        this.itemsAndQuantitiesFulfilled = Collections.unmodifiableMap(fulfilled);
    }

    public ItemsFulfilledEvent(Long fulfillmentGroupId, Map<Long, Integer> fulfilled,
            boolean asynchronous) {
        super(fulfillmentGroupId, asynchronous);
        Assert.notNull(fulfillmentGroupId);
        Assert.notEmpty(fulfilled);
        this.itemsAndQuantitiesFulfilled = Collections.unmodifiableMap(fulfilled);
    }

    public ItemsFulfilledEvent(Long fulfillmentGroupId, ErrorHandler errorHandler,
            Map<Long, Integer> fulfilled) {
        super(fulfillmentGroupId, errorHandler);
        Assert.notNull(fulfillmentGroupId);
        Assert.notEmpty(fulfilled);
        this.itemsAndQuantitiesFulfilled = Collections.unmodifiableMap(fulfilled);
    }

    public ItemsFulfilledEvent(Long fulfillmentGroupId, Map<Long, Integer> fulfilled) {
        super(fulfillmentGroupId);
        Assert.notNull(fulfillmentGroupId);
        Assert.notEmpty(fulfilled);
        this.itemsAndQuantitiesFulfilled = Collections.unmodifiableMap(fulfilled);
    }

    public Long getFulfillmentGroupId() {
        return (Long) super.getSource();
    }

    public Map<Long, Integer> getItemsAndQuantitiesFulfilled() {
        return itemsAndQuantitiesFulfilled;
    }
}
