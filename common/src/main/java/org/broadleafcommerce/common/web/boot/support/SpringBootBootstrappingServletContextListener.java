/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.web.boot.support;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.util.ClassUtils;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

/**
 * <p>
 * Bootstraps a Spring Boot application using a ServletContextListener rather than the default of using a ServletContextInitializer.
 * The use case here is when you absolutely have to use a web.xml and cannot rely on classpath scanning for a ServletContextInitializer
 * 
 * <p>
 * This is designed to work in conjunction with the {@link BroadleafBootServletContextInitializer} (although not requried) which serves
 * as a drop-in replacement for {@link SpringBootServletInitializer}.
 * 
 * <p>
 * Given an application that looks like this:
 * 
 * <pre>
 * package com.mycompany
 * 
 * {@literal @}SpringBootApplication
 * public class MyApplication extends BroadleafBootServletContextInitializer {
 * 
 * }
 * </pre>
 * 
 * <p>
 * A web.xml should contain the following listener configuration:
 * 
 * <pre>
 * {@code
 * <context-param>
 *   <param-name>listenerContextInitializerClass</param-name>
 *  <param-value>com.mycompany.MyApplication</param-value>
 * </context-param>
 * <listener>
 *   <listener-class>org.broadleafcommerce.common.web.boot.support.SpringBootBootstrappingServletContextListener</listener-class>
 * </listener>
 * }
 * </pre>
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see BroadleafBootServletContextInitializer
 */
public class SpringBootBootstrappingServletContextListener implements ServletContextListener {

    public static final String APPLICATION_CLASS = "listenerContextInitializerClass";
    
    protected ContextLoaderListener delegateListener;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            ServletContext servletContext = event.getServletContext();
            Class<WebApplicationInitializer> initializerClass = getInitializerClass(servletContext);
            WebApplicationInitializer initializer = createInitializer(initializerClass);
            initializer.onStartup(servletContext);
            
            WebApplicationContext rootContext = (WebApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            if (rootContext != null) {
                delegateListener = new ContextLoaderListener(rootContext) {
                    @Override
                    public void contextInitialized(ServletContextEvent event) {
                        // initionally umimplemented since it has already initialized
                    }
                };
            }
        } catch (ServletException | ClassNotFoundException | LinkageError | PrivilegedActionException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (delegateListener != null) {
            delegateListener.contextDestroyed(event);
        }
    }
    
    @SuppressWarnings("unchecked")
    protected Class<WebApplicationInitializer> getInitializerClass(ServletContext ctx) throws ClassNotFoundException, LinkageError {
        String clazz = ctx.getInitParameter(APPLICATION_CLASS);
        if (StringUtils.isBlank(clazz)) {
            throw new IllegalStateException(String.format("A %s context-param must be defined that points to your main @SpringBootApplicatino class", APPLICATION_CLASS));
        }
        Class<?> initializerClass = ClassUtils.forName(clazz, this.getClass().getClassLoader());
        if (!WebApplicationInitializer.class.isAssignableFrom(initializerClass)) {
            throw new IllegalStateException(String.format("The %s context-param must be an instance of ServletContextInitializer. Consider extending from %s", APPLICATION_CLASS, BroadleafBootServletContextInitializer.class.getName()));
        }
        
        return (Class<WebApplicationInitializer>) initializerClass;
    }
    
    protected WebApplicationInitializer createInitializer(final Class<WebApplicationInitializer> initializerClass) throws PrivilegedActionException {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(new PrivilegedAction<WebApplicationInitializer>() {
                @Override
                public WebApplicationInitializer run() {
                    return BeanUtils.instantiateClass(initializerClass);
                }
            }, AccessController.getContext());
        } else {
            return BeanUtils.instantiate(initializerClass);
        }
    }
    
}
