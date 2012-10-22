/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.web.api.endpoint.order;

import java.util.HashMap;
import java.util.Set;

import org.broadleafcommerce.core.inventory.exception.InventoryUnavailableException;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.api.wrapper.OrderWrapper;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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

    /**
     * This method takes in a categoryId and productId as path parameters.  In addition, query parameters can be supplied including:
     * 
     * <li>skuId</li>
     * <li>quantity</li>
     * <li>priceOrder</li>
     * 
     * You must provide a skuId OR product options. Product options can be posted as form or querystring parameters. 
     * You must pass in the ProductOption attributeName as the key and the 
     * ProductOptionValue attributeValue as the value.  See {@link CatalogEndpoint}.
     * 
     * @param request
     * @param uriInfo
     * @param categoryId
     * @param productId
     * @param skuId
     * @param quantity
     * @param priceOrder
     * @return OrderWrapper
     */
    @POST
    @Path("{categoryId}/{productId}")
    public OrderWrapper addSkuToOrder(@Context HttpServletRequest request,
    								  @Context UriInfo uriInfo,
                                      @PathParam("categoryId") Long categoryId,
                                      @PathParam("productId") Long productId,
                                      @QueryParam("skuId") Long skuId,
                                      @QueryParam("quantity") @DefaultValue("1") int quantity,
                                      @QueryParam("priceOrder") @DefaultValue("true") boolean priceOrder) {
        Customer customer = CustomerState.getCustomer(request);
        
        if (customer != null) {
            Order cart = orderService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                	//We allow product options to be submitted via form post or via query params.  We need to take 
                	//the product options and build a map with them...
                	MultivaluedMap<String, String> multiValuedMap = uriInfo.getQueryParameters();
                	HashMap<String, String> productOptions = new HashMap<String, String>();
                	
                	//Fill up a map of key values that will represent product options
                	Set<String> keySet = multiValuedMap.keySet();
                	for (String key : keySet) {
                		if (multiValuedMap.getFirst(key) != null) {
                			productOptions.put(key, multiValuedMap.getFirst(key));
                		}
                	}
                	
                	//Remove the items from the map that represent the other query params of the request
                	//Essentially these can't be used as product option names...
                	productOptions.remove("categoryId");
                	productOptions.remove("productId");
                	productOptions.remove("skuId");
                	productOptions.remove("quantity");
                	productOptions.remove("priceOrder");
                	
                	//If a Sku ID was not supplied and no product options were supplied, then we can't process this request
                	if (skuId == null && productOptions.size() == 0) {
                		throw new WebApplicationException(Response.Status.BAD_REQUEST);
                	}
                	
                	OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
                	orderItemRequestDTO.setCategoryId(categoryId);
                	orderItemRequestDTO.setProductId(productId);
                	orderItemRequestDTO.setSkuId(skuId);
                	orderItemRequestDTO.setCategoryId(categoryId);
                	orderItemRequestDTO.setQuantity(quantity);
                	
                	//If we have product options set them on the DTO
                	if (productOptions.size() > 0) {
                		orderItemRequestDTO.setItemAttributes(productOptions);
                	}
                	
                    Order order = orderService.addItem(cart.getId(), orderItemRequestDTO, priceOrder);
                    order = orderService.save(order, priceOrder);
                    
                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrap(order, request);

                    return wrapper;
                } catch (PricingException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                } catch (AddToCartException e) {
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
                } catch (RemoveFromCartException e) {
                	if (e.getCause() instanceof ItemNotFoundException) {
                		throw new WebApplicationException(Response.Status.NOT_FOUND);
                	} else { 
                		throw new WebApplicationException(Response.Status.NOT_FOUND);
                	}
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
                } catch (InventoryUnavailableException e) {
                    throw new WebApplicationException((Response.Status.INTERNAL_SERVER_ERROR));
                } catch (UpdateCartException e) {
                	if (e.getCause() instanceof ItemNotFoundException) {
                		throw new WebApplicationException(Response.Status.NOT_FOUND);
                	} else { 
                		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                	}
                } catch (RemoveFromCartException e) {
                	if (e.getCause() instanceof ItemNotFoundException) {
                		throw new WebApplicationException(Response.Status.NOT_FOUND);
                	} else { 
                		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                	}
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
