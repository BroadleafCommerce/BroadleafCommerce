/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.common.structure.dto.ItemCriteriaDTO;
import org.broadleafcommerce.common.structure.dto.StructuredContentDTO;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public class StructuredContentCartRuleProcessor extends AbstractCartRuleProcessor<StructuredContentDTO> {

    /**
     * Expects to find a valid "Customer" in the valueMap.
     * Uses the customer to locate the cart and then loops through the items in the current
     * cart and checks to see if the cart items rules are met.
     *
     * @param sc
     */
    @Override
    public boolean checkForMatch(StructuredContentDTO sc, Map<String, Object> valueMap) {
        List<ItemCriteriaDTO> itemCriterias = sc.getItemCriteriaDTOList();

        if (itemCriterias != null && itemCriterias.size() > 0) {
            Order order = lookupOrderForCustomer((Customer) valueMap.get("customer"));

            if (order == null || order.getOrderItems() == null || order.getOrderItems().size() < 1) {
                return false;
            }

            for (ItemCriteriaDTO itemCriteria : itemCriterias) {
                if (! checkItemCriteria(itemCriteria, order.getOrderItems())) {
                    // Item criteria check failed.
                    return false;
                }
            }
        }

        return true;
    }

}