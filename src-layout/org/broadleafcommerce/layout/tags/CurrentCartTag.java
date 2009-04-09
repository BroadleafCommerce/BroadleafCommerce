package org.broadleafcommerce.layout.tags;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.security.CustomerState;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CurrentCartTag extends BodyTagSupport {
    // private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;
    private Long customerId;
	private String orderVar;
    private String orderItemsVar;
    private String totalQuantityVar;

	@Override
    public int doStartTag() throws JspException {
		Customer customer = null;
		if (id != null){
	        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
	        CustomerService customerService = (CustomerService) applicationContext.getBean("customerService");
	        customer = customerService.readCustomerById((customerId));
		} else {
			customer =  CustomerState.getCustomer((HttpServletRequest)pageContext.getRequest());
		}
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
        OrderService orderService = (OrderService) applicationContext.getBean("orderService");
        Order order = orderService.findCartForCustomer(customer);
        if (orderVar != null) {
        	pageContext.setAttribute(orderVar, order);
        }
        List<OrderItem> orderItems = null;
        if ((orderItemsVar != null || totalQuantityVar != null) && order != null) {
        	orderItems = order.getOrderItems();
        	if (orderItemsVar != null) {
        		pageContext.setAttribute(orderItemsVar, orderItems);
        	}
        }
        if (totalQuantityVar != null && orderItems != null) {
        	int orderItemsCount = 0;
        	for (OrderItem orderItem : orderItems) {
        		orderItemsCount += orderItem.getQuantity();
        	}
        	pageContext.setAttribute(totalQuantityVar, orderItemsCount);
        } else if (totalQuantityVar != null) {
        	pageContext.setAttribute(totalQuantityVar, 0);
        }
        return EVAL_PAGE;
    }
	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
    public String getOrderVar() {
		return orderVar;
	}
	public void setOrderVar(String orderVar) {
		this.orderVar = orderVar;
	}
	public String getOrderItemsVar() {
		return orderItemsVar;
	}
	public void setOrderItemsVar(String orderItemsVar) {
		this.orderItemsVar = orderItemsVar;
	}
	public String getTotalQuantityVar() {
		return totalQuantityVar;
	}
	public void setTotalQuantityVar(String totalQuantityVar) {
		this.totalQuantityVar = totalQuantityVar;
	}

}
