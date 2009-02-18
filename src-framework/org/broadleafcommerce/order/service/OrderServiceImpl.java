package org.broadleafcommerce.order.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.dao.FullfillmentGroupDao;
import org.broadleafcommerce.order.dao.FullfillmentGroupItemDao;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.dao.PaymentInfoDao;
import org.broadleafcommerce.order.domain.DefaultFullfillmentGroup;
import org.broadleafcommerce.order.domain.FullfillmentGroup;
import org.broadleafcommerce.order.domain.FullfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.dao.ContactInfoDao;
import org.broadleafcommerce.profile.dao.CustomerDao;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private PaymentInfoDao paymentInfoDao;

    @Resource
    private FullfillmentGroupDao fullfillmentGroupDao;

    @Resource
    private FullfillmentGroupItemDao fullfillmentGroupItemDao;
    
    @Resource
    private SkuDao skuDao;

    @Resource
    private ContactInfoDao contactInfoDao;

    @Resource
    private CustomerDao customerDao;

    @Resource
    private AddressDao addressDao;

    @Override
    public Order findCurrentBasketForCustomer(Customer customer) {
        return orderDao.readBasketOrderForCustomer(customer);
    }
    
	@Override
	public DefaultFullfillmentGroup findDefaultFullfillmentGroupForOrder(Order order) {
		DefaultFullfillmentGroup dfg = fullfillmentGroupDao.readDefaultFullfillmentGroupForOrder(order);
		if(dfg.getFullfillmentGroupItems().size() == 0){
			// Only Default fulfillment group has been created so
			// add all orderItems for order to group
			List<OrderItem> orderItems = orderItemDao.readOrderItemsForOrder(order);
			List<FullfillmentGroupItem> fgItems = new ArrayList<FullfillmentGroupItem>();
			for (OrderItem orderItem : orderItems) {
				fullfillmentGroupItemDao.create();
				fgItems.add(this.createFulfillmentGroupItemFromOrderItem(orderItem, dfg.getId()));
			}
			dfg.setFullfillmentGroupItems(fgItems);
			// Go ahead and persist it so we don't have to do this later
			fullfillmentGroupDao.maintainDefaultFullfillmentGroup(dfg);
		}
		return dfg;
	}

	@Override
	public List<FullfillmentGroup> findFullfillmentGroupsForOrder(Order order){
		return fullfillmentGroupDao.readFullfillmentGroupsForOrder(order);
	}	
	
    @Override
    public List<Order> findOrdersForCustomer(Customer customer) {
        return orderDao.readOrdersForCustomer(customer);
    }

    @Override
    public List<OrderItem> findItemsForOrder(Order order) {
        List<OrderItem> result = orderItemDao.readOrderItemsForOrder(order);
        for (OrderItem oi : result) {
            oi.getSku().getItemAttributes();
        }
        return result;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order addContactInfoToOrder(Order order, ContactInfo contactInfo) {
        if (contactInfo.getId() == null) {
            contactInfoDao.maintainContactInfo(contactInfo);
        }
        order.setContactInfo(contactInfo);
        return maintainOrder(order);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItem addItemToOrder(Order order, Sku item, int quantity) {
        OrderItem orderItem = null;
        List<OrderItem> orderItems = orderItemDao.readOrderItemsForOrder(order);
        for (OrderItem orderItem2 : orderItems) {
            if (orderItem2.getSku().getId().equals(item.getId()))
                orderItem = orderItem2;
        }
        if (orderItem == null)
            // orderItem = new OrderItem();
        	orderItem = orderItemDao.create();
        orderItem.setSku(item);
        orderItem.setQuantity(orderItem.getQuantity() + quantity);
        orderItem.setOrder(order);
        return maintainOrderItem(orderItem);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment) {
        payment.setOrder(order);
        if (payment.getAddress() != null && payment.getAddress().getId() == null) {
            payment.setAddress(addressDao.maintainAddress(payment.getAddress()));
        }
        return paymentInfoDao.maintainPaymentInfo(payment);
    }  
    
	@Override
	public FullfillmentGroup addItemToFullfillmentGroup(OrderItem item,
			FullfillmentGroup fullfillmentGroup,
			int quantity) {
		
		FullfillmentGroupItem fgi = null;
		
		if(fullfillmentGroup.getId() == null){
			// API user is trying to add an item to a fulfillment group not created
			fullfillmentGroup = addFullfillmentGroupToOrder(item.getOrder(), fullfillmentGroup);
		}
		// API user is trying to add an item to a fulfillment 
		// Steps are

		// 1) Find the item's existing fulfillment group
		for (FullfillmentGroup fg : item.getOrder().getFullfillmentGroups()) {
			for (FullfillmentGroupItem tempFgi : fg.getFullfillmentGroupItems()){
				if(tempFgi.getOrderItem().getId().equals(item.getId())){
					fgi = tempFgi;
		// 2) remove item from it's existing fulfillment group
					fg.getFullfillmentGroupItems().remove(fg);
					fullfillmentGroupDao.maintainFullfillmentGroup(fg);
				}
			}
		}
		if(fgi == null)
			fgi = createFulfillmentGroupItemFromOrderItem(item, fullfillmentGroup.getId());

		// 3) add the item to the new fulfillment group
		fullfillmentGroupItemDao.maintainFullfillmentGroupItem(fgi);
		return fullfillmentGroupDao.readFullfillmentGroupById(fullfillmentGroup.getId());
	}

	@Override
	public FullfillmentGroup addFullfillmentGroupToOrder(Order order,
			FullfillmentGroup fullfillmentGroup) {

		List<FullfillmentGroup> currentFullfillmentGroups = fullfillmentGroupDao.readFullfillmentGroupsForOrder(order);
		DefaultFullfillmentGroup dfg = fullfillmentGroupDao.readDefaultFullfillmentGroupForOrder(order);
		if(dfg == null){
			// This is the first fulfillment group added so make it the
			// default one
			return fullfillmentGroupDao.maintainDefaultFullfillmentGroup(createDefaultFulfillmentGroupFromFulfillmentGroup(fullfillmentGroup, order.getId()));
		}else if(dfg.getId().equals(fullfillmentGroup.getId())){
			// API user is trying to re-add the default fulfillment group
			// to the same order
			// um....treat it as update/maintain for now
			return fullfillmentGroupDao.maintainDefaultFullfillmentGroup(createDefaultFulfillmentGroupFromFulfillmentGroup(fullfillmentGroup, order.getId()));
		}else if(currentFullfillmentGroups.size() == 1){
			// API user is adding first non default fulfillment group to the order
			// Steps are:
			// 1) Create a list of existing order items (that are by default in the default group)
			List<OrderItem> orderItems = orderItemDao.readOrderItemsForOrder(order);
			// 2) Create a list of fulfillment order items from existing order items
			List<FullfillmentGroupItem> fgItems = new ArrayList<FullfillmentGroupItem>();
			for (OrderItem orderItem : orderItems) {
				fgItems.add(createFulfillmentGroupItemFromOrderItem(orderItem, dfg.getId()));
			}
			// 3) Remove items in new fulfillment group from existing order items 
			for (FullfillmentGroupItem fullfillmentGroupItem : fullfillmentGroup.getFullfillmentGroupItems()) {
				fgItems.remove(fullfillmentGroupItem);
			}
			// 4) maintain default fulfillment group 
			dfg.setFullfillmentGroupItems(fgItems);
			fullfillmentGroupDao.maintainDefaultFullfillmentGroup(dfg);
			// 5) maintain new fulfillment group, returning it
			fullfillmentGroup.setOrderId(order.getId());
			return fullfillmentGroupDao.maintainFullfillmentGroup(fullfillmentGroup);
		}else{
			// API user is adding a new fulfillment group to the order and
			// the order already has multiple fulfillment groups
			fullfillmentGroup.setOrderId(order.getId());

			// 1) For each item in the new fulfillment group
			for (FullfillmentGroupItem fgItem : fullfillmentGroup.getFullfillmentGroupItems()) {
				
				// 2) Find the item's existing fulfillment group
				for (FullfillmentGroup fg : order.getFullfillmentGroups()) {
					for (FullfillmentGroupItem tempFgi : fg.getFullfillmentGroupItems()){
						if(tempFgi.getOrderItem().getId().equals(fgItem.getId())){
				// 3) remove item from it's existing fulfillment group
							fg.getFullfillmentGroupItems().remove(fg);
							fullfillmentGroupDao.maintainFullfillmentGroup(fg);
						}
					}
				}
			}
			
			return fullfillmentGroupDao.maintainFullfillmentGroup(fullfillmentGroup);
		}
	}

	@Override
	public FullfillmentGroup updateFullfillmentGroup(
			FullfillmentGroup fullfillmentGroup) {
		return fullfillmentGroupDao.maintainFullfillmentGroup(fullfillmentGroup);
	}
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItem updateItemInOrder(Order order, OrderItem item) {
        // This isn't quite right. It will need to be changed later to reflect
        // the exact requirements we want.
        // item.setQuantity(quantity);
        item.setOrder(order);
        return maintainOrderItem(item);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order removeItemFromOrder(Order order, OrderItem item) {
        orderItemDao.deleteOrderItem(item);
        calculateOrderTotal(order);
        return order;
    }

	@Override
	public void removeFullfillmentGroupFromOrder(Order order,
			FullfillmentGroup fullfillmentGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order calculateOrderTotal(Order order) {
        double total = 0;
        List<OrderItem> orderItemList = orderItemDao.readOrderItemsForOrder(order);
        for (OrderItem item : orderItemList) {
            total += item.getFinalPrice();
        }

        List<FullfillmentGroup> fullfillmentGroupList = fullfillmentGroupDao.readFullfillmentGroupsForOrder(order);
        for (FullfillmentGroup fullfillmentGroup : fullfillmentGroupList) {
            total += fullfillmentGroup.getCost();
        }
        order.setTotal(total);
        return order;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order confirmOrder(Order order) {
        // TODO Other actions needed to complete order. Code below is only a start.
        return orderDao.submitOrder(order);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelOrder(Order order) {
        orderDao.deleteOrderForCustomer(order);
    }

    protected Order maintainOrder(Order order) {
        calculateOrderTotal(order);
        return orderDao.maintianOrder(order);
    }

    protected OrderItem maintainOrderItem(OrderItem orderItem) {
        orderItem.setFinalPrice(orderItem.getQuantity() * orderItem.getSku().getPrice());
        OrderItem returnedOrderItem = orderItemDao.maintainOrderItem(orderItem);
        maintainOrder(orderItem.getOrder());
        return returnedOrderItem;
    }

    protected DefaultFullfillmentGroup createDefaultFulfillmentGroupFromFulfillmentGroup(FullfillmentGroup fullfillmentGroup, Long orderId){
		DefaultFullfillmentGroup newDfg = fullfillmentGroupDao.createDefault();
		newDfg.setAddress(fullfillmentGroup.getAddress());
		newDfg.setCost(fullfillmentGroup.getCost());
		newDfg.setFullfillmentGroupItems(fullfillmentGroup.getFullfillmentGroupItems());
		newDfg.setMethod(fullfillmentGroup.getMethod());
		newDfg.setOrderId(orderId);
		newDfg.setReferenceNumber(fullfillmentGroup.getReferenceNumber());
		return newDfg;
    	
    }
    
    protected FullfillmentGroupItem createFulfillmentGroupItemFromOrderItem(OrderItem orderItem, Long fulfillmentGroupId){
		FullfillmentGroupItem fgi = fullfillmentGroupItemDao.create();
		fgi.setFullfillmentGroupId(fulfillmentGroupId);
		fgi.setOrderItem(orderItem);
		fgi.setQuantity(orderItem.getQuantity());
    	return fgi;
    }
}
