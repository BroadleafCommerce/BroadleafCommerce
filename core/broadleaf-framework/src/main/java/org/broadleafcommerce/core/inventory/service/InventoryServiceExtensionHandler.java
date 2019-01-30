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
package org.broadleafcommerce.core.inventory.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.checkout.service.workflow.DecrementInventoryActivity;
import org.broadleafcommerce.core.order.service.workflow.CheckAddAvailabilityActivity;

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
     * Usually invoked within the {@link CheckAddAvailabilityActivity} to retrieve the quantity that is available for the given
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

    /**
     * Usually invoked via the OMS {@link ReconcileInventoryChangeOrderActivity} to determine how to handle a change order.
     * @param decrementSkuQuantities
     * @param incrementSkuQuantities
     * @param context
     * @return
     * @throws InventoryUnavailableException
     */
    public ExtensionResultStatusType reconcileChangeOrderInventory(Map<Sku, Integer> decrementSkuQuantities, Map<Sku, Integer> incrementSkuQuantities, Map<String, Object> context) throws InventoryUnavailableException;

    /**
     * Usually invoked via the AdvancedProduct to determine the availability of product bundle.
     * @param product contains Product
     * @param quantity
     * @param holder
     */
    ExtensionResultStatusType isProductBundleAvailable(Product product, int quantity, ExtensionResultHolder<Boolean> holder);
}
