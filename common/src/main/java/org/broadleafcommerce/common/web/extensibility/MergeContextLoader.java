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
package org.broadleafcommerce.common.web.extensibility;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

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
public class MergeContextLoader extends ContextLoader {

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
     * @param parent the parent ApplicationContext to use, or <code>null</code> if none
     * @return the rootId WebApplicationContext
     * @throws BeansException if the context couldn't be initialized
     * @see ConfigurableWebApplicationContext
     */
    @Deprecated
    protected WebApplicationContext createWebApplicationContext(ServletContext servletContext, ApplicationContext parent) throws BeansException {
        MergeXmlWebApplicationContext wac = new MergeXmlWebApplicationContext();
        wac.setParent(parent);
        wac.setServletContext(servletContext);
        wac.setConfigLocation(servletContext.getInitParameter(ContextLoader.CONFIG_LOCATION_PARAM));
        wac.setPatchLocation(servletContext.getInitParameter(PATCH_LOCATION_PARAM));
        wac.setShutdownBean(servletContext.getInitParameter(SHUTDOWN_HOOK_BEAN));
        wac.setShutdownMethod(servletContext.getInitParameter(SHUTDOWN_HOOK_METHOD));
        customizeContext(servletContext, wac);
        wac.refresh();

        return wac;
    }

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
}
