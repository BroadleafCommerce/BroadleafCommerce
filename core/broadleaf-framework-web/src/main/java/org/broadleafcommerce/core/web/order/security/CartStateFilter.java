/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.order.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderLockManager;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.order.security.exception.OrderLockAcquisitionFailureException;
import org.springframework.core.Ordered;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * This filter should be configured after the BroadleafCommerce CustomerStateFilter listener from Spring Security.
 * Retrieves the cart for the current BroadleafCommerce Customer based using the authenticated user OR creates an empty non-modifiable cart and
 * stores it in the request.
 * </p>
 * 
 * <p>
 * This filter is also responsible for establishing a session-wide lock for operations that require a lock, indicated
 * by {@link #requestRequiresLock(ServletRequest)}. By default, this is configured for all POST requests. Requests that
 * are marked as requiring a lock will execute strictly serially as defined by the configured {@link OrderLockManager}.
 * </p>
 *
 * @author bpolster
 * @author Andre Azzolini (apazzolini)
 */
@Component("blCartStateFilter")
public class CartStateFilter extends OncePerRequestFilter implements Ordered {

    protected static final Log LOG = LogFactory.getLog(CartStateFilter.class);

    @Resource(name = "blCartStateRequestProcessor")
    protected CartStateRequestProcessor cartStateProcessor;

    @Resource(name = "blOrderLockManager")
    protected OrderLockManager orderLockManager;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;

    protected List<String> excludedOrderLockRequestPatterns;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {        
        cartStateProcessor.process(new ServletWebRequest(request, response));
        
        if (!requestRequiresLock(request)) {
            chain.doFilter(request, response);
            return;
        }

        Order order = CartState.getCart();

        if (LOG.isTraceEnabled()) {
            LOG.trace("Thread[" + Thread.currentThread().getId() + "] attempting to lock order[" + order.getId() + "]");
        }

        Object lockObject = null;
        try {
            if (lockObject == null) {
                if (getErrorInsteadOfQueue()) {
                    lockObject = orderLockManager.acquireLockIfAvailable(order);
                    if (lockObject == null) {
                        // We weren't able to acquire the lock immediately because some other thread has it. Because the
                        // order.lock.errorInsteadOfQueue property was set to true, we're going to throw an exception now.
                        throw new OrderLockAcquisitionFailureException("Thread[" + Thread.currentThread().getId() +
                                "] could not acquire lock for order[" + order.getId() + "]");
                    }
                } else {
                    lockObject = orderLockManager.acquireLock(order);
                }
            }
    
            if (LOG.isTraceEnabled()) {
                LOG.trace("Thread[" + Thread.currentThread().getId() + "] grabbed lock for order[" + order.getId() + "]");
            }

            // When we have a hold of the lock for the order, we want to reload the order from the database.
            // This is because a different thread could have modified the order in between the time we initially
            // read it for this thread and now, resulting in the order being stale. Additionally, we want to make
            // sure we detach the order from the EntityManager and forcefully reload the order.
            CartState.setCart(orderService.reloadOrder(order));

            chain.doFilter(request, response);
        } finally {
            if (lockObject != null) {
                orderLockManager.releaseLock(lockObject);
            }

            if (LOG.isTraceEnabled()) {
                LOG.trace("Thread[" + Thread.currentThread().getId() + "] released lock for order[" + order.getId() +"]");
            }
        }
    }

    /**
     * By default, all POST requests that are not matched by the {@link #getExcludedOrderLockRequestPatterns()} list
     * (using the {@link AntPathRequestMatcher}) will be marked as requiring a lock on the Order.
     * 
     * @param req
     * @return whether or not the current request requires a lock on the order
     */
    protected boolean requestRequiresLock(ServletRequest req) {
        if (!(req instanceof HttpServletRequest)) {
               return false;
        }
        
        if (!orderLockManager.isActive()) {
            return false;
        }

        HttpServletRequest request = (HttpServletRequest) req;

        if (!request.getMethod().equalsIgnoreCase("post")) {
            return false;
        }
        
        if (excludedOrderLockRequestPatterns != null && excludedOrderLockRequestPatterns.size() > 0) {
            for (String pattern : excludedOrderLockRequestPatterns) {
                RequestMatcher matcher = new AntPathRequestMatcher(pattern);
                if (matcher.matches(request)){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int getOrder() {
        //FilterChainOrder has been dropped from Spring Security 3
        //return FilterChainOrder.REMEMBER_ME_FILTER+1;
        return 1502;
    }

    public List<String> getExcludedOrderLockRequestPatterns() {
        return excludedOrderLockRequestPatterns;
    }

    /**
     * This allows you to declaratively set a list of excluded Request Patterns
     *
     * <bean id="blCartStateFilter" class="org.broadleafcommerce.core.web.order.security.CartStateFilter">
     *     <property name="excludedOrderLockRequestPatterns">
     *         <list>
     *             <value>/exclude-me/**</value>
     *         </list>
     *     </property>
     * </bean>
     *
     **/
    public void setExcludedOrderLockRequestPatterns(List<String> excludedOrderLockRequestPatterns) {
        this.excludedOrderLockRequestPatterns = excludedOrderLockRequestPatterns;
    }

    protected boolean getErrorInsteadOfQueue() {
        return BLCSystemProperty.resolveBooleanSystemProperty("order.lock.errorInsteadOfQueue");
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

}
