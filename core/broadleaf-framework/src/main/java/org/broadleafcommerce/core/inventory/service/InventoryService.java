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
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.service.workflow.CheckAvailabilityActivity;

import java.util.Collection;
import java.util.Map;

/**
 * <p>This basic inventory service checks and adjusts the current inventory of a sku. All Skus will be considered 
 * generally unavailable from an inventory perspective if {@link Sku#isAvaliable()} returns false or if {@link Sku#isActive()}
 * returns false.</p>
 * 
 * <p>Skus with an InventoryType of null or 'ALWAYS_AVAILABLE' will be considered undefined from an inventory perspective, and will generally 
 * be considered available.  However, a request for available quantities of Skus with a null or 'ALWAYS_AVAILABLE' inventory type will 
 * return null (as the {@link Sku} is available but no inventory strategy is defined).</p>
 * 
 * <p>For most implementations outside of the very basic inventory case, you will actually want to use the {@link ContextualInventoryService}.
 * This is the version of the service that is invoked from the checkout workflow in {@link DecrementInventoryActivity} and
 * where the main checks for inventory are in the {@link CheckAvailabilityActivity}</p>
 * 
 * @author Kelly Tisdell
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface InventoryService {

    /**
     * <p>Retrieves the quantity available for a given <b>sku</b>. May return null if no inventory is maintained 
     * for the given <b>sku</b> when Sku.getInventoryType() == null
     * or the {@link InventoryType} of the given <b>sku</b> is {@link InventoryType#ALWAYS_AVAILABLE}. Effectively, if the quantity returned is null, inventory is 
     * undefined, which most likely means it is available.  However, rather than returning an arbitrary integer values (like Integer.MAX_VALUE), 
     * which has specific meaning, we return null as this can be interpreted by the client to mean whatever they define it as (including 
     * infinitely available), which is the most likely scenario.</p>
     * 
     * <p>In practice, this is a convenience method to wrap {@link #retrieveQuantitiesAvailable(Collection)}</p>
     * 
     * @param
     * @return <b>null</b> if there is no inventory strategy defined (meaning, {@link Sku#getInventoryType()} is null or
     * {@link InventoryType#ALWAYS_AVAILABLE}). Otherwise, this returns the quantity of the {@link Sku}
     * {@see ContextualInventoryService#retrieveQuantityAvailable(Sku, Map)}
     */
    public Integer retrieveQuantityAvailable(Sku sku);

    /**
     * <p>Retrieves the quantity available for a given <b>sku</b>. May return null if no inventory is maintained 
     * for the given <b>sku</b> when Sku.getInventoryType() == null
     * or the {@link InventoryType} of the given <b>sku</b> is {@link InventoryType#ALWAYS_AVAILABLE}. Effectively, if the quantity returned is null, inventory is 
     * undefined, which most likely means it is available.  However, rather than returning an arbitrary integer values (like Integer.MAX_VALUE), 
     * which has specific meaning, we return null as this can be interpreted by the client to mean whatever they define it as (including 
     * infinitely available), which is the most likely scenario.</p>
     * 
     * @param skus the set of {@link Sku}s to return inventory for
     * @return a map of the given set of <b>skus</b> to the quantity as represented in the inventory system. The {@link Map#keySet()}
     * is the same collection of given <b>skus</b>
     * @see {@link #retrieveQuantityAvailable(Sku)}
     * @see {@link ContextualInventoryService#retrieveQuantitiesAvailable(Collection, Map)}
     */
    public Map<Sku, Integer> retrieveQuantitiesAvailable(Collection<Sku> skus);
    
    /**
     * <p>Indicates whether the given quantity is available for the particular skuId. The result will be 
     * true if Sku.getInventoryType() == null or not Sku.getInventoryType().equals(InventoryType.ALWAYS_AVAILABLE).</p>
     * 
     * <p>The result will be false if the Sku is inactive, if the Sku.getAvaialable() == false, if the quantity 
     * field is null, or if the quantity requested exceeds the quantity available.</p>
     * 
     * @param sku the {@link Sku} to see if enough quantity is available
     * @param quantity the quantity to check for the given <b>sku<b>
     * @return <b>true</b> if there is available quantity
     * @see {@link ContextualInventoryService#isAvailable(Sku, int, Map)}
     */
    public boolean isAvailable(Sku sku, int quantity);
    
    /**
     * Without worrying about quantities, just checks to see if the given <b>Sku</b> is available. A {@link Sku} is
     * generally available if any of these is true:
     * <ol>
     *  <li>{@link Sku#getInventoryType()} is <b>null</b></li>
     *  <li>{@link Sku#getInventoryType()} is anything but {@link InventoryType#UNAVAILABLE}</b></li>
     *  <li>The now-deprecated {@link Sku#isAvailable()} is <b>true</b> or <b>null</b></li>
     * </ol>
     * 
     * <p>This will return true if {@link Sku#getInventoryType()} is {@link InventoryType#CHECK_QUANTITY}</p>
     * @param sku the {@link Sku} whose availability is being checked
     * @return <b>true</b> or <b>false</b> according to the rules above
     */
    public boolean checkBasicAvailablility(Sku sku);
    
    /**
     * <p>Attempts to decrement inventory if it is available. If the Sku is marked as {@link InventoryType#ALWAYS_AVAILABLE}
     * then this will be a no-op.</p>
     * 
     * <p>This method is a convenience method to wrap {@link #decrementInventory(Map)}</p>
     * 
     * @param sku the {@link Sku} to decrement inventory from
     * @param quantity the quantity to take inventory from
     * @throws InventoryUnavailableException if there is not enough of the given <b>quantity</b> for the given <b>sku</b>
     * @throws IllegalArgumentException if the given quantity is not greater than zero
     * @see {@link ContextualInventoryService#decrementInventory(Sku, int, Map)}
     */
    public void decrementInventory(Sku sku, int quantity) throws InventoryUnavailableException;

    /**
     * <p>Attempts to decrement inventory for a map of Skus and quantities</p>
     * 
     * <p>Quantities must be greater than zero or an IllegalArgumentException will be thrown.</p>
     * 
     * <p>If any of the given {@link Sku}s inventory type is <b>not</b> {@link InventoryType#CHECK_QUANTITY} then this
     * is a no-op and nothing actually happens</p>
     * 
     * @param skuQuantities a map from a {@link Sku} to the quantity attempting to decrement
     * @throws InventoryUnavailableException if there is not enough inventory to decrement from any of the given skus or
     * if {@link #checkBasicAvailablility(Sku)} returns false
     * @throws IllegalArgumentException if any of the quantities of the given skus are less than zero
     * @see {@link ContextualInventoryService#decrementInventory(Map, Map)}
     */
    public void decrementInventory(Map<Sku, Integer> skuQuantities) throws InventoryUnavailableException;

    /**
     * <p>Attempts to increment inventory. Quantity must be greater than zero or an IllegalArgumentException will be thrown.</p>
     * 
     * <p>This is a convenience method to wrap {@link #incrementInventory(Map)}</p>
     * 
     * @param sku the {@link Sku} whose inventory should be incremented
     * @param quantity greater than zero
     * @throws IllegalArgumentException if <b>quantity</b> is less than zero or {@link #retrieveQuantityAvailable(Sku)} for
     * the given <b>sku</b> returns <b>null</b>
     * @see {@link #incrementInventory(Map)}
     * @see {@link ContextualInventoryService#incrementInventory(Sku, int, Map)}
     */
    public void incrementInventory(Sku sku, int quantity);

    /**
     * Attempts to increment inventory for a map of Skus and quantities.
     * 
     * <b>All must not be null and must be greater than zero or an IllegalArgumentException will be thrown.</p>
     * 
     * <p>If any of the given {@link Sku}s inventory type is <b>not</b> {@link InventoryType#CHECK_QUANTITY} then this
     * is a no-op and nothing actually happens</p>
     * 
     * @param skuQuantities the map of a {@link Sku} to the quantity that should be incremented
     * @throws IllegalArgumentException if any of the quantities in the map values are null or less than zero, or if
     * {@link #retrieveQuantityAvailable(Sku)} for the {@link Sku}s in the map is <b>null</b>
     * @see {@link ContextualInventoryService#incrementInventory(Map, Map)}
     */
    public void incrementInventory(Map<Sku, Integer> skuQuantities);

}
