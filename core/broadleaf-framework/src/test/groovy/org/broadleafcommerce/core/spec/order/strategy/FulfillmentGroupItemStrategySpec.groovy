/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.spec.order.strategy

import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao
import org.broadleafcommerce.core.order.domain.BundleOrderItem
import org.broadleafcommerce.core.order.domain.BundleOrderItemImpl
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroup
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.order.domain.OrderItem
import org.broadleafcommerce.core.order.domain.OrderItemImpl
import org.broadleafcommerce.core.order.service.FulfillmentGroupService
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO
import org.broadleafcommerce.core.order.service.type.FulfillmentType
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest
import org.broadleafcommerce.core.order.strategy.FulfillmentGroupItemStrategyImpl

import spock.lang.Specification

/**
 * <ol>
 * <li> onItemAdded Tests #3
 *  <ul>
 *      <li> order.getFulfillmentGroups() != null 
 *          <ul> for each group in Fgs
 *              <li> group.getType() == null && nullFulfillmentTypeGroup == null 
 *                  --> nullFulfillmentTypeGroup = group
 *              </li>
 *              <li> group.getType() != null && fulfillmentGroups.get(group.getType()) == null
 *                  --> fulfillmentGroups.put(group.getType(), group)
 *              </li>   
 *          <ul>
 *      </li>
 *      <li> orderItem is BundleOrderItem
 *          <ul> for doi in bundle
 *              <li> type = resolveFulfillmentType(doi) == null
 *                  --> fulfillmentGroup = nullFulfillmentGroup
 *              </li>
 *              <li> type != null 
 *                  <ul>
 *                      <li> FulfillmentType.PHYSICAL_PICKUP_OR_SHIP.equals(type)
 *                          --> type = FulfillmentType.PHYSICAL_SHIP
 *                      </li>
 *                  </ul>
 *                  --> fulfillmentGroup = fulfillmentGroups.get(type)
 *              </li>
 *              
 *              <li> if fulfillmentGroup==null
 *                  fulfillmentGroup = fulfillmentGroupService.createEmptyFulfillmentGroup()
 *                  order.getFgs.add(fg)
 *                  createFg = true
 *              </li>
 *              
 *              
 *              <li> if createdFg
 *                  <ul>
 *                      <li> if type == null
 *                          --> nullFulfillmentTypeGroup = fg
 *                      </li>
 *                      <li> else type != null
 *                          --> fgs.put(type, fg)
 *                      </li>
 *                  </ul>
 *              </li>
 *          </ul>
 *       </li>
 *       <li> orderItem is DiscreteOrderItem
 *          --> same as above for each doi
 *       </li>
 *       <li> else
 *          --> fg = addItemToFulfillmentGroup(_)
 *       <li>           
 *  </ul>
 * </li>
 * <li> onItemUpdated Tests DONE
 *  <ul>
 *      <li> orderItemQuantityDelta == 0 --> return request (unchanged) </li>
 *      <li> orderItemQuantityDelta != 0 --> request FgisToDelete is set...
 *          <ul>
 *              <li> orderItem is BundleOrderItem --> Fg added for each discrete order item in bundle using updateItemQuantity </li>
 *              <li> orderItem is not BundleOrderItem --> Fg added for order item using updateItemQuantity </li>
 *          </ul>
 *      </li>
 *  </ul>
 * </li>
 * <li> updateItemQuantity Tests #1
 *  <ul>
 *      <li> orderItemQuantityDelta > 0 --> find fgi for orderItem and update quantity && done=true </li>
 *      <li> orderItemQuantityDelta < 0 --> find fgi for orderItem and remainingToDecrement set to -1*orderItemQuantityDelta 
 *          <ul>
 *              <li> remainder == fgi.getQuantity --> add fgi to fgisToDelete && done=true</li>
 *              <li> remainder < fgi.getQuantity --> substract remainder from fgi quantity && done=true </li>
 *              <li> remainder > fgi.getQuantity --> adjust remaining to decrement and add fgi to fgisToDelete </li>
 *          </ul>
 *      </li>
 *      <li> !done --> throw IllegalStateException </li>
 *  </ul>
 * </li>
 * <li> onItemRemoved Tests #2
 *  <ul>
 *      <li> orderItem is BundleOrderItem --> add fgis for DiscreteOrderItems within Bundle to fgisToDelete </li>
 *      <li> orderItem is not BundleOrderItem --> add fgis for orderItem </li>
 *  </ul>
 * </li>
 * <li> verify Tests #4
 *  <ul>
 *      <li> isRemoveEmptyFgs() && order has Fgs
 *          --> remove and delete fgs
 *      </li>
 *      <li> for each fg in order
 *          if oiQuantty == null --> throw IllegalStateException
 *      </li>
 *      <li> for value in quantityMap
 *          if value is 0 --> IllegalStateException
 *      </li>
 *          
 *  </ul>
 * </li>
 * </ol>
 * 
 * @author Nick Crum (ncrum)
 */
class FulfillmentGroupItemStrategySpec extends Specification {

    FulfillmentGroupService mockFulfillmentGroupService = Mock()
    OrderItemService mockOrderItemService = Mock()
    OrderService mockOrderService = Mock()
    FulfillmentGroupItemDao mockFgItemDao = Mock()
    
    CartOperationRequest request;
    FulfillmentGroupItemStrategyImpl strategy;
    
    def setup(){
        request = Spy(CartOperationRequest, constructorArgs:[new OrderImpl(), new OrderItemRequestDTO(), true])
        strategy = Spy(FulfillmentGroupItemStrategyImpl).with {
            fulfillmentGroupService = mockFulfillmentGroupService
            orderItemService = mockOrderItemService
            orderService = mockOrderService
            fgItemDao = mockFgItemDao
            it
        }
    }
    
    // onItemAdded Tests
    
    def "If order has fulfillmentGroups, and one fg has type null, and orderItem is not Discrete or Bundle, the item is added to that fulfillmentGroup"(){
        setup: "Fg for order, and orderItem that is non-discrete and non-bundle"
        Order testOrder = new OrderImpl()
        OrderItem testOi = new OrderItemImpl()
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        testFg.setType(null)
        
        testOrder.setFulfillmentGroups(Arrays.asList(testFg))
        
        request.setOrder(testOrder)
        request.setOrderItem(testOi)
        
        when:
        request = strategy.onItemAdded(request)
        
        then:
        1 * strategy.addItemToFulfillmentGroup(testOrder,testOi,testFg) >> testFg
        
    }
    
    def "If order has no fulfillmentGroups, and orderItem is not Discrete or Bundle, the item is added to a newly created fulfillmentGroup"(){
        setup: "Fg for order, and orderItem that is non-discrete and non-bundle"
        Order testOrder = new OrderImpl()
        OrderItem testOi = new OrderItemImpl()
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        testFg.setType(null)
        
        request.setOrder(testOrder)
        request.setOrderItem(testOi)
        
        mockFulfillmentGroupService.createEmptyFulfillmentGroup() >> testFg
        
        when:
        request = strategy.onItemAdded(request)
        
        then:
        1 * strategy.addItemToFulfillmentGroup(testOrder,testOi,testFg) >> testFg
        testFg.getOrder() == testOrder
        testOrder.getFulfillmentGroups().get(0) == testFg
        
    }
    
    def "If order has fulfillmentGroups, and one fg has type null, and orderItem is Discrete, the item is added to that fulfillmentGroup"(){
        setup: "Fg for order, and orderItem that is discrete"
        Order testOrder = new OrderImpl()
        DiscreteOrderItem testOi = new DiscreteOrderItemImpl()
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        testFg.setType(null)
        
        testOrder.setFulfillmentGroups(Arrays.asList(testFg))
        
        request.setOrder(testOrder)
        request.setOrderItem(testOi)
        
        strategy.resolveFulfillmentType(testOi) >> null
        
        when:
        request = strategy.onItemAdded(request)
        
        then:
        1 * strategy.addItemToFulfillmentGroup(testOrder,testOi,testFg) >> testFg
        
    }
    
    def "If order has fulfillmentGroups, and one fg has type null, and orderItem is Discrete and FulfillmentType is not null, the item is added to that fulfillmentGroup"(){
        setup: "Fg for order, and orderItem that is discrete"
        Order testOrder = new OrderImpl()
        DiscreteOrderItem testOi = new DiscreteOrderItemImpl()
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        testFg.setType(FulfillmentType.PHYSICAL_SHIP)
        
        testOrder.setFulfillmentGroups(Arrays.asList(testFg))
        
        request.setOrder(testOrder)
        request.setOrderItem(testOi)
        
        strategy.resolveFulfillmentType(testOi) >> FulfillmentType.PHYSICAL_PICKUP_OR_SHIP
        
        when:
        request = strategy.onItemAdded(request)
        
        then:
        1 * strategy.addItemToFulfillmentGroup(testOrder,testOi,testFg) >> testFg
        
    }
    
    def "If order has no fulfillmentGroups, and orderItem is Discrete, the item is added to a new fulfillmentGroup"(){
        setup: "Fg for order, and orderItem that is discrete"
        Order testOrder = new OrderImpl()
        DiscreteOrderItem testOi = new DiscreteOrderItemImpl()
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        testFg.setType(null)
        
        request.setOrder(testOrder)
        request.setOrderItem(testOi)
        
        strategy.resolveFulfillmentType(testOi) >> null
        mockFulfillmentGroupService.createEmptyFulfillmentGroup() >> testFg
        when:
        request = strategy.onItemAdded(request)
        
        then:
        1 * strategy.addItemToFulfillmentGroup(testOrder,testOi,testFg) >> testFg
        testFg.getOrder() == testOrder
        testOrder.getFulfillmentGroups().get(0) == testFg
    }
    
    def "If order has no fulfillmentGroups, and orderItem is Bundle, and fulfillmentType is null, the DiscreteOis are added to a new fulfillmentGroup"(){
        setup: "Fg for order, and orderItem that is bundle"
        Order testOrder = new OrderImpl()
        DiscreteOrderItem testDoi = new DiscreteOrderItemImpl()
        BundleOrderItem testOi = new BundleOrderItemImpl()
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        testFg.setOrder(testOrder)
        testFg.setType(null)
        
        
        request.setOrder(testOrder)
        request.setOrderItem(testOi)
        testOi.setDiscreteOrderItems(Arrays.asList(testDoi))
        
        strategy.resolveFulfillmentType(testDoi) >> null
        mockFulfillmentGroupService.createEmptyFulfillmentGroup() >> testFg
        when:
        request = strategy.onItemAdded(request)
        
        then:
        1 * strategy.addItemToFulfillmentGroup(testOrder,testDoi,_,testFg) >> testFg
        testFg.getOrder() == testOrder
        testOrder.getFulfillmentGroups().get(0) == testFg
    }
    
    def "If order has no fulfillmentGroups, and orderItem is Bundle, the DiscreteOis are added to a new fulfillmentGroup"(){
        setup: "Fg for order, and orderItem that is bundle"
        Order testOrder = new OrderImpl()
        DiscreteOrderItem testDoi = new DiscreteOrderItemImpl()
        BundleOrderItem testOi = new BundleOrderItemImpl()
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        testFg.setOrder(testOrder)
        testFg.setType(FulfillmentType.PHYSICAL_SHIP)
        
        
        request.setOrder(testOrder)
        request.setOrderItem(testOi)
        testOi.setDiscreteOrderItems(Arrays.asList(testDoi))
        
        strategy.resolveFulfillmentType(testDoi) >> FulfillmentType.PHYSICAL_PICKUP_OR_SHIP
        mockFulfillmentGroupService.createEmptyFulfillmentGroup() >> testFg
        when:
        request = strategy.onItemAdded(request)
        
        then:
        1 * strategy.addItemToFulfillmentGroup(testOrder,testDoi,_,testFg) >> testFg
        testFg.getOrder() == testOrder
        testOrder.getFulfillmentGroups().get(0) == testFg
    }
    
    
    
    // onItemUpdated Tests
    
    /**
     * orderItemQuantityDelta == 0 --> return request (unchanged)
     */
    def "If orderItemQuantityDelta is zero then nothing is updated"(){
        setup: "set orderItemQuantityDelta to be zero"
        request.orderItemQuantityDelta = 0
        
        when:
        request = strategy.onItemUpdated(request)
        
        then:
        request.getOrderItemQuantityDelta() == 0
        request.getFgisToDelete().size() == 0
        
    }
    
    /**
     * <ul> orderItemQuantityDelta != 0 --> request FgisToDelete is set...
     *     <li> orderItem is BundleOrderItem --> Fg added for each discrete order item in bundle using updateItemQuantity </li>
     */
    def "If orderItemQuantityDelta not zero and orderItem is BundleOrderItem, fulfillment groups added for each item within bundle"(){
        setup: "set orderItemQuantityDelta to be non-zero, set orderItem to be BundleOrderItem, and mock fulfillment groups returned by updateItemQuantity"
        request.orderItemQuantityDelta = 1
        
        BundleOrderItemImpl boi = Spy(BundleOrderItemImpl)
        DiscreteOrderItemImpl doi = new DiscreteOrderItemImpl()
        boi.setDiscreteOrderItems(Arrays.asList(doi))
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        strategy.updateItemQuantity(*_) >> Arrays.asList(testFg)
        
        request.setOrderItem(boi)
        when:
        request = strategy.onItemUpdated(request)
        
        then:
        request.getFgisToDelete().get(0) == testFg
    }
    
    /**
     * <ul> orderItemQuantityDelta != 0 --> request FgisToDelete is set...
     *     <li> orderItem is not BundleOrderItem --> Fg added for order item using updateItemQuantity </li>
     */
    def "If orderItemQuantityDelta not zero and orderItem is not a  BundleOrderItem, fulfillment groups added for the order item"(){
        setup: "set orderItemQuantityDelta to be non-zero, set orderItem not to be BundleOrderItem, and mock fulfillment groups returned by updateItemQuantity"
        request.orderItemQuantityDelta = 1
        
        OrderItemImpl oi = Spy(OrderItemImpl)
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        strategy.updateItemQuantity(*_) >> Arrays.asList(testFg)
        
        request.setOrderItem(oi)
        when:
        request = strategy.onItemUpdated(request)
        
        then:
        request.getFgisToDelete().get(0) == testFg
    }
    
    // updateItemQuantity Tests
    
    /**
     * <li> updateItemQuantity Tests #1
     *  <ul>
     *      <li> orderItemQuantityDelta > 0 --> find fgi for orderItem and update quantity and done=true </li>
     *      <li> orderItemQuantityDelta < 0 --> find fgi for orderItem and remainingToDecrement set to -1*orderItemQuantityDelta 
     *          <ul>
     *              <li> remainder == fgi.getQuantity --> add fgi to fgisToDelete and done=true</li>
     *              <li> remainder < fgi.getQuantity --> substract remainder from fgi quantity and done=true </li>
     *              <li> remainder > fgi.getQuantity --> adjust remaining to decrement and add fgi to fgisToDelete </li>
     *          </ul>
     *      </li>
     *      <li> !done --> throw IllegalStateException </li>
     *  </ul>
     * </li>
     */
    
    /**
     * <li> orderItemQuantityDelta > 0 --> find fgi for orderItem and update quantity, done = true, and no FgisToDelete </li>
     */
    def "If orderItemQuantityDelta greater than zero, then the fulfillmentGroupItem for that orderItem is found and updated"(){
        setup: "orderItemQuantityDelta greater than zero, orderItem fgs and fgis"
        Order testOrder = new OrderImpl()
        OrderItem testOi = new OrderItemImpl()
        int testDelta = 1
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        FulfillmentGroupItem testFgi = new FulfillmentGroupItemImpl()
        
        testFgi.setQuantity(1)
        testFgi.setOrderItem(testOi)
        testFg.addFulfillmentGroupItem(testFgi)
        testOrder.getFulfillmentGroups().add(testFg)
        
        when:
        List<FulfillmentGroupItem> fgisToDelete = strategy.updateItemQuantity(testOrder,testOi,testDelta)
        
        then:
        testFgi.getQuantity() == 2
        fgisToDelete.size() == 0
        
        
    }
    
    /**
     * orderItemQuantityDelta < 0 --> find fgi for orderItem and remainingToDecrement set to -1*orderItemQuantityDelta 
     *  <ul>
     *     <li> remainder >&== fgi.getQuantity --> add fgi to fgisToDelete and done=true</li>
     *  <ul>
     */
    def "If orderItemQuantityDelta less than zero, then fulfillmentGroupItems for that orderItem is found and updated, and if fgItem has quantiy greater than remainder, it is not removed"(){
        setup: "orderItemQuantityDelta greater than zero, orderItem fgs and fgis"
        Order testOrder = new OrderImpl()
        OrderItem testOi = new OrderItemImpl()
        int testDelta = -3
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        FulfillmentGroupItem testFgi = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem testFgi2 = new FulfillmentGroupItemImpl()
        
        testFgi.setQuantity(1)
        testFgi2.setQuantity(3)
        testFgi.setOrderItem(testOi)
        testFgi2.setOrderItem(testOi)
        testFg.addFulfillmentGroupItem(testFgi)
        testFg.addFulfillmentGroupItem(testFgi2)
        testOrder.getFulfillmentGroups().add(testFg)
        
        when:
        List<FulfillmentGroupItem> fgisToDelete = strategy.updateItemQuantity(testOrder,testOi,testDelta)
        
        then:
        testFgi2.getQuantity() == 1
        fgisToDelete.get(0) == testFgi
        
        
    }
    
    /**
     * orderItemQuantityDelta < 0 --> find fgi for orderItem and remainingToDecrement set to -1*orderItemQuantityDelta
     *  <ul>
     *     <li> remainder >&< fgi.getQuantity --> add fgi to fgisToDelete and done=true</li>
     *  <ul>
     */
    def "If orderItemQuantityDelta less than zero, then fulfillmentGroupItems for that orderItem is found and updated, and if fgItem has quantity less then or equal to remainder, it is removed"(){
        setup: "orderItemQuantityDelta greater than zero, orderItem fgs and fgis"
        Order testOrder = new OrderImpl()
        OrderItem testOi = new OrderItemImpl()
        int testDelta = -3
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        FulfillmentGroupItem testFgi = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem testFgi2 = new FulfillmentGroupItemImpl()
        
        testFgi.setQuantity(1)
        testFgi2.setQuantity(2)
        testFgi.setOrderItem(testOi)
        testFgi2.setOrderItem(testOi)
        testFg.addFulfillmentGroupItem(testFgi)
        testFg.addFulfillmentGroupItem(testFgi2)
        testOrder.getFulfillmentGroups().add(testFg)
        
        when:
        List<FulfillmentGroupItem> fgisToDelete = strategy.updateItemQuantity(testOrder,testOi,testDelta)
        
        then:
        fgisToDelete.get(0) == testFgi
        fgisToDelete.get(1) == testFgi2
        
        
    }
    
    /**
     * !done --> throw IllegalStateException
     */
    def "If nothing is changed by the given delta, then updateItemQuantity throws an IllegalStateException"(){
        setup: "delta is set and no fgis given with valid orderItem so done will be false"
        Order testOrder = new OrderImpl()
        OrderItem testOi = new OrderItemImpl()
        int testDelta = 1
        
        when:
        List<FulfillmentGroupItem> fgisToDelete = strategy.updateItemQuantity(testOrder,testOi,testDelta)
        
        then:
        IllegalStateException e = thrown()
        
    }
    
    
    // onItemRemoved Tests #2
    
    /**  
     * <ul>
     *      <li> orderItem is BundleOrderItem --> add fgis for DiscreteOrderItems within Bundle to fgisToDelete </li>
     * </ul>
     */
    def "If orderItem is a BundleOrderItem, then all fgItems within the DiscreteOis within the Bundle will be set to be removed"(){
        setup: "orderItem as BundleOrderItem, DiscreteOis within the Bundle"
        BundleOrderItem testBoi = new BundleOrderItemImpl()
        DiscreteOrderItem testDoi = new DiscreteOrderItemImpl()
        testBoi.getDiscreteOrderItems().add(testDoi)
        FulfillmentGroupItem testFgi = new FulfillmentGroupItemImpl()
        mockFulfillmentGroupService.getFulfillmentGroupItemsForOrderItem(*_) >> Arrays.asList(testFgi)
        
        request.setOrderItem(testBoi)
        
        when:
        request = strategy.onItemRemoved(request)
        
        then:
        request.getFgisToDelete().get(0) == testFgi
    }
    
    /**
     * <ul>
     *      <li> orderItem is not BundleOrderItem --> add fgis for orderItem </li>
     * </ul>
     */
    def "If orderItem is not a BundleOrderItem, then all fgItems within the orderItem will be set to be removed"(){
        setup: "orderItem as not a BundleOrderItem, testOi as the orderItem"
        OrderItem testOi = new OrderItemImpl()
        FulfillmentGroupItem testFgi = new FulfillmentGroupItemImpl()
        mockFulfillmentGroupService.getFulfillmentGroupItemsForOrderItem(*_) >> Arrays.asList(testFgi)
        
        request.setOrderItem(testOi)
        
        when:
        request = strategy.onItemRemoved(request)
        
        then:
        request.getFgisToDelete().get(0) == testFgi
    }
    
    // verify Tests
    
    def "If order has fulfillment groups to remove, they are deleted"(){
        setup:
        Order testOrder = new OrderImpl()
        strategy.isRemoveEmptyFulfillmentGroups() >> true
        
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        
        ArrayList<FulfillmentGroup> testFgs = new ArrayList<FulfillmentGroup>()
        testFgs.add(testFg)
        testOrder.setFulfillmentGroups(testFgs)
        
        request.setOrder(testOrder)
        when:
        request = strategy.verify(request)
        
        then:
        1 * mockFulfillmentGroupService.delete(testFg)
    }
    
    def "If fulfillment groups and order items are not in sync, an IllegalStateException is thrown"(){
        setup:
        Order testOrder = new OrderImpl()
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        FulfillmentGroupItem testFgi = new FulfillmentGroupItemImpl()
        testFg.addFulfillmentGroupItem(testFgi)
        OrderItem testOi1 = new OrderItemImpl().with{
            id = 1
            it
        }
        testFgi.setOrderItem(testOi1)
        
        OrderItem testOi2 = new OrderItemImpl().with{
            id = 2
            it
        }
        
        testOrder.setOrderItems(Arrays.asList(testOi2))
        
        
        ArrayList<FulfillmentGroup> testFgs = new ArrayList<FulfillmentGroup>()
        testFgs.add(testFg)
        testOrder.setFulfillmentGroups(testFgs)
        
        request.setOrder(testOrder)
        when:
        request = strategy.verify(request)
        
        then:
        IllegalStateException e = thrown()
    }
    
    def "If fulfillment groups and order items are in sync, and there is a zero quantity found, an IllegalStateException is thrown"(){
        setup:
        Order testOrder = new OrderImpl()
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        FulfillmentGroupItem testFgi = new FulfillmentGroupItemImpl()
        testFg.addFulfillmentGroupItem(testFgi)
        OrderItem testOi1 = new OrderItemImpl().with{
            id = 1
            quantity=1
            it
        }
        testFgi.setOrderItem(testOi1)
        
        testOrder.setOrderItems(Arrays.asList(testOi1))
        
        
        ArrayList<FulfillmentGroup> testFgs = new ArrayList<FulfillmentGroup>()
        testFgs.add(testFg)
        testOrder.setFulfillmentGroups(testFgs)
        
        request.setOrder(testOrder)
        when:
        request = strategy.verify(request)
        
        then:
        IllegalStateException e = thrown()
    }
    
}
