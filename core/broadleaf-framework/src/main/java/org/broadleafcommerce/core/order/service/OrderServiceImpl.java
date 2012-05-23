/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.service;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupDao;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.dao.OrderDao;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.domain.OrderItemAttributeImpl;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.exception.OrderServiceException;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.dao.PaymentInfoDao;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.pricing.service.advice.PricingExecutionManager;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OrderServiceImpl implements OrderService {

    private static final Log LOG = LogFactory.getLog(OrderServiceImpl.class);

    @Resource(name = "blOrderDao")
    protected OrderDao orderDao;

    @Resource(name = "blPaymentInfoDao")
    protected PaymentInfoDao paymentInfoDao;

    @Resource(name = "blFulfillmentGroupDao")
    protected FulfillmentGroupDao fulfillmentGroupDao;

    @Resource(name = "blFulfillmentGroupItemDao")
    protected FulfillmentGroupItemDao fulfillmentGroupItemDao;

    @Resource(name = "blOfferDao")
    protected OfferDao offerDao;

    @Resource(name = "blPricingExecutionManager")
    protected PricingExecutionManager pricingExecutionManager;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name = "blOrderItemDao")
    protected OrderItemDao orderItemDao;

    @Resource(name = "blSkuDao")
    protected SkuDao skuDao;

    @Resource(name = "blProductDao")
    protected ProductDao productDao;

    @Resource(name = "blCategoryDao")
    protected CategoryDao categoryDao;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blSecurePaymentInfoService")
    protected SecurePaymentInfoService securePaymentInfoService;
    
    @Resource(name = "blOfferService")
    protected OfferService offerService;

    public Order createNamedOrderForCustomer(String name, Customer customer) {
        Order namedOrder = orderDao.create();
        namedOrder.setCustomer(customer);
        namedOrder.setName(name);
        namedOrder.setStatus(OrderStatus.NAMED);
        return persistOrder(namedOrder);
    }

    public Order save(Order order, Boolean priceOrder) throws PricingException {
        return updateOrder(order, priceOrder);
    }

    public Order findOrderById(Long orderId) {
        return orderDao.readOrderById(orderId);
    }

    public List<Order> findOrdersForCustomer(Customer customer) {
        return orderDao.readOrdersForCustomer(customer.getId());
    }

    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus status) {
        return orderDao.readOrdersForCustomer(customer, status);
    }

    public Order findNamedOrderForCustomer(String name, Customer customer) {
        return orderDao.readNamedOrderForCustomer(customer, name);
    }

    public FulfillmentGroup findDefaultFulfillmentGroupForOrder(Order order) {
        FulfillmentGroup fg = fulfillmentGroupDao.readDefaultFulfillmentGroupForOrder(order);

        return fg;
    }

    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity) throws PricingException {
    	return addSkuToOrder(orderId, skuId, productId, categoryId, quantity, true, null);
    }
    
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, Map<String,String> itemAttributes) throws PricingException {
    	return addSkuToOrder(orderId, skuId, productId, categoryId, quantity, true, itemAttributes);
    }
    
    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, boolean priceOrder) throws PricingException {
        return addSkuToOrder(orderId, skuId, productId, categoryId, quantity, priceOrder, null);
    }

    public OrderItem addSkuToOrder(Long orderId, Long skuId, Long productId, Long categoryId, Integer quantity, boolean priceOrder, Map<String,String> itemAttributes) throws PricingException {
        if (orderId == null || skuId == null || quantity == null) {
            return null;
        }

        OrderItemRequestDTO requestDTO = new OrderItemRequestDTO();
        requestDTO.setCategoryId(categoryId);
        requestDTO.setProductId(productId);
        requestDTO.setSkuId(skuId);
        requestDTO.setQuantity(quantity);
        requestDTO.setItemAttributes(itemAttributes);

        return addItemToOrder(orderId, requestDTO, priceOrder);
    }

    public OrderItem addDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest) throws PricingException {
    	return addDiscreteItemToOrder(order, itemRequest, true);
    }

    public OrderItem addDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, boolean priceOrder) throws PricingException {
        DiscreteOrderItem item = orderItemService.createDiscreteOrderItem(itemRequest);
        return addOrderItemToOrder(order, item, priceOrder);
    }

    public OrderItem addDynamicPriceDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) throws PricingException {
    	return addDynamicPriceDiscreteItemToOrder(order, itemRequest, skuPricingConsiderations, true);
    }

    public OrderItem addDynamicPriceDiscreteItemToOrder(Order order, DiscreteOrderItemRequest itemRequest, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations, boolean priceOrder) throws PricingException {
        DiscreteOrderItem item = orderItemService.createDynamicPriceDiscreteOrderItem(itemRequest, skuPricingConsiderations);
        return addOrderItemToOrder(order, item, priceOrder);
    }
    
    public OrderItem addGiftWrapItemToOrder(Order order, GiftWrapOrderItemRequest itemRequest) throws PricingException {
    	return addGiftWrapItemToOrder(order, itemRequest, true);
    }

    public OrderItem addGiftWrapItemToOrder(Order order, GiftWrapOrderItemRequest itemRequest, boolean priceOrder) throws PricingException {
        GiftWrapOrderItem item = orderItemService.createGiftWrapOrderItem(itemRequest);
        return addOrderItemToOrder(order, item, priceOrder);
    }
    
    public OrderItem addBundleItemToOrder(Order order, BundleOrderItemRequest itemRequest) throws PricingException {
    	return addBundleItemToOrder(order, itemRequest, true);
    }

    public OrderItem addBundleItemToOrder(Order order, BundleOrderItemRequest itemRequest, boolean priceOrder) throws PricingException {
        BundleOrderItem item = orderItemService.createBundleOrderItem(itemRequest);
        return addOrderItemToOrder(order, item, priceOrder);
    }
    
    public Order removeItemFromOrder(Long orderId, Long itemId) throws PricingException {
    	return removeItemFromOrder(orderId, itemId, true);
    }

    public Order removeItemFromOrder(Long orderId, Long itemId, boolean priceOrder) throws PricingException {
        Order order = findOrderById(orderId);
        OrderItem orderItem = orderItemService.readOrderItemById(itemId);

        return removeItemFromOrder(order, orderItem, priceOrder);
    }
    
    public Order removeItemFromOrder(Order order, OrderItem item) throws PricingException {
    	return removeItemFromOrder(order, item, true);
    }

    public Order removeItemFromOrder(Order order, OrderItem item, boolean priceOrder) throws PricingException {
        removeOrderItemFromFullfillmentGroup(order, item);
        OrderItem itemFromOrder = order.getOrderItems().remove(order.getOrderItems().indexOf(item));
        orderItemService.delete(itemFromOrder);
        itemFromOrder.setOrder(null);
        order = updateOrder(order, priceOrder);
        return order;
    }
    
    public Order moveItemToOrder(Order originalOrder, Order destinationOrder, OrderItem item) throws PricingException {
    	return moveItemToOrder(originalOrder, destinationOrder, item, true);
    }
    
    public Order moveItemToOrder(Order originalOrder, Order destinationOrder, OrderItem item, boolean priceOrder) throws PricingException {
    	removeOrderItemFromFullfillmentGroup(originalOrder, item);
    	OrderItem itemFromOrder = originalOrder.getOrderItems().remove(originalOrder.getOrderItems().indexOf(item));
    	itemFromOrder.setOrder(null);
    	originalOrder = updateOrder(originalOrder, priceOrder);
    	addOrderItemToOrder(destinationOrder, item, priceOrder);
    	return destinationOrder;
    }

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment) {
        return addPaymentToOrder(order, payment, null);
    }

    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment, Referenced securePaymentInfo) {
        payment.setOrder(order);
        order.getPaymentInfos().add(payment);
        order = persistOrder(order);
        int paymentIndex = order.getPaymentInfos().size() - 1;

        if (securePaymentInfo != null) {
            securePaymentInfoService.save(securePaymentInfo);
        }

        return order.getPaymentInfos().get(paymentIndex);
    }

    public void removeAllPaymentsFromOrder(Order order) {
        removePaymentsFromOrder(order, null);
    }

    public void removePaymentsFromOrder(Order order, PaymentInfoType paymentInfoType) {
        List<PaymentInfo> infos = new ArrayList<PaymentInfo>();
        for (PaymentInfo paymentInfo : order.getPaymentInfos()) {
            if (paymentInfoType == null || paymentInfoType.equals(paymentInfo.getType())) {
                infos.add(paymentInfo);
            }
        }
        order.getPaymentInfos().removeAll(infos);
        for (PaymentInfo paymentInfo : infos) {
            try {
                securePaymentInfoService.findAndRemoveSecurePaymentInfo(paymentInfo.getReferenceNumber(), paymentInfo.getType());
            } catch (WorkflowException e) {
                // do nothing--this is an acceptable condition
                LOG.debug("No secure payment is associated with the PaymentInfo", e);
            }
            order.getPaymentInfos().remove(paymentInfo);
            paymentInfo = paymentInfoDao.readPaymentInfoById(paymentInfo.getId());
            paymentInfoDao.delete(paymentInfo);
        }
    }
    
    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest) throws PricingException {
    	return addFulfillmentGroupToOrder(fulfillmentGroupRequest, true);
    }

    public FulfillmentGroup addFulfillmentGroupToOrder(FulfillmentGroupRequest fulfillmentGroupRequest, boolean priceOrder) throws PricingException {
        FulfillmentGroup fg = fulfillmentGroupDao.create();
        fg.setAddress(fulfillmentGroupRequest.getAddress());
        fg.setOrder(fulfillmentGroupRequest.getOrder());
        fg.setPhone(fulfillmentGroupRequest.getPhone());
        fg.setMethod(fulfillmentGroupRequest.getMethod());
        fg.setService(fulfillmentGroupRequest.getService());
        for (FulfillmentGroupItemRequest request : fulfillmentGroupRequest.getFulfillmentGroupItemRequests()) {
            fg = addItemToFulfillmentGroup(request.getOrderItem(), fg, request.getQuantity(), priceOrder);
        }

        return fg;
    }
    
    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup) throws PricingException {
    	return addFulfillmentGroupToOrder(order, fulfillmentGroup, true);
    }

    public FulfillmentGroup addFulfillmentGroupToOrder(Order order, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException {
        FulfillmentGroup dfg = findDefaultFulfillmentGroupForOrder(order);
        if (dfg == null) {
            fulfillmentGroup.setPrimary(true);
        } else if (dfg.equals(fulfillmentGroup)) {
            // API user is trying to re-add the default fulfillment group to the
            // same order
            fulfillmentGroup.setPrimary(true);
            order.getFulfillmentGroups().remove(dfg);
            // fulfillmentGroupDao.delete(dfg);
        }

        fulfillmentGroup.setOrder(order);
        // 1) For each item in the new fulfillment group
        for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
            // 2) Find the item's existing fulfillment group
            for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                // If the existing fulfillment group is different than passed in
                // fulfillment group
                if (!fg.equals(fulfillmentGroup)) {
                    // 3) remove item from it's existing fulfillment
                    // group
                    fg.getFulfillmentGroupItems().remove(fgItem);
                }
            }
        }
        fulfillmentGroup = fulfillmentGroupDao.save(fulfillmentGroup);
        order.getFulfillmentGroups().add(fulfillmentGroup);
        int fulfillmentGroupIndex = order.getFulfillmentGroups().size() - 1;
        order = updateOrder(order, priceOrder);
        return order.getFulfillmentGroups().get(fulfillmentGroupIndex);
    }
    
    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity) throws PricingException {
    	return addItemToFulfillmentGroup(item, fulfillmentGroup, quantity, true);
    }

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, int quantity, boolean priceOrder) throws PricingException {
    	Order order = item.getOrder();
    	
    	if (order == null) {
    		throw new OrderServiceException("The order item does not have an order associated with it. Check to make sure you're not adding an order item that belongs to a BundleOrderItem. BundleOrderItems cannot be split among fulfillment groups");
    	}
        
        // 1) Find the item's existing fulfillment group, if any
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            Iterator<FulfillmentGroupItem> itr = fg.getFulfillmentGroupItems().iterator();
            while (itr.hasNext()) {
                FulfillmentGroupItem fgItem = itr.next();
                if (fgItem.getOrderItem().equals(item)) {
                    // 2) remove item from it's existing fulfillment group
                    itr.remove();
                    fulfillmentGroupItemDao.delete(fgItem);
                }
            }
        }
        
        if (fulfillmentGroup.getId() == null) {
            // API user is trying to add an item to a fulfillment group not created
            fulfillmentGroup = addFulfillmentGroupToOrder(order, fulfillmentGroup, priceOrder);
        } 
        
        FulfillmentGroupItem fgi = createFulfillmentGroupItemFromOrderItem(item, fulfillmentGroup, quantity);
        fgi = fulfillmentGroupItemDao.save(fgi);

        // 3) add the item to the new fulfillment group
        fulfillmentGroup.addFulfillmentGroupItem(fgi);
        order = updateOrder(order, priceOrder);

        return fulfillmentGroup;
    }
    
    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup) throws PricingException {
    	return addItemToFulfillmentGroup(item, fulfillmentGroup, true);
    }

    public FulfillmentGroup addItemToFulfillmentGroup(OrderItem item, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException {
        return addItemToFulfillmentGroup(item, fulfillmentGroup, item.getQuantity(), priceOrder);
    }
    
    public void updateItemQuantity(Order order, OrderItem item) throws ItemNotFoundException, PricingException {
    	updateItemQuantity(order, item, true);
    }

    public void updateItemQuantity(Order order, OrderItem item, boolean priceOrder) throws ItemNotFoundException, PricingException {
        if (!order.getOrderItems().contains(item)) {
            throw new ItemNotFoundException("Order Item (" + item.getId() + ") not found in Order (" + order.getId() + ")");
        }
        OrderItem itemFromOrder = order.getOrderItems().get(order.getOrderItems().indexOf(item));
        itemFromOrder.setQuantity(item.getQuantity());
        order = updateOrder(order, priceOrder);

    }

    public void removeAllFulfillmentGroupsFromOrder(Order order) throws PricingException {
        removeAllFulfillmentGroupsFromOrder(order, false);
    }

    public void removeAllFulfillmentGroupsFromOrder(Order order, boolean priceOrder) throws PricingException {
        if (order.getFulfillmentGroups() != null) {
            for (Iterator<FulfillmentGroup> iterator = order.getFulfillmentGroups().iterator(); iterator.hasNext();) {
                FulfillmentGroup fulfillmentGroup = iterator.next();
                iterator.remove();
                fulfillmentGroupDao.delete(fulfillmentGroup);
            }
            updateOrder(order, priceOrder);
        }
    }
    
    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup) throws PricingException {
    	removeFulfillmentGroupFromOrder(order, fulfillmentGroup, true);
    }

    @Override
    public void removeFulfillmentGroupFromOrder(Order order, FulfillmentGroup fulfillmentGroup, boolean priceOrder) throws PricingException {
        order.getFulfillmentGroups().remove(fulfillmentGroup);
        fulfillmentGroupDao.delete(fulfillmentGroup);
        updateOrder(order, priceOrder);
    }

    @Override
    public Order addOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException, OfferMaxUseExceededException {
        if( !order.getAddedOfferCodes().contains(offerCode)) {
            if (! offerService.verifyMaxCustomerUsageThreshold(order.getCustomer(), offerCode.getOffer())) {
                throw new OfferMaxUseExceededException("The customer has used this offer code more than the maximum allowed number of times.");
            }
            order.getAddedOfferCodes().add(offerCode);
            order = updateOrder(order, priceOrder);
        }
        return order;
    }

    @Override
    public Order removeOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException {
        order.getAddedOfferCodes().remove(offerCode);
        order = updateOrder(order, priceOrder);
        return order;
    }

    @Override
    public Order removeAllOfferCodes(Order order, boolean priceOrder) throws PricingException {
        order.getAddedOfferCodes().clear();
        order = updateOrder(order, priceOrder);
        return order;
    }

    public void removeNamedOrderForCustomer(String name, Customer customer) {
        Order namedOrder = findNamedOrderForCustomer(name, customer);
        cancelOrder(namedOrder);
    }

    public Order confirmOrder(Order order) {
        return orderDao.submitOrder(order);
    }

    public void cancelOrder(Order order) {
        orderDao.delete(order);
    }

    public List<PaymentInfo> readPaymentInfosForOrder(Order order) {
        return paymentInfoDao.readPaymentInfosForOrder(order);
    }
    
    public OrderItem addOrderItemToOrder(Order order, OrderItem newOrderItem) throws PricingException {
    	return addOrderItemToOrder(order, newOrderItem, true);
    }
    	
    public OrderItem addOrderItemToOrder(Order order, OrderItem newOrderItem, boolean priceOrder) throws PricingException {
        int orderItemIndex;
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.add(newOrderItem);
        newOrderItem.setOrder(order);
        orderItemIndex = orderItems.size() - 1;

        // don't worry about fulfillment groups, since the phase for adding
        // items occurs before shipping arrangements

        order = updateOrder(order, priceOrder);

        return order.getOrderItems().get(orderItemIndex);
    }
    
    public OrderItem addOrderItemToBundle(Order order, BundleOrderItem bundle, DiscreteOrderItem newOrderItem, boolean priceOrder) throws PricingException {
    	int bundleIndex = order.getOrderItems().indexOf(bundle);
        List<DiscreteOrderItem> orderItems = bundle.getDiscreteOrderItems();
        orderItems.add(newOrderItem);
        int orderItemIndex = orderItems.size() - 1;
        newOrderItem.setBundleOrderItem(bundle);

        // don't worry about fulfillment groups, since the phase for adding
        // items occurs before shipping arrangements

        order = updateOrder(order, priceOrder);

        return ((BundleOrderItem) order.getOrderItems().get(bundleIndex)).getDiscreteOrderItems().get(orderItemIndex);
    }
    
    public Order removeItemFromBundle(Order order, BundleOrderItem bundle, OrderItem item, boolean priceOrder) throws PricingException {
        DiscreteOrderItem itemFromBundle = bundle.getDiscreteOrderItems().remove(bundle.getDiscreteOrderItems().indexOf(item));
        orderItemService.delete(itemFromBundle);
        itemFromBundle.setBundleOrderItem(null);
        order = updateOrder(order, priceOrder);
        
        return order;
    }

    /**
     * Adds the passed in name/value pair to the order-item.    If the
     * attribute already exists, then it is updated with the new value.
     * <p/>
     * If the value passed in is null and the attribute exists, it is removed
     * from the order item.
     *
     * @param order
     * @param item
     * @param attributeValues
     * @param priceOrder
     * @return
     */
    @Override
    public Order addOrUpdateOrderItemAttributes(Order order, OrderItem item, Map<String,String> attributeValues, boolean priceOrder) throws ItemNotFoundException, PricingException {
        if (!order.getOrderItems().contains(item)) {
            throw new ItemNotFoundException("Order Item (" + item.getId() + ") not found in Order (" + order.getId() + ")");
        }
        OrderItem itemFromOrder = order.getOrderItems().get(order.getOrderItems().indexOf(item));
        
        Map<String,OrderItemAttribute> orderItemAttributes = itemFromOrder.getOrderItemAttributes();
        if (orderItemAttributes == null) {
            orderItemAttributes = new HashMap<String,OrderItemAttribute>();
            itemFromOrder.setOrderItemAttributes(orderItemAttributes);
        }
        
        boolean changeMade = false;
        for (String attributeName : attributeValues.keySet()) {
            String attributeValue = attributeValues.get(attributeName);
            OrderItemAttribute attribute = orderItemAttributes.get(attributeName);
            if (attribute != null && attribute.getValue().equals(attributeValue)) {
                // no change made.
                continue;
            } else {
                changeMade = true;
                if (attribute == null) {
                    attribute = new OrderItemAttributeImpl();
                    attribute.setOrderItem(itemFromOrder);
                    attribute.setName(attributeName);
                    attribute.setValue(attributeValue);
                } else if (attributeValue == null) {
                    orderItemAttributes.remove(attributeValue);
                } else {
                    attribute.setValue(attributeValue);
                }
            }
        }

        if (changeMade) {
            return updateOrder(order, priceOrder);
        } else {
            return order;
        }
    }

    public Order removeOrderItemAttribute(Order order, OrderItem item, String attributeName, boolean priceOrder) throws ItemNotFoundException, PricingException {
        if (!order.getOrderItems().contains(item)) {
            throw new ItemNotFoundException("Order Item (" + item.getId() + ") not found in Order (" + order.getId() + ")");
        }
        OrderItem itemFromOrder = order.getOrderItems().get(order.getOrderItems().indexOf(item));

        boolean changeMade = false;
        Map<String,OrderItemAttribute> orderItemAttributes = itemFromOrder.getOrderItemAttributes();
        if (orderItemAttributes != null) {
            if (orderItemAttributes.containsKey(attributeName)) {
                changeMade = true;
                orderItemAttributes.remove(attributeName);
            }
        }

        if (changeMade) {
            return updateOrder(order, priceOrder);
        } else {
            return order;
        }
    }

    public FulfillmentGroup createDefaultFulfillmentGroup(Order order, Address address) {
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            if (fulfillmentGroup.isPrimary()) {
                return fulfillmentGroup;
            }
        }

        FulfillmentGroup newFg = fulfillmentGroupService.createEmptyFulfillmentGroup();
        newFg.setOrder(order);
        newFg.setPrimary(true);
        newFg.setAddress(address);
        for (OrderItem orderItem : order.getOrderItems()) {
            newFg.addFulfillmentGroupItem(createFulfillmentGroupItemFromOrderItem(orderItem, newFg, orderItem.getQuantity()));
        }
        return newFg;
    }

    public OrderDao getOrderDao() {
        return orderDao;
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public PaymentInfoDao getPaymentInfoDao() {
        return paymentInfoDao;
    }

    public void setPaymentInfoDao(PaymentInfoDao paymentInfoDao) {
        this.paymentInfoDao = paymentInfoDao;
    }

    public FulfillmentGroupDao getFulfillmentGroupDao() {
        return fulfillmentGroupDao;
    }

    public void setFulfillmentGroupDao(FulfillmentGroupDao fulfillmentGroupDao) {
        this.fulfillmentGroupDao = fulfillmentGroupDao;
    }

    public FulfillmentGroupItemDao getFulfillmentGroupItemDao() {
        return fulfillmentGroupItemDao;
    }

    public void setFulfillmentGroupItemDao(FulfillmentGroupItemDao fulfillmentGroupItemDao) {
        this.fulfillmentGroupItemDao = fulfillmentGroupItemDao;
    }

    public PricingExecutionManager getPricingExecutionManager() {
        return pricingExecutionManager;
    }

    public void setPricingExecutionManager(PricingExecutionManager pricingExecutionManager) {
        this.pricingExecutionManager = pricingExecutionManager;
    }

    public OrderItemService getOrderItemService() {
        return orderItemService;
    }

    public void setOrderItemService(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    public Order findOrderByOrderNumber(String orderNumber) {
        return orderDao.readOrderByOrderNumber(orderNumber);
    }

    protected Order updateOrder(Order order, Boolean priceOrder) throws PricingException {
    	order = persistOrder(order);
    	if (priceOrder) {
    		pricingExecutionManager.executePricing(order);
    	}
        return order;
    }

    protected Order persistOrder(Order order) {
        return orderDao.save(order);
    }

    protected FulfillmentGroupItem createFulfillmentGroupItemFromOrderItem(OrderItem orderItem, FulfillmentGroup fulfillmentGroup, int quantity) {
        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.create();
        fgi.setFulfillmentGroup(fulfillmentGroup);
        fgi.setOrderItem(orderItem);
        fgi.setQuantity(quantity);
        return fgi;
    }

    protected void removeOrderItemFromFullfillmentGroup(Order order, OrderItem orderItem) {
        List<FulfillmentGroup> fulfillmentGroups = order.getFulfillmentGroups();
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
            Iterator<FulfillmentGroupItem> itr = fulfillmentGroup.getFulfillmentGroupItems().iterator();
            while (itr.hasNext()) {
                FulfillmentGroupItem fulfillmentGroupItem = itr.next();
                if (fulfillmentGroupItem.getOrderItem().equals(orderItem)) {
                    itr.remove();
                    fulfillmentGroupItemDao.delete(fulfillmentGroupItem);
                }
            }
        }
    }

    protected DiscreteOrderItemRequest createDiscreteOrderItemRequest(DiscreteOrderItem discreteOrderItem) {
        DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
        itemRequest.setCategory(discreteOrderItem.getCategory());
        itemRequest.setProduct(discreteOrderItem.getProduct());
        itemRequest.setQuantity(discreteOrderItem.getQuantity());
        itemRequest.setSku(discreteOrderItem.getSku());
        
        if (discreteOrderItem.getPersonalMessage() != null) {
            PersonalMessage personalMessage = orderItemService.createPersonalMessage();
            try {
                BeanUtils.copyProperties(personalMessage, discreteOrderItem.getPersonalMessage());
                personalMessage.setId(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            itemRequest.setPersonalMessage(personalMessage);
        }
        
        return itemRequest;
    }

    protected BundleOrderItemRequest createBundleOrderItemRequest(BundleOrderItem bundleOrderItem, List<DiscreteOrderItemRequest> discreteOrderItemRequests) {
        BundleOrderItemRequest bundleOrderItemRequest = new BundleOrderItemRequest();
        bundleOrderItemRequest.setCategory(bundleOrderItem.getCategory());
        bundleOrderItemRequest.setName(bundleOrderItem.getName());
        bundleOrderItemRequest.setQuantity(bundleOrderItem.getQuantity());
        bundleOrderItemRequest.setDiscreteOrderItems(discreteOrderItemRequests);
        return bundleOrderItemRequest;
    }


    /**
     * Returns the order associated with the passed in orderId.
     *
     * @param orderId
     * @return
     */
    protected Order validateOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId required when adding item to order.");
        }

        Order order = findOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("No order found matching passed in orderId " + orderId + " while trying to addItemToOrder.");
        }

        return order;
    }

    protected Product validateProduct(Long productId) {
        if (productId != null) {
            Product product = productDao.readProductById(productId);
            if (product == null) {
                throw new IllegalArgumentException("No product found matching passed in productId " + productId + " while trying to addItemToOrder.");
            }
            return product;
        }
        return null;
    }

    protected Category determineCategory(Product product, Long categoryId) {
        Category category = null;
        if (categoryId != null) {
            category = categoryDao.readCategoryById(categoryId);
        }

        if (category == null && product != null) {
            category = product.getDefaultCategory();
        }
        return category;
    }

    protected Sku determineSku(Product product, Long skuId, Map<String,String> attributeValues) {
        // Check whether the sku is correct given the product options.
        Sku sku = findSkuThatMatchesProductOptions(product, attributeValues);

        if (sku == null && skuId != null) {
            sku = skuDao.readSkuById(skuId);
        }

        if (sku == null && product != null) {
            // Set to the default sku
            sku = product.getDefaultSku();
        }
        return sku;
    }

    /**
     * Checks to make sure the correct SKU is still attached to the order.
     * For example, if you have the SKU for a Large, Red T-shirt in the
     * cart and your UI allows the user to change the one of the attributes
     * (e.g. red to black), then it is possible that the SKU needs to
     * be adjusted as well.
     */
    protected Sku findSkuThatMatchesProductOptions(Product product, Map<String,String> attributeValues) {
        // TODO: Product Options
        return null;
    }


    protected DiscreteOrderItem createDiscreteOrderItem(Sku sku, Product product, Category category, int quantity, Map<String,String> itemAttributes) {
        DiscreteOrderItem item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.DISCRETE);
        item.setSku(sku);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setCategory(category);

        if (itemAttributes != null && itemAttributes.size() > 0) {
            Map<String,OrderItemAttribute> orderItemAttributes = new HashMap<String,OrderItemAttribute>();
            item.setOrderItemAttributes(orderItemAttributes);

            for (String key : itemAttributes.keySet()) {
                String value = itemAttributes.get(key);
                OrderItemAttribute attribute = new OrderItemAttributeImpl();
                attribute.setName(key);
                attribute.setValue(value);
                attribute.setOrderItem(item);
                orderItemAttributes.put(key, attribute);
            }
        }

        item.updatePrices();
        item.assignFinalPrice();
        return item;
    }

    /**
     * Adds an item to the passed in order.
     *
     * The orderItemRequest can be sparsely populated.
     *
     * When priceOrder is false, the system will not reprice the order.   This is more performant in
     * cases such as bulk adds where the repricing could be done for the last item only.
     *
     * @see OrderItemRequestDTO
     * @param orderItemRequestDTO
     * @param priceOrder
     * @return
     */
    @Override
    public OrderItem addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws PricingException {
        if (orderItemRequestDTO.getQuantity() == 0) {
            LOG.debug("Not adding item to order because quantity is zero.");
            return null;
        }

        Order order = validateOrder(orderId);
        Product product = validateProduct(orderItemRequestDTO.getProductId());
        Sku sku = determineSku(product, orderItemRequestDTO.getSkuId(), orderItemRequestDTO.getItemAttributes());
        Category category = determineCategory(product, orderItemRequestDTO.getCategoryId());

        if (! (product instanceof ProductBundle)) {
            DiscreteOrderItem item = createDiscreteOrderItem(sku, product, category, orderItemRequestDTO.getQuantity(), orderItemRequestDTO.getItemAttributes());
            return addOrderItemToOrder(order, item, priceOrder);
        } else {
            ProductBundle bundle = (ProductBundle) product;
            BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemDao.create(OrderItemType.BUNDLE);
            bundleOrderItem.setQuantity(orderItemRequestDTO.getQuantity());
            bundleOrderItem.setCategory(category);
            bundleOrderItem.setSku(sku);
            bundleOrderItem.setName(product.getName());
            bundleOrderItem.setProductBundle(bundle);

            for (SkuBundleItem skuBundleItem : bundle.getSkuBundleItems()) {
                Product bundleProduct = skuBundleItem.getBundle();
                Sku bundleSku = skuBundleItem.getSku();

                Category bundleCategory = determineCategory(bundleProduct, orderItemRequestDTO.getCategoryId());

                DiscreteOrderItem bundleDiscreteItem = createDiscreteOrderItem(bundleSku, bundleProduct, bundleCategory, skuBundleItem.getQuantity(), orderItemRequestDTO.getItemAttributes());
                bundleDiscreteItem.setSkuBundleItem(skuBundleItem);
                bundleDiscreteItem.setBundleOrderItem(bundleOrderItem);
                bundleOrderItem.getDiscreteOrderItems().add(bundleDiscreteItem);
            }

            bundleOrderItem.updatePrices();
            bundleOrderItem.assignFinalPrice();
            return addOrderItemToOrder(order, bundleOrderItem, priceOrder);
        }
    }
}
