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

import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.api.wrapper.OrderWrapper;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
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

    @Resource(name="blOrderService")
    protected OrderService orderService;

    @Resource(name="blOfferService")
    protected OfferService offerService;

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

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
    public OrderWrapper findCartForCustomer(@Context HttpServletRequest request) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer != null) {
            Order cart = orderService.findCartForCustomer(customer);

            if (cart != null) {
                OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                wrapper.wrap(cart, request);

                return wrapper;
            }

            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

   /**
     * Create a new {@code Order} for {@code Customer}
     *
     * @return the cart for the customer
     */
    @POST
    public OrderWrapper createNewCartForCustomer(@Context HttpServletRequest request) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer == null) {
            customer = customerService.createCustomerFromId(null);
        }

        Order cart = orderService.createNewCartForCustomer(customer);

        OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
        wrapper.wrap(cart, request);

        return wrapper;
    }

    @POST
    @Path("{categoryId}/{productId}/{skuId}")
    public OrderWrapper addSkuToOrder(@Context HttpServletRequest request,
                                      @PathParam("categoryId") Long categoryId,
                                      @PathParam("productId") Long productId,
                                      @PathParam("skuId") Long skuId,
                                      @QueryParam("quantity") @DefaultValue("1") int quantity,
                                      @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer != null && skuId != null) {
            Order cart = orderService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                	OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
                	orderItemRequestDTO.setCategoryId(categoryId);
                	orderItemRequestDTO.setProductId(productId);
                	orderItemRequestDTO.setSkuId(skuId);
                	orderItemRequestDTO.setCategoryId(categoryId);
                	orderItemRequestDTO.setQuantity(quantity);
                    Order order = orderService.addItem(cart.getId(), orderItemRequestDTO, priceOrder);
                    order = orderService.save(order, priceOrder);
                    
                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrap(order, request);

                    return wrapper;
                } catch (PricingException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                } catch (WorkflowException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
				}
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @DELETE
    @Path("items/{itemId}")
    public OrderWrapper removeItemFromOrder(@Context HttpServletRequest request,
                                            @PathParam("itemId") Long itemId,
                                            @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer != null) {
            Order cart = orderService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                    Order order = orderService.removeItem(cart.getId(), itemId, priceOrder);
                    order = orderService.save(order, priceOrder);
                    
                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrap(order, request);

                    return wrapper;
                } catch (PricingException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                } catch (ItemNotFoundException e) {
                	throw new WebApplicationException(Response.Status.NOT_FOUND);
				}
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @PUT
    @Path("items/{itemId}")
    public OrderWrapper updateItemQuantity(@Context HttpServletRequest request,
                                               @PathParam("itemId") Long itemId,
                                               @QueryParam("quantity") Integer quantity,
                                               @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer != null) {
            Order cart = orderService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                	OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
                	orderItemRequestDTO.setOrderItemId(itemId);
                	orderItemRequestDTO.setQuantity(quantity);
                    Order order = orderService.updateItemQuantity(cart.getId(), orderItemRequestDTO, priceOrder);
                    order = orderService.save(order, priceOrder);

                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrap(order, request);

                    return wrapper;
                } catch (ItemNotFoundException e) {
                    throw new WebApplicationException(Response.Status.NOT_FOUND);
                } catch (PricingException pe) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }


    @POST
    @Path("offer")
    public OrderWrapper addOfferCode(@Context HttpServletRequest request,
                                     @QueryParam("promoCode") String promoCode,
                                     @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer != null) {
            Order cart = orderService.findCartForCustomer(customer);
            OfferCode offerCode = offerService.lookupOfferCodeByCode(promoCode);
            if (cart != null && offerCode != null) {
                try {
                    cart = orderService.addOfferCode(cart, offerCode, priceOrder);
                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrap(cart, request);

                    return wrapper;
                } catch (PricingException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                } catch (OfferMaxUseExceededException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @DELETE
    @Path("offer")
    public OrderWrapper removeOfferCode(@Context HttpServletRequest request,
                                        @QueryParam("promoCode") String promoCode,
                                        @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer != null) {
            Order cart = orderService.findCartForCustomer(customer);
            OfferCode offerCode = offerService.lookupOfferCodeByCode(promoCode);
            if (cart != null && offerCode != null) {
                try {
                    cart = orderService.removeOfferCode(cart, offerCode, priceOrder);
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

    @DELETE
    @Path("offers")
    public OrderWrapper removeAllOfferCodes(@Context HttpServletRequest request,
                                        @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer != null) {
            Order cart = orderService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                    cart = orderService.removeAllOfferCodes(cart, priceOrder);
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

}
