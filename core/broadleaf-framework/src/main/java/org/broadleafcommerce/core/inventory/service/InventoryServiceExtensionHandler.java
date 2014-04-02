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
package org.broadleafcommerce.core.inventory.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.checkout.service.workflow.DecrementInventoryActivity;
import org.broadleafcommerce.core.order.service.workflow.CheckAvailabilityActivity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;



/**
 * Marker interface to dictate the overridden methods within {@link ContextualInventoryService}. Usually, implementers
 * will want to only override the {@link ContextualInventoryService} methods rather than all of the methods included
 * in {@link InventoryService} and so you will extend from {@link AbstractInventoryServiceExtensionHandler}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link ContextualInventoryService}
 * @see {@link AbstractInventoryServiceExtensionHandler}
 */
public interface InventoryServiceExtensionHandler extends ExtensionHandler {

    /**
     * Usually invoked within the {@link CheckAvailabilityActivity} to retrieve the quantity that is available for the given
     * <b>skus</b>.
     * 
     * @param context can be null. If not null, this should at least contain the {@link ContextualInventoryService#ORDER_KEY}
     * @see {@link ContextualInventoryService#retrieveQuantitiesAvailable(Set, Map)}
     */
    public ExtensionResultStatusType retrieveQuantitiesAvailable(Collection<Sku> skus, Map<String, Object> context, ExtensionResultHolder<Map<Sku, Integer>> result);
    
    /**
     * Usually invoked within the {@link DecrementInventoryActivity} to decrement inventory for the {@link Sku}s that are in
     * <b>skuQuantities</b>
     * 
     * @param context can be null. If not null, this should at least contain the {@link ContextualInventoryService#ORDER_KEY} and/or the
     * {@link ContextualInventoryService#ROLLBACK_STATE_KEY}
     * @see {@link ContextualInventoryService#decrementInventory(Map, Map)}
     */
    public ExtensionResultStatusType decrementInventory(Map<Sku, Integer> skuQuantities, Map<String, Object> context) throws InventoryUnavailableException;

    /**
     * @param context can be null. If not null, this should at least contain the {@link ContextualInventoryService#ROLLBACK_STATE_KEY}
     * @see {@link ContextualInventoryService#incrementInventory(Map, Map)}
     */
    public ExtensionResultStatusType incrementInventory(Map<Sku, Integer> skuQuantities, Map<String, Object> context);
    
}
