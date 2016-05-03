/*
 * #%L
 * BroadleafCommerce Profile Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.web.core.taglib;

import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerPhoneService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class CustomerPhoneTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private Long customerPhoneId;
    private String var;

    public int doStartTag() throws JspException {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
        CustomerState customerState = (CustomerState) applicationContext.getBean("blCustomerState");
        CustomerPhoneService customerPhoneService = (CustomerPhoneService) applicationContext.getBean("blCustomerPhoneService");

        Customer customer = customerState.getCustomer((HttpServletRequest) pageContext.getRequest());

        if(customerPhoneId != null){
            pageContext.setAttribute(var, customerPhoneService.readCustomerPhoneById(customerPhoneId));
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
