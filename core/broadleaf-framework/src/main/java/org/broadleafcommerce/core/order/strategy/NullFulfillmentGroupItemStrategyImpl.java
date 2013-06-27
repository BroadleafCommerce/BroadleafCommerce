/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.core.order.strategy;

import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;

/**
 * This class provides the implementation of a strategy that does not touch 
 * FulfillmentGroupItems when cart add or update operations have been performed.
 * However, the remove operation must still remove the FulfillmentGroupItems, and this
 * strategy will delegate to the default Broadleaf FulfillmentGroupItemStrategy to perform
 * the removal.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class NullFulfillmentGroupItemStrategyImpl extends FulfillmentGroupItemStrategyImpl {
    
    protected boolean removeEmptyFulfillmentGroups = false;
    
    @Override
    public CartOperationRequest onItemAdded(CartOperationRequest request) {
        return request;
    }
    
    @Override
    public CartOperationRequest onItemUpdated(CartOperationRequest request) {
        return request;
    }
    
    /** 
     * When we remove an order item, we must also remove the associated fulfillment group
     * item to respsect the database constraints.
     */
    @Override
    public CartOperationRequest onItemRemoved(CartOperationRequest request) {
        return super.onItemRemoved(request);
    }
    
    @Override
    public CartOperationRequest verify(CartOperationRequest request) {
        return request;
    }
    
    @Override
    public boolean isRemoveEmptyFulfillmentGroups() {
        return removeEmptyFulfillmentGroups;
    }

}
