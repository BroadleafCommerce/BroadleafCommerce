package org.springcommerce.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.order.domain.Order;
import org.springcommerce.order.domain.OrderItem;
import org.springcommerce.order.service.OrderService;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.UserService;
import org.springcommerce.util.Basket;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("basket")
public class ListBasketFormController {
    protected final Log logger = LogFactory.getLog(getClass());

    private String redirectUrl = "";    
    private Basket basket = new Basket();
    private OrderService orderService;    
    private UserService userService;

    @ModelAttribute("basket")
    public Basket getbasket(){
    	Order basketOrder = getUserBasket();
    	basket.setOrder(basketOrder);
    	basket.setItems(orderService.getItemsForOrder(basketOrder.getId()));
    	return basket;
//    	return basket;
    }
    
//    @ModelAttribute("basket")
    public void setBasket(Basket basket){
    	this.basket = basket;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String addSellableItem(@RequestParam("sellableItemId") Long sellableItemId)
    {
    	Order basketOrder = getUserBasket();
    	orderService.addItemToOrder(basketOrder.getId(), sellableItemId, 1);
    	return "redirect:" +redirectUrl;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String listBasket()
    {
    	return "listBasket";
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST)
    public String updateQuantity(Basket basket, BindingResult result){
    	for (OrderItem orderItem : basket.getItems()) {
    		orderService.updateItemInOrder(orderItem.getOrder(), orderItem);
		}
    	return "redirect:"+redirectUrl;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String removeItem(@RequestParam("sellableItemId") Long sellableItemId)
    {
    	Order basketOrder = getUserBasket();
    	orderService.removeItemFromOrder(basketOrder.getId(), sellableItemId);
    	return "redirect:"+redirectUrl;
    }
    
    private Order getUserBasket(){
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	
        User user = userService.readUserByUsername(auth.getName());
        return orderService.getCurrentBasketForUserId(user.getId());    	
    }
    
    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
    
}
