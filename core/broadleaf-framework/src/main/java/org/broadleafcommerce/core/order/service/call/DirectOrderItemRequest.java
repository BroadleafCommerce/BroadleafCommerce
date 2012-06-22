/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.core.order.service.call;

import org.broadleafcommerce.core.order.domain.OrderItem;

import java.util.Map;

/**
 * @author apazzolini
 */
public class DirectOrderItemRequest extends OrderItemRequest {
	
	protected OrderItem orderItem;
	
    public OrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

	public Long getSkuId() {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public void setSkuId(Long skuId) {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public Long getCategoryId() {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public void setCategoryId(Long categoryId) {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public Long getProductId() {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public void setProductId(Long productId) {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public Integer getQuantity() {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public void setQuantity(Integer quantity) {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public Map<String, String> getItemAttributes() {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public void setItemAttributes(Map<String, String> itemAttributes) {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }
    
    public Long getOrderItemId() {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

    public void setOrderItemId(Long orderItemId) {
    	throw new UnsupportedOperationException("The DirectOrderItemRequest is only used when the full OrderItem already exists");
    }

}
