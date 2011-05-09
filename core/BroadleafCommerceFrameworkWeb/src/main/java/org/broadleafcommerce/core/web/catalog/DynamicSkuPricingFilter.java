package org.broadleafcommerce.core.web.catalog;

import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;

import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;

public interface DynamicSkuPricingFilter extends Filter {

	@SuppressWarnings("rawtypes")
	public abstract HashMap getPricingConsiderations(ServletRequest arg0);
	
	public abstract DynamicSkuPricingService getDynamicSkuPricingService(ServletRequest arg0);
	
}
