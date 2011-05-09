package org.broadleafcommerce.core.web.catalog;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;

public abstract class AbstractDynamicSkuPricingFilter implements DynamicSkuPricingFilter {

	public void destroy() {
		//do nothing
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		SkuPricingConsiderationContext.setSkuPricingConsiderationContext(getPricingConsiderations(request));
		SkuPricingConsiderationContext.setSkuPricingService(getDynamicSkuPricingService(request));
		try {
			filterChain.doFilter(request, response);
		} finally {
			SkuPricingConsiderationContext.setSkuPricingConsiderationContext(null);
			SkuPricingConsiderationContext.setSkuPricingService(null);
		}
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		//do nothing
	}
	
	

}
