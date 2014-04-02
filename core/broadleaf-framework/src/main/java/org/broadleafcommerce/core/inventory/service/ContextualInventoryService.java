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

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.checkout.service.workflow.DecrementInventoryActivity;
import org.broadleafcommerce.core.checkout.service.workflow.DecrementInventoryRollbackHandler;
import org.broadleafcommerce.core.order.service.workflow.CheckAvailabilityActivity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Provides the same methods from {@link InventoryService} but with optional, additional context information. This context
 * can then be passed on to an {@link InventoryServiceExtensionHandler}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link InventoryService}
 * @see {@link InventoryServiceExtensionHandler}
 * @see {@link CheckAvailabilityActivity}
 * @see {@link DecrementInventoryActivity}
 */
public interface ContextualInventoryService extends InventoryService {
    
    /**
     * Used as a key in the context map methods below. This is used for the current order that should be used to evaluate
     * the methods below
     */
    public static String ORDER_KEY = "ORDER";

    /**
     * Used as a key in the context map methods below. This key is normally populated from the {@link DecrementInventoryActivity}
     * and is utilized in the {@link DecrementInventoryRollbackHandler}. This can be cast to a Map<String, Object> and is
     * designed such that when it is used, non-read operations (decrement and increment) can add what actually happened
     * so that it can be reversed.
     */
    public static String ROLLBACK_STATE_KEY = "ROLLBACK_STATE";
    
    /**
     * @param context can be null. If not null, this should at least contain the {@link #CART_CONTEXT_KEY}
     * @see {@link #retrieveQuantitiesAvailable(Set)}
     */
    public Integer retrieveQuantityAvailable(Sku sku, Map<String, Object> context);

    /**
     * @param context can be null. If not null, this should at least contain the {@link #CART_CONTEXT_KEY}
     * @see {@link #retrieveQuantitiesAvailable(Set)}
     */
    public Map<Sku, Integer> retrieveQuantitiesAvailable(Collection<Sku> skus, Map<String, Object> context);
    
    /**
     * @param context can be null. If not null, this should at least contain the {@link #CART_CONTEXT_KEY}
     * @see {@link #isAvailable(Sku, int)}
     */
    public boolean isAvailable(Sku sku, int quantity, Map<String, Object> context);
    
    /**
     * @param context can be null. If not null, this should at least contain the {@link #CHECKOUT_CONTEXT_KEY} and/or the
     * {@link #ROLLBACK_STATE_KEY}
     * @see {@link #decrementInventory(Sku, int)}
     */
    public void decrementInventory(Sku sku, int quantity, Map<String, Object> context) throws InventoryUnavailableException;

    /**
     * @param context can be null. If not null, this should at least contain the {@link #CHECKOUT_CONTEXT_KEY} and/or the
     * {@link #ROLLBACK_STATE_KEY}
     * @see {@link #decrementInventory(Map)}
     */
    public void decrementInventory(Map<Sku, Integer> skuQuantities, Map<String, Object> context) throws InventoryUnavailableException;

    /**
     * @param context can be null. If not null, this should at least contain the {@link #ROLLBACK_STATE_KEY}
     * @see {@link #incrementInventory(Sku, int)}
     */
    public void incrementInventory(Sku sku, int quantity, Map<String, Object> context);

    /**
     * @param context can be null. If not null, this should at least contain the {@link #ROLLBACK_STATE_KEY}
     * @see {@link #incrementInventory(Map)}
     */
    public void incrementInventory(Map<Sku, Integer> skuQuantities, Map<String, Object> context);

}
