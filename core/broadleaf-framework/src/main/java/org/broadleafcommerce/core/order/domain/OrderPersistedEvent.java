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

package org.broadleafcommerce.core.order.domain;

import org.springframework.context.ApplicationEvent;


/**
 * An event for whenever an {@link OrderImpl} has been persisted
 *
 * @author Phillip Verheyden (phillipuniverse)
 * 
 * @see {@link OrderPersistedEntityListener}
 */
public class OrderPersistedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    /**
     * @param order the newly persisted customer
     */
    public OrderPersistedEvent(Order order) {
        super(order);
    }
    
    /**
     * Gets the newly-persisted {@link Order} set by the {@link OrderPersistedEntityListener}
     * 
     * @return
     */
    public Order getOrder() {
        return (Order)source;
    }

}
