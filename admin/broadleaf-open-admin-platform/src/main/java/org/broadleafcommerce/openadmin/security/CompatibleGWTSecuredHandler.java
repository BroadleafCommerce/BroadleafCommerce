/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.security;

import net.entropysoft.transmorph.DefaultConverters;
import net.entropysoft.transmorph.Transmorph;
import net.entropysoft.transmorph.converters.beans.BeanToBeanMapping;
import org.apache.commons.collections.MapUtils;
import org.gwtwidgets.server.spring.DefaultRPCServiceExporterFactory;
import org.gwtwidgets.server.spring.GWTHandler;
import org.gwtwidgets.server.spring.GWTRPCServiceExporter;
import org.gwtwidgets.server.spring.RPCServiceExporter;
import org.gwtwidgets.server.spring.ReflectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class CompatibleGWTSecuredHandler extends GWTHandler {

    @Override
	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
		Object handlerWrapper = getMyHandlerInternal(request);
		if (handlerWrapper instanceof HandlerExecutionChain) {
			final Object handler = ((HandlerExecutionChain) handlerWrapper).getHandler();
			if (handler instanceof GWTRPCServiceExporter) {
				final DefaultConverters defaultConverters = new DefaultConverters();
				final Transmorph transmorph = new Transmorph(this.getClass().getClassLoader(), defaultConverters);

				BeanToBeanMapping beanToBeanMapping = null;
				beanToBeanMapping = new BeanToBeanMapping(
						CompatibleGWTSecuredRPCServiceExporter.class,
						GWTRPCServiceExporter.class);
				defaultConverters.getBeanToBean().addBeanToBeanMapping(beanToBeanMapping);

				beanToBeanMapping = new BeanToBeanMapping(
						GWTRPCServiceExporter.class,
						CompatibleGWTSecuredRPCServiceExporter.class);
				defaultConverters.getBeanToBean().addBeanToBeanMapping(beanToBeanMapping);

				final CompatibleGWTSecuredRPCServiceExporter wrapper = transmorph.convert(handler, CompatibleGWTSecuredRPCServiceExporter.class);
				wrapper.afterPropertiesSet();

				return new HandlerExecutionChain(wrapper, ((HandlerExecutionChain) handlerWrapper).getInterceptors());
			}
		}
		return handlerWrapper;
	}

    /*
    This was implemented as a workaround for a problem with jrebel and its spring support.
    For some reason, jrebel causes the handlerMap to be cleared for the handlers we register.
     */
    protected Object getMyHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        Field mapField = AbstractUrlHandlerMapping.class.getDeclaredField("handlerMap");
        mapField.setAccessible(true);
        Map<String, Object> handlerMap = (Map<String, Object>) mapField.get(this);
        if (MapUtils.isEmpty(handlerMap)) {
            afterPropertiesSet();
        }
        Object handler = lookupHandler(lookupPath, request);
        if (handler == null) {
            // We need to care for the default handler directly, since we need to
            // expose the PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE for it as well.
            Object rawHandler = null;
            if ("/".equals(lookupPath)) {
                rawHandler = getRootHandler();
            }
            if (rawHandler == null) {
                rawHandler = getDefaultHandler();
            }
            if (rawHandler != null) {
                // Bean name or resolved handler?
                if (rawHandler instanceof String) {
                    String handlerName = (String) rawHandler;
                    rawHandler = getApplicationContext().getBean(handlerName);
                }
                validateHandler(rawHandler, request);
                handler = buildPathExposingHandler(rawHandler, lookupPath, lookupPath, null);
            }
        }
        if (handler != null && logger.isDebugEnabled()) {
            logger.debug("Mapping [" + lookupPath + "] to " + handler);
        }
        else if (handler == null && logger.isTraceEnabled()) {
            logger.trace("No handler mapping found for [" + lookupPath + "]");
        }
        return handler;
    }

    /*
    This was implemented as a workaround for a problem with jrebel and its spring support.
    For some reason, jrebel causes the handlerMap to be cleared for the handlers we register.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (factory == null){
            DefaultRPCServiceExporterFactory defaultFactory = new DefaultRPCServiceExporterFactory();
            defaultFactory.setResponseCompressionEnabled(responseCompressionEnabled);
            factory = defaultFactory;
        }
        Method scanForAnnotatedBeans = GWTHandler.class.getDeclaredMethod("scanForAnnotatedBeans", ApplicationContext.class);
        scanForAnnotatedBeans.setAccessible(true);
        scanForAnnotatedBeans.invoke(this, getApplicationContext());
        Method initServiceInstance = GWTHandler.class.getDeclaredMethod("initServiceInstance", RPCServiceExporter.class, Object.class, Class[].class);
        initServiceInstance.setAccessible(true);
        Field mapping = GWTHandler.class.getDeclaredField("_mapping");
        mapping.setAccessible(true);
        Map<String, Object> map = (Map<String, Object>) mapping.get(this);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            RPCServiceExporter exporter = factory.create();
            registerHandler(entry.getKey(), initServiceInstance.invoke(this, exporter, entry
                    .getValue(), ReflectionUtils.getExposedInterfaces(entry
                    .getValue().getClass())));
        }
    }

}
