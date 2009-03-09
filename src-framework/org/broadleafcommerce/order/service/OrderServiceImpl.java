package org.broadleafcommerce.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.dao.FulfillmentGroupDao;
import org.broadleafcommerce.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.dao.PaymentInfoDao;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.dao.ContactInfoDao;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.promotion.domain.Offer;
import org.broadleafcommerce.type.FulfillmentGroupType;
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
    private FulfillmentGroupDao fulfillmentGroupDao;

    @Resource
    private FulfillmentGroupItemDao fulfillmentGroupItemDao;

    @Resource
    private ContactInfoDao contactInfoDao;

    @Resource
    private AddressDao addressDao;

    @Override
    public Order findCurrentCartForCustomer(Customer customer) {
        return orderDao.readCartOrdersForCustomer(customer);
    }

    @Override
    public FulfillmentGroupImpl findDefaultFulfillmentGroupForOrder(Order order) {
        FulfillmentGroupImpl fg = fulfillmentGroupDao.readDefaultFulfillmentGroupForOrder(order);
        if (fg.getFulfillmentGroupItems().size() == 0) {
            // Only Default fulfillment group has been created so
            // add all orderItems for order to group
            List<OrderItem> orderItems = orderItemDao.readOrderItemsForOrder(order);
            List<FulfillmentGroupItem> fgItems = new ArrayList<FulfillmentGroupItem>();
            for (OrderItem orderItem : orderItems) {
                fulfillmentGroupItemDao.create();
                fgItems.add(this.createFulfillmentGroupItemFromOrderItem(orderItem, fg.getId()));
            }
            fg.setFulfillmentGroupItems(fgItems);
            // Go ahead and persist it so we don't have to do this later
            // or not
            // fulfillmentGroupDao.maintainDefaultFulfillmentGroup(dfg);
        }
        return fg;
    }

    @Override
    public List<FulfillmentGroup> findFulfillmentGroupsForOrder(Order order) {
        return fulfillmentGroupDao.readFulfillmentGroupsForOrder(order);
    }

    @Override
    public List<Order> findOrdersForCustomer(Customer customer) {
        return orderDao.readOrdersForCustomer(customer);
    }

    @Override
    public List<OrderItem> findItemsForOrder(Order order) {
        List<OrderItem> result = orderItemDao.readOrderItemsForOrder(order);
        // TODO
        //        for (OrderItem oi : result) {
        //            oi.getSku().getItemAttributes();
        //        }
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
    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity) {

        FulfillmentGroupItem fgi = null;

        if (fulfillmentGroup.getId() == null) {
            // API user is trying to add an item to a fulfillment group not created
            fulfillmentGroup = addFulfillmentGroupToOrder(item.getOrder(), fulfillmentGroup);
        }
        // API user is trying to add an item to a fulfillment
        // Steps are

        // 1) Find the item's existing fulfillment group
        for (FulfillmentGroup fg : item.getOrder().getFulfillmentGroups()) {
            for (FulfillmentGroupItem tempFgi : fg.getFulfillmentGroupItems()) {
                if (tempFgi.getOrderItem().getId().equals(item.getId())) {
                    fgi = tempFgi;
                    // 2) remove item from it's existing fulfillment group
                    fg.getFulfillmentGroupItems().remove(fg);
                    fulfillmentGroupDao.maintainFulfillmentGroup(fg);
                }
            }
        }
        if (fgi == null)
            fgi = createFulfillmentGroupItemFromOrderItem(item, fulfillmentGroup.getId());

        // 3) add the item to the new fulfillment group
        fulfillmentGroupItemDao.maintainFulfillmentGroupItem(fgi);
        return fulfillmentGroupDao.readFulfillmentGroupById(fulfillmentGroup.getId());
    }

    @Override
    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup) {

        List<FulfillmentGroup> currentFulfillmentGroups = fulfillmentGroupDao.readFulfillmentGroupsForOrder(order);
        FulfillmentGroupImpl dfg;
        try {
            dfg = fulfillmentGroupDao.readDefaultFulfillmentGroupForOrder(order);
        } catch (NoResultException nre) {
            // This is the first fulfillment group added so make it the
            // default one
            return fulfillmentGroupDao.maintainDefaultFulfillmentGroup(createDefaultFulfillmentGroupFromFulfillmentGroup(fulfillmentGroup, order.getId()));
        }
        // if(dfg == null){
        // }else
        if (dfg.getId().equals(fulfillmentGroup.getId())) {
            // API user is trying to re-add the default fulfillment group
            // to the same order
            // um....treat it as update/maintain for now
            return fulfillmentGroupDao.maintainDefaultFulfillmentGroup(createDefaultFulfillmentGroupFromFulfillmentGroup(fulfillmentGroup, order.getId()));
        } else if (currentFulfillmentGroups.size() == 1) {
            // API user is adding first non default fulfillment group to the order
            // Steps are:
            // 1) Create a list of existing order items (that are by default in the default group)
            List<OrderItem> orderItems = orderItemDao.readOrderItemsForOrder(order);
            // 2) Create a list of fulfillment order items from existing order items
            List<FulfillmentGroupItem> fgItems = new ArrayList<FulfillmentGroupItem>();
            for (OrderItem orderItem : orderItems) {
                fgItems.add(createFulfillmentGroupItemFromOrderItem(orderItem, dfg.getId()));
            }
            // 3) Remove items in new fulfillment group from existing order items
            for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                fgItems.remove(fulfillmentGroupItem);
            }
            // 4) maintain default fulfillment group
            dfg.setFulfillmentGroupItems(fgItems);
            fulfillmentGroupDao.maintainDefaultFulfillmentGroup(dfg);
            // 5) maintain new fulfillment group, returning it
            fulfillmentGroup.setOrderId(order.getId());
            return fulfillmentGroupDao.maintainFulfillmentGroup(fulfillmentGroup);
        } else {
            // API user is adding a new fulfillment group to the order and
            // the order already has multiple fulfillment groups
            fulfillmentGroup.setOrderId(order.getId());

            // 1) For each item in the new fulfillment group
            for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {

                // 2) Find the item's existing fulfillment group
                for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                    for (FulfillmentGroupItem tempFgi : fg.getFulfillmentGroupItems()) {
                        if (tempFgi.getOrderItem().getId().equals(fgItem.getId())) {
                            // 3) remove item from it's existing fulfillment group
                            fg.getFulfillmentGroupItems().remove(fg);
                            fulfillmentGroupDao.maintainFulfillmentGroup(fg);
                        }
                    }
                }
            }

            return fulfillmentGroupDao.maintainFulfillmentGroup(fulfillmentGroup);
        }
    }

    @Override
    public FulfillmentGroup updateFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        return fulfillmentGroupDao.maintainFulfillmentGroup(fulfillmentGroup);
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
    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup) {
        fulfillmentGroupDao.removeFulfillmentGroupForOrder(order, fulfillmentGroup);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order calculateOrderTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItemList = orderItemDao.readOrderItemsForOrder(order);
        for (OrderItem item : orderItemList) {
            total = total.add(item.getFinalPrice());
        }

        List<FulfillmentGroup> fulfillmentGroupList = fulfillmentGroupDao.readFulfillmentGroupsForOrder(order);
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroupList) {
            total = total.add(fulfillmentGroup.getRetailPrice());
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
	public Order addOfferToOrder(Order order, String offerCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order removeOfferFromOrder(Order order, Offer offer) {
		// TODO Auto-generated method stub
		return null;
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
        orderItem.setFinalPrice(orderItem.getSku().getSalePrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        OrderItem returnedOrderItem = orderItemDao.maintainOrderItem(orderItem);
        maintainOrder(orderItem.getOrder());
        return returnedOrderItem;
    }

    protected FulfillmentGroupImpl createDefaultFulfillmentGroupFromFulfillmentGroup(FulfillmentGroup fulfillmentGroup, Long orderId) {
        FulfillmentGroupImpl newFg = fulfillmentGroupDao.createDefault();
        newFg.setAddress(fulfillmentGroup.getAddress());
        newFg.setRetailPrice(fulfillmentGroup.getRetailPrice());
        newFg.setFulfillmentGroupItems(fulfillmentGroup.getFulfillmentGroupItems());
        newFg.setMethod(fulfillmentGroup.getMethod());
        newFg.setOrderId(orderId);
        newFg.setReferenceNumber(fulfillmentGroup.getReferenceNumber());
        newFg.setType(FulfillmentGroupType.DEFAULT);
        return newFg;

    }

    protected FulfillmentGroupItem createFulfillmentGroupItemFromOrderItem(OrderItem orderItem, Long fulfillmentGroupId) {
        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.create();
        fgi.setFulfillmentGroupId(fulfillmentGroupId);
        fgi.setOrderItem(orderItem);
        fgi.setQuantity(orderItem.getQuantity());
        return fgi;
    }
}
