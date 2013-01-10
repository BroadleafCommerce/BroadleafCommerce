package org.broadleafcommerce.profile.web.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component("blCustomerStateFilter")
/**
 * <p>
 * This filter should be configured after the RememberMe listener from Spring Security.
 * Retrieves the Broadleaf Customer based using the authenticated user OR creates an Anonymous customer and stores them
 * in the session.  Calls Customer.setCookied(true) if the authentication token is an instance of
 * {@link org.springframework.security.providers.rememberme.RememberMeAuthenticationToken).   Calls Customer.setLoggedIn(true) if
 * the authentication token is an instance of {@link org.springframework.security.providers.UsernamePasswordAuthenticationToken}
 * </p>
 *
 * @author bpolster
 */
public class CustomerStateFilter extends GenericFilterBean implements ApplicationEventPublisherAware, Ordered {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    private ApplicationEventPublisher eventPublisher;

    private static String customerRequestAttributeName = "customer";
    private static final String ANONYMOUS_CUSTOMER_SESSION_ATTRIBUTE_NAME="_blc_anonymousCustomer";
    private static final String LAST_PUBLISHED_EVENT_SESSION_ATTRIBUTED_NAME="_blc_lastPublishedEvent";

    public void doFilter(ServletRequest baseRequest, ServletResponse baseResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) baseRequest;
        HttpServletResponse response = (HttpServletResponse) baseResponse;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer customer = null;
        if (authentication != null) {
            String userName = request.getUserPrincipal().getName();
            customer = (Customer) request.getAttribute(customerRequestAttributeName);
            if (userName != null && (customer == null || !userName.equals(customer.getUsername()))) {
                // can only get here if the authenticated user does not match the user in session
                customer = customerService.readCustomerByUsername(userName);
                if (logger.isDebugEnabled() && customer != null) {
                    logger.debug("Customer found by username " + userName);
                }
            }
            if (customer != null) {
                ApplicationEvent lastPublishedEvent = (ApplicationEvent) request.getSession(true).getAttribute(LAST_PUBLISHED_EVENT_SESSION_ATTRIBUTED_NAME);
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
                        request.getSession().setAttribute(LAST_PUBLISHED_EVENT_SESSION_ATTRIBUTED_NAME, cookieEvent);
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
                        request.getSession().setAttribute(LAST_PUBLISHED_EVENT_SESSION_ATTRIBUTED_NAME, loggedInEvent);
                    }                        
                } else {
                    customer = null;
                }
                    
            }
        }

        if (customer == null) {
            // This is an anonymous customer.
            // TODO: Handle a custom cookie (different than remember me) that is just for anonymous users.  
            // This can be used to remember their cart from a previous visit.
            // Cookie logic probably needs to be configurable - with TCS as the exception.

            customer = (Customer) request.getSession(true).getAttribute(ANONYMOUS_CUSTOMER_SESSION_ATTRIBUTE_NAME);
            if (customer == null) {
                // TODO: Refactor to use entityConfigMgr directly and remove this method from customerService
                customer = customerService.createCustomerFromId(null);
                customer.setAnonymous(true);
                request.getSession().setAttribute(ANONYMOUS_CUSTOMER_SESSION_ATTRIBUTE_NAME, customer);
            }
        }
        request.setAttribute(customerRequestAttributeName, customer);

        chain.doFilter(request, response);
    }

    public int getOrder() {
        //FilterChainOrder has been dropped from Spring Security 3
        //return FilterChainOrder.REMEMBER_ME_FILTER+1;
        return 1501;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public static String getCustomerRequestAttributeName() {
        return customerRequestAttributeName;
    }

    public static void setCustomerRequestAttributeName(
            String customerRequestAttributeName) {
        CustomerStateFilter.customerRequestAttributeName = customerRequestAttributeName;
    }
}