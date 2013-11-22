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


public class OrderItemRequest extends AbstractOrderItemRequest {

    protected String itemName;

    public OrderItemRequest() {
        super();
    }

    public OrderItemRequest(AbstractOrderItemRequest request) {
        setPersonalMessage(request.getPersonalMessage());
        setQuantity(request.getQuantity());
        setOrder(request.getOrder());
        setSalePriceOverride(request.getSalePriceOverride());
        setRetailPriceOverride(request.getRetailPriceOverride());
    }

    @Override
    public OrderItemRequest clone() {
        OrderItemRequest returnRequest = new OrderItemRequest();
        copyProperties(returnRequest);
        returnRequest.setItemName(itemName);
        return returnRequest;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}
