package org.springcommerce.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.order.domain.BasketOrder;
import org.springcommerce.order.domain.Order;
import org.springcommerce.order.domain.OrderItem;
import org.springcommerce.order.service.OrderService;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.UserService;
import org.springcommerce.util.BasketItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

@Controller
@SessionAttributes("basketItems")
public class ListBasketFormController {
    protected final Log logger = LogFactory.getLog(getClass());

    private String redirectUrl = "";    
    private BasketItems basketItems = new BasketItems();
    private OrderService orderService;    
    private UserService userService;
    
    
    @ModelAttribute("basketItems")
    public BasketItems getBasketItems(){
    	Order basketOrder = getUserBasket();
    	basketItems.setItems(orderService.getItemsForOrder(basketOrder.getId()));
    	return basketItems;
//    	return basketItems;
    }
    
//    @ModelAttribute("basketItems")
    public void setBasketItems(BasketItems basketItems){
    	this.basketItems = basketItems;
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
    public String updateQuantity(BasketItems basketItems, BindingResult result){
    	for (OrderItem orderItem : basketItems.getItems()) {
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
    
//    protected Object formBackingObject(HttpServletRequest request)throws ServletException {
//        if (request.getParameter("sellableItemId") != null){
//            Order basketOrder = getUserBasket();
//        	orderService.addItemToOrder(basketOrder.getId(), new Long(request.getParameter("sellableItemId")), 1);
//        }    	
//
//    	return getBasketItems();
//    }

//    @Override
//    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Order basketOrder = getUserBasket();
//
//
//        List<OrderItem> orderItems = getBasketItems();
//        
//        Map<Object, Object> model = new HashMap<Object, Object>();
//        model.put("listBasket", orderItems);
//
//        return new ModelAndView("listBasket", model);
//        
//    }
    
//    @Override
//    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
//    throws Exception {
////    	if (request.getParameter("updateQuantity") != null){
//    		List<OrderItem> basketItems = (List<OrderItem>)command;
//    		for (OrderItem orderItem : basketItems) {
//				System.out.println("orderItem - sellableId:"+orderItem.getSellableItem().getId()+" new Quantity: "+orderItem.getQuantity());
//			}
//    		
////    	}
//    	
//
//        
//        List<OrderItem> orderItems = getBasketItems();
//
//        Map<Object, Object> model = new HashMap<Object, Object>();
//        model.put("listBasket", orderItems);
//
//        return new ModelAndView("listBasket", model);
//    }
    
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
