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
package org.broadleafcommerce.profile.web.core.security;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This is a basic filter for finding the customer ID on the request and setting the customer object on the request.
 * This must come after the BroadleafRequestFilter (blRequestFilter). This should come after any security filters.
 * This filter DOES NOT provide any security.  It simply looks for a "customerId" parameter on the request or in the request header.  If it finds
 * this parameter it looks up the customer and makes it available as a request attribute.  This is generally for use in a filter chain for RESTful web services,
 * allowing the client consuming services to specify the customerId on whos behalf they are invoking the service.  It is assumed that services are invoked either
 * in a trusted, secured network where no additional security is required.  Or using OAuth or a similar trusted security model.  Whatever security model is used,
 * it should ensure that the caller has access to call the system, and that they have access to do so on behalf of the client whos ID is being determined by this class.
 *
 * For RESTful services, this should be used instead of CustomerStateFilter since it does not look at or touch cookies or session.
 *
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/18/12
 */
public class RestApiCustomerStateFilter extends GenericFilterBean implements Ordered {

    protected static final Log LOG = LogFactory.getLog(RestApiCustomerStateFilter.class);

    @Autowired
    @Qualifier("blCustomerService")
    protected CustomerService customerService;

    public static final String CUSTOMER_ID_ATTRIBUTE = "customerId";
    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String customerId = null;

        HttpServletRequest request = (HttpServletRequest)servletRequest;

        //If someone already set the customer on the request then we don't need to do anything.
        if (request.getAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName()) == null){

            //First check to see if someone already put the customerId on the request
            if (request.getAttribute(CUSTOMER_ID_ATTRIBUTE) != null) {
                customerId = String.valueOf(request.getAttribute(CUSTOMER_ID_ATTRIBUTE));
            }

            if (customerId == null) {
                //If it's not on the request attribute, try the parameter
                customerId = servletRequest.getParameter(CUSTOMER_ID_ATTRIBUTE);
            }

            if (customerId == null) {
                //If it's not on the request parameter, look on the header
                customerId = request.getHeader(CUSTOMER_ID_ATTRIBUTE);
            }

            if (customerId != null && customerId.trim().length() > 0) {

                if (NumberUtils.isNumber(customerId)) {
                    //If we found it, look up the customer and put it on the request.
                    Customer customer = customerService.readCustomerById(Long.valueOf(customerId));
                    if (customer != null) {
                        CustomerState.setCustomer(customer);
                        setupCustomerForRuleProcessing(customer, request);
                    }
                } else {
                    LOG.warn(String.format("The customer id passed in '%s' was not a number", StringUtil.sanitize(customerId)));
                }
            }

            if (customerId == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No customer ID was found for the API request. In order to look up a customer for the request" +
                            " send a request parameter or request header for the '" + CUSTOMER_ID_ATTRIBUTE + "' attribute");
                }
            }
        }

        filterChain.doFilter(request, servletResponse);

    }

    private void setupCustomerForRuleProcessing(Customer customer, HttpServletRequest request) {
        // Setup customer for content rule processing
        @SuppressWarnings("unchecked")
        Map<String,Object> ruleMap = (Map<String, Object>) request.getAttribute(BLC_RULE_MAP_PARAM);
        if (ruleMap == null) {
            ruleMap = new HashMap<String,Object>();
        }
        ruleMap.put("customer", customer);
        request.setAttribute(BLC_RULE_MAP_PARAM, ruleMap);
    }

    @Override
    public int getOrder() {
        return 2000;
    }

    public String getCustomerIdAttributeName() {
        return CUSTOMER_ID_ATTRIBUTE;
    }

}
