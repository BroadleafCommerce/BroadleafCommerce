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
/**
 * @author Austin Rooke (austinrooke)
 */
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.core.catalog.domain.Sku
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.checkout.service.workflow.DecrementInventoryRollbackHandler
import org.broadleafcommerce.core.inventory.service.ContextualInventoryService
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException
import org.broadleafcommerce.core.workflow.state.RollbackFailureException
import org.broadleafcommerce.core.workflow.state.RollbackHandler

class DecrementInventoryRollbackHandlerSpec extends BaseCheckoutRollbackSpec{

    ContextualInventoryService mockInventoryService

    def setup() {
        mockInventoryService = Mock()
        stateConfiguration = new HashMap<String, Object>()
    }

    def "Test that RollbackFailureException is thrown when attempting to increment inventory"() {
        Map<Sku, Integer> inventoryToIncrement = new HashMap<Sku, Integer>()
        Sku sku = new SkuImpl()
        Integer integer = new Integer(1)
        inventoryToIncrement.put(sku, integer)
        stateConfiguration.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_INVENTORY_DECREMENTED, inventoryToIncrement)
        stateConfiguration.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_ORDER_ID, "1")

        RollbackHandler rollbackHandler = new DecrementInventoryRollbackHandler().with(){
            inventoryService = mockInventoryService
            it
        }
        when:"rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then:"RollbackFailureException is thrown"
        1 * mockInventoryService.incrementInventory(_, _) >> {throw new Exception() }
        RollbackFailureException ex = thrown(RollbackFailureException)
        ex.message.equals("An unexpected error occured in the error handler of the checkout workflow trying to compensate"+
                        " for inventory. This happend for order ID: 1. This should be corrected manually!")
    }

    def "Test that RollbackFailureException occured due to InventoryUnavailableException when attempting to decrement inventory"() {
        Map<Sku, Integer> inventoryToDecrement = new HashMap<Sku, Integer>()
        Sku sku = new SkuImpl()
        Integer integer = new Integer(1)
        inventoryToDecrement.put(sku, integer)
        stateConfiguration.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_INVENTORY_INCREMENTED, inventoryToDecrement)
        stateConfiguration.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_ORDER_ID, "2")

        RollbackHandler rollbackHandler = new DecrementInventoryRollbackHandler().with() {
            inventoryService = mockInventoryService
            it
        }
        when:"rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then:"RollbackFailureException is thrown"
        1 * mockInventoryService.decrementInventory(_, _) >> {throw new InventoryUnavailableException("Test") }
        RollbackFailureException ex = thrown(RollbackFailureException)
        ex.message.equals("While trying roll back (decrement) inventory, we found that there was none left decrement.")
    }

    def "Test that RollbackFailureException occured due to RuntimeException when attempting to decrement inventory"() {
        Map<Sku, Integer> inventoryToDecrement = new HashMap<Sku, Integer>()
        Sku sku = new SkuImpl()
        Integer integer = new Integer(1)
        inventoryToDecrement.put(sku, integer)
        stateConfiguration.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_INVENTORY_INCREMENTED, inventoryToDecrement)
        stateConfiguration.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_ORDER_ID, "3")

        RollbackHandler rollbackHandler = new DecrementInventoryRollbackHandler().with() {
            inventoryService = mockInventoryService
            it
        }
        when:"rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then:"RollbackFailureException is thrown"
        1 * mockInventoryService.decrementInventory(_, _) >> { throw new RuntimeException() }
        RollbackFailureException ex = thrown(RollbackFailureException)
        ex.message.equals("An unexpected error occured in the error handler of the checkout workflow trying to compensate"
                        +" for inventory. This happend for order ID: 3. This should be corrected manually!")
    }
}
