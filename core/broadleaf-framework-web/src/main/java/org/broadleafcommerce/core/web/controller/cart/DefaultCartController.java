package org.broadleafcommerce.core.web.controller.cart;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.ItemNotFoundException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DefaultCartController extends AbstractCartController {
	
	public String cart(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
		return ajaxRender("cart", request, model);
	}
	
	//FIXME-APA: Needs to reference SKUs, not Products
	public Map<String, Object> add(HttpServletRequest request, HttpServletResponse response, Model model,
			Long productId,
			Integer quantity) throws IOException, PricingException {
		Customer customer = customerState.getCustomer(request);
		Order cart = cartService.findCartForCustomer(customer);
		if (cart == null) {
			cart = cartService.createNewCartForCustomer(customer);
		}
		
		Product product = catalogService.findProductById(productId);
		OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
		
		itemRequest.setQuantity(1);
		itemRequest.setProductId(product.getId());   
		
		cartService.addItemToOrder(cart.getId(), itemRequest, false);
		cart = cartService.save(cart,  true);
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("productId", String.valueOf(productId));
		responseMap.put("cartItemCount", String.valueOf(cart.getItemCount()));
		responseMap.put("productName", product.getName());
		responseMap.put("quantityAdded", quantity);
		
		if (isAjaxRequest(request)) {
			return responseMap;
		} else {
			sendRedirect(request, response, "/cart");
		}
		
		return null;
	}
	
	public Map<String, Object> updateQuantity(HttpServletRequest request, HttpServletResponse response, Model model,
			Long orderItemId,
			Integer newQuantity) throws IOException, PricingException, ItemNotFoundException {
		Customer customer = customerState.getCustomer(request);
		Order cart = cartService.findCartForCustomer(customer);
		
		OrderItem orderItem = null;
		for (DiscreteOrderItem doi : cart.getDiscreteOrderItems()) {
			if (doi.getId().equals(orderItemId)) {
				orderItem = doi;
			}
		}
		
		orderItem.setQuantity(newQuantity);
		cartService.updateItemQuantity(cart, orderItem);
		cart = cartService.save(cart, true);
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("cartItemCount", String.valueOf(cart.getItemCount()));
		
		if (isAjaxRequest(request)) {
			return responseMap;
		} else {
			sendRedirect(request, response, "/cart");
		}
			
		return null;
	}
	
	public Map<String, Object> remove(HttpServletRequest request, HttpServletResponse response, Model model,
			Long orderItemId) throws IOException, PricingException {
		Customer customer = customerState.getCustomer(request);
		Order cart = cartService.findCartForCustomer(customer);
		
		Long productId = null;
		for (DiscreteOrderItem doi : cart.getDiscreteOrderItems()) {
			if (doi.getId().equals(orderItemId)) {
				productId = doi.getProduct().getId();
			}
		}
		cart = cartService.removeItemFromOrder(cart.getId(), orderItemId);
		cart = cartService.save(cart, true);
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("productId", String.valueOf(productId));
		responseMap.put("cartItemCount", String.valueOf(cart.getItemCount()));
		
		if (isAjaxRequest(request)) {
			return responseMap;
		} else {
			sendRedirect(request, response, "/cart");
		}
			
		return null;
	}
	
	public String empty(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
    	Customer customer = customerState.getCustomer(request);
    	Order cart = cartService.findCartForCustomer(customer);
    	cartService.cancelOrder(cart);
    	return "redirect:/";
	}
	
}
