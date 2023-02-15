/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

package org.broadleafcommerce.site.common.web.filter;

import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.web.filter.AbstractIgnorableFilter;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component("blEstablishSessionFilter")
public class EstablishSessionFilter extends AbstractIgnorableFilter {

    @Override
    public void doFilterUnlessIgnored(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (HttpServletRequest.class.isAssignableFrom(request.getClass())) {
            ((HttpServletRequest) request).getSession();
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean isIgnored(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        boolean response = super.isIgnored(httpServletRequest, httpServletResponse);
        if (!response) {
            //ignore for stateless requests (i.e. rest api)
            response = !BLCRequestUtils.isOKtoUseSession(new ServletWebRequest(httpServletRequest));
        }
        return response;
    }

    @Override
    public int getOrder() {
        return FilterOrdered.PRE_SECURITY_LOW;
    }
}
