package org.broadleafcommerce.core.web.money;

import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;

import org.broadleafcommerce.common.money.CurrencyConversionService;

public interface CurrencyConversionPricingFilter extends Filter {
	
	@SuppressWarnings("rawtypes")
	public HashMap getCurrencyConversionContext(ServletRequest request);
	
	public CurrencyConversionService getCurrencyConversionService(ServletRequest request);
	
}
