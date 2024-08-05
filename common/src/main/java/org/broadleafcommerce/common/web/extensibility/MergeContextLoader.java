/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.extensibility;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.springframework.beans.BeansException;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Performs the actual initialization work for the rootId application context.
 * Called by {@link MergeContextLoaderListener}.
 *
 * <p>Processes a {@link #CONFIG_LOCATION_PARAM "contextConfigLocation"}
 * context-param and passes its value to the context instance, parsing it into
 * potentially multiple file paths which can be separated by any number of
 * commas and spaces, e.g. "WEB-INF/applicationContext1.xml,
 * WEB-INF/applicationContext2.xml". Ant-style path patterns are supported as well,
 * e.g. "WEB-INF/*Context.xml,WEB-INF/spring*.xml" or "WEB-INF/&#42;&#42;/*Context.xml".
 * If not explicitly specified, the context implementation is supposed to use a
 * default location (with XmlWebApplicationContext: "/WEB-INF/applicationContext.xml").
 *
 * <p>Note: In case of multiple config locations, later bean definitions will
 * override ones defined in previously loaded files, at least when using one of
 * Spring's default ApplicationContext implementations. This can be leveraged
 * to deliberately override certain bean definitions via an extra XML file.
 *
 * <p>Above and beyond loading the rootId application context, this class
 * can optionally load or obtain and hook up a shared parent context to
 * the rootId application context. See the
 * {@link #loadParentContext(ServletContext)} method for more information.
 *
 * <p>Additionally, Processes a {@link #PATCH_LOCATION_PARAM "patchConfigLocation"}
 * context-param and passes its value to the context instance, parsing it into
 * potentially multiple file paths which can be separated by any number of
 * commas and spaces, e.g. "WEB-INF/patch1.xml,
 * WEB-INF/patch2.xml". Ant-style path patterns are supported as well,
 * e.g. "WEB-INF/*Patch.xml,WEB-INF/spring*.xml" or "WEB-INF/&#42;&#42;/*Patch.xml".
 * The patch configuration files are merged into the above config
 * {@link org.broadleafcommerce.common.extensibility.context.merge.MergeXmlConfigResource}.
 *
 * @author Jeff Fischer
 */
public class MergeContextLoader extends ContextLoaderListener {

    /**
     * Name of servlet context parameter (i.e., "<code>patchConfigLocation</code>")
     * that can specify the config location for the rootId context.
     */
    public static final String PATCH_LOCATION_PARAM = "patchConfigLocation";
    
    /**
     * Name of a bean to hook before Spring shutdown for this
     * context commences.
     */
    public static final String SHUTDOWN_HOOK_BEAN = "shutdownHookBean";
    
    /**
     * Name of method to call on the shutdown hook bean before
     * Spring shutdown for this context commences
     */
    public static final String SHUTDOWN_HOOK_METHOD = "shutdownHookMethod";

    /**
     * Instantiate the rootId WebApplicationContext for this loader, either the
     * default context class or a custom context class if specified.
     * <p>This implementation expects custom contexts to implement the
     * {@link ConfigurableWebApplicationContext} interface.
     * Can be overridden in subclasses.
     * <p>In addition, {@link #customizeContext} gets called prior to refreshing the
     * context, allowing subclasses to perform custom modifications to the context.
     * @param servletContext current servlet context
     * @return the rootId WebApplicationContext
     * @throws BeansException if the context couldn't be initialized
     * @see ConfigurableWebApplicationContext
     */
    @Override
    protected WebApplicationContext createWebApplicationContext(ServletContext servletContext) throws BeansException {
        MergeXmlWebApplicationContext wac = new MergeXmlWebApplicationContext();
        wac.setServletContext(servletContext);
        wac.setConfigLocation(servletContext.getInitParameter(ContextLoader.CONFIG_LOCATION_PARAM));
        wac.setPatchLocation(servletContext.getInitParameter(PATCH_LOCATION_PARAM));
        wac.setShutdownBean(servletContext.getInitParameter(SHUTDOWN_HOOK_BEAN));
        wac.setShutdownMethod(servletContext.getInitParameter(SHUTDOWN_HOOK_METHOD));
        customizeContext(servletContext, wac);
        //NOTE: in Spring 3.1, refresh gets called automatically. All that is required is to return the context back to Spring
        //wac.refresh();

        return wac;
    }
    

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ThreadLocalManager.remove();
    }

}
