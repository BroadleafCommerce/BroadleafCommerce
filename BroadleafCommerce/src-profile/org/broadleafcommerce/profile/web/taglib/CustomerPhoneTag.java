package org.broadleafcommerce.profile.web.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerPhoneService;
import org.broadleafcommerce.profile.web.CustomerState;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CustomerPhoneTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private Long customerPhoneId;
    private String var;

    public int doStartTag() throws JspException {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
        CustomerState customerState = (CustomerState) applicationContext.getBean("customerState");
        CustomerPhoneService customerPhoneService = (CustomerPhoneService) applicationContext.getBean("customerPhoneService");

        Customer customer = customerState.getCustomer((HttpServletRequest) pageContext.getRequest());

        if(customerPhoneId != null){
            pageContext.setAttribute(var, customerPhoneService.readCustomerPhoneByIdAndCustomerId(customerPhoneId, customer.getId()));
        }else{
            pageContext.setAttribute(var, customerPhoneService.readActiveCustomerPhonesByCustomerId(customer.getId()));
        }

        return EVAL_PAGE;
    }

    public Long getCustomerPhoneId() {
        return customerPhoneId;
    }

    public String getVar() {
        return var;
    }

    public void setCustomerPhoneId(Long customerPhoneId) {
        this.customerPhoneId = customerPhoneId;
    }

    public void setVar(String var) {
        this.var = var;
    }
}
