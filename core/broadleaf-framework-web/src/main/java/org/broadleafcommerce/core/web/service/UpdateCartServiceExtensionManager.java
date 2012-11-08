/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.core.web.service;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;

import java.util.List;


/**
 * @author Andre Azzolini (apazzolini)
 */
public class UpdateCartServiceExtensionManager implements UpdateCartServiceExtensionListener {
    
    protected List<UpdateCartServiceExtensionListener> listeners;

    @Override
    public void validateCart(Order cart) {
        for (UpdateCartServiceExtensionListener listener : listeners) {
            listener.validateCart(cart);
        }
    }
    
    @Override
    public Boolean isAvailable(DiscreteOrderItem doi, BroadleafCurrency currency) {
        boolean available = true;
        for (UpdateCartServiceExtensionListener listener : listeners) {
            available = available && listener.isAvailable(doi, currency);
        }
        return available;
    }
    
    public List<UpdateCartServiceExtensionListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<UpdateCartServiceExtensionListener> listeners) {
        this.listeners = listeners;
    }


}
