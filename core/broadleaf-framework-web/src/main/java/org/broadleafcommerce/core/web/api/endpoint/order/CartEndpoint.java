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
package org.broadleafcommerce.core.web.api.endpoint.order;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferAlreadyAddedException;
import org.broadleafcommerce.core.offer.service.exception.OfferException;
import org.broadleafcommerce.core.offer.service.exception.OfferExpiredException;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.api.BroadleafWebServicesException;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.core.web.api.endpoint.catalog.CatalogEndpoint;
import org.broadleafcommerce.core.web.api.wrapper.OrderWrapper;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.BeansException;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
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

   /**
     * Search for {@code Order} by {@code Customer}
     *
     * @return the cart for the customer
     */
    public OrderWrapper findCartForCustomer(HttpServletRequest request) {
        Order cart = CartState.getCart();
        if (cart == null || cart instanceof NullOrderImpl) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
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
    public OrderWrapper createNewCartForCustomer(HttpServletRequest request) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer == null) {
            customer = customerService.createCustomerFromId(null);
        }

        Order cart = orderService.findCartForCustomer(customer);
        if (cart == null) {
            cart = orderService.createNewCartForCustomer(customer);
            CartState.setCart(cart);
        }

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
            MultiValueMap<String, String> requestParams,
            Long productId,
            Long categoryId,
            int quantity,
            boolean priceOrder) {
        
        Order cart = CartState.getCart();
        if (cart == null || cart instanceof NullOrderImpl) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }

        try {
            //We allow product options to be submitted via form post or via query params.  We need to take 
            //the product options and build a map with them...
            HashMap<String, String> productOptions = getOptions(requestParams);

            OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
            orderItemRequestDTO.setProductId(productId);
            orderItemRequestDTO.setCategoryId(categoryId);
            orderItemRequestDTO.setQuantity(quantity);

            //If we have product options set them on the DTO
            if (productOptions.size() > 0) {
                orderItemRequestDTO.setItemAttributes(productOptions);
            }

            Order order = orderService.addItem(cart.getId(), orderItemRequestDTO, priceOrder);
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

    protected HashMap<String, String> getOptions(MultiValueMap<String, String> requestParams) {
        HashMap<String, String> productOptions = new HashMap<String, String>();

        //Fill up a map of key values that will represent product options
        Set<String> keySet = requestParams.keySet();
        for (String key : keySet) {
            if (requestParams.getFirst(key) != null) {
                //Product options should be returned with "productOption." as a prefix. We'll look for those, and 
                //remove the prefix.
                if (key.startsWith("productOption.")) {
                    productOptions.put(StringUtils.removeStart(key, "productOption."), requestParams.getFirst(key));
                }
            }
        }
        return productOptions;
    }

    public OrderWrapper removeItemFromOrder(HttpServletRequest request,
            Long itemId,
            boolean priceOrder) {

        Order cart = CartState.getCart();
        if (cart == null || cart instanceof NullOrderImpl) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }
        
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
            Integer quantity,
            boolean priceOrder) {

        Order cart = CartState.getCart();

        if (cart == null || cart instanceof NullOrderImpl) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }
        
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
            boolean priceOrder) {

        Order cart = CartState.getCart();

        if (cart == null || cart instanceof NullOrderImpl) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }

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
            boolean priceOrder) {
        Order cart = CartState.getCart();
        if (cart == null || cart instanceof NullOrderImpl) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }

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
            boolean priceOrder) {

        Order cart = CartState.getCart();

        if (cart == null || cart instanceof NullOrderImpl) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }

        try {
            cart = orderService.removeAllOfferCodes(cart, priceOrder);
            return wrapCart(request, cart);

        } catch (PricingException e) {
            throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, e)
                    .addMessage(BroadleafWebServicesException.CART_PRICING_ERROR);
        }
    }

    public OrderWrapper updateProductOptions(HttpServletRequest request,
            MultiValueMap<String, String> requestParams,
            Long itemId,
            boolean priceOrder) {

        Order cart = CartState.getCart();

        if (cart != null) {
            try {
                OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();

                HashMap<String, String> productOptions = getOptions(requestParams);
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
        throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
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
