package org.broadleafcommerce.email.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.email.service.EmailTrackingManager;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.web.CustomerState;

/**
 * 
 * @author jfischer
 *
 */
public class EmailClickTrackingFilter implements Filter {

    private EmailTrackingManager emailTrackingManager;

    @Resource
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
            while(names.hasMoreElements()) {
                String name = (String) names.nextElement();
                parameterMap.put(name, request.getParameter(name));
            }
            Customer customer = customerState.getCustomer((HttpServletRequest)  request);
            Map<String, String> extraValues = new HashMap<String, String>();
            extraValues.put("requestUri", ((HttpServletRequest) request).getRequestURI());
            emailTrackingManager.recordClick( Long.valueOf(emailId) , parameterMap, customer, extraValues);
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
