package org.broadleafcommerce.gwt.security;

import javax.servlet.http.HttpServletRequest;

import net.entropysoft.transmorph.DefaultConverters;
import net.entropysoft.transmorph.Transmorph;
import net.entropysoft.transmorph.converters.beans.BeanToBeanMapping;

import org.gwtwidgets.server.spring.GWTHandler;
import org.gwtwidgets.server.spring.GWTRPCServiceExporter;
import org.springframework.web.servlet.HandlerExecutionChain;

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
