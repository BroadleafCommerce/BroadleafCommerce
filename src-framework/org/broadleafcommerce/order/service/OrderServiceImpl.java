package org.broadleafcommerce.order.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.dao.OrderPaymentDao;
import org.broadleafcommerce.order.dao.OrderShippingDao;
import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderPayment;
import org.broadleafcommerce.order.domain.OrderShipping;
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
    private OrderPaymentDao orderPaymentDao;

    @Resource
    private OrderShippingDao orderShippingDao;

    @Resource
    private SkuDao skuDao;

    @Resource
    private ContactInfoDao contactInfoDao;

    @Resource
    private CustomerDao customerDao;

    @Resource
    private AddressDao addressDao;

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder createOrderForCustomer(Customer customer) {
        BroadleafOrder order = new BroadleafOrder();
        order.setCustomer(customer);
        return maintainOrder(order);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder addContactInfoToOrder(BroadleafOrder order, ContactInfo contactInfo){
        if(contactInfo.getId() == null){
            contactInfoDao.maintainContactInfo(contactInfo);
        }
        order.setContactInfo(contactInfo);
        return maintainOrder(order);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public OrderPayment addPaymentToOrder(BroadleafOrder order, OrderPayment payment) {
        payment.setOrder(order);
        if(payment.getAddress()!= null && payment.getAddress().getId() == null){
            payment.setAddress(addressDao.maintainAddress(payment.getAddress()));
        }
        return orderPaymentDao.maintainOrderPayment(payment);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public OrderShipping addShippingToOrder(BroadleafOrder order, OrderShipping shipping) {
        shipping.setOrder(order);
        if(shipping.getAddress() != null && shipping.getAddress().getId() == null){
            shipping.setAddress(addressDao.maintainAddress(shipping.getAddress()));
        }
        return orderShippingDao.maintainOrderShipping(shipping);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder calculateOrderTotal(BroadleafOrder order) {
        double total = 0;
        List<OrderItem> orderItemList = orderItemDao.readOrderItemsForOrder(order);
        for(OrderItem item : orderItemList){
            total += item.getFinalPrice();
        }

        List<OrderShipping> shippingList = orderShippingDao.readOrderShippingForOrder(order);
        for(OrderShipping shipping : shippingList){
            total += shipping.getCost();
        }
        order.setOrderTotal(total);
        return order;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void cancelOrder(BroadleafOrder order) {
        orderDao.deleteOrderForCustomer(order);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder confirmOrder(BroadleafOrder order) {
        // TODO Other actions needed to complete order.  Code below is only a start.
        return orderDao.submitOrder(order);
    }

    @Override
    public List<OrderItem> getItemsForOrder(BroadleafOrder order) {
        List<OrderItem> result = orderItemDao.readOrderItemsForOrder(order);
        for (OrderItem oi : result) {
            oi.getSku().getItemAttributes();
        }
        return result;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public OrderItem addItemToOrder(BroadleafOrder order, Sku item, int quantity){
        OrderItem orderItem = null;
        List<OrderItem> orderItems = orderItemDao.readOrderItemsForOrder(order);
        for (OrderItem orderItem2 : orderItems) {
            if(orderItem2.getSku().getId().equals(item.getId()))
                orderItem = orderItem2;
        }
        if(orderItem == null)
            orderItem = new OrderItem();
        orderItem.setSku(item);
        orderItem.setQuantity(orderItem.getQuantity()+quantity);
        orderItem.setOrder(order);
        return maintainOrderItem(orderItem);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder removeItemFromOrder(BroadleafOrder order, OrderItem item) {
        orderItemDao.deleteOrderItem(item);
        calculateOrderTotal(order);
        return order;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public OrderItem updateItemInOrder(BroadleafOrder order, OrderItem item) {
        // This isn't quite right.  It will need to be changed later to reflect
        // the exact requirements we want.
        // item.setQuantity(quantity);
        item.setOrder(order);
        return maintainOrderItem(item);
    }

    @Override
    public List<BroadleafOrder> getOrdersForCustomer(Customer customer) {
        return orderDao.readOrdersForCustomer(customer);
    }

    @Override
    public BroadleafOrder getCurrentBasketForCustomer(Customer customer) {
        return orderDao.readBasketOrderForCustomer(customer);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder createOrderForCustomer(long customerId){
        Customer customer = customerDao.readCustomerById(customerId);
        return createOrderForCustomer(customer);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public OrderItem addItemToOrder(Long orderId, Long itemId, int quantity) {
        BroadleafOrder order = orderDao.readOrderById(orderId);
        Sku si = skuDao.readSkuById(itemId);
        return this.addItemToOrder(order, si, quantity);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public OrderPayment addPaymentToOrder(Long orderId, Long paymentId) {
        BroadleafOrder order = orderDao.readOrderById(orderId);
        OrderPayment sop = orderPaymentDao.readOrderPaymentById(paymentId);
        return this.addPaymentToOrder(order, sop);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public OrderShipping addShippingToOrder(Long orderId, Long shippingId) {
        BroadleafOrder order = orderDao.readOrderById(orderId);
        OrderShipping shipping = orderShippingDao.readOrderShippingById(shippingId);
        return this.addShippingToOrder(order, shipping);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder calculateOrderTotal(Long orderId) {
        BroadleafOrder order = orderDao.readOrderById(orderId);
        return this.calculateOrderTotal(order);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void cancelOrder(Long orderId) {
        BroadleafOrder order = orderDao.readOrderById(orderId);
        this.cancelOrder(order);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder confirmOrder(Long orderId) {
        BroadleafOrder order = orderDao.readOrderById(orderId);
        return this.confirmOrder(order);
    }

    @Override
    public List<OrderItem> getItemsForOrder(Long orderId) {
        BroadleafOrder order = orderDao.readOrderById(orderId);
        return this.getItemsForOrder(order);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder removeItemFromOrder(Long orderId, Long itemId) {
        BroadleafOrder order = orderDao.readOrderById(orderId);
        OrderItem item = orderItemDao.readOrderItemById(itemId);
        return this.removeItemFromOrder(order, item);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public OrderItem updateItemInOrder(Long orderId, Long itemId, int quantity, double finalPrice) {
        BroadleafOrder order = orderDao.readOrderById(orderId);
        OrderItem item = orderItemDao.readOrderItemById(itemId);
        item.setQuantity(quantity);
        item.setFinalPrice(finalPrice);
        return this.updateItemInOrder(order, item);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder addContactInfoToOrder(Long orderId, Long contactId){
        BroadleafOrder order = orderDao.readOrderById(orderId);
        ContactInfo ci = contactInfoDao.readContactInfoById(contactId);
        return this.addContactInfoToOrder(order, ci);

    }

    @Override
    public List<BroadleafOrder> getOrdersForCustomer(Long userId) {
        return orderDao.readOrdersForCustomer(userId);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public BroadleafOrder getCurrentBasketForUserId(Long userId) {
        return orderDao.readBasketOrderForCustomer(customerDao.readCustomerById(userId));
    }

    private BroadleafOrder maintainOrder(BroadleafOrder order){
        calculateOrderTotal(order);
        return orderDao.maintianOrder(order);
    }

    private OrderItem maintainOrderItem(OrderItem orderItem){
        orderItem.setFinalPrice(orderItem.getQuantity() * orderItem.getSku().getPrice());
        OrderItem returnedOrderItem = orderItemDao.maintainOrderItem(orderItem);
        maintainOrder(orderItem.getOrder());
        return returnedOrderItem;
    }

}
