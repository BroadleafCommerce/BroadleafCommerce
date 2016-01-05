/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.rest.api.v2.endpoint.order;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferAlreadyAddedException;
import org.broadleafcommerce.core.offer.service.exception.OfferException;
import org.broadleafcommerce.core.offer.service.exception.OfferExpiredException;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderAttribute;
import org.broadleafcommerce.core.order.domain.OrderAttributeImpl;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.rest.api.exception.BroadleafWebServicesException;
import org.broadleafcommerce.core.rest.api.v2.endpoint.catalog.CatalogEndpoint;
import org.broadleafcommerce.core.rest.api.v2.wrapper.OrderAttributeWrapper;
import org.broadleafcommerce.core.rest.api.v2.wrapper.OrderItemAttributeWrapper;
import org.broadleafcommerce.core.rest.api.v2.wrapper.OrderWrapper;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.beans.BeansException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This endpoint depends on JAX-RS to provide cart services.  It should be extended by components that actually wish 
 * to provide an endpoint.  The annotations such as @Path, @Scope, @Context, @PathParam, @QueryParam, 
 * @GET, @POST, @PUT, and @DELETE are purposely not provided here to allow implementors finer control over 
 * the details of the endpoint.
 *
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
public abstract class CartEndpoint extends BaseEndpoint {

    @Resource(name="blOrderService")
    protected OrderService orderService;

    @Resource(name="blOfferService")
    protected OfferService offerService;

    @Resource(name="blCustomerService")
    protected CustomerService customerService;
    
    @Resource(name="blOrderItemService")
    protected OrderItemService orderItemService;
    
    @Resource(name="blCatalogService")
    protected CatalogService catalogService;
    
    /**
     * Search for {@code Order} by {@code Customer}
     *
     * @return the cart for the customer
     */
    public OrderWrapper findCartForCustomer(HttpServletRequest request, Long customerId) {
        if (customerId == null) {
            throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value());
        }
        Customer customer = customerService.readCustomerById(customerId);
        Order cart = null;
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        } else {
            cart = orderService.findCartForCustomer(customer);
        }
        
        if (cart == null) {
            return createNewCartForCustomer(request, customerId);
        }
        
        OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
        wrapper.wrapDetails(cart, request);

        return wrapper;
    }

    /**
     * Create a new {@code Order} for {@code Customer}
     *
     * @return the cart for the customer
     */
    public OrderWrapper createNewCartForCustomer(HttpServletRequest request, Long customerId) {
        if (customerId == null) {
            throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value());
        }
        Customer customer = customerService.readCustomerById(customerId);
        Order cart = null;
        if (customer == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
        } else {
            cart = orderService.createNewCartForCustomer(customer);
        }
        
        if (cart == null || cart instanceof NullOrderImpl) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }
        
        OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
        wrapper.wrapDetails(cart, request);

        return wrapper;
        
    }
    
    public OrderWrapper findCartById(HttpServletRequest request, Long cartId, Long customerId) {
        Order cart = validateCartAndCustomer(customerId, cartId);
        
        OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
        wrapper.wrapDetails(cart, request);

        return wrapper;
    }
    
    /**
     * This method takes in a categoryId and productId as path parameters.  In addition, query parameters can be supplied including:
     * 
     * <li>skuId</li>
     * <li>quantity</li>
     * <li>priceOrder</li>
     * 
     * You must provide a ProductId OR ProductId with product options. Product options can be posted as form or querystring parameters. 
     * You must pass in the ProductOption attributeName as the key and the 
     * ProductOptionValue attributeValue as the value.  See {@link CatalogEndpoint}.
     * 
     * @param request
     * @param uriInfo
     * @param categoryId
     * @param productId
     * @param quantity
     * @param priceOrder
     * @return OrderWrapper
     */
    public OrderWrapper addProductToOrder(HttpServletRequest request,
            Long productId,
            Long cartId,
            List<OrderItemAttributeWrapper> requestParams,
            Long customerId,
            Double itemPrice,
            Long categoryId,
            Integer quantity,
            Boolean priceOrder,
            Long parentOrderItemId) {
        
        if (productId == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.PRODUCT_NOT_FOUND);
        }
        
        return addItemToOrder(request, productId, null, cartId, transformOrderItemAttributeWrappersToMap(requestParams), null, customerId, itemPrice, categoryId, quantity, priceOrder, parentOrderItemId);
    }
    
    public OrderWrapper addSkuToOrder(HttpServletRequest request,
            Long skuId,
            Long cartId,
            List<OrderItemAttributeWrapper> requestParams,
            Long customerId,
            Double itemPrice,
            Long categoryId,
            Integer quantity,
            Boolean priceOrder,
            Long parentOrderItemId) {
        
        Sku sku = catalogService.findSkuById(skuId);
        if (sku == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.SKU_NOT_FOUND);
        }
        Product prod = sku.getProduct();
        
        return addItemToOrder(request, prod != null ? prod.getId() : null, skuId, cartId, transformOrderItemAttributeWrappersToMap(requestParams), null, customerId, itemPrice, categoryId, quantity, priceOrder, parentOrderItemId);
    }
    
    public OrderWrapper addNonDiscreteOrderItemToOrder(HttpServletRequest request,
            Long cartId,
            String itemName,
            List<OrderItemAttributeWrapper> requestParams,
            Long customerId,
            Double itemPrice,
            Long categoryId,
            Integer quantity,
            Boolean priceOrder,
            Long parentOrderItemId) {
        
        if (StringUtils.isBlank(itemName)) {
            throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value())
                .addMessage(BroadleafWebServicesException.MISSING_ITEM_NAME);
        }
        return addItemToOrder(request, null, null, cartId, transformOrderItemAttributeWrappersToMap(requestParams), itemName, customerId, itemPrice, categoryId, quantity, priceOrder, parentOrderItemId);
        
    }
    
    public OrderWrapper removeItemFromOrder(HttpServletRequest request,
            Long itemId,
            Long cartId,
            Long customerId,
            Boolean priceOrder) {

        Order cart = validateCartAndCustomer(customerId, cartId);
        
        try {
            Order order = orderService.removeItem(cart.getId(), itemId, priceOrder);
            order = orderService.save(order, priceOrder);

            OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
            wrapper.wrapDetails(order, request);

            return wrapper;
        } catch (PricingException e) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        } catch (RemoveFromCartException e) {
            if (e.getCause() instanceof ItemNotFoundException) {
                throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value(), null, null, e.getCause())
                        .addMessage(BroadleafWebServicesException.CART_ITEM_NOT_FOUND, itemId);
            } else {
                throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e);
            }
        }
    }

    public OrderWrapper updateItemQuantity(HttpServletRequest request,
            Long itemId,
            Long cartId,
            Long customerId,
            Integer quantity,
            Boolean priceOrder) {

        Order cart = validateCartAndCustomer(customerId, cartId);
        
        try {
            OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
            orderItemRequestDTO.setOrderItemId(itemId);
            orderItemRequestDTO.setQuantity(quantity);
            Order order = orderService.updateItemQuantity(cart.getId(), orderItemRequestDTO, priceOrder);
            order = orderService.save(order, priceOrder);

            OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
            wrapper.wrapDetails(order, request);

            return wrapper;
        } catch (UpdateCartException e) {
            if (e.getCause() instanceof ItemNotFoundException) {
                throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value(), null, null, e.getCause())
                        .addMessage(BroadleafWebServicesException.CART_ITEM_NOT_FOUND, itemId);
            } else {
                throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e)
                        .addMessage(BroadleafWebServicesException.UPDATE_CART_ERROR);
            }
        } catch (RemoveFromCartException e) {
            if (e.getCause() instanceof ItemNotFoundException) {
                throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value(), null, null, e.getCause())
                        .addMessage(BroadleafWebServicesException.CART_ITEM_NOT_FOUND, itemId);
            } else {
                throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e)
                        .addMessage(BroadleafWebServicesException.UPDATE_CART_ERROR);
            }
        } catch (PricingException pe) {
           throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, pe)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        }
    }

    public OrderWrapper addOfferCode(HttpServletRequest request,
            String promoCode,
            Long cartId,
            Long customerId,
            Boolean priceOrder) {

        Order cart = validateCartAndCustomer(customerId, cartId);
        OfferCode offerCode = offerService.lookupOfferCodeByCode(promoCode);

        if (offerCode == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.PROMO_CODE_INVALID, promoCode);
        }

        try {
            cart = orderService.addOfferCode(cart, offerCode, priceOrder);
            OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
            wrapper.wrapDetails(cart, request);

            return wrapper;
        } catch (PricingException e) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        } catch (OfferException e) {
            Throwable t = e.getCause();
            if (t instanceof OfferMaxUseExceededException) {
                throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value(), null, null, e)
                .addMessage(BroadleafWebServicesException.PROMO_CODE_MAX_USAGES, promoCode);
            } else if (t instanceof OfferExpiredException) {
                throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value(), null, null, e)
                .addMessage(BroadleafWebServicesException.PROMO_CODE_EXPIRED, promoCode);
            } else if (t instanceof OfferAlreadyAddedException) {
                throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value(), null, null, e)
                .addMessage(BroadleafWebServicesException.PROMO_CODE_ALREADY_ADDED, promoCode);
            } else {
                throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value(), null, null, e)
                .addMessage(BroadleafWebServicesException.PROMO_CODE_INVALID, promoCode);
            }
        }
    }

    public OrderWrapper removeOfferCode(HttpServletRequest request,
            String promoCode,
            Long cartId,
            Long customerId,
            Boolean priceOrder) {
        Order cart = validateCartAndCustomer(customerId, cartId);
        OfferCode offerCode = offerService.lookupOfferCodeByCode(promoCode);
        if (offerCode == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.PROMO_CODE_INVALID, promoCode);
        }

        try {
            cart = orderService.removeOfferCode(cart, offerCode, priceOrder);
            OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
            wrapper.wrapDetails(cart, request);

            return wrapper;
        } catch (PricingException e) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        }

    }

    public OrderWrapper removeAllOfferCodes(HttpServletRequest request,
            Long cartId,
            Long customerId,
            boolean priceOrder) {

        Order cart = validateCartAndCustomer(customerId, cartId);

        try {
            cart = orderService.removeAllOfferCodes(cart, priceOrder);
            return wrapCart(request, cart);

        } catch (PricingException e) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        }
    }

    public OrderWrapper updateProductOptions(HttpServletRequest request,
            List<OrderItemAttributeWrapper> requestParams,
            Long cartId,
            Long customerId,
            Long itemId,
            Boolean priceOrder) {

        Order cart = validateCartAndCustomer(customerId, cartId);
        try {
            OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();

            HashMap<String, String> productOptions = parseOptions(transformOrderItemAttributeWrappersToMap(requestParams));
            orderItemRequestDTO.setOrderItemId(itemId);
            //If we have product options set them on the DTO
            if (productOptions.size() > 0) {
                orderItemRequestDTO.setItemAttributes(productOptions);
            }
            Order order = orderService.updateProductOptionsForItem(cart.getId(), orderItemRequestDTO, priceOrder);

            order = orderService.save(order, priceOrder);
            return wrapCart(request, cart);
        } catch (UpdateCartException e) {
            if (e.getCause() instanceof ItemNotFoundException) {
                throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value(), null, null, e.getCause())
                .addMessage(BroadleafWebServicesException.CART_ITEM_NOT_FOUND, itemId);
            } else {
                throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e)
                        .addMessage(BroadleafWebServicesException.UPDATE_CART_ERROR);
            }
        } catch (PricingException pe) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, pe)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        }

    }
    
    public OrderWrapper deleteProductOptions(HttpServletRequest request,
            List<OrderItemAttributeWrapper> requestParams,
            Long cartId,
            Long customerId,
            Long itemId,
            Boolean priceOrder) {
        
        Order cart = validateCartAndCustomer(customerId, cartId);
        try {
            OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();

            Map<String, String> productOptions = parseOptions(transformOrderItemAttributeWrappersToMap(requestParams));
            orderItemRequestDTO.setOrderItemId(itemId);
            OrderItem item = orderItemService.readOrderItemById(itemId);
            if (item == null) {
                throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.CART_ITEM_NOT_FOUND);
            }
            
            //If we have product options set them on the DTO
            if (productOptions.size() > 0) {
                Map<String, OrderItemAttribute> attributes = item.getOrderItemAttributes();
                for (String key : productOptions.keySet()) {
                    attributes.remove(key);
                }
                orderItemService.saveOrderItem(item);
            }
            
            cart = orderService.save(cart, priceOrder);
            return wrapCart(request, cart);
        } catch (PricingException pe) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, pe)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        }
        
    }
    
    public OrderWrapper updateOrderAttributes(HttpServletRequest request,
            List<OrderAttributeWrapper> requestParams,
            Long cartId,
            Long customerId,
            Boolean priceOrder) {
        
        Order cart = validateCartAndCustomer(customerId, cartId);
        try {
            Map<String, String> options = parseOptions(transformOrderAttributeWrappersToMap(requestParams));
            
            if (options.size() > 0) {
                Map<String, OrderAttribute> attributes = cart.getOrderAttributes();
                Map<String, OrderAttribute> newAttributes = getOrderAttributeMap(options, cart);
                for(String key : newAttributes.keySet()) {
                    attributes.put(key, newAttributes.get(key));
                }
            }
            
            cart = orderService.save(cart, priceOrder);
            return wrapCart(request, cart);
        } catch (PricingException pe) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, pe)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        }
    }
    
    public OrderWrapper deleteOrderAttributes(HttpServletRequest request,
            List<OrderAttributeWrapper> requestParams,
            Long cartId,
            Long customerId,
            Boolean priceOrder) {
        
        Order cart = validateCartAndCustomer(customerId, cartId);
        try {
            Map<String, String> options = parseOptions(transformOrderAttributeWrappersToMap(requestParams));
            
            if (options.size() > 0) {
                Map<String, OrderAttribute> attributes = cart.getOrderAttributes();
                Map<String, OrderAttribute> newAttributes = getRemainingOrderAttributes(options, attributes);
                attributes.clear();
                for(String key : newAttributes.keySet()) {
                    attributes.put(key, newAttributes.get(key));
                }
            }
            
            cart = orderService.save(cart, priceOrder);
            return wrapCart(request, cart);
        } catch (PricingException pe) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, pe)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        }
    }
    
    protected Map<String, OrderAttribute> getOrderAttributeMap(Map<String, String> options, Order order) {
        Map<String, OrderAttribute> attributes = new HashMap<String, OrderAttribute>();
        for (String key : options.keySet()) {
            OrderAttribute attribute = new OrderAttributeImpl();
            attribute.setName(key);
            attribute.setValue(options.get(key));
            attribute.setOrder(order);
            attributes.put(key, attribute);
        }
        return attributes;
    }
    
    protected Map<String, OrderAttribute> getRemainingOrderAttributes(Map<String, String> attrsToRemove, Map<String, OrderAttribute> attrs) {
        Map<String, OrderAttribute> resultParams = new HashMap<String, OrderAttribute>();
        for (String key : attrs.keySet()) {
            if (!attrsToRemove.containsKey(key)) {
                resultParams.put(key, attrs.get(key));
            }
        }
        return resultParams;
    }
    
    protected OrderWrapper addItemToOrder(HttpServletRequest request,
            Long productId,
            Long skuId,
            Long cartId,
            Map<String, String> requestParams,
            String itemName,
            Long customerId,
            Double itemPrice,
            Long categoryId,
            Integer quantity,
            Boolean priceOrder,
            Long parentOrderItemId) {
    
        Order cart = validateCartAndCustomer(customerId, cartId);
        
        try {
            
            if (productId == null && skuId == null && StringUtils.isBlank(itemName)) {
                throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value())
                    .addMessage(BroadleafWebServicesException.INVALID_ADD_TO_CART_REQUEST);
            }
            
            OrderItemRequestDTO orderItemRequestDTO = populateOrderItemRequestDTO(request, 
                requestParams, productId, skuId, itemName, itemPrice, categoryId, quantity, parentOrderItemId);
            
            Order order = orderService.addItemWithPriceOverrides(cart.getId(), orderItemRequestDTO, priceOrder);
            order = orderService.save(order, priceOrder);
    
            OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
            wrapper.wrapDetails(order, request);
    
            return wrapper;
        } catch (PricingException e) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e);
        } catch (AddToCartException e) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e);
        }
        
    }
    
    protected OrderItemRequestDTO populateOrderItemRequestDTO(HttpServletRequest request,
            Map<String, String> requestParams,
            Long productId,
            Long skuId,
            String itemName,
            Double itemPrice, 
            Long categoryId,
            Integer quantity,
            Long parentOrderItemId) {
        
        //We allow product options to be submitted via form post or via query params.  We need to take 
        //the product options and build a map with them...
        HashMap<String, String> productOptions = parseOptions(requestParams);
        OrderItemRequestDTO orderItemRequestDTO = null;
        if (itemName == null) {
            orderItemRequestDTO = new OrderItemRequestDTO();
        } else {
            orderItemRequestDTO = new NonDiscreteOrderItemRequestDTO();
        }
            
        if (productId != null) {
            orderItemRequestDTO.setProductId(productId);
        }
        if (skuId != null) {
            orderItemRequestDTO.setSkuId(skuId);
        }
        if (StringUtils.isNotBlank(itemName)) {
            ((NonDiscreteOrderItemRequestDTO) orderItemRequestDTO).setItemName(itemName);
        }
        if (categoryId != null) {
            orderItemRequestDTO.setCategoryId(categoryId);
        }
        if (quantity != null) {
            orderItemRequestDTO.setQuantity(quantity);
        }
        if (parentOrderItemId != null) {
            orderItemRequestDTO.setParentOrderItemId(parentOrderItemId);
        }
        if (itemPrice != null) {
            orderItemRequestDTO.setOverrideRetailPrice(new Money(itemPrice));
            orderItemRequestDTO.setOverrideSalePrice(orderItemRequestDTO.getOverrideRetailPrice());
        }
    
        //If we have product options set them on the DTO
        if (productOptions.size() > 0) {
            orderItemRequestDTO.setItemAttributes(productOptions);
        }
        
        return orderItemRequestDTO;
    }
    
    protected HashMap<String, String> parseOptions(Map<String, String> requestParams) {
        HashMap<String, String> productOptions = new HashMap<String, String>();
    
        //Fill up a map of key values that will represent product options
        Set<String> keySet = requestParams.keySet();
        for (String key : keySet) {
            if (requestParams.get(key) != null) {
                //Product options should be returned with "productOption." as a prefix. We'll look for those, and 
                //remove the prefix.
                productOptions.put(StringUtils.removeStart(key, "productOption."), requestParams.get(key));
            }
        }
        return productOptions;
    }

    protected Order validateCartAndCustomer(Long customerId, Long cartId) {
        Order cart = orderService.findOrderById(cartId);
        if (cart == null || cart instanceof NullOrderImpl) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }
        if (customerId != null) {
            Customer customer = customerService.readCustomerById(customerId);
            if (customer == null) {
                throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
            } else if (ObjectUtils.notEqual(customer.getId(), cart.getCustomer().getId())) {
                throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value())
                    .addMessage(BroadleafWebServicesException.CART_CUSTOMER_MISMATCH);
            }
        }
        return cart; 
    }
    
    protected Map<String, String> transformOrderItemAttributeWrappersToMap(List<OrderItemAttributeWrapper> oiaws) {
        Map<String, String> params = new HashMap<>();
        if (oiaws == null) {
            return params;
        }
        for (OrderItemAttributeWrapper oiaw : oiaws) {
            params.put(oiaw.getName(), oiaw.getValue());
        }
        return params;
    }
    
    protected Map<String, String> transformOrderAttributeWrappersToMap(List<OrderAttributeWrapper> oaws) {
        Map<String, String> params = new HashMap<>();
        if (oaws == null) {
            return params;
        }
        for (OrderAttributeWrapper oaw : oaws) {
            params.put(oaw.getName(), oaw.getValue());
        }
        return params;
    }
    
    protected OrderWrapper wrapCart(HttpServletRequest request, Order cart) {

        try {
            OrderWrapper orderWrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
            orderWrapper.wrapDetails(cart, request);
            return orderWrapper;
        } catch (BeansException e) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e);
        }

    }
}
