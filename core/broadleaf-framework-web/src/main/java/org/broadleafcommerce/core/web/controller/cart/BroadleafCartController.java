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
package org.broadleafcommerce.core.web.controller.cart;

import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.exception.OfferAlreadyAddedException;
import org.broadleafcommerce.core.offer.service.exception.OfferException;
import org.broadleafcommerce.core.offer.service.exception.OfferExpiredException;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.AddToCartItem;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.IllegalCartOperationException;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
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
    protected static String cartPageRedirect = "redirect:/cart";
    
    @Value("${solr.index.use.sku}")
    protected boolean useSku;
    
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
     */
    public String add(HttpServletRequest request, HttpServletResponse response, Model model,
            AddToCartItem itemRequest) throws IOException, AddToCartException, PricingException  {
        Order cart = CartState.getCart();
        
        // If the cart is currently empty, it will be the shared, "null" cart. We must detect this
        // and provision a fresh cart for the current customer upon the first cart add
        if (cart == null || cart instanceof NullOrderImpl) {
            cart = orderService.createNewCartForCustomer(CustomerState.getCustomer(request));
        }

        updateCartService.validateCart(cart);

        cart = orderService.addItem(cart.getId(), itemRequest, false);
        cart = orderService.save(cart,  true);
        
        return isAjaxRequest(request) ? getCartView() : getCartPageRedirect();
    }
    
    /**
     * Takes in an item request, adds the item to the customer's current cart, and returns.
     * 
     * Calls the addWithOverrides method on the orderService which will honor the override
     * prices on the AddToCartItem request object.
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
            AddToCartItem itemRequest) throws IOException, AddToCartException, PricingException {
        Order cart = CartState.getCart();

        // If the cart is currently empty, it will be the shared, "null" cart. We must detect this
        // and provision a fresh cart for the current customer upon the first cart add
        if (cart == null || cart instanceof NullOrderImpl) {
            cart = orderService.createNewCartForCustomer(CustomerState.getCustomer(request));
        }

        updateCartService.validateCart(cart);

        cart = orderService.addItemWithPriceOverrides(cart.getId(), itemRequest, false);
        cart = orderService.save(cart, true);

        return isAjaxRequest(request) ? getCartView() : getCartPageRedirect();
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
            AddToCartItem itemRequest) throws IOException, UpdateCartException, PricingException, RemoveFromCartException {
        Order cart = CartState.getCart();

        cart = orderService.updateItemQuantity(cart.getId(), itemRequest, true);
        cart = orderService.save(cart, false);
        
        if (isAjaxRequest(request)) {
            Map<String, Object> extraData = new HashMap<String, Object>();
            if(useSku) {
                extraData.put("skuId", itemRequest.getSkuId());
            } else {
                extraData.put("productId", itemRequest.getProductId());
            }
            extraData.put("cartItemCount", cart.getItemCount());
            model.addAttribute("blcextradata", new ObjectMapper().writeValueAsString(extraData));
            return getCartView();
        } else {
            return getCartPageRedirect();
        }
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
            AddToCartItem itemRequest) throws IOException, PricingException, RemoveFromCartException {
        Order cart = CartState.getCart();
        
        cart = orderService.removeItem(cart.getId(), itemRequest.getOrderItemId(), false);
        cart = orderService.save(cart, true);
        
        if (isAjaxRequest(request)) {
            Map<String, Object> extraData = new HashMap<String, Object>();
            extraData.put("cartItemCount", cart.getItemCount());
            if(useSku) {
                extraData.put("skuId", itemRequest.getSkuId());
            } else {
                extraData.put("productId", itemRequest.getProductId());
            }
            model.addAttribute("blcextradata", new ObjectMapper().writeValueAsString(extraData));
            return getCartView();
        } else {
            return getCartPageRedirect();
        }
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
     * @throws ItemNotFoundException
     * @throws OfferMaxUseExceededException 
     */
    public String addPromo(HttpServletRequest request, HttpServletResponse response, Model model,
            String customerOffer) throws IOException, PricingException {
        Order cart = CartState.getCart();
        
        Boolean promoAdded = false;
        String exception = "";
        
        if (cart != null && !(cart instanceof NullOrderImpl)) {
            OfferCode offerCode = offerService.lookupOfferCodeByCode(customerOffer);
            if (offerCode != null) {
                try {
                    orderService.addOfferCode(cart, offerCode, false);
                    promoAdded = true;
                    cart = orderService.save(cart, true);
                } catch (OfferException e) {
                    Throwable t = e.getCause();
                    if (t instanceof OfferMaxUseExceededException) {
                        exception = "Use Limit Exceeded";
                    } else if (t instanceof OfferExpiredException) {
                        exception = "Offer Has Expired";
                    } else if (t instanceof OfferAlreadyAddedException) {
                        exception = "Offer Has Already Been Added";
                    } else {
                        exception = "An Unknown Offer Error Has Occured";
                    }
                }
            } else {
                exception = "Invalid Code";
            }
        } else {
            exception = "Invalid Cart";
        }

        if (isAjaxRequest(request)) {
            Map<String, Object> extraData = new HashMap<String, Object>();
            extraData.put("promoAdded", promoAdded);
            extraData.put("exception" , exception);
            model.addAttribute("blcextradata", new ObjectMapper().writeValueAsString(extraData));
            return getCartView();
        } else {
            model.addAttribute("exception", exception);
            return getCartView();
        }
        
    }
    
    /** Removes offer from cart
     * 
     * @param request
     * @param response
     * @param model
     * @return the return view
     * @throws IOException
     * @throws PricingException
     * @throws ItemNotFoundException
     * @throws OfferMaxUseExceededException 
     */
    public String removePromo(HttpServletRequest request, HttpServletResponse response, Model model,
            Long offerCodeId) throws IOException, PricingException {
        Order cart = CartState.getCart();
        
        OfferCode offerCode = offerService.findOfferCodeById(offerCodeId);

        orderService.removeOfferCode(cart, offerCode, false);
        cart = orderService.save(cart, true);

        return isAjaxRequest(request) ? getCartView() : getCartPageRedirect();
    }

    public String getCartView() {
        return cartView;
    }

    public String getCartPageRedirect() {
        return cartPageRedirect;
    }
    
    public Map<String, String> handleIllegalCartOpException(IllegalCartOperationException ex) {
        Map<String, String> returnMap = new HashMap<String, String>();
        returnMap.put("error", "illegalCartOperation");
        returnMap.put("exception", BLCMessageUtils.getMessage(ex.getType()));
        return returnMap;
    }
    

}
