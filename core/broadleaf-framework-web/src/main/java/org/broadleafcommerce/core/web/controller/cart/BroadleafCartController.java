/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.controller.cart;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.exception.OfferAlreadyAddedException;
import org.broadleafcommerce.core.offer.service.exception.OfferException;
import org.broadleafcommerce.core.offer.service.exception.OfferExpiredException;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.AddToCartItem;
import org.broadleafcommerce.core.order.service.call.ConfigurableOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.IllegalCartOperationException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * In charge of performing the various modify cart operations
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class BroadleafCartController extends AbstractCartController {

    protected static String cartView = "cart/cart";
    protected static String checkoutView = "checkout/checkout";
    protected static String cartPageRedirect = "redirect:/cart";
    protected static String configureView = "configure/partials/configure";
    protected static String configurePageRedirect = "redirect:/cart/configure";

    protected static String ALL_PRODUCTS_ATTRIBUTE_NAME = "blcAllDisplayedProducts";

    @Value("${automatically.add.complete.items}")
    protected boolean automaticallyAddCompleteItems;

    /**
     * Renders the cart page.
     * 
     * If the method was invoked via an AJAX call, it will render the "ajax/cart" template.
     * Otherwise, it will render the "cart" template.
     *
     * Will reprice the order if the currency has been changed.
     * 
     * @param request
     * @param response
     * @param model
     * @throws PricingException
     */
    public String cart(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
        Order cart = CartState.getCart();
        if (cart != null && !(cart instanceof NullOrderImpl)) {
            model.addAttribute("paymentRequestDTO", dtoTranslationService.translateOrder(CartState.getCart()));
        }
        return getCartView();
    }
    
    /**
     * Takes in an item request, adds the item to the customer's current cart, and returns.
     * 
     * If the method was invoked via an AJAX call, it will render the "ajax/cart" template.
     * Otherwise, it will perform a 302 redirect to "/cart"
     * 
     * @param request
     * @param response
     * @param model
     * @param itemRequest
     * @throws IOException
     * @throws AddToCartException 
     * @throws PricingException
     * @throws RemoveFromCartException 
     * @throws NumberFormatException 
     */
    public String add(HttpServletRequest request, HttpServletResponse response, Model model,
            OrderItemRequestDTO itemRequest) throws IOException, AddToCartException, PricingException, NumberFormatException, RemoveFromCartException, IllegalArgumentException  {
        Order cart = CartState.getCart();
        
        // If the cart is currently empty, it will be the shared, "null" cart. We must detect this
        // and provision a fresh cart for the current customer upon the first cart add
        if (cart == null || cart instanceof NullOrderImpl) {
            cart = orderService.createNewCartForCustomer(CustomerState.getCustomer(request));
        }

        // Validate the add to cart
        updateCartService.validateAddToCartRequest(itemRequest, cart);

        // if this is an update to an existing order item, remove the old before proceeding
        if (isUpdateRequest(request)) {
            String originalOrderItem = request.getParameter("originalOrderItem");
            if (StringUtils.isNotEmpty(originalOrderItem)) {
                Long originalOrderItemId = Long.parseLong(originalOrderItem);
                updateAddRequestQuantities(itemRequest, originalOrderItemId);

                cart = orderService.removeItem(cart.getId(), originalOrderItemId, false);
                cart = orderService.save(cart, true);
            }
        }

        cart = orderService.addItem(cart.getId(), itemRequest, false);
        cart = orderService.save(cart, true);

        return isAjaxRequest(request) ? getCartView() : getCartPageRedirect();
    }

    protected void updateAddRequestQuantities(OrderItemRequestDTO itemRequest, Long originalOrderItemId) {
        // Update the request to match the quantity of the order item it's replacing
        OrderItem orderItem = orderItemService.readOrderItemById(originalOrderItemId);

        // Make sure there is actually an order item to process
        if (orderItem == null) {
            return;
        }

        itemRequest.setQuantity(orderItem.getQuantity());
        for (OrderItemRequestDTO childDTO : itemRequest.getChildOrderItems()) {
            childDTO.setQuantity(childDTO.getQuantity() * orderItem.getQuantity());
        }
    }

    protected boolean isUpdateRequest(HttpServletRequest request) {
        return request.getParameter("isUpdateRequest") != null && Boolean.parseBoolean(request.getParameter("isUpdateRequest"));
    }

    /**
     * Takes in an item request, adds the item to the customer's current cart, and returns.
     * 
     * Calls the addWithOverrides method on the orderService which will honor the override
     * prices on the OrderItemRequestDTO request object.
     * 
     * Implementors must secure this method to avoid accidentally exposing the ability for 
     * malicious clients to override prices by hacking the post parameters.
     * 
     * @param request
     * @param response
     * @param model
     * @param itemRequest
     * @throws IOException
     * @throws AddToCartException 
     * @throws PricingException
     */
    public String addWithPriceOverride(HttpServletRequest request, HttpServletResponse response, Model model,
            OrderItemRequestDTO itemRequest) throws IOException, AddToCartException, PricingException {
        Order cart = CartState.getCart();

        // If the cart is currently empty, it will be the shared, "null" cart. We must detect this
        // and provision a fresh cart for the current customer upon the first cart add
        if (cart == null || cart instanceof NullOrderImpl) {
            cart = orderService.createNewCartForCustomer(CustomerState.getCustomer(request));
        }

        updateCartService.validateAddToCartRequest(itemRequest, cart);

        cart = orderService.addItemWithPriceOverrides(cart.getId(), itemRequest, false);
        cart = orderService.save(cart, true);

        return isAjaxRequest(request) ? getCartView() : getCartPageRedirect();
    }

    @Deprecated
    public String addWithPriceOverride(HttpServletRequest request, HttpServletResponse response, Model model,
                                       AddToCartItem itemRequest) throws IOException, AddToCartException, PricingException {
        return addWithPriceOverride(request, response, model, (OrderItemRequestDTO) itemRequest);
    }

    /**
     * Takes a product id and builds out a dependant order item tree.  If it determines the order
     * item is safe to add, it will proceed to calling the "add" method.
     *
     * If the method was invoked via an AJAX call, it will render the "ajax/configure" template.
     * Otherwise, it will perform a 302 redirect to "/cart/configure"
     *
     * In the case that an "add" happened it will render either the "ajax/cart" or perform a 302
     * redirect to "/cart"
     *
     * @param request
     * @param response
     * @param model
     * @param productId
     * @throws IOException
     * @throws AddToCartException
     * @throws PricingException
     */
    public String configure(HttpServletRequest request, HttpServletResponse response, Model model,
                            Long productId) throws IOException, AddToCartException, PricingException, Exception {

        Product product = catalogService.findProductById(productId);
        ConfigurableOrderItemRequest itemRequest = orderItemService.createConfigurableOrderItemRequestFromProduct(product);

        orderItemService.modifyOrderItemRequest(itemRequest);

        // If this item request is safe to add, go ahead and add it.
        if (isSafeToAdd(itemRequest)) {
            return add(request, response, model, itemRequest);
        }

        model.addAttribute("baseItem", itemRequest);
        model.addAttribute("isUpdateRequest", Boolean.TRUE);
        model.addAttribute(ALL_PRODUCTS_ATTRIBUTE_NAME, orderItemService.findAllProductsInRequest(itemRequest));

        return isAjaxRequest(request) ? getConfigureView() : getConfigurePageRedirect();
    }

    /**
     * Takes an order item id and rebuilds the dependant order item tree with the current quantities and options set.
     *
     * If the method was invoked via an AJAX call, it will render the "ajax/configure" template.
     * Otherwise, it will perform a 302 redirect to "/cart/configure"
     *
     * @param request
     * @param response
     * @param model
     * @param orderItemId
     * @throws IOException
     * @throws AddToCartException
     * @throws PricingException
     */
    public String reconfigure(HttpServletRequest request, HttpServletResponse response, Model model,
                            Long orderItemId) throws IOException, AddToCartException, PricingException, Exception {

        DiscreteOrderItem orderItem = (DiscreteOrderItem) orderItemService.readOrderItemById(orderItemId);

        Long productId = orderItem.getProduct().getId();
        Product product = catalogService.findProductById(productId);
        ConfigurableOrderItemRequest itemRequest = orderItemService.createConfigurableOrderItemRequestFromProduct(product);

        orderItemService.modifyOrderItemRequest(itemRequest);
        orderItemService.mergeOrderItemRequest(itemRequest, orderItem);

        // update quantities and product options
        itemRequest.setQuantity(orderItem.getQuantity());

        model.addAttribute("baseItem", itemRequest);
        model.addAttribute("isUpdateRequest", Boolean.TRUE);
        model.addAttribute("originalOrderItem", orderItemId);
        model.addAttribute(ALL_PRODUCTS_ATTRIBUTE_NAME, orderItemService.findAllProductsInRequest(itemRequest));

        return isAjaxRequest(request) ? getConfigureView() : getConfigurePageRedirect();
    }

    /**
     * Takes in an item request and updates the quantity of that item in the cart. If the quantity
     * was passed in as 0, it will remove the item.
     * 
     * If the method was invoked via an AJAX call, it will render the "ajax/cart" template.
     * Otherwise, it will perform a 302 redirect to "/cart"
     * 
     * @param request
     * @param response
     * @param model
     * @param itemRequest
     * @throws IOException
     * @throws PricingException
     * @throws UpdateCartException
     * @throws RemoveFromCartException 
     */
    public String updateQuantity(HttpServletRequest request, HttpServletResponse response, Model model,
            OrderItemRequestDTO itemRequest) throws IOException, UpdateCartException, PricingException, RemoveFromCartException {
        Order cart = CartState.getCart();

        cart = orderService.updateItemQuantity(cart.getId(), itemRequest, true);
        cart = orderService.save(cart, false);
        
        if (isAjaxRequest(request)) {
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("skuId", itemRequest.getSkuId());
            extraData.put("productId", itemRequest.getProductId());
            extraData.put("cartItemCount", cart.getItemCount());
            model.addAttribute("blcextradata", new ObjectMapper().writeValueAsString(extraData));
            return getCartView();
        } else {
            return getCartPageRedirect();
        }
    }

    @Deprecated
    public String updateQuantity(HttpServletRequest request, HttpServletResponse response, Model model,
                                 AddToCartItem itemRequest) throws IOException, UpdateCartException, PricingException, RemoveFromCartException {
        return updateQuantity(request, response, model, (OrderItemRequestDTO) itemRequest);
    }

    /**
     * Takes in an item request, updates the quantity of that item in the cart, and returns
     * 
     * If the method was invoked via an AJAX call, it will render the "ajax/cart" template.
     * Otherwise, it will perform a 302 redirect to "/cart"
     * 
     * @param request
     * @param response
     * @param model
     * @param itemRequest
     * @throws IOException
     * @throws PricingException
     * @throws RemoveFromCartException 
     */
    public String remove(HttpServletRequest request, HttpServletResponse response, Model model,
            OrderItemRequestDTO itemRequest) throws IOException, PricingException, RemoveFromCartException {
        Order cart = CartState.getCart();
        
        cart = orderService.removeItem(cart.getId(), itemRequest.getOrderItemId(), false);
        cart = orderService.save(cart, true);
        
        if (isAjaxRequest(request)) {
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("cartItemCount", cart.getItemCount());
            extraData.put("skuId", itemRequest.getSkuId());
            extraData.put("productId", itemRequest.getProductId());
            model.addAttribute("blcextradata", new ObjectMapper().writeValueAsString(extraData));
            return getCartView();
        } else {
            return getCartPageRedirect();
        }
    }

    @Deprecated
    public String remove(HttpServletRequest request, HttpServletResponse response, Model model,
                         ConfigurableOrderItemRequest itemRequest) throws IOException, PricingException, RemoveFromCartException {
        return remove(request, response, model, (OrderItemRequestDTO) itemRequest);
    }

    /**
     * Cancels the current cart and redirects to the homepage
     * 
     * @param request
     * @param response
     * @param model
     * @throws PricingException
     */
    public String empty(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
        Order cart = CartState.getCart();
        orderService.cancelOrder(cart);
        CartState.setCart(null);
        return "redirect:/";
    }
    
    /** Attempts to add provided Offer to Cart
     * 
     * @param request
     * @param response
     * @param model
     * @param customerOffer
     * @return the return view
     * @throws IOException
     * @throws PricingException
     * @throws OfferMaxUseExceededException 
     */
    public String addPromo(HttpServletRequest request, HttpServletResponse response, Model model,
            String customerOffer) throws IOException, PricingException {
        Order cart = CartState.getCart();
        
        Boolean promoAdded = false;
        String exception = "";
        
        if (cart != null && !(cart instanceof NullOrderImpl)) {
            List<OfferCode> offerCodes = offerService.lookupAllOfferCodesByCode(customerOffer);
            if (CollectionUtils.isNotEmpty(offerCodes)) {
                for (OfferCode offerCode : offerCodes) {
                    if (offerCode != null) {
                        try {
                            orderService.addOfferCode(cart, offerCode, false);
                            promoAdded = true;
                        } catch (OfferException e) {
                            if (e instanceof OfferMaxUseExceededException) {
                                exception = "Use Limit Exceeded";
                            } else if (e instanceof OfferExpiredException) {
                                exception = "Offer Has Expired";
                            } else if (e instanceof OfferAlreadyAddedException) {
                                exception = "Offer Has Already Been Added";
                            } else {
                                exception = "An Unknown Offer Error Has Occurred";
                            }
                        }
                    } else {
                        exception = "Invalid Code";
                    }
                }
                if(exception.equals("")) {
                    cart = orderService.save(cart, true);
                }
            } else {
                exception = "Unknown Code";
            }
        } else {
            exception = "Invalid Cart";
        }

        if (isAjaxRequest(request)) {
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("promoAdded", promoAdded);
            extraData.put("exception" , exception);
            model.addAttribute("blcextradata", new ObjectMapper().writeValueAsString(extraData));
        } else {
            model.addAttribute("exception", exception);
        }

        return isCheckoutContext(request) ? getCheckoutView() : getCartView();
    }

    protected boolean isCheckoutContext(HttpServletRequest request) {
        String isCheckoutContext = request.getParameter("isCheckoutContext");

        return Boolean.parseBoolean(isCheckoutContext);
    }

    /** Removes offer from cart
     * 
     * @param request
     * @param response
     * @param model
     * @return the return view
     * @throws IOException
     * @throws PricingException
     * @throws OfferMaxUseExceededException 
     */
    public String removePromo(HttpServletRequest request, HttpServletResponse response, Model model,
            Long offerCodeId) throws IOException, PricingException {
        Order cart = CartState.getCart();
        
        OfferCode offerCode = offerService.findOfferCodeById(offerCodeId);

        orderService.removeOfferCode(cart, offerCode, false);
        cart = orderService.save(cart, true);


        if (isCheckoutContext(request)) {
            return getCheckoutView();
        } else {
            return isAjaxRequest(request) ? getCartView() : getCartPageRedirect();
        }
    }

    public String getCartView() {
        return cartView;
    }

    public String getCartPageRedirect() {
        return cartPageRedirect;
    }

    public String getConfigureView() {
        return configureView;
    }

    public String getConfigurePageRedirect() {
        return configurePageRedirect;
    }

    public String getCheckoutView() {
        return checkoutView;
    }

    public Map<String, String> handleIllegalCartOpException(IllegalCartOperationException ex) {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("error", "illegalCartOperation");
        returnMap.put("exception", BLCMessageUtils.getMessage(ex.getType()));
        return returnMap;
    }

    protected boolean isSafeToAdd(ConfigurableOrderItemRequest itemRequest) {
        if (!automaticallyAddCompleteItems) {
            return false;
        }

        boolean canSafelyAdd = true;
        for (OrderItemRequestDTO child : itemRequest.getChildOrderItems()) {
            ConfigurableOrderItemRequest configurableRequest = (ConfigurableOrderItemRequest) child;
            Product childProduct = configurableRequest.getProduct();

            if (childProduct == null) {
                return false;
            }

            int minQty = configurableRequest.getMinQuantity();
            if (minQty == 0 || childProduct.getProductOptionXrefs().size() > 0) {
                return false;
            }

            canSafelyAdd = isSafeToAdd(configurableRequest);
        }
        return canSafelyAdd;
    }
}
