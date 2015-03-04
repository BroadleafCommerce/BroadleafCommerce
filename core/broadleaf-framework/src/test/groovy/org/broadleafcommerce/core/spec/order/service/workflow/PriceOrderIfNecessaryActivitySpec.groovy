package org.broadleafcommerce.core.spec.order.service.workflow

import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao
import org.broadleafcommerce.core.order.domain.FulfillmentGroup
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.order.domain.OrderItem
import org.broadleafcommerce.core.order.domain.OrderItemImpl
import org.broadleafcommerce.core.order.service.OrderItemService
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest
import org.broadleafcommerce.core.order.service.workflow.PriceOrderIfNecessaryActivity



/*
 * 1) request.getMultishipOptionsToBeDelete() not empty
 *      * delete those items
 *      
 * 2) request.getFgisToDelete() not empty
 *      * delete those items
 *      
 * 3) request.getOIsToDelete() not empty
 *      * delete those items
 *      
 * 4) for each oi in order
 *      a) if BundleOrderItem
 *          * for each doi in bundle
 *              * savedDoi = orderItemServe.saveOrderItem(doi)
 *              * remove from bundle
 *              * doisToAdd.add(savedDoi)
 *          * re-add saved doi's to bundle
 *          * add bundle to savedOrderItems
 *          * for each doi in savedBundle
 *              * set bundle for doi to be savedBundle
 *      b) not Bundle
 *          * getOiFgiMap called on oi
 *          * oi put in savedOrderItems
 *          
 *  5) for each oi in order
 *      * remove oi
 *      * add savedOi from savedOrderItems to oisToadd
 *  * add oisToAdd to order
 *  
 *  6) for each entry in oiFgiMap
 *      a) for each fgi in entry value
 *          * set to oi from savedOrderItems
 *      b) if key == request orderItem
 *          * request orderItem set from savedOrderItems
 *  
 *  7) for each oi in order
 *      a) oi has id equal to itemRequest's parentOrderItemId
 *          * add request's OrderItem to oi's childOrderItems
 *          
 *  8) orderService saves the order with optional pricing
 *   
 *  
 */
class PriceOrderIfNecessaryActivitySpec extends BaseOrderWorkflowSpec {
    
    OrderService mockOrderService = Mock()
    OrderItemService mockOrderItemService = Mock()
    FulfillmentGroupItemDao mockFgItemDao = Mock()
    OrderMultishipOptionService mockOrderMultishipOptionService = Mock()
    
    def setup(){
        activity = Spy(PriceOrderIfNecessaryActivity).with {
            orderService = mockOrderService
            orderItemService = mockOrderItemService
            fgItemDao = mockFgItemDao
            orderMultishipOptionService = mockOrderMultishipOptionService
            it
        }
    } 
    
    def "If there are multiship options to delete, they should be deleted"(){
        setup: "setup multiship options to delete"
        ArrayList<Long[]> testMultishipOptions = new ArrayList<Long[]>()
        Long[] msOption1 = new Long[2]
        msOption1[0] = new Long(1)
        msOption1[1] = null
        testMultishipOptions.add(msOption1)
        
        Long[] msOption2 = new Long[2]
        msOption2[0] = new Long(2)
        msOption2[1] = 1
        testMultishipOptions.add(msOption2)
        
        ((CartOperationRequest)context.seedData).setMultishipOptionsToDelete(testMultishipOptions)
        
        Order testOrder = new OrderImpl()
        context.seedData.setOrder(testOrder)
        mockOrderService.save(*_) >> testOrder
        when: "the activity is executed"
        context = activity.execute(context)
        
        then: "the multiship options are now deleted"
        2 * mockOrderMultishipOptionService.deleteOrderItemOrderMultishipOptions(*_)
    
    }
    
    def "If there are fulfillment group items to delete, they should be deleted"(){
        setup: "setup fulfillment group items to delete"
        ArrayList<FulfillmentGroupItem> testFgis = new ArrayList<FulfillmentGroupItem>();
        FulfillmentGroup testFg = new FulfillmentGroupImpl()
        
        FulfillmentGroupItem testFgi = new FulfillmentGroupItemImpl()
        
        testFg.addFulfillmentGroupItem(testFgi)
        testFgis.add(testFgi)
        
        Order testOrder = Spy(OrderImpl)
        testOrder.setFulfillmentGroups(Arrays.asList(testFg))
        context.seedData.setOrder(testOrder)
        
        ((CartOperationRequest)context.seedData).setFgisToDelete(testFgis)
        
        mockOrderService.save(*_) >> testOrder
        when: "the activity is executed"
        context = activity.execute(context)
        
        then: "the fulfillment group items are now deleted"
        testOrder.getFulfillmentGroups().indexOf(testFgi) == -1
    }
    
    def "If there are OrderItems to delete, they should be deleted"(){
        setup: "setup order items to delete"
        ArrayList<OrderItem> testOis = new ArrayList<OrderItem>()
        OrderItem testOi1 = new OrderItemImpl()
        OrderItem testOi2 = new OrderItemImpl()
        testOi2.setParentOrderItem(testOi1)
        testOi1.getChildOrderItems().add(testOi2)
        
        OrderImpl testOrder = new OrderImpl()
        
        testOis.add(testOi1)
        testOis.add(testOi2)
        
        testOrder.addOrderItem(testOi1)
        testOrder.addOrderItem(testOi2)
        ((CartOperationRequest)context.seedData).setOisToDelete(testOis)
        context.seedData.setOrder(testOrder)
        
        context.seedData.getOrderItem() >> new OrderItemImpl()
        
        when: "the activity is executed"
        context = activity.execute(context)
        
        then: "the order items are now deleted"
        testOrder.getOrderItems().indexOf(testOi1) == -1
    }
}
