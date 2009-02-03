package org.broadleafcommerce.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.service.UserService;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ListOrdersFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private OrderService orderService;
    
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

    @Override
	protected Object formBackingObject(HttpServletRequest request)throws ServletException {
    	return new Order();
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	
        User user = userService.readUserByUsername(auth.getName());
        List<Order> orderList = orderService.getOrdersForUser(user.getId());
        Map<Object, Object> model = new HashMap<Object, Object>();
        model.put("orderList", orderList);

        return new ModelAndView("listOrders", model);
    }
}
