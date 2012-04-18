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

package org.broadleafcommerce.core.web.api.endpoint.order;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.api.wrapper.OrderItemWrapper;
import org.broadleafcommerce.core.web.api.wrapper.OrderWrapper;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * JAXRS endpoint for providing RESTful services related to the shopping cart.
 *
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@Component("blRestCartEndpoint")
@Scope("singleton")
@Path("/cart/")
@Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CartEndpoint implements ApplicationContextAware {

    @Resource(name="blCartService")
    protected CartService cartService;

    @Resource(name="blCustomerState")
    protected CustomerState customerState;

    protected ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

   /**
     * Search for {@code Order} by {@code Customer}
     *
     * @return the cart for the customer
     */
    @GET
    @Path("find")
    public OrderWrapper findCartForCustomer(@Context HttpServletRequest request) {
        Customer customer = customerState.getCustomer(request);

        if (customer != null) {
            Order cart = cartService.findCartForCustomer(customer);

            if (cart != null) {
                OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                wrapper.wrap(cart, request);

                return wrapper;
            }
        }

        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

   /**
     * Create a new {@code Order} for {@code Customer}
     *
     * @return the cart for the customer
     */
    @GET
    @Path("create")
    public OrderWrapper createNewCartForCustomer(@Context HttpServletRequest request) {
        Customer customer = customerState.getCustomer(request);

        if (customer != null) {
            Order cart = cartService.createNewCartForCustomer(customer);

            OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
            wrapper.wrap(cart, request);

            return wrapper;
        }

        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @POST
    @Path("add/{categoryId}/{productId}/{skuId}")
    public OrderItemWrapper addSkuToOrder(@Context HttpServletRequest request,
                                      @PathParam("categoryId") Long categoryId,
                                      @PathParam("productId") Long productId,
                                      @PathParam("skuId") Long skuId,
                                      @QueryParam("quantity") @DefaultValue("1") int quantity,
                                      @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = customerState.getCustomer(request);

        if (customer != null && skuId != null) {
            Order cart = cartService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                    OrderItem orderItem = cartService.addSkuToOrder(cart.getId(), skuId, productId, categoryId, quantity, priceOrder);
                    OrderItemWrapper wrapper = (OrderItemWrapper) context.getBean(OrderItemWrapper.class.getName());
                    wrapper.wrap(orderItem, request);

                    return wrapper;
                } catch (PricingException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                }

            }
        }

        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @POST
    @Path("remove/{itemId}")
    public OrderWrapper removeItemFromOrder(@Context HttpServletRequest request,
                                            @PathParam("itemId") Long itemId,
                                            @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = customerState.getCustomer(request);

        if (customer != null) {
            Order cart = cartService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                    Order order = cartService.removeItemFromOrder(cart.getId(), itemId, priceOrder);
                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrap(order, request);

                    return wrapper;
                } catch (PricingException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                }

            }
        }

        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @POST
    @Path("update/{itemId}/{quantity}")
    public OrderWrapper updateItemQuantity(@Context HttpServletRequest request,
                                               @PathParam("itemId") Long itemId,
                                               @PathParam("quantity") Integer quantity,
                                               @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = customerState.getCustomer(request);

        if (customer != null) {
            Order cart = cartService.findCartForCustomer(customer);
            if (cart != null) {
                OrderItem item = (OrderItem) CollectionUtils.find(cart.getOrderItems(),
                        new BeanPropertyValueEqualsPredicate("id", itemId));
                item.setQuantity(quantity);
                try {
                    cartService.updateItemQuantity(cart, item, priceOrder);

                    Order order = cartService.save(cart, priceOrder);
                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrap(order, request);

                    return wrapper;
                } catch (ItemNotFoundException e) {
                    throw new WebApplicationException(Response.Status.NOT_FOUND);
                } catch (PricingException pe) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
        }

        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }



}
