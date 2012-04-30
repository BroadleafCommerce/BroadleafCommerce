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
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupRequest;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.api.wrapper.FulfillmentGroupItemWrapper;
import org.broadleafcommerce.core.web.api.wrapper.FulfillmentGroupWrapper;
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
import java.util.ArrayList;
import java.util.List;

/**
 * JAXRS endpoint for exposing the fulfillment group process as a set of RESTful services.
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@Component("blRestFulfillmentEndpoint")
@Scope("singleton")
@Path("/cart/fulfillment/")
@Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FulfillmentEndpoint implements ApplicationContextAware {

    @Resource(name="blCheckoutService")
    protected CheckoutService checkoutService;

    @Resource(name="blCartService")
    protected CartService cartService;

    @Resource(name="blCustomerState")
    protected CustomerState customerState;

    protected ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @GET
    @Path("groups")
    public List<FulfillmentGroupWrapper> findFulfillmentGroupsForOrder(@Context HttpServletRequest request) {
        Customer customer = customerState.getCustomer(request);

        if (customer != null) {
            Order cart = cartService.findCartForCustomer(customer);
            if (cart != null && cart.getFulfillmentGroups() != null && !cart.getFulfillmentGroups().isEmpty()) {
                List<FulfillmentGroup> fulfillmentGroups = cart.getFulfillmentGroups();
                List<FulfillmentGroupWrapper> fulfillmentGroupWrappers = new ArrayList<FulfillmentGroupWrapper>();
                for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
                    FulfillmentGroupWrapper fulfillmentGroupWrapper = (FulfillmentGroupWrapper) context.getBean(FulfillmentGroupWrapper.class.getName());
                    fulfillmentGroupWrapper.wrap(fulfillmentGroup, request);
                    fulfillmentGroupWrappers.add(fulfillmentGroupWrapper);
                }
                return fulfillmentGroupWrappers;
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @DELETE
    @Path("groups")
    public OrderWrapper removeAllFulfillmentGroupsFromOrder(@Context HttpServletRequest request,
                                                            @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = customerState.getCustomer(request);

        if (customer != null) {
            Order cart = cartService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                    cartService.removeAllFulfillmentGroupsFromOrder(cart, priceOrder);
                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrap(cart, request);
                    return wrapper;
                } catch (PricingException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @POST
    @Path("group")
    public FulfillmentGroupWrapper addFulfillmentGroupToOrder(@Context HttpServletRequest request,
                                                              FulfillmentGroupWrapper wrapper,
                                                              @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = customerState.getCustomer(request);

        if (customer != null) {
            Order cart = cartService.findCartForCustomer(customer);
            if (cart != null) {
                FulfillmentGroupRequest fulfillmentGroupRequest = wrapper.unwrap(request, context);

                if (fulfillmentGroupRequest.getOrder() != null && fulfillmentGroupRequest.getOrder().getId().equals(cart.getId())){
                    try {
                        fulfillmentGroupRequest.setOrder(cart);
                        FulfillmentGroup fulfillmentGroup = cartService.addFulfillmentGroupToOrder(fulfillmentGroupRequest, priceOrder);
                        FulfillmentGroupWrapper fulfillmentGroupWrapper = (FulfillmentGroupWrapper) context.getBean(FulfillmentGroupWrapper.class.getName());
                        fulfillmentGroupWrapper.wrap(fulfillmentGroup, request);
                        return fulfillmentGroupWrapper;
                    } catch (PricingException e) {
                        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                    }
                }
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @PUT
    @Path("group/{fulfillmentGroupId}")
    public FulfillmentGroupWrapper addItemToFulfillmentGroup(@Context HttpServletRequest request,
                                                                 @PathParam("fulfillmentGroupId") Long fulfillmentGroupId,
                                                                 FulfillmentGroupItemWrapper wrapper,
                                                                 @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = customerState.getCustomer(request);

        if (customer != null) {
            Order cart = cartService.findCartForCustomer(customer);
            if (cart != null) {
                FulfillmentGroupItemRequest fulfillmentGroupItemRequest = wrapper.unwrap(request, context);
                if (fulfillmentGroupItemRequest.getOrderItem() != null) {
                    FulfillmentGroup fulfillmentGroup = null;
                    OrderItem orderItem = null;

                    for (FulfillmentGroup fg : cart.getFulfillmentGroups()) {
                        if (fg.getId().equals(fulfillmentGroupId)){
                            fulfillmentGroup = fg;
                        }
                    }

                    for (OrderItem oi : cart.getOrderItems()) {
                        if (oi.getId().equals(fulfillmentGroupItemRequest.getOrderItem().getId())){
                            orderItem = oi;
                        }
                    }

                    if (fulfillmentGroup != null && orderItem != null) {
                        try {
                            FulfillmentGroup fg = cartService.addItemToFulfillmentGroup(orderItem, fulfillmentGroup, fulfillmentGroupItemRequest.getQuantity(), priceOrder);
                            FulfillmentGroupWrapper fulfillmentGroupWrapper = (FulfillmentGroupWrapper) context.getBean(FulfillmentGroupWrapper.class.getName());
                            fulfillmentGroupWrapper.wrap(fg, request);
                            return fulfillmentGroupWrapper;

                        } catch (PricingException e) {
                            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                        }
                    }
                }
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }
}
