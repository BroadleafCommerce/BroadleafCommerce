/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
