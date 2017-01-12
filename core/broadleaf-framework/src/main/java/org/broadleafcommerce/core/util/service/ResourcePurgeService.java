/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
