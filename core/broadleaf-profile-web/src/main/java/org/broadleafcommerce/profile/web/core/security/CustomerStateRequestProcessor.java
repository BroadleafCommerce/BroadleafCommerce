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
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.util.BLCRequestUtils;
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
    
    @Resource(name = "blCustomerMergeExtensionManager")
    protected CustomerMergeExtensionManager customerMergeExtensionManager;

    protected ApplicationEventPublisher eventPublisher;

    public static final String ANONYMOUS_CUSTOMER_SESSION_ATTRIBUTE_NAME = "_blc_anonymousCustomer";
    public static final String ANONYMOUS_CUSTOMER_ID_SESSION_ATTRIBUTE_NAME = "_blc_anonymousCustomerId";
    private static final String LAST_PUBLISHED_EVENT_CLASS_SESSION_ATTRIBUTE_NAME = "_blc_lastPublishedEventClass";
    private static final String LAST_PUBLISHED_EVENT_USERNAME_SESSION_ATTRIBUTE_NAME = "_blc_lastPublishedEventUsername";
    public static final String OVERRIDE_CUSTOMER_SESSION_ATTR_NAME = "_blc_overrideCustomerId";
    public static final String ANONYMOUS_CUSTOMER_MERGED_SESSION_ATTRIBUTE_NAME = "_blc_anonymousCustomerMerged";

    @Override
    public void process(WebRequest request) {
        Customer customer = null;
        Long overrideId = null;
        if (BLCRequestUtils.isOKtoUseSession(request)) {
            overrideId = (Long) request.getAttribute(OVERRIDE_CUSTOMER_SESSION_ATTR_NAME, WebRequest.SCOPE_GLOBAL_SESSION);
        }
        if (overrideId != null) {
            customer = customerService.readCustomerById(overrideId);
            if (customer != null && !customer.isRegistered() && !customer.isLoggedIn() && !customer.isCookied()) {
                customer.setAnonymous(true);
            }
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
                    String lastPublishedEventClass = (String) BLCRequestUtils.getSessionAttributeIfOk(request, LAST_PUBLISHED_EVENT_CLASS_SESSION_ATTRIBUTE_NAME);
                    String eventUsername = (String) BLCRequestUtils.getSessionAttributeIfOk(request, LAST_PUBLISHED_EVENT_USERNAME_SESSION_ATTRIBUTE_NAME);
                    
                    if (authentication instanceof RememberMeAuthenticationToken) {
                        // set transient property of customer
                        customer.setCookied(true);
                        boolean publishRememberMeEvent = true;
                        if (CustomerAuthenticatedFromCookieEvent.class.getName().equals(lastPublishedEventClass)) {
                            if (userName.equals(eventUsername)) {
                                publishRememberMeEvent = false;
                            }
                        }
                        if (publishRememberMeEvent) {
                            CustomerAuthenticatedFromCookieEvent cookieEvent = new CustomerAuthenticatedFromCookieEvent(customer, this.getClass().getName());
                            publishEvent(cookieEvent, request, CustomerAuthenticatedFromCookieEvent.class.getName(), userName);
                        }
                    } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
                        customer.setLoggedIn(true);
                        boolean publishLoggedInEvent = true;
                        if (CustomerLoggedInEvent.class.getName().equals(lastPublishedEventClass)) {
                            if (userName.equals(eventUsername)) {
                                publishLoggedInEvent = false;
                            }
                        }
                        if (publishLoggedInEvent) {
                            CustomerLoggedInEvent loggedInEvent = new CustomerLoggedInEvent(customer, this.getClass().getName()); 
                            publishEvent(loggedInEvent, request, CustomerLoggedInEvent.class.getName(), userName);
                        }
                    } else {
                        customer = resolveAuthenticatedCustomer(authentication);
                    }
                }
            }
        }

        if (customer == null) {
            // This is an anonymous customer.
            // TODO: Handle a custom cookie (different than remember me) that is just for anonymous users.  
            // This can be used to remember their cart from a previous visit.
            // Cookie logic probably needs to be configurable - with TCS as the exception.

            customer = resolveAnonymousCustomer(request);
        } else {
            //Does this customer need to have an anonymous customer's data merged into it?
            customer = mergeCustomerIfRequired(request, customer);
        }
        CustomerState.setCustomer(customer);

        // Setup customer for content rule processing
        @SuppressWarnings("unchecked")
        Map<String,Object> ruleMap = (Map<String, Object>) request.getAttribute(BLC_RULE_MAP_PARAM, WebRequest.SCOPE_REQUEST);
        if (ruleMap == null) {
            ruleMap = new HashMap<String,Object>();
        }
        ruleMap.put("customer", customer);
        request.setAttribute(BLC_RULE_MAP_PARAM, ruleMap, WebRequest.SCOPE_REQUEST);
        
    }
    
    protected void publishEvent(ApplicationEvent event, WebRequest request, String eventClass, String username) {
        eventPublisher.publishEvent(event);
        BLCRequestUtils.setSessionAttributeIfOk(request, LAST_PUBLISHED_EVENT_CLASS_SESSION_ATTRIBUTE_NAME, eventClass);
        BLCRequestUtils.setSessionAttributeIfOk(request, LAST_PUBLISHED_EVENT_USERNAME_SESSION_ATTRIBUTE_NAME, username);
    }
    
    /**
     * Allows the merging of anonymous customer data and / or session data, to the logged in customer, if required. 
     * This is written to only require it to happen once.
     * @param request
     * @param customer
     * @return
     */
    protected Customer mergeCustomerIfRequired(WebRequest request, Customer customer) {
        if (BLCRequestUtils.isOKtoUseSession(request)) {
            //Don't call this if it has already been called
            if (request.getAttribute(getAnonymousCustomerMergedSessionAttributeName(), WebRequest.SCOPE_GLOBAL_SESSION) == null) {
                //Set this so we don't do this every time.
                request.setAttribute(getAnonymousCustomerMergedSessionAttributeName(), Boolean.TRUE, WebRequest.SCOPE_GLOBAL_SESSION);

                Customer anonymousCustomer = getAnonymousCustomer(request);
                customer = copyAnonymousCustomerInfoToCustomer(request, anonymousCustomer, customer);
            }
        }
        return customer;
    }

    /**
     * This allows the customer object to be augmented by information that may have been stored on the 
     * anonymous customer or session.  After login, a new instance of customer is created that is different from the 
     * anonymous customer.  In many cases, there are reasons that the anonymous customer may have had data associated with 
     * them that is required on the new customer.  For example, customer attributes, promotions, promo codes, etc. 
     * may have been associated with the anonymous customer, and we want them to be copied to this customer.  
     * The default implementation does not copy data. It simply provides a hook for implementors to extend / implement 
     * this method. You should consider security when copying data from one customer to another.
     * 
     * @param request
     * @param anonymous
     * @param customer
     * @return
     */
    protected Customer copyAnonymousCustomerInfoToCustomer(WebRequest request, Customer anonymous, Customer customer) {
        if (customerMergeExtensionManager != null) {
            ExtensionResultHolder<Customer> resultHolder = new ExtensionResultHolder<Customer>();
            resultHolder.setResult(customer);
            customerMergeExtensionManager.getProxy().merge(resultHolder, request, anonymous);
            
            if (resultHolder.getThrowable() != null) {
                if (resultHolder.getThrowable() instanceof RuntimeException) {
                    throw ((RuntimeException) resultHolder.getThrowable());
                } else {
                    throw new RuntimeException("An unexpected error occured merging the anonymous customer",
                            resultHolder.getThrowable());
                }
            }
            
            return customerService.saveCustomer(resultHolder.getResult());
        }
        return customer;
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
     * <p>Implementors can subclass to change how anonymous customers are created. Note that this method is intended to actually create the anonymous
     * customer if one does not exist. If you are looking to just get the current anonymous customer (if it exists) then instead use the
     * {@link #getAnonymousCustomer(WebRequest)} method.<p>
     * 
     * <p>The intended behavior of this method is as follows:</p>
     * 
     * <ul>
     *  <li>Look for a {@link Customer} on the session</li>
     *  <ul>
     *      <li>If a customer is found in session, keep using the session-based customer</li>
     *      <li>If a customer is not found in session</li>
     *      <ul>
     *          <li>Look for a customer ID in session</li>
     *          <li>If a customer ID is found in session:</li>
     *          <ul><li>Look up the customer in the database</ul></li>
     *      </ul>
     *      <li>If no there is no customer ID in session (and thus no {@link Customer})</li>
     *      <ol>
     *          <li>Create a new customer</li>
     *          <li>Put the newly-created {@link Customer} in session</li>
     *      </ol>
     *  </ul>
     * </ul>
     * 
     * @param request
     * @return
     * @see {@link #getAnonymousCustomer(WebRequest)}
     * @see {@link #getAnonymousCustomerAttributeName()}
     * @see {@link #getAnonymousCustomerIdAttributeName()}
     */
    public Customer resolveAnonymousCustomer(WebRequest request) {
        Customer customer;
        customer = getAnonymousCustomer(request);
        
        //If there is no Customer object in session, AND no customer id in session, create a new customer
        //and store the entire customer in session (don't persist to DB just yet)
        if (customer == null) {
            customer = customerService.createNewCustomer();
            if (BLCRequestUtils.isOKtoUseSession(request)) {
                request.setAttribute(getAnonymousCustomerSessionAttributeName(), customer, WebRequest.SCOPE_GLOBAL_SESSION);
            }
        }
        customer.setAnonymous(true);

        return customer;
    }
    
    /**
     * Returns the anonymous customer that was saved in session. This first checks for a full customer in session (meaning
     * that the customer has not already been persisted) and returns that. If there is no full customer in session (and
     * there is instead just an anonymous customer ID) then this will look up the customer from the database using that and
     * return it.
     * 
     * @param request the current request
     * @return the anonymous customer in session or null if there is no anonymous customer represented in session
     * @see {@link #getAnonymousCustomerSessionAttributeName()} 
     * @see {@link #getAnonymousCustomerIdSessionAttributeName()}
     */
    public Customer getAnonymousCustomer(WebRequest request) {
        if (BLCRequestUtils.isOKtoUseSession(request)) {
            Customer anonymousCustomer = (Customer) request.getAttribute(getAnonymousCustomerSessionAttributeName(),
                    WebRequest.SCOPE_GLOBAL_SESSION);
            if (anonymousCustomer == null) {
                //Customer is not in session, see if we have just a customer ID in session (the anonymous customer might have
                //already been persisted)
                Long customerId = (Long) request.getAttribute(getAnonymousCustomerIdSessionAttributeName(), WebRequest.SCOPE_GLOBAL_SESSION);
                if (customerId != null) {
                    //we have a customer ID in session, look up the customer from the database to ensure we have an up-to-date
                    //customer to store in CustomerState
                    anonymousCustomer = customerService.readCustomerById(customerId);
                }
            }
            return anonymousCustomer;
        }
        return null;
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
     * <p>Returns the session attribute to store the anonymous customer ID. This session attribute should be used to track
     * anonymous customers that have not registered but have state in the database. When users first visit the Broadleaf
     * site, a new {@link Customer} is instantiated but is <b>only saved in session</b> and not persisted to the database. However,
     * once that user adds something to the cart, that {@link Customer} is now saved in the database and it no longer makes
     * sense to pull back a full {@link Customer} object from session, as any session-based {@link Customer} will be out of
     * date in regards to Hibernate (specifically with lists).</p>
     * 
     * <p>So, once Broadleaf detects that the session-based {@link Customer} has been persisted, it should remove the session-based
     * {@link Customer} and then utilize just the customer ID from session.</p>
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
    
    /**
     * This is the name of a session attribute that holds whether or not the anonymous customer has been merged into 
     * the logged in customer.  This is useful for tracking as often there is an anonymous customer that has customer 
     * attributes or other data that is saved on the customer in the database or in transient properties.  It is often 
     * beneficial, after logging in, to copy certain properties to the logged in customer.
     * @return
     */
    public static String getAnonymousCustomerMergedSessionAttributeName() {
        return ANONYMOUS_CUSTOMER_MERGED_SESSION_ATTRIBUTE_NAME;
    }
}
