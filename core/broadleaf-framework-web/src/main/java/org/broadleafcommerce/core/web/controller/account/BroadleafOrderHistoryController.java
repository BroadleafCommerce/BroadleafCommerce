package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

public class BroadleafOrderHistoryController extends AbstractAccountController {

    protected static String orderHistoryView = "account/orderHistory";
    protected static String orderDetailsView = "account/partials/orderDetails";
    protected static String orderDetailsRedirectView = "account/partials/orderDetails";
    
    public String viewOrderHistory(HttpServletRequest request, Model model) {
        List<Order> orders = orderService.findOrdersForCustomer(CustomerState.getCustomer(), OrderStatus.SUBMITTED);
        model.addAttribute("orders", orders);
        return getOrderHistoryView();
    }

    public String viewOrderDetails(HttpServletRequest request, Model model, String orderNumber) {
        Order order = orderService.findOrderByOrderNumber(orderNumber);
        if (order == null) {
        	throw new IllegalArgumentException("The orderNumber provided is not valid");
        }
        model.addAttribute("order", order);
        return isAjaxRequest(request) ? getOrderDetailsView() : getOrderDetailsRedirectView();
    }

	public static String getOrderHistoryView() {
		return orderHistoryView;
	}

	public static void setOrderHistoryView(String orderHistoryView) {
		BroadleafOrderHistoryController.orderHistoryView = orderHistoryView;
	}

	public static String getOrderDetailsView() {
		return orderDetailsView;
	}

	public static void setOrderDetailsView(String orderDetailsView) {
		BroadleafOrderHistoryController.orderDetailsView = orderDetailsView;
	}

	public static String getOrderDetailsRedirectView() {
		return orderDetailsRedirectView;
	}

	public static void setOrderDetailsRedirectView(String orderDetailsRedirectView) {
		BroadleafOrderHistoryController.orderDetailsRedirectView = orderDetailsRedirectView;
	}

}
