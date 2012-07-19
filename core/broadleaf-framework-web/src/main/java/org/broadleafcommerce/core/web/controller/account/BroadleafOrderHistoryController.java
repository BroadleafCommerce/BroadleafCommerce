package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

public class BroadleafOrderHistoryController extends AbstractAccountController {

    protected String orderHistoryView = "account/orderHistory";
    protected String orderDetailsView = "ajax:account/partials/orderDetails";
    protected String orderDetailsRedirectView = "account/partials/orderDetails";
    
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

    public String getOrderHistoryView() {
        return orderHistoryView;
    }

    public void setOrderHistoryView(String orderHistoryView) {
        this.orderHistoryView = orderHistoryView;
    }

    public String getOrderDetailsView() {
        return orderDetailsView;
    }

    public void setOrderDetailsView(String orderDetailsView) {
        this.orderDetailsView = orderDetailsView;
    }

    public String getOrderDetailsRedirectView() {
        return orderDetailsRedirectView;
    }

    public void setOrderDetailsRedirectView(String orderDetailsRedirectView) {
        this.orderDetailsRedirectView = orderDetailsRedirectView;
    }

}
