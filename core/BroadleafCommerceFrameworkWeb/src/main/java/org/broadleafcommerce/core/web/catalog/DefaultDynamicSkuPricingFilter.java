package org.broadleafcommerce.core.web.catalog;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;

/**
 * Register this filter via Spring DelegatingFilterProxy, or register your own implementation
 * that provides additional, desirable members to the pricingConsiderations Map
 * that is generated from the getPricingConsiderations method.
 * 
 * @author jfischer
 *
 */
public class DefaultDynamicSkuPricingFilter extends AbstractDynamicSkuPricingFilter {
	
	@Resource(name="blDynamicSkuPricingService")
	protected DynamicSkuPricingService skuPricingService;
	
	@Resource(name="blCustomerState")
    protected CustomerState customerState;

	public DynamicSkuPricingService getDynamicSkuPricingService(ServletRequest arg0) {
		return skuPricingService;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap getPricingConsiderations(ServletRequest request) {
		HashMap pricingConsiderations = new HashMap();
		Customer customer = customerState.getCustomer((HttpServletRequest)  request);
		pricingConsiderations.put("customer", customer);
		
		return pricingConsiderations;
	}

}
