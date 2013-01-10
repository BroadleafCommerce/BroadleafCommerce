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
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.service.OfferService;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.FulfillmentGroupService;
import org.broadleafcommerce.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.order.web.model.AddToCartItem;
import org.broadleafcommerce.order.web.model.CartOrderItem;
import org.broadleafcommerce.order.web.model.CartSummary;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.web.CustomerState;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller("blCartController")
@SessionAttributes("cartSummary")
@RequestMapping("/basket")
public class CartController {

    private static final Log LOG = LogFactory.getLog(CartController.class);

    @Resource(name="blCartService")
    protected final CartService cartService;
    @Resource(name="blCustomerState")
    protected final CustomerState customerState;
    @Resource(name="blCatalogService")
    protected final CatalogService catalogService;
    @Resource(name="blFulfillmentGroupService")
    protected final FulfillmentGroupService fulfillmentGroupService;
    @Resource(name="blOfferService")
    protected OfferService offerService;
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
        this.fulfillmentGroupService = null;
    }

    @ModelAttribute("fulfillmentGroups")
    public List<FulfillmentGroup> initFulfillmentGroups () {
        List<FulfillmentGroup> fulfillmentGroups = new ArrayList<FulfillmentGroup>();
        FulfillmentGroup standardGroup = new FulfillmentGroupImpl();
        FulfillmentGroup expeditedGroup = new FulfillmentGroupImpl();
        standardGroup.setMethod("standard");
        expeditedGroup.setMethod("expedited");
        fulfillmentGroups.add(standardGroup);
        fulfillmentGroups.add(expeditedGroup);
        return fulfillmentGroups;
    }

    @RequestMapping(value = "/viewCart.htm", method = RequestMethod.GET)
    public String viewCart(ModelMap model, HttpServletRequest request) throws PricingException {
        Order cart = retrieveCartOrder(request, model);
        CartSummary cartSummary = new CartSummary();

        if (cart.getOrderItems() != null ) {
            for (OrderItem orderItem : cart.getOrderItems()) {
                if (orderItem instanceof DiscreteOrderItem) {
                    Sku sku = catalogService.findSkuById(((DiscreteOrderItem) orderItem).getSku().getId());
                    if (!(sku.getSalePrice().equals(((DiscreteOrderItem) orderItem).getSalePrice()))) {
                        orderItem.setSalePrice(sku.getSalePrice());
                    }
                    if (!(sku.getRetailPrice().equals(((DiscreteOrderItem) orderItem).getRetailPrice()))) {
                        orderItem.setRetailPrice(sku.getRetailPrice());
                    }
    
                    if (orderItem.getSalePrice() != orderItem.getRetailPrice()) {
                        orderItem.setPrice(orderItem.getSalePrice());
                    }
                    else {
                        orderItem.setPrice(orderItem.getRetailPrice());
                    }
    
                    orderItem.getPrice();
                }
            }
        }

        if (cart.getOrderItems() != null ) {
            for (OrderItem orderItem : cart.getOrderItems()) {
                CartOrderItem cartOrderItem = new CartOrderItem();
                cartOrderItem.setOrderItem(orderItem);
                cartOrderItem.setQuantity(orderItem.getQuantity());
                cartSummary.getRows().add(cartOrderItem);
            }
        }

        if ((cart.getFulfillmentGroups() != null) && (cart.getFulfillmentGroups().isEmpty() == false)) {
            String cartShippingMethod = cart.getFulfillmentGroups().get(0).getMethod();
            String cartShippingService = cart.getFulfillmentGroups().get(0).getService();

            if (cartShippingMethod != null) {
                if (cartShippingMethod.equals("standard")) {
                    cartSummary = createFulfillmentGroup(cartSummary, "standard", cartShippingService, cart);
                }
                else if (cartShippingMethod.equals("expedited")) {
                    cartSummary = createFulfillmentGroup(cartSummary, "expedited", cartShippingService, cart);
                }
            }
        }

        updateFulfillmentGroups(cartSummary, cart);
        cartSummary.setOrderDiscounts(cart.getTotalAdjustmentsValue().getAmount());
        model.addAttribute("cartSummary", cartSummary);
        return cartViewRedirect ? "redirect:" + cartView : cartView;
    }

    /*
     * The addItem method adds a product items with one or more quantity to the cart by adding thes
     * item to a list and calling the addItems method.
     */
    @RequestMapping(value = "/addItem.htm", method = {RequestMethod.GET, RequestMethod.POST})
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
                OrderItem orderItem;
                if (addToCartItem.getOrderId() != null) {
                    orderItem = cartService.addSkuToOrder(addToCartItem.getOrderId(), addToCartItem.getSkuId(), addToCartItem.getProductId(), addToCartItem.getCategoryId(), addToCartItem.getQuantity());
                }
                else {
                    orderItem = cartService.addSkuToOrder(currentCartOrder.getId(), addToCartItem.getSkuId(), addToCartItem.getProductId(), addToCartItem.getCategoryId(), addToCartItem.getQuantity());
                }
                orderItemsAdded.add(orderItem);
            } catch (PricingException e) {
                LOG.error("Unable to price the order: ("+currentCartOrder.getId()+")", e);
            }
        }

        model.addAttribute("orderItemsAdded", orderItemsAdded);

        if(!ajax) {
            return addItemViewRedirect ? "redirect:" + addItemView : addItemView;
        } else {
            return "catalog/fragments/addToCartModal";
        }
    }

    @RequestMapping(value = "/viewCart.htm", params="removeItemFromCart", method = {RequestMethod.GET, RequestMethod.POST})
    public String removeItem(@RequestParam long orderItemId, @ModelAttribute CartSummary cartSummary, ModelMap model, HttpServletRequest request) {
        Order currentCartOrder = retrieveCartOrder(request, model);
        try {
            currentCartOrder = cartService.removeItemFromOrder(currentCartOrder.getId(), orderItemId);
        } catch (PricingException e) {
            model.addAttribute("error", "remove");
            LOG.error("An error occurred while removing an item from the cart: ("+orderItemId+")", e);
        }
        cartSummary.setOrderDiscounts(currentCartOrder.getTotalAdjustmentsValue().getAmount());

        return removeItemViewRedirect ? "redirect:" + removeItemView : removeItemView;
    }

    @RequestMapping(value = "/beginCheckout.htm", method = RequestMethod.GET)
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

    @RequestMapping(value = "/viewCart.htm", params="updateItemQuantity", method = RequestMethod.POST)
    public String updateItemQuantity(@ModelAttribute(value="cartSummary") CartSummary cartSummary, Errors errors, ModelMap model, HttpServletRequest request) throws PricingException {
        if (errors.hasErrors()) {
            model.addAttribute("cartSummary", cartSummary);
            return cartView;
        }
        Order currentCartOrder = retrieveCartOrder(request, model);
        List<OrderItem> orderItems = currentCartOrder.getOrderItems();
        List<CartOrderItem> items = new ArrayList<CartOrderItem>(cartSummary.getRows());
        for (CartOrderItem cartOrderItem : items) {
            OrderItem orderItem = (OrderItem)CollectionUtils.find(orderItems,
                    new BeanPropertyValueEqualsPredicate("id", cartOrderItem.getOrderItem().getId()));
            //in case the item was removed from the cart from another browser tab
            if (orderItem != null) {
                if (cartOrderItem.getQuantity() > 0) {
                    orderItem.setQuantity(cartOrderItem.getQuantity());
                    try {
                        cartService.updateItemQuantity(currentCartOrder, orderItem);
                    } catch (ItemNotFoundException e) {
                        LOG.error("Item not found in order: ("+orderItem.getId()+")", e);
                    } catch (PricingException e) {
                        LOG.error("Unable to price the order: ("+currentCartOrder.getId()+")", e);
                    }
                } else {
                    try {
                        cartService.removeItemFromOrder(currentCartOrder, orderItem);
                        cartSummary.getRows().remove(cartOrderItem);
                    } catch (Exception e) {
                        // TODO: handle exception gracefully
                        LOG.error("Unable to remove item from the order: ("+currentCartOrder.getId()+")");
                    }
                }
            }
        }
        cartSummary.setOrderDiscounts(currentCartOrder.getTotalAdjustmentsValue().getAmount());
        return cartView;
    }

    @RequestMapping(params="checkout", method = RequestMethod.POST)
    public String checkout(@ModelAttribute(value="cartSummary") CartSummary cartSummary, Errors errors, ModelMap model, HttpServletRequest request) throws PricingException {
        Order currentCartOrder = retrieveCartOrder(request, model);
        updateFulfillmentGroups(cartSummary, currentCartOrder);
        return "redirect:/checkout/checkout.htm";
    }

    @RequestMapping(params="updateShipping", method = RequestMethod.POST)
    public String updateShipping (@ModelAttribute(value="cartSummary") CartSummary cartSummary, ModelMap model, HttpServletRequest request) throws PricingException {
        Order currentCartOrder = retrieveCartOrder(request, model);
        cartSummary = createFulfillmentGroup(cartSummary, cartSummary.getFulfillmentGroup().getMethod(), cartSummary.getFulfillmentGroup().getService(), currentCartOrder);
        model.addAttribute("currentCartOrder", updateFulfillmentGroups(cartSummary, currentCartOrder));
        model.addAttribute("cartSummary", cartSummary);
        return cartView;
    }

    @RequestMapping(value = "/viewCart.htm", params="updatePromo", method = RequestMethod.POST)
    public String updatePromoCode (@ModelAttribute(value="cartSummary") CartSummary cartSummary, ModelMap model, HttpServletRequest request) throws PricingException {
        Order currentCartOrder = retrieveCartOrder(request, model);

        if (cartSummary.getPromoCode() != null) {
            OfferCode code = offerService.lookupOfferCodeByCode(cartSummary.getPromoCode());

            if (code != null ) {
                currentCartOrder.addAddedOfferCode(code);
                List<Offer> offers = offerService.buildOfferListForOrder(currentCartOrder);
                offerService.applyOffersToOrder(offers, currentCartOrder);
                currentCartOrder = updateFulfillmentGroups(cartSummary, currentCartOrder);
                cartSummary.setOrderDiscounts(currentCartOrder.getTotalAdjustmentsValue().getAmount());
            }
            else {
                model.addAttribute("promoError", "Invalid promo code entered.");

            }
        }

        cartSummary.setPromoCode(null);
        model.addAttribute("currentCartOrder", currentCartOrder );
        model.addAttribute("cartSummary", cartSummary);
        return cartView;
    }
    
    protected Order updateFulfillmentGroups (CartSummary cartSummary, Order currentCartOrder) throws PricingException {
        FulfillmentGroup fg = cartSummary.getFulfillmentGroup();
        if (fg.getId() == null) {
            cartService.removeAllFulfillmentGroupsFromOrder(currentCartOrder, false);
            for(CartOrderItem item : cartSummary.getRows()) {
                item.getOrderItem().setOrder(currentCartOrder);
                fg = cartService.addItemToFulfillmentGroup(item.getOrderItem(), fg, item.getQuantity(), false);
            }
            cartSummary.setFulfillmentGroup(fg);
        }
        return cartService.save(currentCartOrder, true);
    }

    protected CartSummary createFulfillmentGroup (CartSummary cartSummary, String shippingMethod, String service, Order cart) {
        FulfillmentGroup fulfillmentGroup = new FulfillmentGroupImpl();
        fulfillmentGroup.setMethod(shippingMethod);
        fulfillmentGroup.setService(service);
        fulfillmentGroup.setOrder(cart);
        cartSummary.setFulfillmentGroup(fulfillmentGroup);
        return cartSummary;
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
}