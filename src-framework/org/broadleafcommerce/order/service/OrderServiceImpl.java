package org.broadleafcommerce.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MethodNotSupportedException;
import javax.persistence.NoResultException;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.order.dao.FulfillmentGroupDao;
import org.broadleafcommerce.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.dao.PaymentInfoDao;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.pricing.service.PricingService;
import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.type.FulfillmentGroupType;
import org.broadleafcommerce.type.OrderStatus;
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
    private AddressDao addressDao;

    @Resource
    private PricingService pricingService;

    private boolean rollupOrderItems = true;

    @Override
    public Order createNamedOrderForCustomer(String name, Customer customer) {
        Order namedOrder = orderDao.create();
        namedOrder.setCustomer(customer);
        namedOrder.setName(name);
        namedOrder.setStatus(OrderStatus.NAMED);
        return orderDao.maintianOrder(namedOrder);

    }

    @Override
    public Order findOrderById(Long orderId) {
        return orderDao.readOrderById(orderId);
    }

    @Override
    public Order findCartForCustomer(Customer customer, boolean createIfDoesntExist) {
        return orderDao.readCartForCustomer(customer, createIfDoesntExist);
    }

    @Override
    public Order findCartForCustomer(Customer customer) {
        return orderDao.readCartForCustomer(customer, false);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Order> findOrdersForCustomer(Customer customer) {
        return orderDao.readOrdersForCustomer(customer.getId());
    }

    @Override
    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus status) {
        return orderDao.readOrdersForCustomer(customer, status);
    }

    @Override
    public Order findNamedOrderForCustomer(String name, Customer customer) {
        return orderDao.readNamedOrderForCustomer(customer, name);
    }

    @Override
    public FulfillmentGroup findDefaultFulfillmentGroupForOrder(Order order) {
        FulfillmentGroup fg = fulfillmentGroupDao.readDefaultFulfillmentGroupForOrder(order);
        if (fg.getFulfillmentGroupItems().size() == 0) {
            // Only Default fulfillment group has been created so
            // add all orderItems for order to group
            List<OrderItem> orderItems = order.getOrderItems();
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
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItem addSkuToOrder(Order order, Sku item, int quantity) {
        return addSkuToOrder(order, item, null, null, quantity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItem addSkuToOrder(Order order, Sku item, Product product, Category category, int quantity) {
        OrderItem orderItem = addSkuToLocalOrder(order, item, product, category, quantity);
        return maintainOrderItem(orderItem);
    }

    @Override
    public List<OrderItem> addSkusToOrder(Map<String, Integer> skuIdQtyMap, Order order) throws MethodNotSupportedException {
        // for (String skuId : skuIdQtyMap.keySet()) {
        // Sku sku = catalogservice.findSkuById(skuId);
        // }
        // // TODO Implement if needed
        // return null;
        throw new MethodNotSupportedException();
    }

    @Override
    public OrderItem addItemToCartFromNamedOrder(Order order, Sku item, int quantity) {
        removeItemFromOrder(order, item.getId());
        return addSkuToOrder(order, item, quantity);
    }

    @Override
    public Order addAllItemsToCartFromNamedOrder(Order namedOrder) {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer(), true);
        for (OrderItem orderItem : namedOrder.getOrderItems()) {
            removeItemFromOrder(namedOrder, orderItem.getId());
            addSkuToOrder(cartOrder, orderItem.getSku(), orderItem.getQuantity());
        }
        return cartOrder;
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
    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup) {

        FulfillmentGroup dfg;
        try {
            dfg = fulfillmentGroupDao.readDefaultFulfillmentGroupForOrder(order);
        } catch (NoResultException nre) {
            // This is the first fulfillment group added so make it the
            // default one
            // return
            // fulfillmentGroupDao.maintainDefaultFulfillmentGroup(createDefaultFulfillmentGroupFromFulfillmentGroup(fulfillmentGroup,
            // order));
            return createDefaultFulfillmentGroupFromFulfillmentGroup(fulfillmentGroup, order);
        }
        // if(dfg == null){
        // }else
        if (dfg.getId().equals(fulfillmentGroup.getId())) {
            // API user is trying to re-add the default fulfillment group
            // to the same order
            // um....treat it as update/maintain for now
            return createDefaultFulfillmentGroupFromFulfillmentGroup(fulfillmentGroup, order);
            // return
            // fulfillmentGroupDao.maintainDefaultFulfillmentGroup(createDefaultFulfillmentGroupFromFulfillmentGroup(
            // fulfillmentGroup, order));
        } else {
            // API user is adding a new fulfillment group to the order
            fulfillmentGroup.setOrderId(order.getId());

            // 1) For each item in the new fulfillment group
            if (fulfillmentGroup.getFulfillmentGroupItems() != null) {

                for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {

                    // 2) Find the item's existing fulfillment group

                    for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                        for (FulfillmentGroupItem tempFgi : fg.getFulfillmentGroupItems()) {
                            if (tempFgi.getOrderItem().getId().equals(fgItem.getId())) {
                                // 3) remove item from it's existing fulfillment
                                // group
                                fg.getFulfillmentGroupItems().remove(fg);
                            }
                        }
                        fulfillmentGroupDao.maintainFulfillmentGroup(fg);
                    }
                }
            }

            FulfillmentGroup returnedFg = fulfillmentGroupDao.maintainFulfillmentGroup(fulfillmentGroup);
            order.addFulfillmentGroup(returnedFg);
            maintainOrder(order);
            return returnedFg;
        }
    }

    @Override
    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity) {

        FulfillmentGroupItem fgi = null;
        Order order = orderDao.readOrderById(item.getOrderId());

        if (fulfillmentGroup.getId() == null) {
            // API user is trying to add an item to a fulfillment group not
            // created
            fulfillmentGroup = addFulfillmentGroupToOrder(order, fulfillmentGroup);
        }
        // API user is trying to add an item to a fulfillment
        // Steps are

        // 1) Find the item's existing fulfillment group
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem tempFgi : fg.getFulfillmentGroupItems()) {
                if (tempFgi.getOrderItem().getId().equals(item.getId())) {
                    fgi = tempFgi;
                    // 2) remove item from it's existing fulfillment group
                    fg.getFulfillmentGroupItems().remove(fg);
                    fulfillmentGroupDao.maintainFulfillmentGroup(fg);
                }
            }
        }
        if (fgi == null) {
            fgi = createFulfillmentGroupItemFromOrderItem(item, fulfillmentGroup.getId());
        }

        // 3) add the item to the new fulfillment group
        fulfillmentGroupItemDao.maintainFulfillmentGroupItem(fgi);
        return fulfillmentGroupDao.readFulfillmentGroupById(fulfillmentGroup.getId());
    }

    @Override
    public Order addOfferToOrder(Order order, String offerCode) {
        throw new UnsupportedOperationException();
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
        // item.setOrder(order);
        return maintainOrderItem(item);
    }

    @Override
    public List<OrderItem> updateItemsInOrder(Order order, List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            // orderItem.setOrder(order);
            // TODO change this so it persists them all at once instead of each
            // at a time
            maintainOrderItem(orderItem);
        }
        return orderItems;
    }

    @Override
    public OrderItem moveItemToCartFromNamedOrder(Order namedOrder, Sku item, int quantity) {
        removeItemFromOrder(namedOrder, item.getId());
        return addSkuToOrder(namedOrder, item, quantity);

    }

    @Override
    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder, boolean deleteNamedOrder) {
        Order cartOrder = addAllItemsToCartFromNamedOrder(namedOrder);
        if (deleteNamedOrder) {
            orderDao.deleteOrderForCustomer(namedOrder);
        }
        return cartOrder;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order removeItemFromOrder(Order order, long orderItemId) {
        OrderItem orderItem = orderItemDao.readOrderItemById(orderItemId);
        if (orderItem == null) {
            return null;
        }
        orderItemDao.deleteOrderItem(orderItem);
        pricingService.calculateOrderTotal(order);
        return orderDao.readOrderById(order.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order removeItemFromOrder(Order order, OrderItem item) {
        orderItemDao.deleteOrderItem(item);
        order.getFulfillmentGroups();
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fgItem : fg.getFulfillmentGroupItems()) {
                if (fgItem.getOrderItem().equals(item)) {
                    fulfillmentGroupItemDao.deleteFulfillmentGroupItem(fgItem);
                }
            }
        }
        pricingService.calculateOrderTotal(order);
        order.getOrderItems().remove(item);
        orderDao.maintianOrder(order);
        return orderDao.readOrderById(order.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAllFulfillmentGroupsFromOrder(Order order) {
        for (Iterator<FulfillmentGroup> iterator = order.getFulfillmentGroups().iterator(); iterator.hasNext();) {
            iterator.next();
            iterator.remove();
        }
        maintainOrder(order);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup) {
        order.getFulfillmentGroups().remove(fulfillmentGroup);
        maintainOrder(order);
    }

    @Override
    public Order removeOfferFromOrder(Order order, Offer offer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNamedOrderForCustomer(String name, Customer customer) {
        Order namedOrder = findNamedOrderForCustomer(name, customer);
        orderDao.deleteOrderForCustomer(namedOrder);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order confirmOrder(Order order) {
        // TODO Other actions needed to complete order
        // (such as calling something to make sure the order is fulfilled
        // somehow).
        // Code below is only a start.
        return orderDao.submitOrder(order);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelOrder(Order order) {
        orderDao.deleteOrderForCustomer(order);
    }

    protected Order maintainOrder(Order order) {
        pricingService.calculateOrderTotal(order);
        return orderDao.maintianOrder(order);
    }

    protected OrderItem maintainOrderItem(OrderItem orderItem) {
        orderItem.setPrice(orderItem.getSku().getSalePrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        OrderItem returnedOrderItem = orderItemDao.maintainOrderItem(orderItem);
        // maintainOrder(orderItem.getOrder());
        return returnedOrderItem;
    }

    protected FulfillmentGroup createDefaultFulfillmentGroupFromFulfillmentGroup(FulfillmentGroup fulfillmentGroup, Order order) {
        FulfillmentGroup newFg = fulfillmentGroupDao.createDefault();
        newFg.setAddress(fulfillmentGroup.getAddress());
        newFg.setRetailPrice(fulfillmentGroup.getRetailPrice());
        newFg.setFulfillmentGroupItems(fulfillmentGroup.getFulfillmentGroupItems());
        newFg.setMethod(fulfillmentGroup.getMethod());
        // newFg.setOrderId(orderId);
        newFg.setOrderId(order.getId());
        newFg.setReferenceNumber(fulfillmentGroup.getReferenceNumber());
        newFg.setType(FulfillmentGroupType.DEFAULT);
        newFg = fulfillmentGroupDao.maintainDefaultFulfillmentGroup(newFg);
        order.addFulfillmentGroup(newFg);
        order = maintainOrder(order);
        for (OrderItem orderItem : order.getOrderItems()) {
            newFg = addItemToFulfillmentGroup(orderItem, newFg, orderItem.getQuantity());
        }

        return newFg;

    }

    protected FulfillmentGroupItem createFulfillmentGroupItemFromOrderItem(OrderItem orderItem, Long fulfillmentGroupId) {
        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.create();
        fgi.setFulfillmentGroupId(fulfillmentGroupId);
        fgi.setOrderItem(orderItem);
        fgi.setQuantity(orderItem.getQuantity());
        return fgi;
    }

    protected OrderItem addSkuToLocalOrder(Order order, Sku item, Product product, Category category, int quantity) {
        OrderItem orderItem = null;
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null && rollupOrderItems) {
            for (OrderItem orderItem2 : orderItems) {
                if (orderItem2.getSku().getId().equals(item.getId())) {
                    orderItem = orderItem2;
                    break;
                }
            }
        }
        if (orderItem == null) {
            // orderItem = new OrderItem();
            orderItem = orderItemDao.create();
        }
        orderItem.setProduct(product);
        orderItem.setCategory(category);
        orderItem.setSku(item);
        orderItem.setQuantity(orderItem.getQuantity() + quantity);
        // orderItem.setOrder(order);
        orderItem.setOrderId(order.getId());
        return orderItem;
    }

    @Override
    public MergeCartResponse mergeCart(Customer customer, Long anonymousCartId) {
        MergeCartResponse mergeCartResponse = new MergeCartResponse();
        Order customerCart = findCartForCustomer(customer, false);
        Order anonymousCart = findOrderById(anonymousCartId);
        if (anonymousCart != null && anonymousCart.getOrderItems() != null && !anonymousCart.getOrderItems().isEmpty()) {
            if (customerCart == null) {
                customerCart = findCartForCustomer(customer, true);
            }
            // TODO improve merge algorithm to support various requirements -
            // currently we'll just add items
            for (OrderItem orderItem : anonymousCart.getOrderItems()) {
                if (orderItem.getSku().isActive()) {
                    addSkuToOrder(customerCart, orderItem.getSku(), orderItem.getProduct(), orderItem.getCategory(), orderItem.getQuantity());
                    mergeCartResponse.getAddedItems().add(orderItem);
                } else {
                    mergeCartResponse.getRemovedItems().add(orderItem);
                }
                removeItemFromOrder(anonymousCart, orderItem.getId());
                orderDao.deleteOrderForCustomer(anonymousCart);
            }
        }
        mergeCartResponse.setOrder(customerCart);
        return mergeCartResponse;
    }

    public boolean isRollupOrderItems() {
        return rollupOrderItems;
    }

    public void setRollupOrderItems(boolean rollupOrderItems) {
        this.rollupOrderItems = rollupOrderItems;
    }
}
