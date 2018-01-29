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
package org.broadleafcommerce.core.order.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.util.TableCreator;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.util.TypedPredicate;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferAlreadyAddedException;
import org.broadleafcommerce.core.offer.service.exception.OfferException;
import org.broadleafcommerce.core.offer.service.exception.OfferExpiredException;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.dao.OrderDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.NullOrderFactory;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;
import org.broadleafcommerce.core.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.IllegalCartOperationException;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.payment.dao.OrderPaymentDao;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.core.payment.service.SecureOrderPaymentService;
import org.broadleafcommerce.core.pricing.service.PricingService;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.ActivityMessages;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.Processor;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.hibernate.FlushMode;
import org.hibernate.Session;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * @author apazzolini
 */
@Service("blOrderService")
@ManagedResource(objectName="org.broadleafcommerce:name=OrderService", description="Order Service", currencyTimeLimit=15)
public class OrderServiceImpl implements OrderService {
    private static final Log LOG = LogFactory.getLog(OrderServiceImpl.class);

    /* DAOs */
    @Resource(name = "blOrderPaymentDao")
    protected OrderPaymentDao paymentDao;
    
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

    @Resource(name = "blSecureOrderPaymentService")
    protected SecureOrderPaymentService securePaymentInfoService;

    @Resource(name = "blMergeCartService")
    protected MergeCartService mergeCartService;
    
    @Resource(name = "blOrderServiceExtensionManager")
    protected OrderServiceExtensionManager extensionManager;
    
    /* Workflows */
    @Resource(name = "blAddItemWorkflow")
    protected Processor addItemWorkflow;
    
    @Resource(name = "blUpdateProductOptionsForItemWorkflow")
    private Processor updateProductOptionsForItemWorkflow;

    @Resource(name = "blUpdateItemWorkflow")
    protected Processor updateItemWorkflow;
    
    @Resource(name = "blRemoveItemWorkflow")
    protected Processor removeItemWorkflow;

    @Resource(name = "blTransactionManager")
    protected PlatformTransactionManager transactionManager;

    @Value("${pricing.retry.count.for.lock.failure}")
    protected int pricingRetryCountForLockFailure = 3;

    @Value("${pricing.retry.wait.interval.for.lock.failure}")
    protected long pricingRetryWaitIntervalForLockFailure = 500L;

    /**
     * Advanced setting. Should Hibernate auto flush before queries during an
     * add-to-cart workflow. This should generally be able to be left false. This is a performance measure and
     * add-to-cart operations will be more efficient when this is false.
     */
    @Value("${auto.flush.on.query.during.add.to.cart:false}")
    protected boolean autoFlushAddToCart = false;

    /**
     * Advanced setting. Should Hibernate auto flush before queries during an
     * update-cart workflow. This should generally be able to be left false. This is a performance measure and
     * update-cart operations will be more efficient when this is false.
     */
    @Value("${auto.flush.on.query.during.update.cart:false}")
    protected boolean autoFlushUpdateCart = false;

    /**
     * Advanced setting. Should Hibernate auto flush before queries during an
     * remove-from-cart workflow. This should generally be able to be left false. This is a performance measure and
     * remove-from-cart operations will be more efficient when this is false.
     */
    @Value("${auto.flush.on.query.during.remove.from.cart:false}")
    protected boolean autoFlushRemoveFromCart = false;

    /**
     * Advanced setting. Should Hibernate auto flush before queries during an
     * order save pricing flow. This should generally be able to be left false. This is a performance measure and
     * save operations will be more efficient when this is false.
     */
    @Value("${auto.flush.on.query.during.cart.pricing.save:false}")
    protected boolean autoFlushSaveCart = false;

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    /* Fields */
    protected boolean moveNamedOrderItems = true;
    protected boolean deleteEmptyNamedOrders = true;

    protected Boolean automaticallyMergeLikeItems;

    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

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
        
        return persist(namedOrder); // No need to price here
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
    public List<Order> findOrdersByIds(List<Long> orderIds) {
        return orderDao.readOrdersByIds(orderIds);
    }

    @Override
    public Order findOrderById(Long orderId, boolean refresh) {
        return orderDao.readOrderById(orderId, refresh);
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
    public List<Order> findOrdersByDateRange(Date startDate, Date endDate) {
        return orderDao.readOrdersByDateRange(startDate, endDate);
    }

    @Override
    public List<Order> findOrdersForCustomersInDateRange(List<Long> customerIds, Date startDate, Date endDate) {
        return orderDao.readOrdersForCustomersInDateRange(customerIds, startDate, endDate);
    }

    @Override
    public List<OrderPayment> findPaymentsForOrder(Order order) {
        return paymentDao.readPaymentsForOrder(order);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public OrderPayment addPaymentToOrder(Order order, OrderPayment payment, Referenced securePaymentInfo) {
        payment.setOrder(order);
        order.getPayments().add(payment);
        order = persist(order);
        int paymentIndex = order.getPayments().size() - 1;

        if (securePaymentInfo != null) {
            securePaymentInfoService.save(securePaymentInfo);
        }

        return order.getPayments().get(paymentIndex);
    }
    
    @Override
    public Order save(Order order, boolean priceOrder, boolean repriceItems) throws PricingException {
        if (repriceItems) {
            order.updatePrices();
        }
        return save(order, priceOrder);
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
                Session session = em.unwrap(Session.class);
                FlushMode current = session.getFlushMode();
                try {
                    if (!autoFlushSaveCart) {
                        //Performance measure. Hibernate will sometimes perform an autoflush when performing query operations and this can
                        //be expensive. It is possible to avoid the autoflush if there's no concern about queries in the flow returning
                        //incorrect results because something has not been flushed to the database yet.
                        session.setFlushMode(FlushMode.MANUAL);
                    }
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
                } finally {
                    if (!autoFlushSaveCart) {
                        session.setFlushMode(current);
                    }
                }
            }

            //make the final save of the priced order
            status = TransactionUtils.createTransaction("saveOrder",
                                TransactionDefinition.PROPAGATION_REQUIRED, transactionManager);
            Session session = em.unwrap(Session.class);
            FlushMode current = session.getFlushMode();
            try {
                if (!autoFlushSaveCart) {
                    //Performance measure. Hibernate will sometimes perform an autoflush when performing query operations and this can
                    //be expensive. It is possible to avoid the autoflush if there's no concern about queries in the flow returning
                    //incorrect results because something has not been flushed to the database yet.
                    session.setFlushMode(FlushMode.MANUAL);
                }
                order = persist(order);

                if (extensionManager != null) {
                    extensionManager.getProxy().attachAdditionalDataToOrder(order, priceOrder);
                }
                if (!autoFlushSaveCart) {
                    session.setFlushMode(current);
                }
                TransactionUtils.finalizeTransaction(status, transactionManager, false);
            } catch (RuntimeException ex) {
                TransactionUtils.finalizeTransaction(status, transactionManager, true);
                throw ex;
            } finally {
                if (!autoFlushSaveCart && !session.getFlushMode().equals(current)) {
                    session.setFlushMode(current);
                }
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
        orderMultishipOptionService.deleteAllOrderMultishipOptions(order);
        orderDao.delete(order);
    }

    @Override
    @Transactional("blTransactionManager")
    public Order addOfferCode(Order order, OfferCode offerCode, boolean priceOrder) throws PricingException, OfferException {
        ArrayList<OfferCode> offerCodes = new ArrayList<OfferCode>();
        offerCodes.add(offerCode);
        return addOfferCodes(order, offerCodes, priceOrder);
    }

    @Override
    @Transactional("blTransactionManager")
    public Order addOfferCodes(Order order, List<OfferCode> offerCodes, boolean priceOrder) throws PricingException, OfferException {
        preValidateCartOperation(order);
        Set<Offer> addedOffers = offerService.getUniqueOffersFromOrder(order);
        if (extensionManager != null) {
            extensionManager.getProxy().addOfferCodes(order, offerCodes, priceOrder);
        }
        if (offerCodes != null && !offerCodes.isEmpty()) {
            for (OfferCode offerCode : offerCodes) {
                
                if (order.getAddedOfferCodes().contains(offerCode) || addedOffers.contains(offerCode.getOffer())) {
                    throw new OfferAlreadyAddedException("The offer has already been added.");
                } else if (!offerService.verifyMaxCustomerUsageThreshold(order, offerCode)) {
                    throw new OfferMaxUseExceededException("The customer has used this offer code more than the maximum allowed number of times.");
                } else if (!offerCode.isActive() || !offerCode.getOffer().isActive()) {
                    throw new OfferExpiredException("The offer has expired.");
                }

                order.getAddedOfferCodes().add(offerCode);

            }
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

        // Remove any order items that are children
        CollectionUtils.filter(items,  new TypedPredicate<OrderItem>() {
            @Override
            public boolean eval(OrderItem orderItem) {
                return orderItem.getParentOrderItem() == null;
            }
        });
        for (OrderItem item : items) {
            OrderItemRequestDTO orderItemRequest = orderItemService.buildOrderItemRequestDTOFromOrderItem(item);
            cartOrder = addItem(cartOrder.getId(), orderItemRequest, priceOrder);

            if (moveNamedOrderItems) {
                removeItem(namedOrder.getId(), item.getId(), false);
            }
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

        OrderItemRequestDTO orderItemRequest = orderItemService.buildOrderItemRequestDTOFromOrderItem(item);
        cartOrder = addItem(cartOrder.getId(), orderItemRequest, priceOrder);

        if (moveNamedOrderItems) {
            removeItem(namedOrder.getId(), item.getId(), false);
        }

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

        OrderItemRequestDTO orderItemRequest = orderItemService.buildOrderItemRequestDTOFromOrderItem(item);
        orderItemRequest.setQuantity(quantity);
        cartOrder = addItem(cartOrder.getId(), orderItemRequest, priceOrder);

        if (moveNamedOrderItems) {
            // Update the old item to its new quantity only if we're moving items
            OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
            orderItemRequestDTO.setOrderItemId(item.getId());
            orderItemRequestDTO.setQuantity(item.getQuantity() - quantity);
            updateItemQuantity(namedOrder.getId(), orderItemRequestDTO, false);
        }
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
        preValidateCartOperation(order);
        if (getAutomaticallyMergeLikeItems()) {
            OrderItem item = findMatchingItem(order, orderItemRequestDTO);
            if (item != null && item.getParentOrderItem() == null) {
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
            int numAdditionRequests = priceOrder ? (getTotalChildOrderItems(orderItemRequestDTO)) : -1;
            int currentAddition = 1;

            CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, currentAddition == numAdditionRequests);

            Session session = em.unwrap(Session.class);
            FlushMode current = session.getFlushMode();
            if (!autoFlushAddToCart) {
                //Performance measure. Hibernate will sometimes perform an autoflush when performing query operations and this can
                //be expensive. It is possible to avoid the autoflush if there's no concern about queries in the flow returning
                //incorrect results because something has not been flushed to the database yet.
                session.setFlushMode(FlushMode.MANUAL);
            }
            ProcessContext<CartOperationRequest> context;
            try {
                context = (ProcessContext<CartOperationRequest>) addItemWorkflow.doActivities(cartOpRequest);
            } finally {
                if (!autoFlushAddToCart) {
                    session.setFlushMode(current);
                }
            }

            List<ActivityMessageDTO> orderMessages = new ArrayList<ActivityMessageDTO>();
            orderMessages.addAll(((ActivityMessages) context).getActivityMessages());

            // Update the orderItemRequest incase it changed during the initial add to cart workflow
            orderItemRequestDTO = context.getSeedData().getItemRequest();
            numAdditionRequests = priceOrder ? (getTotalChildOrderItems(orderItemRequestDTO) - 1) : -1;
            addChildItems(orderItemRequestDTO, numAdditionRequests, currentAddition, context, orderMessages);

            context.getSeedData().getOrder().setOrderMessages(orderMessages);
            return context.getSeedData().getOrder();
        } catch (WorkflowException e) {
            throw new AddToCartException("Could not add to cart", getCartOperationExceptionRootCause(e));
        }

    }

    @Override
    public int getTotalChildOrderItems(OrderItemRequestDTO orderItemRequestDTO) {
        int count = 1;
        for (OrderItemRequestDTO childRequest : orderItemRequestDTO.getChildOrderItems()) {
            count += getTotalChildOrderItems(childRequest);
        }
        return count;
    }

    @Override
    public void addChildItems(OrderItemRequestDTO orderItemRequestDTO, int numAdditionRequests, int currentAddition, ProcessContext<CartOperationRequest> context, List<ActivityMessageDTO> orderMessages) throws WorkflowException {
        if (CollectionUtils.isNotEmpty(orderItemRequestDTO.getChildOrderItems())) {
            Long parentOrderItemId = context.getSeedData().getOrderItem().getId();
            for (OrderItemRequestDTO childRequest : orderItemRequestDTO.getChildOrderItems()) {
                childRequest.setParentOrderItemId(parentOrderItemId);
                currentAddition++;

                if (childRequest.getQuantity() > 0) {
                    CartOperationRequest childCartOpRequest = new CartOperationRequest(context.getSeedData().getOrder(), childRequest, currentAddition == numAdditionRequests);
                    Session session = em.unwrap(Session.class);
                    FlushMode current = session.getFlushMode();
                    if (!autoFlushAddToCart) {
                        //Performance measure. Hibernate will sometimes perform an autoflush when performing query operations and this can
                        //be expensive. It is possible to avoid the autoflush if there's no concern about queries in the flow returning
                        //incorrect results because something has not been flushed to the database yet.
                        session.setFlushMode(FlushMode.MANUAL);
                    }
                    ProcessContext<CartOperationRequest> childContext;
                    try {
                        childContext = (ProcessContext<CartOperationRequest>) addItemWorkflow.doActivities(childCartOpRequest);
                    } finally {
                        if (!autoFlushAddToCart) {
                            session.setFlushMode(current);
                        }
                    }
                    orderMessages.addAll(((ActivityMessages) childContext).getActivityMessages());

                    addChildItems(childRequest, numAdditionRequests, currentAddition, childContext, orderMessages);
                }
            }
        }
    }

    @Override
    public void addDependentOrderItem(OrderItemRequestDTO parentOrderItemRequest, OrderItemRequestDTO dependentOrderItem) {
        parentOrderItemRequest.getChildOrderItems().add(dependentOrderItem);
    }

    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = {UpdateCartException.class, RemoveFromCartException.class})
    public Order updateItemQuantity(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws UpdateCartException, RemoveFromCartException {
        Order order = findOrderById(orderId);
        preValidateCartOperation(order);
        preValidateUpdateQuantityOperation(findOrderById(orderId), orderItemRequestDTO);
        if (orderItemRequestDTO.getQuantity() == 0) {
            return removeItem(orderId, orderItemRequestDTO.getOrderItemId(), priceOrder);
        }
        
        try {
            CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, priceOrder);
            Session session = em.unwrap(Session.class);
            FlushMode current = session.getFlushMode();
            if (!autoFlushUpdateCart) {
                //Performance measure. Hibernate will sometimes perform an autoflush when performing query operations and this can
                //be expensive. It is possible to avoid the autoflush if there's no concern about queries in the flow returning
                //incorrect results because something has not been flushed to the database yet.
                session.setFlushMode(FlushMode.MANUAL);
            }
            ProcessContext<CartOperationRequest> context;
            try {
                context = (ProcessContext<CartOperationRequest>) updateItemWorkflow.doActivities(cartOpRequest);
            } finally {
                if (!autoFlushUpdateCart) {
                    session.setFlushMode(current);
                }
            }
            context.getSeedData().getOrder().getOrderMessages().addAll(((ActivityMessages) context).getActivityMessages());
            return context.getSeedData().getOrder();
        } catch (WorkflowException e) {
            throw new UpdateCartException("Could not update cart quantity", getCartOperationExceptionRootCause(e));
        }
    }

    @Override
    @Transactional(value = "blTransactionManager", rollbackFor = {RemoveFromCartException.class})
    public Order removeItem(Long orderId, Long orderItemId, boolean priceOrder) throws RemoveFromCartException {
        preValidateCartOperation(findOrderById(orderId));
        try {
            OrderItem oi = orderItemService.readOrderItemById(orderItemId);
            if (oi == null) {
                throw new WorkflowException(new ItemNotFoundException());
            }
            List<Long> childrenToRemove = new ArrayList<Long>();
            if (oi instanceof BundleOrderItem) {
                List<DiscreteOrderItem> bundledItems = ((BundleOrderItem) oi).getDiscreteOrderItems();
                for (DiscreteOrderItem doi : bundledItems) {
                    findAllChildrenToRemove(childrenToRemove, doi);
                }
            } else {
                findAllChildrenToRemove(childrenToRemove, oi);
            }
            for (Long childToRemove : childrenToRemove) {
                removeItemInternal(orderId, childToRemove, false);
            }                    

            return removeItemInternal(orderId, orderItemId, priceOrder);
        } catch (WorkflowException e) {
            throw new RemoveFromCartException("Could not remove from cart", getCartOperationExceptionRootCause(e));
        }
    }

    protected void findAllChildrenToRemove(List<Long> childrenToRemove, OrderItem orderItem){
        if (CollectionUtils.isNotEmpty(orderItem.getChildOrderItems())) {
            for (OrderItem childOrderItem : orderItem.getChildOrderItems()) {
                findAllChildrenToRemove(childrenToRemove, childOrderItem);
                childrenToRemove.add(childOrderItem.getId());
            }
        }
    }
    
    protected Order removeItemInternal(Long orderId, Long orderItemId, boolean priceOrder) throws WorkflowException {
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setOrderItemId(orderItemId);
        CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, priceOrder);
        Session session = em.unwrap(Session.class);
        FlushMode current = session.getFlushMode();
        if (!autoFlushRemoveFromCart) {
            //Performance measure. Hibernate will sometimes perform an autoflush when performing query operations and this can
            //be expensive. It is possible to avoid the autoflush if there's no concern about queries in the flow returning
            //incorrect results because something has not been flushed to the database yet.
            session.setFlushMode(FlushMode.MANUAL);
        }
        ProcessContext<CartOperationRequest> context;
        try {
            context = (ProcessContext<CartOperationRequest>) removeItemWorkflow.doActivities(cartOpRequest);
        } finally {
            if (!autoFlushRemoveFromCart) {
                session.setFlushMode(current);
            }
        }
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
        
        if (automaticallyMergeLikeItems != null) {
            return automaticallyMergeLikeItems;
        }

        return BLCSystemProperty.resolveBooleanSystemProperty("automatically.merge.like.items", true);
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
    public void removePaymentsFromOrder(Order order, PaymentType paymentInfoType) {
        List<OrderPayment> infos = new ArrayList<OrderPayment>();
        for (OrderPayment paymentInfo : order.getPayments()) {
            if (paymentInfoType == null || paymentInfoType.equals(paymentInfo.getType())) {
                infos.add(paymentInfo);
            }
        }
        order.getPayments().removeAll(infos);
        for (OrderPayment paymentInfo : infos) {
            try {
                securePaymentInfoService.findAndRemoveSecurePaymentInfo(paymentInfo.getReferenceNumber(), paymentInfo.getType());
            } catch (WorkflowException e) {
                // do nothing--this is an acceptable condition
                LOG.debug("No secure payment is associated with the OrderPayment", e);
            }
            order.getPayments().remove(paymentInfo);
            paymentInfo = paymentDao.readPaymentById(paymentInfo.getId());
            paymentDao.delete(paymentInfo);
        }
    }

    @Override
    @Transactional("blTransactionManager")
    public void removePaymentFromOrder(Order order, OrderPayment payment){
        OrderPayment paymentToRemove = null;
        for (OrderPayment info : order.getPayments()){
            if (info.equals(payment)){
                paymentToRemove = info;
            }
        }
        if (paymentToRemove != null){
            try {
                securePaymentInfoService.findAndRemoveSecurePaymentInfo(paymentToRemove.getReferenceNumber(), payment.getType());
            } catch (WorkflowException e) {
                // do nothing--this is an acceptable condition
                LOG.debug("No secure payment is associated with the OrderPayment", e);
            }
            order.getPayments().remove(paymentToRemove);
            payment = paymentDao.readPaymentById(paymentToRemove.getId());
            paymentDao.delete(payment);
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
     * @param item1Attributes
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
    public Order reloadOrder(Order order) {
        if (order == null || order instanceof NullOrderImpl || order.getId() == null) {
            return order;
        }

        return orderDao.readOrderById(order.getId(), true);
    }

    @Override
    @Transactional("blTransactionManager")
    public boolean acquireLock(Order order) {
        return orderDao.acquireLock(order);
    }

    @Override
    public boolean releaseLock(Order order) {
        return orderDao.releaseLock(order);
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
    
    @Override
    public void preValidateCartOperation(Order cart) {
        ExtensionResultHolder erh = new ExtensionResultHolder();
        extensionManager.getProxy().preValidateCartOperation(cart, erh);
        if (erh.getThrowable() instanceof IllegalCartOperationException) {
            throw ((IllegalCartOperationException) erh.getThrowable());
        } else if (erh.getThrowable() != null) {
            throw new RuntimeException(erh.getThrowable());
        }
    }

    @Override
    public void preValidateUpdateQuantityOperation(Order cart, OrderItemRequestDTO dto) {
        ExtensionResultHolder erh = new ExtensionResultHolder();
        extensionManager.getProxy().preValidateUpdateQuantityOperation(cart, dto, erh);
        if (erh.getThrowable() instanceof IllegalCartOperationException) {
            throw ((IllegalCartOperationException) erh.getThrowable());
        } else if (erh.getThrowable() != null) {
            throw new RuntimeException(erh.getThrowable());
        }
    }

    @Override
    public void refresh(Order order) {
        orderDao.refresh(order);
    }

    @Override
    public Order findCartForCustomerWithEnhancements(Customer customer) {
        ExtensionResultHolder<Order> erh = new ExtensionResultHolder<Order>();
        ExtensionResultStatusType resultStatusType = extensionManager.findCartForCustomerWithEnhancements(customer, erh);
        if (ExtensionResultStatusType.NOT_HANDLED != resultStatusType) {
            return erh.getResult();
        }
        return findCartForCustomer(customer);
    }

    @Override
    public Order findCartForCustomerWithEnhancements(Customer customer, Order candidateOrder) {
        ExtensionResultHolder<Order> erh = new ExtensionResultHolder<Order>();
        ExtensionResultStatusType resultStatusType = extensionManager.findCartForCustomerWithEnhancements(customer, candidateOrder, erh);
        if (ExtensionResultStatusType.NOT_HANDLED != resultStatusType) {
            return erh.getResult();
        }
        return candidateOrder;
    }

    @Override
    public List<Order> findOrdersByEmail(String email) {
        return orderDao.readOrdersByEmail(email);
    }
}
