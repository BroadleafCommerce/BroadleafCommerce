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

package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.domain.OrderItemAttributeImpl;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author apazzolini
 */
@Service("blOrderItemService")
public class OrderItemServiceImpl implements OrderItemService {

    @Resource(name = "blOrderItemDao")
    protected OrderItemDao orderItemDao;
    
	@Override
	public OrderItem readOrderItemById(Long orderItemId) {
        return orderItemDao.readOrderItemById(orderItemId);
	}

	@Override
	public void delete(OrderItem item) {
        orderItemDao.delete(item);
	}

	@Override
	public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemDao.saveOrderItem(orderItem);
	}
	
	@Override
    public DiscreteOrderItem createDiscreteOrderItem(Sku sku, Product product, Category category, int quantity, Map<String,String> itemAttributes) {
        DiscreteOrderItem item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.DISCRETE);
        item.setSku(sku);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setCategory(category);

        if (itemAttributes != null && itemAttributes.size() > 0) {
            Map<String,OrderItemAttribute> orderItemAttributes = new HashMap<String,OrderItemAttribute>();
            item.setOrderItemAttributes(orderItemAttributes);

            for (String key : itemAttributes.keySet()) {
                String value = itemAttributes.get(key);
                OrderItemAttribute attribute = new OrderItemAttributeImpl();
                attribute.setName(key);
                attribute.setValue(value);
                attribute.setOrderItem(item);
                orderItemAttributes.put(key, attribute);
            }
        }

        item.updatePrices();
        item.assignFinalPrice();
        return item;
    }


}
