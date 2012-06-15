package org.broadleafcommerce.core.web.controller.cart;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.order.model.AddToCartItem;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BroadleafCartController extends AbstractCartController {
	
	public String cart(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
		return ajaxRender("cart", request, model);
	}
	
	/**
	 * Takes in an item request, adds the item to the customer's current cart, and returns.
	 * 
	 * If the method was invoked via an AJAX call, the returned object will be a map consisting of:
	 * 		- productId
	 * 		- productName
	 * 		- quantityAdded
	 * 		- cartItemCount (the total count, ie numItems * quantity of each item)
	 * 
	 * If the method was NOT invoked via AJAX, it will send a redirect to the page specified by nonAjaxSuccessUrl.
	 * The url redirect will take into account the current application context.
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param nonAjaxSuccessUrl
	 * @param itemRequest
	 * @throws IOException
	 * @throws PricingException
	 */
	public Map<String, Object> add(HttpServletRequest request, HttpServletResponse response, Model model,
			String nonAjaxSuccessUrl,
			AddToCartItem itemRequest) throws IOException, PricingException {
		Order cart = CartState.getCart(request);
		
		// If the cart is currently empty, it will be the shared, "null" cart. We must detect this
		// and provision a fresh cart for the current customer upon the first cart add
		if (cart == null || cart.equals(cartService.getNullOrder())) {
			cart = cartService.createNewCartForCustomer(CustomerState.getCustomer(request));
		}
		
		cartService.addItemToOrder(cart.getId(), itemRequest, false);
		cart = cartService.save(cart,  true);
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("productId", itemRequest.getProductId());
		responseMap.put("productName", catalogService.findProductById(itemRequest.getProductId()).getName());
		responseMap.put("quantityAdded", itemRequest.getQuantity());
		responseMap.put("cartItemCount", String.valueOf(cart.getItemCount()));
		
		if (isAjaxRequest(request)) {
			return responseMap;
		} else {
			sendRedirect(request, response, nonAjaxSuccessUrl);
		}
		
		return null;
	}
	
	/**
	 * Takes in an item request and updates the quantity of that item in the cart. If the quantity
	 * was passed in as 0, it will remove the item.
	 * 
	 * If the method was invoked via an AJAX call, the returned object will be a map consisting of:
	 * 		- productId
	 * 		- cartItemCount (the total count, ie numItems * quantity of each item)
	 * 
	 * If the method was NOT invoked via AJAX, it will send a redirect to the page specified by nonAjaxSuccessUrl.
	 * The url redirect will take into account the current application context.
	 * @param request
	 * @param response
	 * @param model
	 * @param nonAjaxSuccessUrl
	 * @param itemRequest
	 * @throws IOException
	 * @throws PricingException
	 * @throws ItemNotFoundException
	 */
	public Map<String, Object> updateQuantity(HttpServletRequest request, HttpServletResponse response, Model model,
			String nonAjaxSuccessUrl,
			AddToCartItem itemRequest) throws IOException, PricingException, ItemNotFoundException {
		Order cart = CartState.getCart(request);
		
		cartService.updateItemQuantity(cart, itemRequest);
		cart = cartService.save(cart, true);
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("productId", itemRequest.getProductId());
		responseMap.put("cartItemCount", cart.getItemCount());
		
		if (isAjaxRequest(request)) {
			return responseMap;
		} else {
			sendRedirect(request, response, "/cart");
		}
			
		return null;
	}
	
	/**
	 * Takes in an item request, updates the quantity of that item in the cart, and returns
	 * 
	 * If the method was invoked via an AJAX call, the returned object will be a map consisting of:
	 * 		- productId
	 * 		- cartItemCount (the total count, ie numItems * quantity of each item)
	 * 
	 * If the method was NOT invoked via AJAX, it will send a redirect to the page specified by nonAjaxSuccessUrl.
	 * The url redirect will take into account the current application context.
	 * @param request
	 * @param response
	 * @param model
	 * @param nonAjaxSuccessUrl
	 * @param itemRequest
	 * @throws IOException
	 * @throws PricingException
	 * @throws ItemNotFoundException
	 */
	public Map<String, Object> remove(HttpServletRequest request, HttpServletResponse response, Model model,
			String nonAjaxSuccessUrl,
			AddToCartItem itemRequest) throws IOException, PricingException, ItemNotFoundException {
		Order cart = CartState.getCart(request);
		
		cart = cartService.removeItemFromOrder(cart.getId(), itemRequest.getOrderItemId());
		cart = cartService.save(cart, true);
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("cartItemCount", cart.getItemCount());
		responseMap.put("productId", itemRequest.getProductId());
		
		if (isAjaxRequest(request)) {
			return responseMap;
		} else {
			sendRedirect(request, response, "/cart");
		}
			
		return null;
	}
	
	/**
	 * Cancels the current cart and redirects to the homepage
	 * @param request
	 * @param response
	 * @param model
	 * @throws PricingException
	 */
	public String empty(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
		Order cart = CartState.getCart(request);
    	cartService.cancelOrder(cart);
    	return "redirect:/";
	}
	
}
