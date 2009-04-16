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
import org.broadleafcommerce.catalog.service.CatalogService;
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

    @Resource
    private CatalogService catalogService;

    private boolean rollupOrderItems = true;

    private boolean moveNamedOrderItems = true;

    private boolean deleteEmptyNamedOrders = true;

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
        FulfillmentGroup fg  = null;
        try{
            fg = fulfillmentGroupDao.readDefaultFulfillmentGroupForOrder(order);
        }catch(NoResultException nre){
            fg = createDefaultFulfillmentGroup(order);
            fg = fulfillmentGroupDao.maintainDefaultFulfillmentGroup(fg);
        }
        if (fg.getFulfillmentGroupItems() == null || fg.getFulfillmentGroupItems().size() == 0) {
            // Only Default fulfillment group has been created so
            // add all orderItems for order to group
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                FulfillmentGroupItem fgi = this.createFulfillmentGroupItemFromOrderItem(orderItem, fg.getId());
                fgi = fulfillmentGroupItemDao.maintainFulfillmentGroupItem(fgi);
                fg.addFulfillmentGroupItem(fgi);
            }
            // Go ahead and persist it so we don't have to do this later
            fulfillmentGroupDao.maintainDefaultFulfillmentGroup(fg);
        }
        return fg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItem addSkuToOrder(Order order, Sku sku, int quantity) {
        return addSkuToOrder(order, sku, null, null, quantity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItem addSkuToOrder(Order order, Sku sku, Product product, Category category, int quantity) {
        OrderItem orderItem = addSkuToLocalOrder(order, sku, product, category, quantity);
        return maintainOrderItem(orderItem);
    }

    @Override
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, int quantity) {
        Order order = orderDao.readOrderById(orderId);
        Sku sku = catalogService.findSkuById(skuId);
        Product product = catalogService.findProductById(productId);
        Category category = catalogService.findCategoryById(categoryId);
        return addSkuToOrder(order, sku, product, category, quantity);
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
    public OrderItem addItemToCartFromNamedOrder(Order order, Sku sku, int quantity) {
        removeItemFromOrder(order, sku.getId());
        return addSkuToOrder(order, sku, quantity);
    }

    @Override
    public Order addAllItemsToCartFromNamedOrder(Order namedOrder) {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer(), true);
        for (OrderItem orderItem : namedOrder.getOrderItems()) {
            if(moveNamedOrderItems) {
                removeItemFromOrder(namedOrder, orderItem.getId());
            }
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
            //          order.setFulfillmentGroups(new Vector<FulfillmentGroup>([fg]));
            FulfillmentGroup newDfg = findDefaultFulfillmentGroupForOrder(order);

            order.addFulfillmentGroup(newDfg);
            newDfg.setAddress(fulfillmentGroup.getAddress());
            fulfillmentGroupDao.maintainDefaultFulfillmentGroup(newDfg);
            return newDfg;
        }
        // if(dfg == null){
        // }else
        if (dfg.getId().equals(fulfillmentGroup.getId())) {
            // API user is trying to re-add the default fulfillment group
            // to the same order
            // um....treat it as update/maintain for now
            return fulfillmentGroupDao.maintainDefaultFulfillmentGroup(fulfillmentGroup);
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
            //fulfillmentGroup.setFulfillmentGroupItems(new ArrayList<FulfillmentGroupItem>());
        }
        // API user is trying to add an item to an existing fulfillment group
        // Steps are

        order = orderDao.readOrderById(item.getOrderId());


        // 1) Find the item's existing fulfillment group
        for (Iterator<FulfillmentGroup> fgIterator = order.getFulfillmentGroups().iterator(); fgIterator.hasNext();) {
            FulfillmentGroup fg = fgIterator.next();
            if (fg.getFulfillmentGroupItems() != null) {
                List<FulfillmentGroupItem> itemsToRemove = new ArrayList<FulfillmentGroupItem>();
                for (Iterator<FulfillmentGroupItem> fgiIterator = fg.getFulfillmentGroupItems().iterator(); fgiIterator.hasNext();) {
                    FulfillmentGroupItem tempFgi =  fgiIterator.next();
                    if (tempFgi.getOrderItem().getId().equals(item.getId())) {
                        fgi = tempFgi;
                        // 2) remove item from it's existing fulfillment group
                        itemsToRemove.add(fgi);
                    }
                }
                fg.getFulfillmentGroupItems().remove(itemsToRemove);
                fulfillmentGroupDao.maintainFulfillmentGroup(fg);
            }
        }
        if (fgi == null) {
            fgi = createFulfillmentGroupItemFromOrderItem(item, fulfillmentGroup.getId());
        }

        // 3) add the item to the new fulfillment group
        if(fulfillmentGroup.getType()!= null && fulfillmentGroup.getType() != FulfillmentGroupType.DEFAULT) {
            fulfillmentGroup.addFulfillmentGroupItem(fgi);
        }

        fulfillmentGroup = fulfillmentGroupDao.maintainFulfillmentGroup(fulfillmentGroup);
        fgi.setFulfillmentGroupId(fulfillmentGroup.getId());
        fgi = fulfillmentGroupItemDao.maintainFulfillmentGroupItem(fgi);
        return fulfillmentGroup;
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
    public OrderItem moveItemToCartFromNamedOrder(Order namedOrder, Long orderItemId, int quantity) {
        OrderItem orderItem = orderItemDao.readOrderItemById(orderItemId);
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer(), true);
        if(moveNamedOrderItems) {
            Order updatedNamedOrder = removeItemFromOrder(namedOrder, orderItem);
            if(updatedNamedOrder.getOrderItems().size() == 0 && deleteEmptyNamedOrders) {
                orderDao.deleteOrderForCustomer(updatedNamedOrder);
            }

        }
        return addSkuToOrder(cartOrder, orderItem.getSku(), orderItem.getProduct(), orderItem.getCategory(), quantity);

    }

    @Override
    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder) {
        Order cartOrder = addAllItemsToCartFromNamedOrder(namedOrder);
        if (deleteEmptyNamedOrders) {
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
        if (order.getFulfillmentGroups() != null) {
            for (Iterator<FulfillmentGroup> iterator = order.getFulfillmentGroups().iterator(); iterator.hasNext();) {
                FulfillmentGroup fulfillmentGroup = iterator.next();
                iterator.remove();
                fulfillmentGroupDao.removeFulfillmentGroupForOrder(order, fulfillmentGroup);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup) {
        order.getFulfillmentGroups().remove(fulfillmentGroup);
        maintainOrder(order);
        fulfillmentGroupDao.removeFulfillmentGroupForOrder(order, fulfillmentGroup);
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

    protected FulfillmentGroup createDefaultFulfillmentGroup(Order order) {
        FulfillmentGroup newFg = fulfillmentGroupDao.createDefault();
        newFg.setOrderId(order.getId());
        newFg.setType(FulfillmentGroupType.DEFAULT);
        //        for (OrderItem orderItem : order.getOrderItems()) {
        //            newFg = addItemToFulfillmentGroup(orderItem, newFg, orderItem.getQuantity());
        //        }

        return newFg;
    }


    protected FulfillmentGroupItem createFulfillmentGroupItemFromOrderItem(OrderItem orderItem, Long fulfillmentGroupId) {
        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.create();
        fgi.setFulfillmentGroupId(fulfillmentGroupId);
        fgi.setOrderItem(orderItem);
        fgi.setQuantity(orderItem.getQuantity());
        return fgi;
    }

    protected OrderItem addSkuToLocalOrder(Order order, Sku sku, Product product, Category category, int quantity) {
        OrderItem orderItem = null;
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null && rollupOrderItems) {
            for (OrderItem orderItem2 : orderItems) {
                if (orderItem2.getSku().getId().equals(sku.getId())) {
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
        orderItem.setSku(sku);
        orderItem.setQuantity(orderItem.getQuantity() + quantity);
        // orderItem.setOrder(order);
        orderItem.setOrderId(order.getId());
        return orderItem;
    }

    /*
     * (non-Javadoc)
     * @seeorg.broadleafcommerce.order.service.OrderService#mergeCart(org.
     * broadleafcommerce.profile.domain.Customer, java.lang.Long)
     */
    @Override
    public MergeCartResponse mergeCart(Customer customer, Long anonymousCartId) {
        MergeCartResponse mergeCartResponse = new MergeCartResponse();
        Order customerCart = findCartForCustomer(customer, false);
        // reconstruct cart items (make sure they are valid)
        ReconstructCartResponse reconstructCartResponse = reconstructCart(customer);
        mergeCartResponse.setRemovedItems(reconstructCartResponse.getRemovedItems());
        customerCart = reconstructCartResponse.getOrder();

        // add anonymous cart items (make sure they are valid)
        if ((customerCart == null || !customerCart.getId().equals(anonymousCartId)) && anonymousCartId != null) {
            Order anonymousCart = findOrderById(anonymousCartId);
            if (anonymousCart != null && anonymousCart.getOrderItems() != null && !anonymousCart.getOrderItems().isEmpty()) {
                if (customerCart == null) {
                    customerCart = findCartForCustomer(customer, true);
                }
                // TODO improve merge algorithm to support various requirements
                // currently we'll just add items
                for (OrderItem orderItem : anonymousCart.getOrderItems()) {
                    if (orderItem.getSku().isActive(orderItem.getProduct(), orderItem.getCategory())) {
                        addSkuToOrder(customerCart, orderItem.getSku(), orderItem.getProduct(), orderItem.getCategory(), orderItem.getQuantity());
                        mergeCartResponse.getAddedItems().add(orderItem);
                    } else {
                        mergeCartResponse.getRemovedItems().add(orderItem);
                    }
                    removeItemFromOrder(anonymousCart, orderItem.getId());
                    orderDao.deleteOrderForCustomer(anonymousCart);
                }
            }
        }
        mergeCartResponse.setOrder(customerCart);
        return mergeCartResponse;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.order.service.OrderService#reconstructCart(org.
     * broadleafcommerce.profile.domain.Customer)
     */
    @Override
    public ReconstructCartResponse reconstructCart(Customer customer) {
        ReconstructCartResponse reconstructCartResponse = new ReconstructCartResponse();
        Order customerCart = findCartForCustomer(customer, false);
        if (customerCart != null) {
            for (OrderItem orderItem : customerCart.getOrderItems()) {
                if (!orderItem.getSku().isActive(orderItem.getProduct(), orderItem.getCategory())) {
                    reconstructCartResponse.getRemovedItems().add(orderItem);
                    removeItemFromOrder(customerCart, orderItem.getId());
                }
            }
        }
        reconstructCartResponse.setOrder(customerCart);
        return reconstructCartResponse;
    }

    public boolean isRollupOrderItems() {
        return rollupOrderItems;
    }

    public void setRollupOrderItems(boolean rollupOrderItems) {
        this.rollupOrderItems = rollupOrderItems;
    }

    public boolean isMoveNamedOrderItems() {
        return moveNamedOrderItems;
    }

    public void setMoveNamedOrderItems(boolean moveNamedOrderItems) {
        this.moveNamedOrderItems = moveNamedOrderItems;
    }

    public boolean isDeleteEmptyNamedOrders() {
        return deleteEmptyNamedOrders;
    }

    public void setDeleteEmptyNamedOrders(boolean deleteEmptyNamedOrders) {
        this.deleteEmptyNamedOrders = deleteEmptyNamedOrders;
    }


}
