/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.web.core.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;


/**
 * @author Phillip Verheyden
 * @see {@link CustomerStateFilter}
 */
@Component("blCustomerStateRequestProcessor")
public class CustomerStateRequestProcessor extends AbstractBroadleafWebRequestProcessor implements ApplicationEventPublisherAware {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    private ApplicationEventPublisher eventPublisher;

    protected static String customerRequestAttributeName = "customer";
    public static final String ANONYMOUS_CUSTOMER_SESSION_ATTRIBUTE_NAME="_blc_anonymousCustomer";
    private static final String LAST_PUBLISHED_EVENT_SESSION_ATTRIBUTED_NAME="_blc_lastPublishedEvent";

    @Override
    public void process(WebRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer customer = null;
        if ((authentication != null) && !(authentication instanceof AnonymousAuthenticationToken)) {
            String userName = authentication.getName();
            customer = (Customer) request.getAttribute(customerRequestAttributeName, WebRequest.SCOPE_REQUEST);
            if (userName != null && (customer == null || !userName.equals(customer.getUsername()))) {
                // can only get here if the authenticated user does not match the user in session
                customer = customerService.readCustomerByUsername(userName);
                if (logger.isDebugEnabled() && customer != null) {
                    logger.debug("Customer found by username " + userName);
                }
            }
            if (customer != null) {
                ApplicationEvent lastPublishedEvent = (ApplicationEvent) request.getAttribute(LAST_PUBLISHED_EVENT_SESSION_ATTRIBUTED_NAME, WebRequest.SCOPE_REQUEST);
                if (authentication instanceof RememberMeAuthenticationToken) {
                    // set transient property of customer
                    customer.setCookied(true);
                    boolean publishRememberMeEvent = true;
                    if (lastPublishedEvent != null && lastPublishedEvent instanceof CustomerAuthenticatedFromCookieEvent) {
                        CustomerAuthenticatedFromCookieEvent cookieEvent = (CustomerAuthenticatedFromCookieEvent) lastPublishedEvent;
                        if (userName.equals(cookieEvent.getCustomer().getUsername())) {
                            publishRememberMeEvent = false;
                        }
                    }
                    if (publishRememberMeEvent) {
                        CustomerAuthenticatedFromCookieEvent cookieEvent = new CustomerAuthenticatedFromCookieEvent(customer, this.getClass().getName()); 
                        eventPublisher.publishEvent(cookieEvent);
                        request.setAttribute(LAST_PUBLISHED_EVENT_SESSION_ATTRIBUTED_NAME, cookieEvent, WebRequest.SCOPE_REQUEST);
                    }                       
                } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
                    customer.setLoggedIn(true);
                    boolean publishLoggedInEvent = true;
                    if (lastPublishedEvent != null && lastPublishedEvent instanceof CustomerLoggedInEvent) {
                        CustomerLoggedInEvent loggedInEvent = (CustomerLoggedInEvent) lastPublishedEvent;
                        if (userName.equals(loggedInEvent.getCustomer().getUsername())) {
                            publishLoggedInEvent= false;
                        }
                    }
                    if (publishLoggedInEvent) {
                        CustomerLoggedInEvent loggedInEvent = new CustomerLoggedInEvent(customer, this.getClass().getName()); 
                        eventPublisher.publishEvent(loggedInEvent);
                        request.setAttribute(LAST_PUBLISHED_EVENT_SESSION_ATTRIBUTED_NAME, loggedInEvent, WebRequest.SCOPE_REQUEST);
                    }                        
                } else {
                    customer = resolveAuthenticatedCustomer(authentication);
                }
            }
        }

        if (customer == null) {
            // This is an anonymous customer.
            // TODO: Handle a custom cookie (different than remember me) that is just for anonymous users.  
            // This can be used to remember their cart from a previous visit.
            // Cookie logic probably needs to be configurable - with TCS as the exception.

            customer = resolveAnonymousCustomer(request);
        }
        request.setAttribute(customerRequestAttributeName, customer, WebRequest.SCOPE_REQUEST);

        // Setup customer for content rule processing
        Map<String,Object> ruleMap = (Map<String, Object>) request.getAttribute(BLC_RULE_MAP_PARAM, WebRequest.SCOPE_REQUEST);
        if (ruleMap == null) {
            ruleMap = new HashMap<String,Object>();
        }
        ruleMap.put("customer", customer);
        request.setAttribute(BLC_RULE_MAP_PARAM, ruleMap, WebRequest.SCOPE_REQUEST);
        
    }
    
    /**
     * Subclasses can extend to resolve other types of Authentication tokens
     * @param authentication
     * @return
     */
    public Customer resolveAuthenticatedCustomer(Authentication authentication) {
        return null;
    }

    /**
     * Implementors can subclass to change how anonymous customers are created.
     * @param request
     * @return
     */
    public Customer resolveAnonymousCustomer(WebRequest request) {
        Customer customer;
        customer = (Customer) request.getAttribute(getAnonymousCustomerAttributeName(), WebRequest.SCOPE_GLOBAL_SESSION);
        if (customer == null) { 
            customer = customerService.createNewCustomer();
            customer.setAnonymous(true);
            request.setAttribute(getAnonymousCustomerAttributeName(), customer, WebRequest.SCOPE_GLOBAL_SESSION);
        }
        return customer;
    }

    /**
     * Returns the session attribute to store the anonymous customer.
     * Some implementations may wish to have a different anonymous customer instance (and as a result a different cart). 
     * @return
     */
    public String getAnonymousCustomerAttributeName() {
        return ANONYMOUS_CUSTOMER_SESSION_ATTRIBUTE_NAME;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public static String getCustomerRequestAttributeName() {
        return customerRequestAttributeName;
    }

    public static void setCustomerRequestAttributeName(
            String customerRequestAttributeName) {
        CustomerStateRequestProcessor.customerRequestAttributeName = customerRequestAttributeName;
    }

}
