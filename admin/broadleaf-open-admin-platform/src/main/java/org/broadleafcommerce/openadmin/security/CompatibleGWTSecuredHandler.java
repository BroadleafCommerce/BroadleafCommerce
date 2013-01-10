/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.security;

import javax.servlet.http.HttpServletRequest;

import net.entropysoft.transmorph.DefaultConverters;
import net.entropysoft.transmorph.Transmorph;
import net.entropysoft.transmorph.converters.beans.BeanToBeanMapping;

import org.gwtwidgets.server.spring.GWTHandler;
import org.gwtwidgets.server.spring.GWTRPCServiceExporter;
import org.springframework.web.servlet.HandlerExecutionChain;

/**
 * 
 * @author jfischer
 *
 */
public class CompatibleGWTSecuredHandler extends GWTHandler {

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        final Object handlerWrapper = super.getHandlerInternal(request);
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

                final CompatibleGWTSecuredRPCServiceExporter wrapper = (CompatibleGWTSecuredRPCServiceExporter) transmorph.convert(handler, CompatibleGWTSecuredRPCServiceExporter.class);
                wrapper.afterPropertiesSet();

                return new HandlerExecutionChain(wrapper, ((HandlerExecutionChain) handlerWrapper).getInterceptors());
            }
        }
        return handlerWrapper;
    }

}
