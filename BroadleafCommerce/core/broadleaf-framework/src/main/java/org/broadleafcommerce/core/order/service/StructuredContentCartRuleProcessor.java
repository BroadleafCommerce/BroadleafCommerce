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

package org.broadleafcommerce.core.order.service;

import java.util.*;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentItemCriteria;
import org.broadleafcommerce.cms.structure.service.AbstractStructuredContentRuleProcessor;
import org.broadleafcommerce.core.order.dao.OrderDao;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.profile.core.domain.Customer;

/**
 * Created by bpolster.
 */
public class StructuredContentCartRuleProcessor extends AbstractStructuredContentRuleProcessor {
    private OrderDao orderDao;

    /**
     * Expects to find a valid "Customer" in the valueMap.
     * Uses the customer to locate the cart and then loops through the items in the current
     * cart and checks to see if the cart items rules are met.
     *
     * @param sc
     * @return
     */
    @Override
    public boolean checkForMatch(StructuredContent sc, Map<String, Object> valueMap) {
        if (sc != null && sc.getStructuredContentMatchRules() != null) {
            Set<StructuredContentItemCriteria> itemCriteriaSet = sc.getQualifyingItemCriteria();

            if (itemCriteriaSet != null && itemCriteriaSet.size() > 0) {
                Order order = lookupOrderForCustomer((Customer) valueMap.get("customer"));

                if (order == null || order.getOrderItems() == null || order.getOrderItems().size() < 1) {
                    return false;
                }

                for(StructuredContentItemCriteria itemCriteria : itemCriteriaSet) {
                    if (! checkItemCriteria(itemCriteria, order.getOrderItems())) {
                        // Item criteria check failed.
                        return false;
                    }
                }

            }
        }


        return true;
    }

    private Order lookupOrderForCustomer(Customer c) {
        Order o = null;
        if (c != null) {
            o = orderDao.readCartForCustomer(c);
        }

        return o;
    }

    private boolean checkItemCriteria(StructuredContentItemCriteria itemCriteria, List<OrderItem> orderItems) {
        Map<String,Object> vars = new HashMap<String, Object>();
        int foundCount = 0;
        Iterator<OrderItem> items = orderItems.iterator();
        while (foundCount < itemCriteria.getQuantity() && items.hasNext()) {
            OrderItem currentItem = items.next();
            vars.put("discreteOrderItem", currentItem);
            boolean match = executeExpression(itemCriteria.getOrderItemMatchRule(), vars);

            if (match) {
                foundCount = foundCount + currentItem.getQuantity();
            }
        }
        return (foundCount >= itemCriteria.getQuantity().intValue());
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public OrderDao getOrderDao() {
        return orderDao;
    }
}
