/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.util.service;

import java.util.Map;

/**
 * Service capable of deleting old or defunct entities from the persistence layer (e.g. Carts and anonymous Customers)
 *
 * @author Jeff Fischer
 */
public interface ResourcePurgeService {

    /**
     * Execute a purge of carts from the persistence layer based on the configuration parameters. The default
     * implementation is capable of looking at any combination of name, status and creation date. Take a look
     * at {@link org.broadleafcommerce.core.order.service.OrderService#findCarts(String[],
     * org.broadleafcommerce.core.order.service.type.OrderStatus[], java.util.Date, Boolean, int, int)}
     * for more info on the default behavior.
     *
     * @param config Map of params used to drive the selection of carts to purge
     */
    void purgeCarts(Map<String, String> config);

    void purgeCustomers(final Map<String, String> config);

}
