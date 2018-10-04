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
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.order.web.model.ProductItem;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
public class WishlistController extends CartController {

    private static final Log LOG = LogFactory.getLog(WishlistController.class);

    public WishlistController() {
        super();
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String viewWishlists(ModelMap model, HttpServletRequest request) {
        List<Order> wishlistOrders = cartService.findOrdersForCustomer(customerState.getCustomer(request), OrderStatus.NAMED);
        model.addAttribute("wishlists", wishlistOrders);
        return "success";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String createWishlist(ModelMap model, HttpServletRequest request) {
        Order wishlistOrder = retrieveCartOrder(request);
        if (model.containsAttribute("wishlistItems")) {
            List<ProductItem> productItemList = (List<ProductItem>)model.get("productItemList");
            if (productItemList != null) {
                for (ProductItem productItem : productItemList) {
                    try {
                        cartService.addSkuToOrder(wishlistOrder.getId(), productItem.getSkuId(), productItem.getProductId(),productItem.getCategoryId(), productItem.getQuantity(), true);
                    } catch (PricingException e) {
                        LOG.error("An exception occured while pricing the order: ("+wishlistOrder.getId()+")", e);
                        //TODO How to handle from the UI perspective???
                    }
                }
            }
        }
        model.remove("wishlistItems");
        model.addAttribute("wishList", wishlistOrder);
        return "success";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String removeWishlist(@RequestParam String wishlistName, ModelMap model, HttpServletRequest request) {
        cartService.removeNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        return "success";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String moveItemToCart(@ModelAttribute OrderItem orderItem, @RequestParam String wishlistName, ModelMap model, HttpServletRequest request) {
        Order wishlistOrder = cartService.findNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        try {
            cartService.moveItemToCartFromNamedOrder(wishlistOrder, orderItem, true);
        } catch (Exception e) {
            LOG.error("An exception occured while pricing the order: ("+wishlistOrder.getId()+")", e);
            //TODO: handle this properly from a UI perspective
        }
        return "redirect:success";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String moveAllItemsToCart(@RequestParam String wishlistName, ModelMap model, HttpServletRequest request) {
        Order wishlistOrder = cartService.findNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        try {
            cartService.moveAllItemsToCartFromNamedOrder(wishlistOrder, true);
        } catch (PricingException e) {
            LOG.error("An exception occured while pricing the order: ("+wishlistOrder.getId()+")", e);
            //TODO: handle this properly from a UI perspective
        }
        return "redirect:success";
    }

    // override the retreiveOrder method in CartController to return a name wishlist order
    protected Order retrieveCartOrder(HttpServletRequest request) {
        String wishlistName = request.getParameter("wishlistName");
        Order currentWishlistOrder = cartService.createNamedOrderForCustomer(wishlistName, customerState.getCustomer(request));
        return currentWishlistOrder;
    }

}
