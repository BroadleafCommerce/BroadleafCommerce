package org.broadleafcommerce.layout.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.security.CustomerState;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CustomerLookupTag extends BodyTagSupport {
    // private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;
    private String var;
    private Long customerId;

	@Override
    public int doStartTag() throws JspException {
		if (customerId != null){
	        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
	        CustomerService customerService = (CustomerService) applicationContext.getBean("customerService");
	        pageContext.setAttribute(var, customerService.readCustomerById((customerId)));
		} else {
			Customer customer = CustomerState.getCustomer((HttpServletRequest)pageContext.getRequest());
			pageContext.setAttribute(var, customer);
		}
        return EVAL_PAGE;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
}
