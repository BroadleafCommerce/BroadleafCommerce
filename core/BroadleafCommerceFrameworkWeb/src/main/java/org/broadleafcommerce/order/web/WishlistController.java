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
package org.broadleafcommerce.order.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.order.web.model.AddToCartItem;
import org.broadleafcommerce.order.web.model.CartOrderItem;
import org.broadleafcommerce.order.web.model.CartSummary;
import org.broadleafcommerce.order.web.model.WishlistRequest;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 1) Created the WishlistController as an extension of the CartController
 * 2) Methods addItem, addItems, removeItem, and updateItemQuantity are defined in the CartController
 * 3) Renamed selectWishlist method to viewWishlists
 * 4) Renamed addWishlist method to createWishlist
 * 5) Modified the moveItemToCart method to take in a OrderItem object
 * 6) Removed the displayWishlist method because it is not needed
 * 7) Changed all the Error objects to BindingResult
 * 8) Override the retrieveOrder method to return a wishlist order
 */
@Controller("blWishlistController")
@RequestMapping("/wishlist")
public class WishlistController extends CartController {

    private static final Log LOG = LogFactory.getLog(WishlistController.class);

    public WishlistController() {
        super();
    }

    @RequestMapping(method =  {RequestMethod.POST}, params="addToWishlist")
    public String addToWishlist(ModelMap model, HttpServletRequest request, @ModelAttribute WishlistRequest wishlistRequest, BindingResult errors) {
        if (wishlistRequest.getWishlistName() != null && wishlistRequest.getWishlistName().length() > 0) {
            Order wishlist = cartService.findNamedOrderForCustomer(wishlistRequest.getWishlistName(), customerState.getCustomer(request));
            AddToCartItem addToCartItem = new AddToCartItem();
            addToCartItem.setCategoryId(wishlistRequest.getAddCategoryId());
            addToCartItem.setOrderId(wishlist.getId());
            addToCartItem.setProductId(wishlistRequest.getAddProductId());
            addToCartItem.setQuantity(1);
            addToCartItem.setSkuId(wishlistRequest.getAddSkuId());
            addItem(false, addToCartItem, errors, model, request);
            return viewWishlist(model, request, wishlist.getId());
        }
        else {
            model.addAttribute("wishlistRequest", wishlistRequest);
            return createWishlistName(model, request);
        }
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String showWishlists(ModelMap model, HttpServletRequest request) {
        List<Order> wishlistOrders = cartService.findOrdersForCustomer(customerState.getCustomer(request), OrderStatus.NAMED);
        model.addAttribute("wishlists", wishlistOrders);
        return "wishlist/showWishlists";
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String viewWishlist(ModelMap model, HttpServletRequest request, @RequestParam("id") Long id) {
        Order wishlist = cartService.findOrderById(id);
        CartSummary cartSummary = new CartSummary();
        
        if (wishlist == null) {
            return "redirect:/basket/currentCart.htm";
        }

        if (wishlist.getOrderItems() != null ) {
            for (OrderItem orderItem : wishlist.getOrderItems()) {
                CartOrderItem cartOrderItem = new CartOrderItem();
                cartOrderItem.setOrderItem(orderItem);
                cartOrderItem.setQuantity(orderItem.getQuantity());
                cartSummary.getRows().add(cartOrderItem);
            }
        }

        model.addAttribute("cartSummary", cartSummary);
        model.addAttribute("wishlist", wishlist);
        return "wishlist/viewWishlist";
    }
    
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String createWishlist(ModelMap model, HttpServletRequest request, @ModelAttribute WishlistRequest wishlistRequest) {
        boolean wishlistCreated = false;
        Order wishlistOrder = new OrderImpl();
        
        if (wishlistRequest.getWishlistName() != null && wishlistRequest.getWishlistName().length() > 0) {
            wishlistOrder = createWishlistCart(request, wishlistRequest.getWishlistName());
            wishlistCreated = true;
        }
        if (wishlistCreated == false) {
            model.addAttribute("wishlistError", "Please enter a valid wishlist name.");
            return "wishlist/createWishlistName";
        }
        
        if (wishlistRequest.getAddSkuId() != null && wishlistRequest.getAddCategoryId() != null && 
                wishlistRequest.getAddProductId() != null && wishlistRequest.getQuantity() != null && wishlistCreated == true ) {
            try {
                cartService.addSkuToOrder(wishlistOrder.getId(), wishlistRequest.getAddSkuId(), wishlistRequest.getAddProductId(),
                        wishlistRequest.getAddCategoryId(), wishlistRequest.getQuantity());
            } catch (PricingException e) {
                LOG.error("An exception occured while pricing the order: ("+wishlistOrder.getId()+")", e);
            }
        }
        
        return viewWishlist(model, request, wishlistOrder.getId());
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String createWishlistName(ModelMap model, HttpServletRequest request) {
        WishlistRequest wishlistRequest = (WishlistRequest) model.get("wishlistRequest");
        
        if (wishlistRequest != null) {
            model.addAttribute("wishlistRequest", model.get("wishlistRequest"));
        }
        else {
            model.addAttribute("wishlistRequest", new WishlistRequest());
        }
        
        return "wishlist/createWishlistName";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String removeWishlist(@RequestParam String wishlistName, ModelMap model, HttpServletRequest request) {
        cartService.removeNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        return showWishlists(model, request);
    }
    
    @RequestMapping(method = {RequestMethod.GET})
    public String removeWishlistItem(@RequestParam long orderItemId, @RequestParam long orderId, ModelMap model, HttpServletRequest request) {
        Order wishlist = cartService.findOrderById(orderId);
        try {
            wishlist = cartService.removeItemFromOrder(wishlist.getId(), orderItemId);
        } catch (PricingException e) {
            LOG.error("An error occurred while removing an item from the cart: ("+orderItemId+")", e);
        }
        
        return viewWishlist(model, request, wishlist.getId());
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String moveItemToCart(@RequestParam long orderItemId, @RequestParam String wishlistName, ModelMap model, HttpServletRequest request) {
        Order wishlistOrder = cartService.findNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));

        try {
            cartService.moveItemToCartFromNamedOrder(customerState.getCustomer(request).getId(), 
                    wishlistOrder.getName(), orderItemId, new Integer(1));
        } catch (Exception e) {
            LOG.error("An exception occured while pricing the order: ("+wishlistOrder.getId()+")", e);
            //TODO: handle this properly from a UI perspective
        }
        
        return viewWishlist(model, request, wishlistOrder.getId());
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String moveAllItemsToCart(@RequestParam String wishlistName, ModelMap model, HttpServletRequest request) {
        Order wishlistOrder = cartService.findNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        try {
            cartService.moveAllItemsToCartFromNamedOrder(wishlistOrder);
        } catch (PricingException e) {
            LOG.error("An exception occured while pricing the order: ("+wishlistOrder.getId()+")", e);
            //TODO: handle this properly from a UI perspective
        }
        return "redirect:/basket/currentCart.htm";
    }

    // override the retreiveOrder method in CartController to return a name wishlist order
    protected Order createWishlistCart(HttpServletRequest request, String wishlistName) {
        Order currentWishlistOrder = cartService.createNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        return currentWishlistOrder;
    }

}
