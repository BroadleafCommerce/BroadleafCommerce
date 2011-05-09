package com.gwtincubator.security.server;

import javax.servlet.http.HttpServletRequest;

import org.gwtwidgets.server.spring.GWTHandler;
import org.gwtwidgets.server.spring.GWTRPCServiceExporter;
import org.springframework.web.servlet.HandlerExecutionChain;

/**
 * Specific version of George Georgovassilis useful GWTHandler class.
 *
 * @author David MARTIN
 * 
 * BroadleafCommerce
 * Changed to remove transmorph dependency
 * @author jfischer
 */
public class GWTSecuredHandler extends GWTHandler {

	/**
	 * A new security enhanced GWTRPCServiceExporter class is returned into the HandlerExecutionChain.
	 * @param request the HttpServletRequest
	 * @return a HandlerExecutionChain object
	 */
	@Override
	protected Object getHandlerInternal(final HttpServletRequest request) throws Exception {
		final Object handlerWrapper = super.getHandlerInternal(request);
		if (handlerWrapper instanceof HandlerExecutionChain) {
			final Object handler = ((HandlerExecutionChain) handlerWrapper).getHandler();
			if (handler instanceof GWTRPCServiceExporter) {
				GWTRPCSecuredServiceExporter securedExporter = new GWTRPCSecuredServiceExporter((GWTRPCServiceExporter) handler);
				return new HandlerExecutionChain(securedExporter, ((HandlerExecutionChain) handlerWrapper).getInterceptors());
			}
		}
		return handlerWrapper;
	}

}
