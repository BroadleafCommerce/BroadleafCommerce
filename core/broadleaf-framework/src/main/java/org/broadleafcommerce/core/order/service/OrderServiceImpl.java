/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.TableCreator;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.dao.OrderDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.NullOrderFactory;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;
import org.broadleafcommerce.core.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.payment.dao.PaymentInfoDao;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.pricing.service.PricingService;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.ActivityMessages;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.SequenceProcessor;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author apazzolini
 */
@Service("blOrderService")
@ManagedResource(objectName="org.broadleafcommerce:name=OrderService", description="Order Service", currencyTimeLimit=15)
public class OrderServiceImpl implements OrderService {
    private static final Log LOG = LogFactory.getLog(OrderServiceImpl.class);
    
    /* DAOs */
    @Resource(name = "blPaymentInfoDao")
    protected PaymentInfoDao paymentInfoDao;
    
    @Resource(name = "blOrderDao")
    protected OrderDao orderDao;
    
    @Resource(name = "blOfferDao")
    protected OfferDao offerDao;

    /* Factories */
    @Resource(name = "blNullOrderFactory")
    protected NullOrderFactory nullOrderFactory;
    
    /* Services */
    @Resource(name = "blPricingService")
    protected PricingService pricingService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;
    
    @Resource(name = "blOfferService")
    protected OfferService offerService;

    @Resource(name = "blSecurePaymentInfoService")
    protected SecurePaymentInfoService securePaymentInfoService;

    @Resource(name = "blMergeCartService")
    protected MergeCartService mergeCartService;
    
    @Resource(name = "blOrderServiceExtensionManager")
    protected OrderServiceExtensionManager extensionManager;
    
    /* Workflows */
    @Resource(name = "blAddItemWorkflow")
    protected SequenceProcessor addItemWorkflow;
    
    @Resource(name = "blUpdateProductOptionsForItemWorkflow")
    private SequenceProcessor updateProductOptionsForItemWorkflow;

    @Resource(name = "blUpdateItemWorkflow")
    protected SequenceProcessor updateItemWorkflow;
    
    @Resource(name = "blRemoveItemWorkflow")
    protected SequenceProcessor removeItemWorkflow;

    @Resource(name = "blTransactionManager")
    protected PlatformTransactionManager transactionManager;

    @Value("${pricing.retry.count.for.lock.failure}")
    protected int pricingRetryCountForLockFailure = 3;

    @Value("${pricing.retry.wait.interval.for.lock.failure}")
    protected long pricingRetryWaitIntervalForLockFailure = 500L;
    
    /* Fields */
    protected boolean moveNamedOrderItems = true;
    protected boolean deleteEmptyNamedOrders = true;

    @Value("${automatically.merge.like.items}")
    protected boolean automaticallyMergeLikeItems;

    @Override
    @Transactional("blTransactionManager")
    public Order createNewCartForCustomer(Customer customer) {
        return orderDao.createNewCartForCustomer(customer);
    }

    @Override
    @Transactional("blTransactionManager")
    public Order createNamedOrderForCustomer(String name, Customer customer) {
        Order namedOrder = orderDao.create();
        namedOrder.setCustomer(customer);
        namedOrder.setName(name);
        namedOrder.setStatus(OrderStatus.NAMED);
        
        if (extensionManager != null) {
            extensionManager.getProxy().attachAdditionalDataToNewNamedCart(customer, namedOrder);
        }
        
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            namedOrder.setLocale(BroadleafRequestContext.getBroadleafRequestContext().getLocale());
        }
        
        return orderDao.save(namedOrder); // No need to price here
    }

    @Override
    public Order findNamedOrderForCustomer(String name, Customer customer) {
        return orderDao.readNamedOrderForCustomer(customer, name);
    }

    @Override
    public Order findOrderById(Long orderId) {
        return orderDao.readOrderById(orderId);
    }

    @Override
    public Order getNullOrder() {
        return nullOrderFactory.getNullOrder();
    }

    @Override
    public Order findCartForCustomer(Customer customer) {
        return orderDao.readCartForCustomer(customer);
    }

    @Override
    public List<Order> findOrdersForCustomer(Customer customer) {
        return orderDao.readOrdersForCustomer(customer.getId());
    }

    @Override
    public List<Order> findOrdersForCustomer(Customer customer, OrderStatus status) {
        return orderDao.readOrdersForCustomer(customer, status);
    }

    @Override
    public Order findOrderByOrderNumber(String orderNumber) {
        return orderDao.readOrderByOrderNumber(orderNumber);
    }

    @Override
    public List<PaymentInfo> findPaymentInfosForOrder(Order order) {
        return paymentInfoDao.readPaymentInfosForOrder(order);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public PaymentInfo addPaymentToOrder(Order order, PaymentInfo payment, Referenced securePaymentInfo) {
        payment.setOrder(order);
        order.getPaymentInfos().add(payment);
        order = persist(order);
        int paymentIndex = order.getPaymentInfos().size() - 1;

        if (securePaymentInfo != null) {
            securePaymentInfoService.save(securePaymentInfo);
        }

        return order.getPaymentInfos().get(paymentIndex);
    }

    @Override
    public Order save(Order order, Boolean priceOrder) throws PricingException {
        //persist the order first
        TransactionStatus status = TransactionUtils.createTransaction("saveOrder",
                    TransactionDefinition.PROPAGATION_REQUIRED, transactionManager);
        try {
            order = persist(order);
            TransactionUtils.finalizeTransaction(status, transactionManager, false);
        } catch (RuntimeException ex) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw ex;
        }

        //make any pricing changes - possibly retrying with the persisted state if there's a lock failure
        if (priceOrder) {
            int retryCount = 0;
            boolean isValid = false;
            while (!isValid) {
                try {
                    order = pricingService.executePricing(order);
                    isValid = true;
                } catch (Exception ex) {
                    boolean isValidCause = false;
                    Throwable cause = ex;
                    while (!isValidCause) {
                        if (cause.getClass().equals(LockAcquisitionException.class)) {
                            isValidCause = true;
                        }
                        cause = cause.getCause();
                        if (cause == null) {
                            break;
                        }
                    }
                    if (isValidCause) {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("Problem acquiring lock during pricing call - attempting to price again.");
                        }
                        isValid = false;
                        if (retryCount >= pricingRetryCountForLockFailure) {
                            if (LOG.isInfoEnabled()) {
                                LOG.info("Problem acquiring lock during pricing call. Retry limit exceeded at (" + retryCount + "). Throwing exception.");
                            }
                            if (ex instanceof PricingException) {
                                throw (PricingException) ex;
                            } else {
                                throw new PricingException(ex);
                            }
                        } else {
                            order = findOrderById(order.getId());
                            retryCount++;
                        }
                        try {
                            Thread.sleep(pricingRetryWaitIntervalForLockFailure);
                        } catch (Throwable e) {
                            //do nothing
                        }
                    } else {
                        if (ex instanceof PricingException) {
                            throw (PricingException) ex;
                        } else {
                            throw new PricingException(ex);
                        }
                    }
                }
            }

            //make the final save of the priced order
            status = TransactionUtils.createTransaction("saveOrder",
                                TransactionDefinition.PROPAGATION_REQUIRED, transactionManager);
            try {
                order = persist(order);
                TransactionUtils.finalizeTransaction(status, transactionManager, false);
            } catch (RuntimeException ex) {
                TransactionUtils.finalizeTransaction(status, transactionManager, true);
                throw ex;
            }
        }

        return order;
    }
    
    // This method exists to provide OrderService methods the ability to save an order
    // without having to worry about a PricingException being thrown.
    protected Order persist(Order order) {
        return orderDao.save(order);
    }

    @Override
    @Transactional("blTransactionManager")
    public void cancelOrder(Order order) {
        orderDao.delete(order);
    }
    @Override
    @Transactional("blTransactionManager")
    public void deleteOrder(Order order) {
        orderDao.delete(order);
    }
    @Override
    @Transactional("blTransactionManager")
    public Order addOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException, OfferMaxUseExceededException {
        if( !order.getAddedOfferCodes().contains(offerCode)) {
            if (! offerService.verifyMaxCustomerUsageThreshold(order.getCustomer(), offerCode.getOffer())) {
                throw new OfferMaxUseExceededException("The customer has used this offer code more than the maximum allowed number of times.");
            }
            order.getAddedOfferCodes().add(offerCode);
            order = save(order, priceOrder);
        }
        return order;   
    }

    @Override
    @Transactional("blTransactionManager")
    public Order removeOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException {
        order.getAddedOfferCodes().remove(offerCode);
        order = save(order, priceOrder);
        return order;   
    }

    @Override
    @Transactional("blTransactionManager")
    public Order removeAllOfferCodes(Order order, boolean priceOrder) throws PricingException {
         order.getAddedOfferCodes().clear();
         order = save(order, priceOrder);
         return order;  
    }

    @Override
    @ManagedAttribute(description="The delete empty named order after adding items to cart attribute", currencyTimeLimit=15)
    public void setDeleteEmptyNamedOrders(boolean deleteEmptyNamedOrders) {
        this.deleteEmptyNamedOrders = deleteEmptyNamedOrders;
    }
    
    @Override
    public OrderItem findLastMatchingItem(Order order, Long skuId, Long productId) {
        if (order.getOrderItems() != null) {
            for (int i=(order.getOrderItems().size()-1); i >= 0; i--) {
                OrderItem currentItem = (order.getOrderItems().get(i));
                if (currentItem instanceof DiscreteOrderItem) {
                    DiscreteOrderItem discreteItem = (DiscreteOrderItem) currentItem;
                    if (skuId != null) {
                        if (discreteItem.getSku() != null && skuId.equals(discreteItem.getSku().getId())) {
                            return discreteItem;
                        }
                    } else if (productId != null && discreteItem.getProduct() != null && productId.equals(discreteItem.getProduct().getId())) {
                        return discreteItem;
                    }

                } else if (currentItem instanceof BundleOrderItem) {
                    BundleOrderItem bundleItem = (BundleOrderItem) currentItem;
                    if (skuId != null) {
                        if (bundleItem.getSku() != null && skuId.equals(bundleItem.getSku().getId())) {
                            return bundleItem;
                        }
                    } else if (productId != null && bundleItem.getProduct() != null && productId.equals(bundleItem.getProduct().getId())) {
                        return bundleItem;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    @Transactional("blTransactionManager")
    public Order confirmOrder(Order order) {
        return orderDao.submitOrder(order);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public Order addAllItemsFromNamedOrder(Order namedOrder, boolean priceOrder) throws RemoveFromCartException, AddToCartException {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer());
        if (cartOrder == null) {
            cartOrder = createNewCartForCustomer(namedOrder.getCustomer());
        }
        List<OrderItem> items = new ArrayList<OrderItem>(namedOrder.getOrderItems());
        for (OrderItem item : items) {
            if (moveNamedOrderItems) {
                removeItem(namedOrder.getId(), item.getId(), false);
            }
            
            OrderItemRequestDTO orderItemRequest = orderItemService.buildOrderItemRequestDTOFromOrderItem(item);
            cartOrder = addItem(cartOrder.getId(), orderItemRequest, priceOrder);
        }
        
        if (deleteEmptyNamedOrders) {
            cancelOrder(namedOrder);
        }
        
        return cartOrder;
    }
    
    @Override
    @Transactional("blTransactionManager")
    public Order addItemFromNamedOrder(Order namedOrder, OrderItem item, boolean priceOrder) throws RemoveFromCartException, AddToCartException {
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer());
        if (cartOrder == null) {
            cartOrder = createNewCartForCustomer(namedOrder.getCustomer());
        }
        
        if (moveNamedOrderItems) {
            removeItem(namedOrder.getId(), item.getId(), false);
        }
            
        OrderItemRequestDTO orderItemRequest = orderItemService.buildOrderItemRequestDTOFromOrderItem(item);
        cartOrder = addItem(cartOrder.getId(), orderItemRequest, priceOrder);
        
        if (namedOrder.getOrderItems().size() == 0 && deleteEmptyNamedOrders) {
            cancelOrder(namedOrder);
        }
            
        return cartOrder;
    }
    
    @Override
    @Transactional("blTransactionManager")
    public Order addItemFromNamedOrder(Order namedOrder, OrderItem item, int quantity, boolean priceOrder) throws RemoveFromCartException, AddToCartException, UpdateCartException {
        // Validate that the quantity requested makes sense
        if (quantity < 1 || quantity > item.getQuantity()) {
            throw new IllegalArgumentException("Cannot move 0 or less quantity");
        } else if (quantity == item.getQuantity()) {
            return addItemFromNamedOrder(namedOrder, item, priceOrder);
        }
        
        Order cartOrder = orderDao.readCartForCustomer(namedOrder.getCustomer());
        if (cartOrder == null) {
            cartOrder = createNewCartForCustomer(namedOrder.getCustomer());
        }
        
        if (moveNamedOrderItems) {
            // Update the old item to its new quantity only if we're moving items
            OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
            orderItemRequestDTO.setOrderItemId(item.getId());
            orderItemRequestDTO.setQuantity(item.getQuantity() - quantity);
            updateItemQuantity(namedOrder.getId(), orderItemRequestDTO, false);
        }
            
        OrderItemRequestDTO orderItemRequest = orderItemService.buildOrderItemRequestDTOFromOrderItem(item);
        orderItemRequest.setQuantity(quantity);
        cartOrder = addItem(cartOrder.getId(), orderItemRequest, priceOrder);
            
        return cartOrder;
    }
    
    @Override
    @Transactional("blTransactionManager")
    public OrderItem addGiftWrapItemToOrder(Order order, GiftWrapOrderItemRequest itemRequest, boolean priceOrder) throws PricingException {
        GiftWrapOrderItem item = orderItemService.createGiftWrapOrderItem(itemRequest);
        item.setOrder(order);
        item = (GiftWrapOrderItem) orderItemService.saveOrderItem(item);
        
        order.getOrderItems().add(item);
        order = save(order, priceOrder);
        
        return item;
    }
    
    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = {AddToCartException.class})
    public Order addItem(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws AddToCartException {
        // Don't allow overrides from this method.
        orderItemRequestDTO.setOverrideRetailPrice(null);
        orderItemRequestDTO.setOverrideSalePrice(null);
        return addItemWithPriceOverrides(orderId, orderItemRequestDTO, priceOrder);
    }

    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = { AddToCartException.class })
    public Order addItemWithPriceOverrides(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws AddToCartException {
        Order order = findOrderById(orderId);
        if (automaticallyMergeLikeItems) {
            OrderItem item = findMatchingItem(order, orderItemRequestDTO);
            if (item != null) {
                orderItemRequestDTO.setQuantity(item.getQuantity() + orderItemRequestDTO.getQuantity());
                orderItemRequestDTO.setOrderItemId(item.getId());
                try {
                    return updateItemQuantity(orderId, orderItemRequestDTO, priceOrder);
                } catch (RemoveFromCartException e) {
                    throw new AddToCartException("Unexpected error - system tried to remove item while adding to cart", e);
                } catch (UpdateCartException e) {
                    throw new AddToCartException("Could not update quantity for matched item", e);
                }
            }
        }
        try {
            // We only want to price on the last addition for performance reasons and only if the user asked for it.
            int numAdditionRequests = priceOrder ? (1 + orderItemRequestDTO.getChildOrderItems().size()) : -1;
            int currentAddition = 1;

            CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, currentAddition == numAdditionRequests);
            ProcessContext<CartOperationRequest> context = (ProcessContext<CartOperationRequest>) addItemWorkflow.doActivities(cartOpRequest);

            List<ActivityMessageDTO> orderMessages = new ArrayList<ActivityMessageDTO>();
            orderMessages.addAll(((ActivityMessages) context).getActivityMessages());
            
            if (CollectionUtils.isNotEmpty(orderItemRequestDTO.getChildOrderItems())) {
                for (OrderItemRequestDTO childRequest : orderItemRequestDTO.getChildOrderItems()) {
                    childRequest.setParentOrderItemId(context.getSeedData().getOrderItem().getId());
                    currentAddition++;

                    CartOperationRequest childCartOpRequest = new CartOperationRequest(context.getSeedData().getOrder(), childRequest, currentAddition == numAdditionRequests);
                    ProcessContext<CartOperationRequest> childContext = (ProcessContext<CartOperationRequest>) addItemWorkflow.doActivities(childCartOpRequest);
                    orderMessages.addAll(((ActivityMessages) childContext).getActivityMessages());
                }
            }
            
            context.getSeedData().getOrder().setOrderMessages(orderMessages);
            return context.getSeedData().getOrder();
        } catch (WorkflowException e) {
            throw new AddToCartException("Could not add to cart", getCartOperationExceptionRootCause(e));
        }

    }

    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = {UpdateCartException.class, RemoveFromCartException.class})
    public Order updateItemQuantity(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws UpdateCartException, RemoveFromCartException {
        if (orderItemRequestDTO.getQuantity() == 0) {
            return removeItem(orderId, orderItemRequestDTO.getOrderItemId(), priceOrder);
        }
        
        try {
            CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, priceOrder);
            ProcessContext<CartOperationRequest> context = (ProcessContext<CartOperationRequest>) updateItemWorkflow.doActivities(cartOpRequest);
            context.getSeedData().getOrder().getOrderMessages().addAll(((ActivityMessages) context).getActivityMessages());
            return context.getSeedData().getOrder();
        } catch (WorkflowException e) {
            throw new UpdateCartException("Could not update cart quantity", getCartOperationExceptionRootCause(e));
        }
    }

    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = {RemoveFromCartException.class})
    public Order removeItem(Long orderId, Long orderItemId, boolean priceOrder) throws RemoveFromCartException {
        try {
            OrderItem oi = orderItemService.readOrderItemById(orderItemId);
            if (CollectionUtils.isNotEmpty(oi.getChildOrderItems())) {
                List<Long> childrenToRemove = new ArrayList<Long>();
                for (OrderItem childOrderItem : oi.getChildOrderItems()) {
                    childrenToRemove.add(childOrderItem.getId());
                }
                for (Long childToRemove : childrenToRemove) {
                    removeItemInternal(orderId, childToRemove, false);
                }
            }

            return removeItemInternal(orderId, orderItemId, priceOrder);
        } catch (WorkflowException e) {
            throw new RemoveFromCartException("Could not remove from cart", getCartOperationExceptionRootCause(e));
        }
    }
    
    protected Order removeItemInternal(Long orderId, Long orderItemId, boolean priceOrder) throws WorkflowException {
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setOrderItemId(orderItemId);
        CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, priceOrder);
        ProcessContext<CartOperationRequest> context = (ProcessContext<CartOperationRequest>) removeItemWorkflow.doActivities(cartOpRequest);
        context.getSeedData().getOrder().getOrderMessages().addAll(((ActivityMessages) context).getActivityMessages());
        return context.getSeedData().getOrder();
    }

    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = { RemoveFromCartException.class })
    public Order removeInactiveItems(Long orderId, boolean priceOrder) throws RemoveFromCartException {
        Order order = findOrderById(orderId);
        try {

            for (OrderItem currentItem : new ArrayList<OrderItem>(order.getOrderItems())) {
                if (!currentItem.isSkuActive()) {
                    removeItem(orderId, currentItem.getId(), priceOrder);
                }
            }

        } catch (Exception e) {
            throw new RemoveFromCartException("Could not remove from cart", e.getCause());
        }
        return findOrderById(orderId);
    }

    @Override
    public boolean getAutomaticallyMergeLikeItems() {
        return automaticallyMergeLikeItems;
    }

    @Override
    public void setAutomaticallyMergeLikeItems(boolean automaticallyMergeLikeItems) {
        this.automaticallyMergeLikeItems = automaticallyMergeLikeItems;
    }
    
    @Override
    @ManagedAttribute(description="The move item from named order when adding to the cart attribute", currencyTimeLimit=15)
    public boolean isMoveNamedOrderItems() {
        return moveNamedOrderItems;
    }

    @Override
    @ManagedAttribute(description="The move item from named order when adding to the cart attribute", currencyTimeLimit=15)
    public void setMoveNamedOrderItems(boolean moveNamedOrderItems) {
        this.moveNamedOrderItems = moveNamedOrderItems;
    }

    @Override
    @ManagedAttribute(description="The delete empty named order after adding items to cart attribute", currencyTimeLimit=15)
    public boolean isDeleteEmptyNamedOrders() {
        return deleteEmptyNamedOrders;
    }

    @Override
    @Transactional("blTransactionManager")
    public void removeAllPaymentsFromOrder(Order order) {
        removePaymentsFromOrder(order, null);
    }

    @Override
    @Transactional("blTransactionManager")
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

    @Override
    @Transactional("blTransactionManager")
    public void removePaymentFromOrder(Order order, PaymentInfo paymentInfo){
        PaymentInfo paymentInfoToRemove = null;
        for(PaymentInfo info : order.getPaymentInfos()){
            if(info.equals(paymentInfo)){
                paymentInfoToRemove = info;
            }
        }
        if(paymentInfoToRemove != null){
            try {
                securePaymentInfoService.findAndRemoveSecurePaymentInfo(paymentInfoToRemove.getReferenceNumber(), paymentInfo.getType());
            } catch (WorkflowException e) {
                // do nothing--this is an acceptable condition
                LOG.debug("No secure payment is associated with the PaymentInfo", e);
            }
            order.getPaymentInfos().remove(paymentInfoToRemove);
            paymentInfo = paymentInfoDao.readPaymentInfoById(paymentInfoToRemove.getId());
            paymentInfoDao.delete(paymentInfo);
        }
    }
    
    /**
     * This method will return the exception that is immediately below the deepest 
     * WorkflowException in the current stack trace.
     * 
     * @param e the workflow exception that contains the requested root cause
     * @return the root cause of the workflow exception
     */
    protected Throwable getCartOperationExceptionRootCause(WorkflowException e) {
        Throwable cause = e.getCause();
        if (cause == null) {
            return e;
        }
        
        Throwable currentCause = cause;
        while (currentCause.getCause() != null) {
            currentCause = currentCause.getCause();
            if (currentCause instanceof WorkflowException) {
                cause = currentCause.getCause();
            }
        }
        
        return cause;
    }

    /**
     * Returns true if the two items attributes exactly match.
     * @param item1
     * @param item2
     * @return
     */
    protected boolean compareAttributes(Map<String, OrderItemAttribute> item1Attributes, OrderItemRequestDTO item2) {
        int item1AttributeSize = item1Attributes == null ? 0 : item1Attributes.size();
        int item2AttributeSize = item2.getItemAttributes() == null ? 0 : item2.getItemAttributes().size();

        if (item1AttributeSize != item2AttributeSize) {
            return false;
        }

        for (String key : item2.getItemAttributes().keySet()) {
            String itemOneValue = (item1Attributes.get(key) == null) ? null : item1Attributes.get(key).getValue();
            String itemTwoValue = item2.getItemAttributes().get(key);
            if (!itemTwoValue.equals(itemOneValue)) {
                return false;
            }
        }
        return true;
    }

    protected boolean itemMatches(Sku item1Sku, Product item1Product, Map<String, OrderItemAttribute> item1Attributes,
            OrderItemRequestDTO item2) {
        // Must match on SKU and options
        if (item1Sku != null && item2.getSkuId() != null) {
            if (item1Sku.getId().equals(item2.getSkuId())) {
                return true;
            }
        } else {
            if (item1Product != null && item2.getProductId() != null) {
                if (item1Product.getId().equals(item2.getProductId())) {
                    return compareAttributes(item1Attributes, item2);
                }
            }
        }
        return false;
    }

    protected OrderItem findMatchingItem(Order order, OrderItemRequestDTO itemToFind) {
        if (order == null) {
            return null;
        }
        for (OrderItem currentItem : order.getOrderItems()) {
            if (currentItem instanceof DiscreteOrderItem) {
                DiscreteOrderItem discreteItem = (DiscreteOrderItem) currentItem;
                if (itemMatches(discreteItem.getSku(), discreteItem.getProduct(), discreteItem.getOrderItemAttributes(),
                        itemToFind)) {
                    return discreteItem;
                }
            } else if (currentItem instanceof BundleOrderItem) {
                BundleOrderItem bundleItem = (BundleOrderItem) currentItem;
                if (itemMatches(bundleItem.getSku(), bundleItem.getProduct(), null, itemToFind)) {
                    return bundleItem;
                }
            }
        }
        return null;
    }

    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = { UpdateCartException.class })
    public Order updateProductOptionsForItem(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws UpdateCartException {
        try {
            CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, priceOrder);
            ProcessContext<CartOperationRequest> context = (ProcessContext<CartOperationRequest>) updateProductOptionsForItemWorkflow.doActivities(cartOpRequest);
            context.getSeedData().getOrder().getOrderMessages().addAll(((ActivityMessages) context).getActivityMessages());
            return context.getSeedData().getOrder();
        } catch (WorkflowException e) {
            throw new UpdateCartException("Could not product options", getCartOperationExceptionRootCause(e));
        }
    }

    @Override
    public void printOrder(Order order, Log log) {
        if (!log.isDebugEnabled()) {
            return;
        }
        
        TableCreator tc = new TableCreator(new TableCreator.Col[] {
            new TableCreator.Col("Order Item", 30),
            new TableCreator.Col("Qty"),
            new TableCreator.Col("Unit Price"),
            new TableCreator.Col("Avg Adj"),
            new TableCreator.Col("Total Adj"),
            new TableCreator.Col("Total Price")
        });

        for (OrderItem oi : order.getOrderItems()) {
            tc.addRow(new String[] {
                oi.getName(),
                String.valueOf(oi.getQuantity()),
                String.valueOf(oi.getPriceBeforeAdjustments(true)),
                String.valueOf(oi.getAverageAdjustmentValue()),
                String.valueOf(oi.getTotalAdjustmentValue()),
                String.valueOf(oi.getTotalPrice())
            });
        }
        
        tc.addSeparator()
            .withGlobalRowHeaderWidth(15)
            .addRow("Subtotal", order.getSubTotal())
            .addRow("Order Adj.", order.getOrderAdjustmentsValue())
            .addRow("Tax", order.getTotalTax())
            .addRow("Shipping", order.getTotalShipping())
            .addRow("Total", order.getTotal())
            .addSeparator();
        
        log.debug(tc.toString());
    }

}
