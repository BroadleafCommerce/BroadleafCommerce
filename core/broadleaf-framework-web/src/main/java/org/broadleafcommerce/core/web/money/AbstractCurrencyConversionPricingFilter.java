package org.broadleafcommerce.core.web.money;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.broadleafcommerce.common.money.CurrencyConversionContext;

public abstract class AbstractCurrencyConversionPricingFilter implements CurrencyConversionPricingFilter {
	
	public void destroy() {
		//do nothing
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		CurrencyConversionContext.setCurrencyConversionContext(getCurrencyConversionContext(request));
		CurrencyConversionContext.setCurrencyConversionService(getCurrencyConversionService(request));
		try {
			filterChain.doFilter(request, response);
		} finally {
			CurrencyConversionContext.setCurrencyConversionContext(null);
			CurrencyConversionContext.setCurrencyConversionService(null);
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
		//do nothing
	}
	
}
