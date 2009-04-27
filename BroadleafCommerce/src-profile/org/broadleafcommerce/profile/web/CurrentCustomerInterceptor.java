package org.broadleafcommerce.profile.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CurrentCustomerInterceptor extends HandlerInterceptorAdapter {

    private final static String CUSTOMER_REQUEST_ATTR_NAME = "customer";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
        CustomerState customerState = (CustomerState) applicationContext.getBean("customerState");
        Customer requestCustomer;
        checkSession: {
            Customer sessionCustomer = customerState.getCustomer(request);
            if (sessionCustomer != null) {
                requestCustomer = sessionCustomer;
                break checkSession;
            }
            String cookieCustomerIdVal = CookieUtils.getCookieValue(request, CookieUtils.CUSTOMER_COOKIE_NAME);
            Long cookieCustomerId = null;
            if (cookieCustomerIdVal != null) {
                cookieCustomerId = new Long(cookieCustomerIdVal);
            }

            CustomerService customerService = (CustomerService) applicationContext.getBean("customerService");
            if (cookieCustomerId != null) {
                Customer persistedCookieCustomer = customerService.readCustomerById(cookieCustomerId);
                if (persistedCookieCustomer != null) {
                    customerState.setCustomer(persistedCookieCustomer, request);
                    requestCustomer = persistedCookieCustomer;
                    break checkSession;
                } else {
                    Customer anonymousCookieCustomer = customerService.createCustomerFromId(cookieCustomerId);
                    customerState.setCustomer(anonymousCookieCustomer, request);
                    requestCustomer = anonymousCookieCustomer;
                    break checkSession;
                }
            }

            // if no customer in session or cookie, create a new one
            Customer firstTimeCustomer = customerService.createCustomerFromId(null);
            CookieUtils.setCookieValue(response, CookieUtils.CUSTOMER_COOKIE_NAME, firstTimeCustomer.getId() + "","/",604800);
            customerState.setCustomer(firstTimeCustomer, request);
            requestCustomer = firstTimeCustomer;
        }
        request.setAttribute(CUSTOMER_REQUEST_ATTR_NAME, requestCustomer);
        return true;
    }
}
