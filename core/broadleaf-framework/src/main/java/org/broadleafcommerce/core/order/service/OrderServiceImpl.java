/*
 * Copyright 2012 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.inventory.exception.InventoryUnavailableException;
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
import org.broadleafcommerce.core.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.order.service.workflow.CartOperationContext;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.payment.dao.PaymentInfoDao;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.pricing.service.PricingService;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.SequenceProcessor;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

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
    protected OrderServiceExtensionListener extensionManager;
    
    /* Workflows */
    @Resource(name = "blAddItemWorkflow")
    protected SequenceProcessor addItemWorkflow;
    
    @Resource(name = "blUpdateItemWorkflow")
    protected SequenceProcessor updateItemWorkflow;
    
    @Resource(name = "blRemoveItemWorkflow")
    protected SequenceProcessor removeItemWorkflow;
    
    /* Fields */
    protected boolean moveNamedOrderItems = true;
    protected boolean deleteEmptyNamedOrders = true;
    protected boolean automaticallyMergeLikeItems = true; 

	@Override
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
        
        extensionManager.attachAdditionalDataToNewNamedCart(customer, namedOrder);
        
                        //FIXME: apa fix this
                        //namedOrder.setLocale(locale);
                        //namedOrder.setCurrency(priceList.getCurrencyCode());
        
        
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
    @Transactional(value = "blTransactionManager")
	public Order save(Order order, Boolean priceOrder) throws PricingException {
        if (priceOrder) {
            order = pricingService.executePricing(order);
        }
        return persist(order);
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
	        try {
                updateItemQuantity(namedOrder.getId(), orderItemRequestDTO, false);
            } catch (InventoryUnavailableException e) {
                throw new UpdateCartException("Could not update cart quantity", e.getCause());
            }
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
    	try {
    		CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, priceOrder);
    		CartOperationContext context = (CartOperationContext) addItemWorkflow.doActivities(cartOpRequest);
    		return context.getSeedData().getOrder();
    	} catch (WorkflowException e) {
    		throw new AddToCartException("Could not add to cart", getCartOperationExceptionRootCause(e));
    	}
    }

	@Override
    @Transactional(value = "blTransactionManager", rollbackFor = {UpdateCartException.class, RemoveFromCartException.class})
	public Order updateItemQuantity(Long orderId, OrderItemRequestDTO orderItemRequestDTO, boolean priceOrder) throws UpdateCartException, RemoveFromCartException, InventoryUnavailableException {
		if (orderItemRequestDTO.getQuantity() == 0) {
			return removeItem(orderId, orderItemRequestDTO.getOrderItemId(), priceOrder);
		}
		
    	try {
    		CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, priceOrder);
    		CartOperationContext context = (CartOperationContext) updateItemWorkflow.doActivities(cartOpRequest);
    		return context.getSeedData().getOrder();
    	} catch (WorkflowException e) {
            if (e.getCause() instanceof InventoryUnavailableException) {
                throw new InventoryUnavailableException(e.getCause());
            }
    		throw new UpdateCartException("Could not update cart quantity", getCartOperationExceptionRootCause(e));
    	}
	}

	@Override
    @Transactional(value = "blTransactionManager", rollbackFor = {RemoveFromCartException.class})
	public Order removeItem(Long orderId, Long orderItemId, boolean priceOrder) throws RemoveFromCartException {
    	try {
    		OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
    		orderItemRequestDTO.setOrderItemId(orderItemId);
    		CartOperationRequest cartOpRequest = new CartOperationRequest(findOrderById(orderId), orderItemRequestDTO, priceOrder);
    		CartOperationContext context = (CartOperationContext) removeItemWorkflow.doActivities(cartOpRequest);
    		return context.getSeedData().getOrder();
    	} catch (WorkflowException e) {
    		throw new RemoveFromCartException("Could not remove from cart", getCartOperationExceptionRootCause(e));
    	}
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
}
