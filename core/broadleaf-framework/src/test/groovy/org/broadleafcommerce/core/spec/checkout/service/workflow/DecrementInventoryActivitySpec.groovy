/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.catalog.domain.Sku
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.checkout.service.workflow.DecrementInventoryActivity
import org.broadleafcommerce.core.inventory.service.ContextualInventoryService
import org.broadleafcommerce.core.inventory.service.type.InventoryType
import org.broadleafcommerce.core.order.domain.BundleOrderItem
import org.broadleafcommerce.core.order.domain.BundleOrderItemImpl
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl
import org.broadleafcommerce.core.order.domain.OrderItem
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl
import org.broadleafcommerce.core.workflow.state.NullCheckoutRollbackHandler
import org.broadleafcommerce.core.workflow.state.RollbackHandler
import org.broadleafcommerce.core.workflow.state.RollbackStateLocal

class DecrementInventoryActivitySpec extends BaseCheckoutActivitySpec{

    ContextualInventoryService mockInventoryService
    DiscreteOrderItem discreteOrderItem
    BundleOrderItem bundleOrderItem
    RollbackHandler mockRollbackHandler

    def setup(){
        def rollbackStateLocal = new RollbackStateLocal()
        rollbackStateLocal.setThreadId("SPOCK_THREAD")
        rollbackStateLocal.setWorkflowId("TEST")
        RollbackStateLocal.setRollbackStateLocal(rollbackStateLocal)

        new ActivityStateManagerImpl().init()

        discreteOrderItem = new DiscreteOrderItemImpl()
        bundleOrderItem = new BundleOrderItemImpl()
        DiscreteOrderItem discreteOrderItemForBundleOrderItem = new DiscreteOrderItemImpl()
        List<OrderItem> orderItems = new ArrayList()

        mockInventoryService = Mock()

        //each discreteOrderItem will need a unique sku, and a quantity
        //each sku will need to have its inventoryType set to InventoryType.CHECK_QUANTITY
        //each bundleOrderItem will need a List<DiscreteOrderItem> holding a DiscreteOrderItem
        //each '' will also need a sku and a quantity value with its sku's inventoryType set to InventoryType.CHECK_QUANTITY
        discreteOrderItem.sku = new SkuImpl()
        discreteOrderItem.sku.inventoryType = InventoryType.CHECK_QUANTITY
        discreteOrderItem.sku.id = 0
        discreteOrderItem.sku.setRetailPrice(new Money(1.00))
        discreteOrderItem.quantity = 1
        discreteOrderItemForBundleOrderItem.sku = new SkuImpl()
        discreteOrderItemForBundleOrderItem.sku.inventoryType = InventoryType.CHECK_QUANTITY
        discreteOrderItemForBundleOrderItem.sku.id = 1
        discreteOrderItemForBundleOrderItem.sku.setRetailPrice(new Money(1.00))
        discreteOrderItemForBundleOrderItem.quantity = 1
        Sku bundleOrderItemSku = new SkuImpl()
        bundleOrderItemSku.setId(2)
        bundleOrderItemSku.setRetailPrice(new Money(1.00))
        bundleOrderItemSku.setInventoryType(InventoryType.CHECK_QUANTITY)
        bundleOrderItem.sku = bundleOrderItemSku
        bundleOrderItem.quantity = 1
        bundleOrderItem.discreteOrderItems << discreteOrderItemForBundleOrderItem
        context.seedData.order.orderItems << discreteOrderItem
        context.seedData.order.orderItems << bundleOrderItem

        Map<Sku, Integer> skuIntegerMap = new HashMap<>()
        skuIntegerMap.put(discreteOrderItem.sku, discreteOrderItem.quantity)
        skuIntegerMap.put(discreteOrderItemForBundleOrderItem.sku, discreteOrderItemForBundleOrderItem.quantity)
        skuIntegerMap.put(bundleOrderItem.sku, bundleOrderItem.quantity)

        mockInventoryService.buildSkuInventoryMap(*_) >> skuIntegerMap

    }


    def "Test DecrementInventory with Valid Data"() {
        setup:"I have one DiscreteOrderItem by itself in the order and one BundleOrderItem holding one DiscreteOrderItem in the Order"

        activity = new DecrementInventoryActivity().with {
            inventoryService = mockInventoryService
            rollbackHandler = new NullCheckoutRollbackHandler()
            it
        }

        when: "I execute the DecerementInventoryActivity"
        context = activity.execute(context)

        then: "decrementInventory() should have run once and there should be 3 state containers for the Activity State Manager rollback thread"
        def containers = ActivityStateManagerImpl.stateManager.stateMap.get("SPOCK_THREAD_TEST")
        containers.size() == 1
        1 * mockInventoryService.decrementInventory(_, _)
    }
}
