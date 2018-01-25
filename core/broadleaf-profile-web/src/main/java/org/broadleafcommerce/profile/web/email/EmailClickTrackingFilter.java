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
package org.broadleafcommerce.profile.web.email;

import org.broadleafcommerce.common.email.service.EmailTrackingManager;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jfischer
 *
 */
public class EmailClickTrackingFilter implements Filter {

    private EmailTrackingManager emailTrackingManager;

    @Autowired
    @Qualifier("blCustomerState")
    protected CustomerState customerState;

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        //do nothing
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String emailId = request.getParameter("email_id");
        if ( emailId != null ) {
            /*
             * The direct parameter map from the request object returns its values
             * in arrays (at least for the tomcat implementation). We make our own
             * parameter map to avoid this situation.
             */
            Map<String, String> parameterMap = new HashMap<String, String>();
            Enumeration names = request.getParameterNames();

            String customerId = request.getParameter("customerId");
            while(names.hasMoreElements()) {
                String name = (String) names.nextElement();
                if (! "customerId".equals(name)) {
                    parameterMap.put(name, request.getParameter(name));
                }
            }

            if (customerId == null) {
                // Attempt to get customerId from current cookied customer
                Customer customer = customerState.getCustomer((HttpServletRequest)  request);
                if (customer != null && customer.getId() != null) {
                    customerId = customer.getId().toString();
                }

            }
            Map<String, String> extraValues = new HashMap<String, String>();
            extraValues.put("requestUri", ((HttpServletRequest) request).getRequestURI());
            emailTrackingManager.recordClick( Long.valueOf(emailId) , parameterMap, customerId, extraValues);
        }
        chain.doFilter(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        //do nothing
    }

    /**
     * @return the emailTrackingManager
     */
    public EmailTrackingManager getEmailTrackingManager() {
        return emailTrackingManager;
    }

    /**
     * @param emailTrackingManager the emailTrackingManager to set
     */
    public void setEmailTrackingManager(EmailTrackingManager emailTrackingManager) {
        this.emailTrackingManager = emailTrackingManager;
    }

}
