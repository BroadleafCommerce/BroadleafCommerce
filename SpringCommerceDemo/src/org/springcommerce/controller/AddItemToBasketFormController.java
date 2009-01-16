package org.springcommerce.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.checkout.domain.SalesOrder;
import org.springcommerce.checkout.domain.SalesOrderItem;
import org.springcommerce.checkout.service.SalesOrderService;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.UserService;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class AddItemToBasketFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private SalesOrderService orderService;
    
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setSalesOrderService(SalesOrderService orderService) {
		this.orderService = orderService;
	}

    protected Object formBackingObject(HttpServletRequest request)throws ServletException {
    	return new SalesOrderItem();
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	
        User user = userService.readUserByUsername(auth.getName());
        SalesOrder basketOrder = orderService.getCurrentBasketForUserId(user.getId());
        if (request.getParameter("sellableItemId") != null){
        	orderService.addItemToOrder(basketOrder.getId(), new Long(request.getParameter("sellableItemId")), 1);
        }

        List<SalesOrderItem> orderItems = orderService.getItemsForOrder(basketOrder.getId());
        
        Map<Object, Object> model = new HashMap<Object, Object>();
        model.put("listBasket", orderItems);

        return new ModelAndView("listBasket", model);
    }
}
