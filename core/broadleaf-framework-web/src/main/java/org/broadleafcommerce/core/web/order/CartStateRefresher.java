/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.web.order;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderPersistedEntityListener;
import org.broadleafcommerce.core.order.domain.OrderPersistedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * {@link ApplicationListener} responsible for updating {@link CartState} with a new version that was persisted.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * 
 * @see {@link OrderPersistedEntityListener}
 * @see {@link OrderPersistedEvent}
 */
@Component("blCartStateRefresher")
public class CartStateRefresher implements ApplicationListener<OrderPersistedEvent> {

    /**
     * Resets {@link CartState} with the newly persisted Order. This ensures that whatever is returned from
     * {@link CartState#getCart()} will always be the most up-to-date database version (meaning, safe to write to the DB).
     */
    @Override
    public void onApplicationEvent(final OrderPersistedEvent event) {
        Order dbOrder = event.getOrder();
        //Update the cart state ONLY IF the IDs of the newly persisted order and whatever is already in CartState match
        //TODO: what if CartState.getCart() is null? Need to know if the order can become CartState
        if (CartState.getCart() != null && CartState.getCart().getId().equals(dbOrder.getId())) {
            CartState.setCart(dbOrder);
        }
    }

}
