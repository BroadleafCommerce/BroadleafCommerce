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
package org.broadleafcommerce.core.web.order.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderLockManager;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.core.Ordered;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
public class CartStateFilter extends GenericFilterBean implements  Ordered {

    protected static final Log LOG = LogFactory.getLog(CartStateFilter.class);

    @Resource(name = "blCartStateRequestProcessor")
    protected CartStateRequestProcessor cartStateProcessor;

    @Resource(name = "blOrderLockManager")
    protected OrderLockManager orderLockManager;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;

    protected List<String> excludedOrderLockRequestPatterns;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {        
        cartStateProcessor.process(new ServletWebRequest((HttpServletRequest) request, (HttpServletResponse) response));
        
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
            lockObject = orderLockManager.acquireLock(order);
    
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
            orderLockManager.releaseLock(lockObject);

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
        
        HttpServletRequest request = (HttpServletRequest) req;

        if (!((HttpServletRequest) request).getMethod().equalsIgnoreCase("post")) {
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

}
