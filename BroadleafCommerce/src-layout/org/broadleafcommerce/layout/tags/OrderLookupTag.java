package org.broadleafcommerce.layout.tags;

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

public class OrderLookupTag extends BodyTagSupport {
    // private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;
    private Long customerId;
    private Long orderId;
    private String orderName;
    private String orderVar;
    private String totalQuantityVar;

    @Override
    public int doStartTag() throws JspException {
        Customer customer = null;
        if (customerId != null) {
            WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
            CustomerService customerService = (CustomerService) applicationContext.getBean("customerService");
            customer = customerService.readCustomerById((customerId));
        } else {
            customer = CustomerState.getCustomer((HttpServletRequest) pageContext.getRequest());
        }
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
        OrderService orderService = (OrderService) applicationContext.getBean("orderService");
        Order order = null;
        if (orderName != null && orderId != null) {
            throw new IllegalArgumentException("Only orderName or orderId attribute may be specified on orderLookup tag");
        } else if (orderId != null) {
            order = orderService.findOrderById(orderId);
        } else if (orderName != null) {
            order = orderService.findNamedOrderForCustomer(orderName, customer);
        } else {
            order = orderService.findCartForCustomer(customer);
        }
        if (orderVar != null) {
            pageContext.setAttribute(orderVar, order);
        }
        if (totalQuantityVar != null) {
            int orderItemsCount = 0;
            if (order != null && order.getOrderItems() != null) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    orderItemsCount += orderItem.getQuantity();
                }
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

    public String getTotalQuantityVar() {
        return totalQuantityVar;
    }

    public void setTotalQuantityVar(String totalQuantityVar) {
        this.totalQuantityVar = totalQuantityVar;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
