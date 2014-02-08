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
package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.core.order.domain.Order;

import java.util.Map;

public class CheckoutSeed implements CheckoutResponse {

    protected Order order;
    protected final Map<String, Object> userDefinedFields;

    public CheckoutSeed(Order order, Map<String, Object> userDefinedFields) {
        this.order = order;
        this.userDefinedFields = userDefinedFields;
    }

    @Override
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }

    public Map<String, Object> getUserDefinedFields() {
        return userDefinedFields;
    }
}
