package org.broadleafcommerce.core.web.controller.cart;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.order.model.AddToCartItem;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafCartController extends AbstractCartController {
	
	/**
	 * Renders the cart page.
	 * 
	 * If the method was invoked via an AJAX call, it will render the "ajax/cart" template.
	 * Otherwise, it will render the "cart" template.
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws PricingException
	 */
	public String cart(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
		return ajaxRender("cart", request, model);
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
	 * @throws PricingException
	 */
	public String add(HttpServletRequest request, HttpServletResponse response, Model model,
			AddToCartItem itemRequest) throws IOException, PricingException {
		Order cart = CartState.getCart(request);
		
		// If the cart is currently empty, it will be the shared, "null" cart. We must detect this
		// and provision a fresh cart for the current customer upon the first cart add
		if (cart == null || cart.equals(cartService.getNullOrder())) {
			cart = cartService.createNewCartForCustomer(CustomerState.getCustomer(request));
		}
		
		cartService.addItemToOrder(cart.getId(), itemRequest, false);
		cart = cartService.save(cart,  true);
		CartState.setCart(request, cart);
		
    	return isAjaxRequest(request) ? "ajax/cart" : "redirect:/cart";
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
	 * @throws ItemNotFoundException
	 */
	public String updateQuantity(HttpServletRequest request, HttpServletResponse response, Model model,
			AddToCartItem itemRequest) throws IOException, PricingException, ItemNotFoundException {
		Order cart = CartState.getCart(request);
		cartService.updateItemQuantity(cart, itemRequest);
		cart = cartService.save(cart, true);
		CartState.setCart(request, cart);
		
		if (isAjaxRequest(request)) {
			Map<String, Object> extraData = new HashMap<String, Object>();
			extraData.put("productId", itemRequest.getProductId());
			extraData.put("cartItemCount", cart.getItemCount());
			model.addAttribute("blcextradata", new ObjectMapper().writeValueAsString(extraData));
			return "ajax/cart";
		} else {
			return "redirect:/cart";
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
	 * @param nonAjaxSuccessUrl
	 * @param itemRequest
	 * @throws IOException
	 * @throws PricingException
	 * @throws ItemNotFoundException
	 */
	public String remove(HttpServletRequest request, HttpServletResponse response, Model model,
			AddToCartItem itemRequest) throws IOException, PricingException, ItemNotFoundException {
		Order cart = CartState.getCart(request);
		
		cart = cartService.removeItemFromOrder(cart.getId(), itemRequest.getOrderItemId());
		cart = cartService.save(cart, true);
		CartState.setCart(request, cart);
		
		if (isAjaxRequest(request)) {
			Map<String, Object> extraData = new HashMap<String, Object>();
			extraData.put("cartItemCount", cart.getItemCount());
			extraData.put("productId", itemRequest.getProductId());
			model.addAttribute("blcextradata", new ObjectMapper().writeValueAsString(extraData));
			return "ajax/cart";
		} else {
			return "redirect:/cart";
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
		Order cart = CartState.getCart(request);
    	cartService.cancelOrder(cart);
		CartState.setCart(request, null);
		
    	return "redirect:/";
	}
	
	/** Attempts to add provided Offer to Cart
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param customerOffer
	 * @return
	 * @throws IOException
	 * @throws PricingException
	 * @throws ItemNotFoundException
	 * @throws OfferMaxUseExceededException 
	 */
	
	public String addPromo(HttpServletRequest request, HttpServletResponse response, Model model,
			String customerOffer) throws IOException, PricingException {
		Order cart = CartState.getCart(request);
		
		Boolean promoAdded = false;
		String exception = "";
		
		OfferCode offerCode = offerService.lookupOfferCodeByCode(customerOffer);
		
		if(offerCode!=null) {
			try {
				cartService.addOfferCode(cart, offerCode, false);
				promoAdded = true;
				cart = cartService.save(cart, true);
			} catch(OfferMaxUseExceededException e) {
				exception = "Use Limit Exceeded";
			}
		} else {
			exception = "Invalid Code";
		}
		
		CartState.setCart(request, cart);
		
		if (isAjaxRequest(request)) {
			Map<String, Object> extraData = new HashMap<String, Object>();
			extraData.put("promoAdded", promoAdded);
			extraData.put("exception" , exception);
			model.addAttribute("blcextradata", new ObjectMapper().writeValueAsString(extraData));
			return "ajax/cart";
		} else {
			return "redirect:/cart";
		}
		
	}
	
	/** Removes offer from cart
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param offerId
	 * @return
	 * @throws IOException
	 * @throws PricingException
	 * @throws ItemNotFoundException
	 * @throws OfferMaxUseExceededException 
	 */
	
	public String removePromo(HttpServletRequest request, HttpServletResponse response, Model model,
			Long offerCodeId) throws IOException, PricingException {
		Order cart = CartState.getCart(request);
		
		OfferCode offerCode = offerService.findOfferCodeById(offerCodeId);

		cartService.removeOfferCode(cart, offerCode, false);
		cart = cartService.save(cart, true);
		CartState.setCart(request, cart);

		if (isAjaxRequest(request)) {
			return "ajax/cart";
		} else {
			return "redirect:/cart";
		}	
	}
}
