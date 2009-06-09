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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.order.web.model.AddToCartItem;
import org.broadleafcommerce.order.web.model.AddToCartItems;
import org.broadleafcommerce.order.web.model.CartOrderItem;
import org.broadleafcommerce.order.web.model.CartSummary;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.web.CustomerState;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller("blCartController")
public class CartController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    protected final CartService cartService;
    @Resource
    protected final CustomerState customerState;
    @Resource
    protected final CatalogService catalogService;
    protected String cartView;
    protected boolean cartViewRedirect;
    protected String addItemView;
    protected boolean addItemViewRedirect;
    protected String removeItemView;
    protected boolean removeItemViewRedirect;

    public CartController() {
        this.cartService = null;
        this.customerState = null;
        this.catalogService = null;
    }

    public void setCartView(String cartView) {
        this.cartView = cartView;
    }

    public void setAddItemView(String addItemView) {
        this.addItemView = addItemView;
    }

    public void setCartViewRedirect(boolean cartViewRedirect) {
        this.cartViewRedirect = cartViewRedirect;
    }

    public void setAddItemViewRedirect(boolean addItemViewRedirect) {
        this.addItemViewRedirect = addItemViewRedirect;
    }

    public void setRemoveItemView(String removeItemView) {
        this.removeItemView = removeItemView;
    }

    public void setRemoveItemViewRedirect(boolean removeItemViewRedirect) {
        this.removeItemViewRedirect = removeItemViewRedirect;
    }

    /*
     * The addItem method adds a product items with one or more quantity to the cart by adding thes
     * item to a list and calling the addItems method.
     */
    @RequestMapping(value = "addItem.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String addItem(@RequestParam(required=false) Boolean ajax,
            @ModelAttribute AddToCartItem addToCartItem,
            BindingResult errors,
            ModelMap model,
            HttpServletRequest request) {
        if (ajax == null) {
            ajax = false;
        }

        /*
        new AddToCartItemsValidator().validate(addToCartItems, errors);
        if (errors.hasErrors()) {
            return refererRedirect(model, errors, request, ajax);
        }
         */

        Order currentCartOrder = retrieveCartOrder(request, model);
        List<OrderItem> orderItemsAdded = new ArrayList<OrderItem>();

        if (addToCartItem.getQuantity() > 0) {
            try {
                OrderItem orderItem = cartService.addSkuToOrder(currentCartOrder.getId(), addToCartItem.getSkuId(), addToCartItem.getProductId(), addToCartItem.getCategoryId(), addToCartItem.getQuantity());
                orderItemsAdded.add(orderItem);
            } catch (PricingException e) {
                e.printStackTrace();
            }
        }

        model.addAttribute("orderItemsAdded", orderItemsAdded);

        if(!ajax) {
            return addItemViewRedirect ? "redirect:" + addItemView : addItemView;
        } else {
            return "catalog/fragments/addToCartModal";
        }
    }

    @RequestMapping(value = "addToWishList.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String addToWishlist(@ModelAttribute AddToCartItems addToCartItems, BindingResult errors, ModelMap model,
            HttpServletRequest request) {
        return "redirect:success";  // need to be redirected to the viewWishlists method of the WishlistController
    }

    @RequestMapping(value = "viewCart.htm", params="removeItemFromCart", method = {RequestMethod.GET, RequestMethod.POST})
    public String removeItem(@RequestParam long orderItemId, ModelMap model, HttpServletRequest request) {
        Order currentCartOrder = retrieveCartOrder(request, model);
        try {
            currentCartOrder = cartService.removeItemFromOrder(currentCartOrder.getId(), orderItemId);
        } catch (PricingException e) {
            model.addAttribute("error", "remove");
            logger.error("An error occurred while removing an item from the cart.", e);
        }

        return removeItemViewRedirect ? "redirect:" + removeItemView : removeItemView;
    }

    @RequestMapping(value = "beginCheckout.htm", method = RequestMethod.GET)
    public String beginCheckout(@ModelAttribute CartSummary cartSummary, BindingResult errors, @RequestParam (required = false) Boolean isStorePickup, ModelMap model, HttpServletRequest request) {
        String view = "error"; //updateItemQuantity(orderItemList, errors, model, request);
        if (!view.equals("error")) {
            model.addAttribute("isStorePickup", isStorePickup);
            if (SecurityContextHolder.getContext().getAuthentication() == null || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
                model.addAttribute("nextStep", "checkout");
                return "login";
            }
        }
        return view;
    }

    @RequestMapping(value = "viewCart.htm", params="updateItemQuantity", method = RequestMethod.POST)
    public String updateItemQuantity(@ModelAttribute(value="cartSummary") CartSummary cartSummary, Errors errors, ModelMap model, HttpServletRequest request) {
        if (errors.hasErrors()) {
            model.addAttribute("cartSummary", cartSummary);
            return cartView;
        }
        Order currentCartOrder = retrieveCartOrder(request, model);
        List<OrderItem> orderItems = currentCartOrder.getOrderItems();
        for (CartOrderItem cartOrderItem : cartSummary.getRows()) {
            OrderItem orderItem = (OrderItem)CollectionUtils.find(orderItems,
                    new BeanPropertyValueEqualsPredicate("id", cartOrderItem.getOrderItemId()));
            //in case the item was removed from the cart from another browser tab
            if (orderItem != null) {
                if (cartOrderItem.getQuantity() > 0) {
                    orderItem.setQuantity(cartOrderItem.getQuantity());
                    try {
                        cartService.updateItemInOrder(currentCartOrder, orderItem);
                    } catch (ItemNotFoundException e) {
                        e.printStackTrace();
                    } catch (PricingException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        cartService.removeItemFromOrder(currentCartOrder, orderItem);
                    } catch (Exception e) {
                        // TODO: handle exception gracefully
                        e.printStackTrace();
                    }
                }
            }
        }
        return cartViewRedirect ? "redirect:" + removeItemView : removeItemView;
    }

    @RequestMapping(value = "viewCart.htm", method = RequestMethod.GET)
    public String viewCart(ModelMap model, HttpServletRequest request) {
        logger.debug("Processing View Cart!");
        Order cart = retrieveCartOrder(request, model);
        CartSummary cartSummary = new CartSummary();

        for (OrderItem orderItem : cart.getOrderItems()) {
            CartOrderItem cartOrderItem = new CartOrderItem();
            cartOrderItem.setOrderItemId(orderItem.getId());
            cartOrderItem.setQuantity(orderItem.getQuantity());
            cartSummary.getRows().add(cartOrderItem);
        }

        model.addAttribute("cartSummary", cartSummary);

        return cartViewRedirect ? "redirect:" + cartView : cartView;
    }

    protected Order retrieveCartOrder(HttpServletRequest request, ModelMap model) {
        Customer currentCustomer = customerState.getCustomer(request);
        Order currentCartOrder = null;
        if (currentCustomer != null) {
            currentCartOrder = cartService.findCartForCustomer(currentCustomer);
            if (currentCartOrder == null) {
                currentCartOrder = cartService.createNewCartForCustomer(currentCustomer);
            }
        }

        model.addAttribute("currentCartOrder", currentCartOrder);
        return currentCartOrder;
    }

}
