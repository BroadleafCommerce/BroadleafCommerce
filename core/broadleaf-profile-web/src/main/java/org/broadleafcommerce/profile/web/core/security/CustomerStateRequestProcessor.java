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
package org.broadleafcommerce.profile.web.core.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.BroadleafRequestCustomerResolverImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.broadleafcommerce.profile.web.core.CustomerStateRefresher;
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
    
    protected ApplicationEventPublisher eventPublisher;

    public static final String ANONYMOUS_CUSTOMER_SESSION_ATTRIBUTE_NAME = "_blc_anonymousCustomer";
    public static final String ANONYMOUS_CUSTOMER_ID_SESSION_ATTRIBUTE_NAME = "_blc_anonymousCustomerId";
    private static final String LAST_PUBLISHED_EVENT_SESSION_ATTRIBUTED_NAME = "_blc_lastPublishedEvent";

    @Override
    public void process(WebRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer customer = null;
        if ((authentication != null) && !(authentication instanceof AnonymousAuthenticationToken)) {
            String userName = authentication.getName();
            customer = (Customer) BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().getCustomer(request);
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
        CustomerState.setCustomer(customer);

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
     * Implementors can subclass to change how anonymous customers are created. The intended behavior is as follows:
     * 
     * 1. Look for a {@link Customer} on the session
     *   - If a customer is found in session, keep using the session-based customer
     *   - If a customer is not found in session
     *       - Look for a customer ID in session
     *            - If a customer ID is found in session:
     *                  Look up the customer in the database
     *       - If no there is no customer ID in session (and thus no {@link Customer})
     *           1. Create a new customer
     *           2. Put the newly-created {@link Customer} in session
     * 
     * @param request
     * @return
     * @see {@link #getAnonymousCustomerAttributeName()}
     * @see {@link #getAnonymousCustomerIdAttributeName()}
     */
    public Customer resolveAnonymousCustomer(WebRequest request) {
        Customer customer;
        customer = (Customer) request.getAttribute(getAnonymousCustomerSessionAttributeName(), WebRequest.SCOPE_GLOBAL_SESSION);
        if (customer == null) {
            //Customer is not in session, see if we have just a customer ID in session (the anonymous customer might have
            //already been persisted)
            Long customerId = (Long) request.getAttribute(getAnonymousCustomerIdSessionAttributeName(), WebRequest.SCOPE_GLOBAL_SESSION);
            if (customerId != null) {
                //we have a customer ID in session, look up the customer from the database to ensure we have an up-to-date
                //customer to store in CustomerState
                customer = customerService.readCustomerById(customerId);
            }

            //If there is no Customer object in session, AND no customer id in session, create a new customer
            //and store the entire customer in session (don't persist to DB just yet)
            if (customer == null) {
                customer = customerService.createNewCustomer();
                request.setAttribute(getAnonymousCustomerSessionAttributeName(), customer, WebRequest.SCOPE_GLOBAL_SESSION);
            }
        }

        customer.setAnonymous(true);

        return customer;
    }
    
    /**
     * Returns the session attribute to store the anonymous customer.
     * Some implementations may wish to have a different anonymous customer instance (and as a result a different cart). 
     * 
     * The entire Customer should be stored in session ONLY if that Customer has not already been persisted to the database.
     * Once it has been persisted (like once the user has added something to the cart) then {@link #getAnonymousCustomerIdAttributeName()}
     * should be used instead.
     * 
     * @return the session attribute for an anonymous {@link Customer} that has not been persisted to the database yet 
     */
    public static String getAnonymousCustomerSessionAttributeName() {
        return ANONYMOUS_CUSTOMER_SESSION_ATTRIBUTE_NAME;
    }

    /**
     * Returns the session attribute to store the anonymous customer ID. This session attribute should be used to track
     * anonymous customers that have not registered but have state in the database. When users first visit the Broadleaf
     * site, a new {@link Customer} is instantiated but is <b>only saved in session</b> and not persisted to the database. However,
     * once that user adds something to the cart, that {@link Customer} is now saved in the database and it no longer makes
     * sense to pull back a full {@link Customer} object from session, as any session-based {@link Customer} will be out of
     * date in regards to Hibernate (specifically with lists).
     * 
     * So, once Broadleaf detects that the session-based {@link Customer} has been persisted, it should remove the session-based
     * {@link Customer} and then utilize just the customer ID from session.
     * 
     * @see {@link CustomerStateRefresher}
     */
    public static String getAnonymousCustomerIdSessionAttributeName() {
        return ANONYMOUS_CUSTOMER_ID_SESSION_ATTRIBUTE_NAME;
    }
    
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * The request-scoped attribute that should store the {@link Customer}.
     * 
     * <pre>
     * Customer customer = (Customer) request.getAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName());
     * //this is equivalent to the above invocation
     * Customer customer = CustomerState.getCustomer();
     * </pre>
     * @return
     * @see {@link CustomerState}
     */
    public static String getCustomerRequestAttributeName() {
        return BroadleafRequestCustomerResolverImpl.getRequestCustomerResolver().getCustomerRequestAttributeName();
    }
    
}
