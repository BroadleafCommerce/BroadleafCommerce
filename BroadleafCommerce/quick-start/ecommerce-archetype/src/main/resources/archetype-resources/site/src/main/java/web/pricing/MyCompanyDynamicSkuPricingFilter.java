#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.pricing;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.core.web.catalog.AbstractDynamicSkuPricingFilter;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Component;

@Component("myCompanyDynamicSkuPricingFilter")
public class MyCompanyDynamicSkuPricingFilter extends AbstractDynamicSkuPricingFilter {
	
	@Resource(name="myCompanyDynamicSkuPricingService")
	protected DynamicSkuPricingService skuPricingService;
	
	@Resource(name="blCustomerState")
    protected CustomerState customerState;

	@Override
	public DynamicSkuPricingService getDynamicSkuPricingService(ServletRequest arg0) {
		return skuPricingService;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public HashMap getPricingConsiderations(ServletRequest request) {
		HashMap pricingConsiderations = new HashMap();
		Customer customer = customerState.getCustomer((HttpServletRequest)  request);
		pricingConsiderations.put("customer", customer);
		//TODO put any additional information necessary to price for this customer in the pricing consideration
		
		return pricingConsiderations;
	}

}
