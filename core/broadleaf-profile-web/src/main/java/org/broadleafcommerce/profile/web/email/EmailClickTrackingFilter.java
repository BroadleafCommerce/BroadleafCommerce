/*
 * #%L
 * BroadleafCommerce Profile Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.profile.web.email;

import org.broadleafcommerce.common.email.service.EmailTrackingManager;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jfischer
 *
 */
public class EmailClickTrackingFilter implements Filter {

    private EmailTrackingManager emailTrackingManager;

    @Resource(name="blCustomerState")
    protected CustomerState customerState;

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        //do nothing
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
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
