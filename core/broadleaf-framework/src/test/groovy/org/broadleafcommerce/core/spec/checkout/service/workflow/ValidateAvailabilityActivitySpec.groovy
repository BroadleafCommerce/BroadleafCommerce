/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.checkout.service.workflow.ValidateAvailabilityActivity
import org.broadleafcommerce.core.inventory.service.InventoryServiceImpl
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException
import org.broadleafcommerce.core.order.domain.BundleOrderItemImpl
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl
import org.broadleafcommerce.core.order.domain.OrderItemImpl

/*
 * 1a) orderItem instanceOf DiscreteOrderItem
 *     sku is set using DiscreteOrderItem.getSku() CONTINUE
 * 1b) orderItem instanceOf BundleOrderItem
 *     sku is set using BundleOrderItem.getSku() CONTINUE
 * 1c) LOG.warn() issued and return context EXIT
 *
 * 2) !sku.isAvailable()
 *      * throw InventoryUnavailableException EXIT
 *
 * 3) return context
 *
 * 4) !sku.isActive()
 *      * throw IllegalArgumentException EXIT
 */
class ValidateAvailabilityActivitySpec extends BaseCheckoutActivitySpec {

    InventoryServiceImpl mockInventoryService = Spy(InventoryServiceImpl) {
        retrieveQuantityAvailable(*_) >> 0
    }

    /*
     * 1) mock for Services
     * 2) setup activity
     */

    def setup() {
        activity = Spy(ValidateAvailabilityActivity).with {
            inventoryService = mockInventoryService
            it
        }
    }

    def "If the order item is non-null, and there is a DiscreteOrderItem, then a Sku from that DiscreteOrderItem will be tested for availability"(){

        setup: "setup a discrete order item"
        DiscreteOrderItemImpl mockOrderItem = Spy(DiscreteOrderItemImpl)
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        mockSku.isActive() >> true
        context.seedData.order.orderItems << mockOrderItem
        mockOrderItem.getQuantity() >> 0

        when: "the activity is executed"
        context = activity.execute(context)

        then: "that sku is checked for availability"
        1 * mockOrderItem.getSku() >> mockSku
        1 * mockInventoryService.checkSkuAvailability(*_)
        1 * mockSku.isAvailable() >> true
    }

    def "If the order item is non-null, and there is a BundleOrderItem, then a Sku from that BundleOrderItem will be tested for availability"(){
        setup: "setup a bundle order item"
        BundleOrderItemImpl mockOrderItem = Spy(BundleOrderItemImpl)
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        mockSku.isActive() >> true
        context.seedData.order.orderItems << mockOrderItem
        mockOrderItem.getQuantity() >> 0

        when: "the activity is executed"
        context = activity.execute(context)

        then: "that sku is checked for availability"
        1 * mockOrderItem.getSku() >> mockSku
        1 * mockInventoryService.checkSkuAvailability(*_)
        1 * mockSku.isAvailable() >> true
    }

    def "If the order item is non-null, and the order item is not a familiar item, then availability is not checked"(){
        setup: "setup order item"
        OrderItemImpl mockOrderItem = Spy(OrderItemImpl)
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        mockSku.isActive() >> true
        context.seedData.order.orderItems << mockOrderItem
        mockOrderItem.getQuantity() >> 0

        when: "the activity is executed"
        context = activity.execute(context)

        then: "availability is not checked"
        0 * mockOrderItem.getSku() >> mockSku
        0 * mockInventoryService.checkSkuAvailability(*_)
        0 * mockSku.isAvailable()
    }

    def "If a sku is found to not be available, an InventoryUnavailableException is thrown"(){
        setup: "Sku is setup to be found and not available"
        DiscreteOrderItemImpl mockOrderItem = Spy(DiscreteOrderItemImpl)
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        mockSku.isActive() >> true
        mockOrderItem.getQuantity() >> 0
        mockOrderItem.getId() >> null
        context.seedData.order.orderItems << mockOrderItem

        when: "the activity is executed"
        context = activity.execute(context)

        then: "InventoryUnavailableException is thrown"
        1 * mockOrderItem.getSku() >> mockSku
        1 * mockInventoryService.checkSkuAvailability(*_)
        1 * mockSku.isAvailable() >> false
        Exception e = thrown()
        e instanceof InventoryUnavailableException
    }

    def "If the order item is non-null, and the sku is not active, we throw an IllegalArgumentException"(){
        setup: "Setup order item and inactive sku"
        DiscreteOrderItemImpl mockOrderItem = Spy(DiscreteOrderItemImpl)
        SkuImpl mockSku = Spy(SkuImpl)
        mockSku.getId() >> 1
        mockSku.isActive() >> false
        context.seedData.order.orderItems << mockOrderItem
        mockOrderItem.getQuantity() >> 1

        when: "The activity is executed"
        context = activity.execute(context)

        then: "IllegalArgumentException is thrown"
        1 * mockOrderItem.getSku() >> mockSku
        IllegalArgumentException e = thrown()
    }

}
