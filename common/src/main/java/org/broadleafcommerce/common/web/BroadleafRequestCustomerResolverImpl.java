/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * By default, we'll resolve the customer from the "customer" attribute on the request.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Service("blRequestCustomerResolver")
public class BroadleafRequestCustomerResolverImpl implements ApplicationContextAware, BroadleafRequestCustomerResolver {
    
    private static ApplicationContext applicationContext;

    protected static String customerRequestAttributeName = "customer";

    @Override
    public Object getCustomer(HttpServletRequest request) {
        return getCustomer(new ServletWebRequest(request));
    }
    
    @Override
    public Object getCustomer() {
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        return getCustomer(request);
    }

    @Override
    public Object getCustomer(WebRequest request) {
        return request.getAttribute(getCustomerRequestAttributeName(), WebRequest.SCOPE_REQUEST);
    }

    @Override
    public void setCustomer(Object customer) {
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        request.setAttribute(getCustomerRequestAttributeName(), customer, WebRequest.SCOPE_REQUEST);
    }

    @Override
    public String getCustomerRequestAttributeName() {
        return customerRequestAttributeName;
    }

    @Override
    public void setCustomerRequestAttributeName(String customerRequestAttributeName) {
        BroadleafRequestCustomerResolverImpl.customerRequestAttributeName = customerRequestAttributeName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BroadleafRequestCustomerResolverImpl.applicationContext = applicationContext;
    }
    
    public static BroadleafRequestCustomerResolver getRequestCustomerResolver() {
        return (BroadleafRequestCustomerResolver) applicationContext.getBean("blRequestCustomerResolver");
    }
    
}